package pl.iskri.pocketlock

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.os.BatteryManager
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import kotlin.math.roundToInt

/**
 * Battery icon (outline + fill level, lightning bolt while charging) with an optional percentage.
 * Styling comes from [Prefs]; the level is read from the sticky ACTION_BATTERY_CHANGED broadcast.
 * The receiver is only registered while the view is attached and the indicator is enabled.
 */
class BatteryIndicatorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private val icon = IconView(context)
    private val label = TextView(context)
    private var level = -1
    private var charging = false
    private var registered = false
    private var rootW = 0
    private var rootH = 0

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) = readBattery(intent)
    }

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        addView(icon, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
        label.setShadowLayer(dp(3f), 0f, 0f, 0xAA000000.toInt())
        label.includeFontPadding = false
        addView(label, LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT))
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        syncRegistration()
    }

    override fun onDetachedFromWindow() {
        unregister()
        super.onDetachedFromWindow()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        // Our own size is known only now (and changes with the level text), so re-clamp.
        if (rootW > 0 && rootH > 0) updateTranslation()
    }

    /** Re-reads the preferences: visibility, size, colors, percentage on/off. */
    fun applyStyle() {
        val enabled = Prefs.isBatteryEnabled(context)
        visibility = if (enabled) VISIBLE else GONE
        val scale = Prefs.batteryScale(context)
        icon.unit = dp(1f) * scale
        label.setTextSize(TypedValue.COMPLEX_UNIT_PX, dp(16f) * scale)
        label.setPadding((dp(6f) * scale).roundToInt(), 0, 0, 0)
        label.visibility = if (Prefs.isBatteryPercentEnabled(context)) VISIBLE else GONE
        syncRegistration()
        render()
    }

    /** Places the indicator at the stored center fraction, kept fully inside the screen. */
    fun positionIn(width: Int, height: Int) {
        rootW = width
        rootH = height
        updateTranslation()
    }

    private fun updateTranslation() {
        if (width <= 0 || height <= 0) return
        val inset = dp(10f)
        val x = Prefs.batteryCenterX(context) * rootW - width / 2f
        val y = Prefs.batteryCenterY(context) * rootH - height / 2f
        translationX = x.coerceIn(inset, (rootW - width - inset).coerceAtLeast(inset))
        translationY = y.coerceIn(inset, (rootH - height - inset).coerceAtLeast(inset))
    }

    private fun syncRegistration() {
        if (!isAttachedToWindow) return
        if (Prefs.isBatteryEnabled(context)) {
            if (!registered) {
                // Sticky broadcast: registering returns the current state immediately.
                val sticky = try {
                    context.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
                } catch (_: Throwable) {
                    null
                }
                registered = true
                readBattery(sticky)
            }
        } else {
            unregister()
        }
    }

    private fun unregister() {
        if (!registered) return
        registered = false
        try {
            context.unregisterReceiver(receiver)
        } catch (_: Throwable) {
        }
    }

    private fun readBattery(intent: Intent?) {
        intent ?: return
        val raw = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        if (raw < 0 || scale <= 0) return
        level = (raw * 100f / scale).roundToInt().coerceIn(0, 100)
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        charging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
            status == BatteryManager.BATTERY_STATUS_FULL
        render()
    }

    private fun render() {
        // Outline and text always keep the normal color; only the fill turns to the charging
        // color, so the percentage stays easy to read.
        val color = Prefs.batteryColor(context)
        icon.level = level
        icon.charging = charging
        icon.color = color
        icon.fillColor = if (charging) Prefs.batteryChargingColor(context) else color
        icon.invalidate()
        label.text = if (level >= 0) "$level%" else ""
        label.setTextColor(color)
    }

    private fun dp(value: Float): Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, resources.displayMetrics)

    /** Draws the battery body, its fill and (while charging) a lightning bolt. */
    private class IconView(context: Context) : View(context) {

        var unit = 1f
            set(value) {
                if (field != value) {
                    field = value
                    requestLayout()
                }
            }
        var level = -1
        var charging = false
        var color = 0xFFFFFFFF.toInt()
        var fillColor = 0xFFFFFFFF.toInt()

        private val outline = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }
        private val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
        private val nubPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
        private val boltFill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
            color = 0xFFFFFFFF.toInt()
        }
        private val boltEdge = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            color = 0xCC000000.toInt()
        }
        private val body = RectF()
        private val fillRect = RectF()
        private val nub = RectF()
        private val bolt = Path()

        override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
            setMeasuredDimension((ICON_W * unit).roundToInt(), (ICON_H * unit).roundToInt())
        }

        override fun onDraw(canvas: Canvas) {
            val u = unit
            val stroke = 1.5f * u
            outline.strokeWidth = stroke
            outline.color = color
            fill.color = fillColor
            nubPaint.color = color

            body.set(stroke / 2f, stroke / 2f, BODY_W * u - stroke / 2f, ICON_H * u - stroke / 2f)
            canvas.drawRoundRect(body, 2.5f * u, 2.5f * u, outline)

            nub.set(BODY_W * u + 0.5f * u, ICON_H * u * 0.3f, ICON_W * u, ICON_H * u * 0.7f)
            canvas.drawRoundRect(nub, 1f * u, 1f * u, nubPaint)

            if (level > 0) {
                val gap = stroke + 1.2f * u
                val innerW = BODY_W * u - 2f * gap
                val w = (innerW * level / 100f).coerceAtLeast(1.5f * u)
                fillRect.set(gap, gap, gap + w, ICON_H * u - gap)
                canvas.drawRoundRect(fillRect, 1f * u, 1f * u, fill)
            }

            if (charging) {
                // Bolt centered on the body, white with a dark edge so it reads on any fill color.
                val cx = BODY_W * u / 2f
                val cy = ICON_H * u / 2f
                val s = u * 0.55f
                bolt.reset()
                bolt.moveTo(cx + 1.5f * s, cy - 8f * s)
                bolt.lineTo(cx - 5f * s, cy + 1f * s)
                bolt.lineTo(cx - 0.5f * s, cy + 1f * s)
                bolt.lineTo(cx - 1.5f * s, cy + 8f * s)
                bolt.lineTo(cx + 5f * s, cy - 1f * s)
                bolt.lineTo(cx + 0.5f * s, cy - 1f * s)
                bolt.close()
                boltEdge.strokeWidth = 0.9f * u
                canvas.drawPath(bolt, boltEdge)
                canvas.drawPath(bolt, boltFill)
            }
        }

        private companion object {
            const val ICON_W = 30f
            const val ICON_H = 15f
            const val BODY_W = 27f
        }
    }
}
