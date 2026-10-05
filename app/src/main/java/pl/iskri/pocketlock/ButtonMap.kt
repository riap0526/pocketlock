package pl.iskri.pocketlock

import android.view.KeyEvent
import kotlin.math.abs

/**
 * Friendly names for the gamepad/console key codes that can appear on the lock screen. Any key
 * code without a dedicated name falls back to its platform name.
 */
object ButtonMap {

    /**
     * Maps the D-pad, which many handhelds report only as the analog HAT axes (no DPAD key
     * events), to the matching DPAD key code so it can be handled like any other button.
     */
    fun hatKey(hatX: Float, hatY: Float): Int? {
        if (abs(hatX) < 0.5f && abs(hatY) < 0.5f) return null
        return if (abs(hatX) >= abs(hatY)) {
            if (hatX > 0f) KeyEvent.KEYCODE_DPAD_RIGHT else KeyEvent.KEYCODE_DPAD_LEFT
        } else {
            if (hatY > 0f) KeyEvent.KEYCODE_DPAD_DOWN else KeyEvent.KEYCODE_DPAD_UP
        }
    }

    private val names = mapOf(
        KeyEvent.KEYCODE_BUTTON_A to "A",
        KeyEvent.KEYCODE_BUTTON_B to "B",
        KeyEvent.KEYCODE_BUTTON_X to "X",
        KeyEvent.KEYCODE_BUTTON_Y to "Y",
        KeyEvent.KEYCODE_BUTTON_L1 to "L1",
        KeyEvent.KEYCODE_BUTTON_R1 to "R1",
        KeyEvent.KEYCODE_BUTTON_L2 to "L2 (digital)",
        KeyEvent.KEYCODE_BUTTON_R2 to "R2 (digital)",
        KeyEvent.KEYCODE_BUTTON_THUMBL to "Left stick press (L3)",
        KeyEvent.KEYCODE_BUTTON_THUMBR to "Right stick press (R3)",
        KeyEvent.KEYCODE_BUTTON_START to "Start",
        KeyEvent.KEYCODE_BUTTON_SELECT to "Select",
        KeyEvent.KEYCODE_BUTTON_MODE to "Mode / Home",
        KeyEvent.KEYCODE_DPAD_UP to "D-pad Up",
        KeyEvent.KEYCODE_DPAD_DOWN to "D-pad Down",
        KeyEvent.KEYCODE_DPAD_LEFT to "D-pad Left",
        KeyEvent.KEYCODE_DPAD_RIGHT to "D-pad Right",
        KeyEvent.KEYCODE_DPAD_CENTER to "D-pad Center",
        KeyEvent.KEYCODE_VOLUME_UP to "Volume +",
        KeyEvent.KEYCODE_VOLUME_DOWN to "Volume -",
        KeyEvent.KEYCODE_BACK to "Back",
        KeyEvent.KEYCODE_MENU to "Menu",
        KeyEvent.KEYCODE_HOME to "Home"
    )

    fun label(keyCode: Int): String {
        val name = names[keyCode] ?: KeyEvent.keyCodeToString(keyCode)
            .removePrefix("KEYCODE_")
            .replace('_', ' ')
        return "$name (keyCode $keyCode)"
    }
}
