package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class EmulatorPreferences(
    val defaultOrientation: String = "Auto", // Auto, Portrait, Landscape
    val fastForwardSpeed: Float = 2.0f,
    val frameSkip: Int = 0,
    val showFps: Boolean = true,
    val integerScaling: Boolean = false,
    val bilinearFilter: Boolean = true,
    val aspectRatioMode: String = "Original", // Original, 4:3, 16:9, Stretch
    val controllerOpacity: Float = 0.85f,
    val buttonScale: Float = 1.0f,
    val hapticFeedbackEnabled: Boolean = true,
    val confirmBeforeExit: Boolean = true,
    val darkThemeMode: String = "AMOLED Dark"
)

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("emuzen_prefs", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<EmulatorPreferences> = _settings.asStateFlow()

    private fun loadSettings(): EmulatorPreferences {
        return EmulatorPreferences(
            defaultOrientation = prefs.getString("default_orientation", "Auto") ?: "Auto",
            fastForwardSpeed = prefs.getFloat("fast_forward_speed", 2.0f),
            frameSkip = prefs.getInt("frame_skip", 0),
            showFps = prefs.getBoolean("show_fps", true),
            integerScaling = prefs.getBoolean("integer_scaling", false),
            bilinearFilter = prefs.getBoolean("bilinear_filter", true),
            aspectRatioMode = prefs.getString("aspect_ratio_mode", "Original") ?: "Original",
            controllerOpacity = prefs.getFloat("controller_opacity", 0.85f),
            buttonScale = prefs.getFloat("button_scale", 1.0f),
            hapticFeedbackEnabled = prefs.getBoolean("haptic_feedback", true),
            confirmBeforeExit = prefs.getBoolean("confirm_exit", true),
            darkThemeMode = prefs.getString("theme_mode", "AMOLED Dark") ?: "AMOLED Dark"
        )
    }

    fun updateSettings(newSettings: EmulatorPreferences) {
        prefs.edit().apply {
            putString("default_orientation", newSettings.defaultOrientation)
            putFloat("fast_forward_speed", newSettings.fastForwardSpeed)
            putInt("frame_skip", newSettings.frameSkip)
            putBoolean("show_fps", newSettings.showFps)
            putBoolean("integer_scaling", newSettings.integerScaling)
            putBoolean("bilinear_filter", newSettings.bilinearFilter)
            putString("aspect_ratio_mode", newSettings.aspectRatioMode)
            putFloat("controller_opacity", newSettings.controllerOpacity)
            putFloat("button_scale", newSettings.buttonScale)
            putBoolean("haptic_feedback", newSettings.hapticFeedbackEnabled)
            putBoolean("confirm_exit", newSettings.confirmBeforeExit)
            putString("theme_mode", newSettings.darkThemeMode)
            apply()
        }
        _settings.value = newSettings
    }
}
