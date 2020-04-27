package br.com.training.android.tictactoeapp

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class Notifications() {
    private val _notifyTag = "new request"

    fun notify(ctx: Context, message: String, number: Int){
        val intent = Intent(ctx, LoginActivity::class.java)
        val builder = NotificationCompat.Builder(ctx).setDefaults(Notification.DEFAULT_ALL)
            .setContentTitle("New request")
            .setContentText(message)
            .setNumber(number)
            .setSmallIcon(R.drawable.tictac)
            .setContentIntent(PendingIntent.getActivity(ctx, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT))
            .setAutoCancel(true)
        val notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(_notifyTag, 0, builder.build())
    }

}