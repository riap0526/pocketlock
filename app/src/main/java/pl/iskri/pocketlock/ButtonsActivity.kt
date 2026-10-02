package pl.iskri.pocketlock

import android.app.Activity
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import kotlin.math.roundToInt

class ButtonsActivity : Activity() {

    private lateinit var list: LinearLayout
    private lateinit var prompt: TextView
    private lateinit var addButton: Button
    private var listening = false
    private var lastHatKey = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buttons)

        list = findViewById(R.id.buttons_list)
        prompt = findViewById(R.id.tvCapturePrompt)
        addButton = findViewById(R.id.btnAddButton)

        addButton.setOnClickListener { setListening(!listening) }
        findViewById<Button>(R.id.btnRemoveAll).setOnClickListener { confirmRemoveAll() }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val content = findViewById<ScrollView>(R.id.buttons_content)
            content.setOnApplyWindowInsetsListener { view, insets ->
                val gestureBottom = insets.getInsets(WindowInsets.Type.systemGestures()).bottom
                view.setPadding(
                    view.paddingLeft,
                    view.paddingTop,
                    view.paddingRight,
                    dp(72) + gestureBottom
                )
                insets
            }
        }

        rebuildList()
    }

    override fun onResume() {
        super.onResume()
        rebuildList()
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (listening && event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
            if (event.keyCode == KeyEvent.KEYCODE_BACK) {
                setListening(false)
                return true
            }
            addKey(event.keyCode)
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    override fun dispatchGenericMotionEvent(event: MotionEvent): Boolean {
        if (listening) {
            val keyCode = ButtonMap.hatKey(
                event.getAxisValue(MotionEvent.AXIS_HAT_X),
                event.getAxisValue(MotionEvent.AXIS_HAT_Y)
            )
            if (keyCode != null) {
                if (keyCode != lastHatKey) {
                    lastHatKey = keyCode
                    addKey(keyCode)
                    return true
                }
            } else {
                lastHatKey = 0
            }
        }
        return super.dispatchGenericMotionEvent(event)
    }

    private fun addKey(keyCode: Int) {
        Prefs.captureButton(this, keyCode)
        Prefs.restoreButton(this, keyCode)
        setListening(false)
        rebuildList()
        Toast.makeText(
            this,
            getString(R.string.button_added, ButtonMap.label(keyCode)),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun visibleKeys(): List<Int> =
        (Prefs.capturedButtons(this) - Prefs.removedButtons(this)).sorted()

    private fun rebuildList() {
        list.removeAllViews()
        val keys = visibleKeys()
        if (keys.isEmpty()) {
            list.addView(infoText(getString(R.string.buttons_empty)))
            return
        }
        for (keyCode in keys) {
            list.addView(buttonRow(keyCode))
        }
    }

    private fun buttonRow(keyCode: Int): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(4), 0, dp(4))
        }

        val label = TextView(this).apply {
            text = ButtonMap.label(keyCode)
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 16f
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val remove = Button(this).apply {
            text = getString(R.string.btn_remove)
            setOnClickListener {
                Prefs.removeButton(this@ButtonsActivity, keyCode)
                rebuildList()
            }
        }

        row.addView(label)
        row.addView(remove)
        return row
    }

    private fun confirmRemoveAll() {
        if (visibleKeys().isEmpty()) return
        AlertDialog.Builder(this)
            .setTitle(R.string.remove_all_confirm_title)
            .setMessage(R.string.remove_all_confirm_message)
            .setPositiveButton(R.string.btn_remove_all) { _, _ ->
                Prefs.removeAllButtons(this)
                rebuildList()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun setListening(value: Boolean) {
        listening = value
        lastHatKey = 0
        addButton.setText(
            if (value) R.string.btn_assign_cancel else R.string.btn_add_button
        )
        prompt.visibility = if (value) View.VISIBLE else View.GONE
        prompt.text = getString(R.string.buttons_capture_prompt)
    }

    private fun infoText(text: String): TextView = TextView(this).apply {
        setTextColor(0xFFAAAAAA.toInt())
        textSize = 14f
        this.text = text
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).roundToInt()
}
