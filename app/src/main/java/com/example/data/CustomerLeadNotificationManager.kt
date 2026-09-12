package com.example.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R

/**
 * System notification manager for real-time customer leads, emergency trade inquiries,
 * and dispatch alerts on Android.
 */
object CustomerLeadNotificationManager {

    const val CHANNEL_CUSTOMER_LEADS = "c520x_customer_leads"
    const val CHANNEL_DISPATCH_ALERTS = "c520x_dispatch_alerts"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // 1. High-Priority Emergency Customer Inquiries Channel
            val leadsChannel = NotificationChannel(
                CHANNEL_CUSTOMER_LEADS,
                "Emergency Customer Inquiries & Leads",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Real-time alerts for incoming homeowner and commercial job inquiries, Thumbtack leads, and booking requests."
                enableLights(true)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 150, 300, 150, 400)
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
            }

            // 2. Dispatch Alerts Channel
            val dispatchChannel = NotificationChannel(
                CHANNEL_DISPATCH_ALERTS,
                "C520X Autonomous Dispatches",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Technician assignment and route tracking updates."
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(leadsChannel)
            notificationManager.createNotificationChannel(dispatchChannel)
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun showCustomerLeadNotification(context: Context, lead: LeadEntity) {
        createNotificationChannels(context)

        if (!hasNotificationPermission(context)) {
            return
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("TARGET_TAB", "LEADS")
            putExtra("LEAD_ID", lead.id)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            lead.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val urgencyEmoji = when (lead.urgency.lowercase()) {
            "emergency" -> "🚨 EMERGENCY"
            "today" -> "⚡ TODAY"
            else -> "📋 NEW LEAD"
        }

        val formattedBudget = if (lead.budget > 0) "$${"%,.0f".format(lead.budget)}" else "Custom Quote"

        val notification = NotificationCompat.Builder(context, CHANNEL_CUSTOMER_LEADS)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("$urgencyEmoji: ${lead.serviceTrade} • $formattedBudget")
            .setContentText("${lead.clientName} (${lead.source}): ${lead.projectDescription}")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        "${lead.clientName}\n" +
                                "Trade: ${lead.serviceTrade} | Budget: $formattedBudget\n" +
                                "Source: ${lead.source} | Urgency: ${lead.urgency}\n" +
                                "Phone: ${lead.clientPhone}\n\n" +
                                "Scope: ${lead.projectDescription}\n\n" +
                                "Tap to open C520X Dispatch and send instant 80/20 quote!"
                    )
            )
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 300, 150, 300, 150, 400))
            .build()

        try {
            val notificationId = if (lead.id != 0) lead.id else (System.currentTimeMillis() % 100000).toInt()
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (e: SecurityException) {
            // Handled gracefully if permission revoked
        }
    }
}
