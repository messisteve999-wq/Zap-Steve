package com.steve.zapsteve.notifications

import android.app.Notification
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.steve.zapsteve.overlay.FloatingWindowService

class MessageNotificationListener : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn == null) return
        val packageName = sbn.packageName ?: return
        if (!isSupportedApp(packageName)) return

        val extras = sbn.notification.extras ?: return
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()
        val text = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()
            ?.takeIf { it.isNotBlank() }
            ?: extras.getCharSequence(Notification.EXTRA_TEXT)?.toString().orEmpty()

        if (text.isBlank()) return
        // Keep the overlay payload short and avoid collecting whole histories.
        val message = buildString {
            if (title.isNotBlank()) append(title.take(70)).append(": ")
            append(text.take(500))
        }

        try {
            startService(
                Intent(this, FloatingWindowService::class.java)
                    .setAction(FloatingWindowService.ACTION_NEW_MESSAGE)
                    .putExtra(FloatingWindowService.EXTRA_MESSAGE, message)
            )
        } catch (_: Exception) {
            // The user can open the floating panel manually if Android blocks background launch.
        }
    }

    private fun isSupportedApp(pkg: String): Boolean {
        val p = pkg.lowercase()
        return p.contains("whatsapp") ||
            p.contains("facebook.orca") || // Messenger
            p.contains("messaging") ||
            p.contains("sms") ||
            p.contains("textra") ||
            p.contains("telegram") ||
            p.contains("instagram")
    }
}
