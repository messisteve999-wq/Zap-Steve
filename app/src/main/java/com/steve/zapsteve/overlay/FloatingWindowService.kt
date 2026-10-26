package com.steve.zapsteve.overlay

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import com.steve.zapsteve.ai.LocalSuggestionEngine
import com.steve.zapsteve.notifications.MessageNotificationListener

class FloatingWindowService : Service() {
    companion object {
        const val ACTION_SHOW = "com.steve.zapsteve.SHOW"
        const val ACTION_HIDE = "com.steve.zapsteve.HIDE"
        const val ACTION_NEW_MESSAGE = "com.steve.zapsteve.NEW_MESSAGE"
        const val EXTRA_MESSAGE = "message"
    }

    private lateinit var wm: WindowManager
    private var root: LinearLayout? = null
    private var params: WindowManager.LayoutParams? = null
    private var messageView: TextView? = null
    private var suggestionsBox: LinearLayout? = null
    private var input: EditText? = null

    private val bg = 0xFF101714.toInt()
    private val panel = 0xFF1B2821.toInt()
    private val green = 0xFF25D366.toInt()
    private val white = 0xFFF1F5F2.toInt()
    private val muted = 0xFFB5C4BA.toInt()

    override fun onCreate() {
        super.onCreate()
        wm = getSystemService(WINDOW_SERVICE) as WindowManager
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_HIDE -> removeWindow()
            ACTION_NEW_MESSAGE -> {
                val text = intent.getStringExtra(EXTRA_MESSAGE).orEmpty()
                showWindow()
                updateMessage(text)
            }
            else -> showWindow()
        }
        return START_NOT_STICKY
    }

    private fun showWindow() {
        if (root != null) return
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(12), dp(12), dp(12), dp(12))
            setBackgroundColor(bg)
        }
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundColor(panel)
            setPadding(dp(8), dp(6), dp(4), dp(6))
        }
        val title = TextView(this).apply {
            text = "✦ ZAP STEVE"
            textSize = 16f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(green)
        }
        header.addView(title, LinearLayout.LayoutParams(0, dp(40), 1f))
        val close = Button(this).apply {
            text = "×"
            isAllCaps = false
            setOnClickListener { removeWindow() }
        }
        header.addView(close, LinearLayout.LayoutParams(dp(44), dp(40)))
        container.addView(header)

        messageView = TextView(this).apply {
            text = "Message détecté ou texte de test :"
            textSize = 14f
            setTextColor(white)
            setPadding(dp(8), dp(10), dp(8), dp(8))
        }
        container.addView(messageView)

        input = EditText(this).apply {
            hint = "Teste ici un message reçu…"
            setTextColor(white)
            setHintTextColor(muted)
            setBackgroundColor(panel)
            setPadding(dp(8), dp(8), dp(8), dp(8))
            minLines = 2
            maxLines = 3
        }
        container.addView(input)

        val actions = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        val generate = Button(this).apply {
            text = "5 suggestions"
            isAllCaps = false
            setOnClickListener { generateSuggestions(input?.text?.toString().orEmpty()) }
        }
        actions.addView(generate, LinearLayout.LayoutParams(0, dp(46), 1f))
        val sample = Button(this).apply {
            text = "Exemple"
            isAllCaps = false
            setOnClickListener {
                input?.setText("Tu fais quoi de beau ce soir ?")
                generateSuggestions("Tu fais quoi de beau ce soir ?")
            }
        }
        actions.addView(sample, LinearLayout.LayoutParams(0, dp(46), 1f))
        container.addView(actions)

        val scroll = ScrollView(this)
        suggestionsBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        scroll.addView(suggestionsBox)
        container.addView(scroll, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, dp(270)
        ))

        val lp = WindowManager.LayoutParams(
            dp(330),
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            x = dp(8)
            y = dp(90)
        }

        // Allow moving the panel by dragging its header.
        header.setOnTouchListener(object : View.OnTouchListener {
            var startX = 0
            var startY = 0
            var touchX = 0f
            var touchY = 0f
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startX = lp.x; startY = lp.y
                        touchX = event.rawX; touchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        lp.x = startX - (event.rawX - touchX).toInt()
                        lp.y = startY + (event.rawY - touchY).toInt()
                        try { wm.updateViewLayout(container, lp) } catch (_: Exception) {}
                        return true
                    }
                }
                return false
            }
        })

        try {
            wm.addView(container, lp)
            root = container
            params = lp
            generateSuggestions("")
        } catch (e: Exception) {
            Toast.makeText(this, "Impossible d'afficher la fenêtre. Vérifie l'autorisation.", Toast.LENGTH_LONG).show()
            stopSelf()
        }
    }

    private fun updateMessage(text: String) {
        messageView?.text = "Message reçu (aperçu notification) :"
        input?.setText(text)
        generateSuggestions(text)
    }

    private fun generateSuggestions(text: String) {
        val box = suggestionsBox ?: return
        box.removeAllViews()
        val results = LocalSuggestionEngine.generate(text)
        results.forEach { (category, suggestion) ->
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(8), dp(7), dp(8), dp(5))
                setBackgroundColor(panel)
            }
            card.addView(TextView(this).apply {
                this.text = category
                textSize = 12f
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(green)
            })
            card.addView(TextView(this).apply {
                this.text = suggestion
                textSize = 14f
                setTextColor(white)
                setPadding(0, dp(3), 0, dp(5))
            })
            card.addView(Button(this).apply {
                text = "Copier"
                isAllCaps = false
                setOnClickListener {
                    val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
                    clipboard.setPrimaryClip(android.content.ClipData.newPlainText("Suggestion Zap Steve", suggestion))
                    Toast.makeText(this@FloatingWindowService, "Suggestion copiée.", Toast.LENGTH_SHORT).show()
                }
            })
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.topMargin = dp(7)
            box.addView(card, lp)
        }
    }

    private fun removeWindow() {
        root?.let {
            try { wm.removeView(it) } catch (_: Exception) {}
        }
        root = null
        messageView = null
        suggestionsBox = null
        input = null
        stopSelf()
    }

    override fun onDestroy() {
        root?.let { try { wm.removeView(it) } catch (_: Exception) {} }
        root = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
}
