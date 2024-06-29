package com.example.pop_upnotification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.pop_upnotification.ui.theme.PopupNotificationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PopupNotificationTheme {
                MainScreen()
            }
        }
    }
}

@Composable
private fun MainScreen(){
    val context = LocalContext.current
    val NOTIFICATION_ID = 123450991
    val CHANNEL_ID = "DemoChannel"
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {}
    )
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()) {
            Button(onClick = {

                createNotificationChannel(context, CHANNEL_ID)

                val builder = notificationBuilder(context, CHANNEL_ID)

                checkPermissionAndSendNotification(context,
                    notificationPermissionLauncher,
                    builder,
                    NOTIFICATION_ID)
            }) {
                Text(text = "Create Notification")
            }
        }
    }
}

private fun createNotificationChannel(context: Context, CHANNEL_ID: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Demo Notification Channel"
        val descriptionText = "This is the definition of the Demo Notification channel"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager=context.getSystemService(
            Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

private fun notificationBuilder(context: Context,
                                CHANNEL_ID: String
) : NotificationCompat.Builder {
    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle("My notification")
        .setContentText("Much longer text that cannot fit one line...")
        .setStyle(NotificationCompat.BigTextStyle()
            .bigText("Much longer text that cannot fit one line... and I am writing " +
                    "some random stuff to make this text big and " +
                    "big and way more bigger than the previous one"))
        .setPriority(NotificationCompat.PRIORITY_HIGH)
    return builder
}

private fun checkPermissionAndSendNotification(context: Context,
                                               notificationPermissionLauncher:
                                               ManagedActivityResultLauncher<String, Boolean>,
                                               builder: NotificationCompat.Builder,
                                               NOTIFICATION_ID: Int
){
    with(NotificationManagerCompat.from(context)) {
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(
                android.Manifest.permission.POST_NOTIFICATIONS
            )
            return@with
        }
        notify(NOTIFICATION_ID, builder.build())
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PopupNotificationTheme {
        MainScreen()
    }
}