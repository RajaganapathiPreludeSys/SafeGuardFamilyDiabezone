package com.safeguardFamily.diabezone.common

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.safeguardFamily.diabezone.R
import com.safeguardFamily.diabezone.ui.activity.DashboardActivity

class FirebaseMessageReceiver : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("Token", "onNewToken: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.notification != null) {
            showNotification(
                remoteMessage.notification!!.title,
                remoteMessage.notification!!.body
            )
        }
    }

    private fun getCustomDesign(title: String?, message: String?): RemoteViews {
        val remoteViews = RemoteViews(
            applicationContext.packageName,
            R.layout.notification
        )
        remoteViews.setTextViewText(R.id.title, title)
        remoteViews.setTextViewText(R.id.message, message)
        remoteViews.setImageViewResource(R.id.icon, R.mipmap.ic_launcher)
        return remoteViews
    }

    private fun showNotification(title: String?, message: String?) {
        val intent = Intent(this, DashboardActivity::class.java)
        val channelId = "notification_channel"
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        var builder = NotificationCompat.Builder(
            applicationContext,
            channelId
        )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        builder = builder.setContent(getCustomDesign(title, message))
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, builder.build())
    }
}