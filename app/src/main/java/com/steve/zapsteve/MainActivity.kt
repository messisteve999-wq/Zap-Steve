package com.steve.zapsteve

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.steve.zapsteve.overlay.FloatingWindowService

class MainActivity : Activity() {
    private val bg = 0xFF101714.toInt()
    private val panel = 0xFF1B2821.toInt()
    private val green = 0xFF25D366.toInt()
    private val white = 0xFFF1F5F2.toInt()
    private val muted = 0xFFB5C4BA.toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(22), dp(28), dp(22), dp(24))
            setBackgroundColor(bg)
            gravity = Gravity.TOP
        }
        root.addView(TextView(this).apply {
            text = "ZAP STEVE"
            textSize = 28f
            setTextColor(green)
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        })
        root.addView(TextView(this).apply {
            text = "Assistant de réponses • version 0.1.0"
            textSize = 14f
            setTextColor(muted)
            setPadding(0, dp(4), 0, dp(18))
        })
        root.addView(TextView(this).apply {
            text = "Zap Steve utilise les notifications visibles pour détecter certains messages. Il ne lit pas directement toutes les conversations des applications."
            textSize = 15f
            setTextColor(white)
            setPadding(dp(14), dp(14), dp(14), dp(14))
            setBackgroundColor(panel)
        }, matchWrap())

        addButton(root, "1. Autoriser la fenêtre flottante") {
            if (!Settings.canDrawOverlays(this)) {
                startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
            } else {
                toast("Autorisation déjà accordée.")
            }
        }
        addButton(root, "2. Autoriser l'accès aux notifications") {
            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
        }
        addButton(root, "3. Afficher la fenêtre Zap Steve") {
            if (!Settings.canDrawOverlays(this)) {
                toast("Autorise d'abord la fenêtre flottante.")
                startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
            } else {
                startService(Intent(this, FloatingWindowService::class.java).setAction(FloatingWindowService.ACTION_SHOW))
                toast("Fenêtre Zap Steve démarrée.")
            }
        }
        addButton(root, "Masquer la fenêtre flottante") {
            startService(Intent(this, FloatingWindowService::class.java).setAction(FloatingWindowService.ACTION_HIDE))
        }
        root.addView(TextView(this).apply {
            text = "\nConfidentialité : le moteur de suggestions est local et basé sur des règles. Aucun message n'est envoyé automatiquement. Les suggestions doivent être copiées puis envoyées par toi."
            textSize = 13f
            setTextColor(muted)
        }, matchWrap())
        setContentView(root)
    }

    private fun addButton(root: LinearLayout, label: String, action: () -> Unit) {
        val b = Button(this).apply {
            text = label
            isAllCaps = false
            setTextColor(white)
            backgroundTintList = android.content.res.ColorStateList.valueOf(0xFF263A2E.toInt())
            setPadding(dp(10), dp(9), dp(10), dp(9))
            setOnClickListener { action() }
        }
        val p = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        p.topMargin = dp(12)
        root.addView(b, p)
    }

    private fun matchWrap() = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
    private fun toast(s: String) = Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
}
