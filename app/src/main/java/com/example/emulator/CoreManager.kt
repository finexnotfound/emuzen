package com.example.emulator

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.emulator.cores.GameBoyCore
import com.example.emulator.cores.ModularStubCore
import com.example.model.AnalogStickType
import com.example.model.ConsoleSystem
import com.example.model.ControllerButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

class CoreManager(private val context: Context) {

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        manager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private var activeCore: EmulatorCore? = null
    private var renderJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    var primaryBitmap: Bitmap? = null
        private set
    var secondaryBitmap: Bitmap? = null
        private set

    private val _currentFps = MutableStateFlow(60f)
    val currentFps: StateFlow<Float> = _currentFps.asStateFlow()

    private val _isFastForwarding = MutableStateFlow(false)
    val isFastForwarding: StateFlow<Boolean> = _isFastForwarding.asStateFlow()

    private val _speedMultiplier = MutableStateFlow(1.0f)
    val speedMultiplier: StateFlow<Float> = _speedMultiplier.asStateFlow()

    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()

    private val _frameCounter = MutableStateFlow(0L)
    val frameCounter: StateFlow<Long> = _frameCounter.asStateFlow()

    fun getActiveCore(): EmulatorCore? = activeCore

    fun loadAndStart(system: ConsoleSystem, romFile: File, speed: Float = 1.0f): CoreInitResult {
        stop()

        val core: EmulatorCore = when (system) {
            ConsoleSystem.GAME_BOY -> GameBoyCore()
            else -> ModularStubCore(system)
        }

        activeCore = core

        val romBytes = try {
            romFile.readBytes()
        } catch (e: Exception) {
            return CoreInitResult(false, "Could not read ROM: ${e.message}", e.stackTraceToString())
        }

        val initResult = core.loadRom(romBytes, romFile.name)
        if (!initResult.success) {
            return initResult
        }

        // Initialize bitmaps with native dimensions
        primaryBitmap = Bitmap.createBitmap(system.nativeWidth, system.nativeHeight, Bitmap.Config.ARGB_8888)
        if (system.hasDualScreen || system.hasTouchScreen) {
            secondaryBitmap = Bitmap.createBitmap(system.nativeWidth, system.nativeHeight, Bitmap.Config.ARGB_8888)
        } else {
            secondaryBitmap = null
        }

        core.setSpeed(speed)
        _speedMultiplier.value = speed
        core.start()
        _isPaused.value = false

        startRenderLoop()

        return initResult
    }

    private fun startRenderLoop() {
        renderJob?.cancel()
        renderJob = scope.launch {
            var lastTime = System.nanoTime()
            while (isActive) {
                val core = activeCore ?: break
                val bmp1 = primaryBitmap ?: break
                val bmp2 = secondaryBitmap

                if (!_isPaused.value) {
                    val frameInfo = core.renderFrame(bmp1, bmp2)
                    _currentFps.value = frameInfo.fps
                    _frameCounter.value = frameInfo.frameNumber
                }

                // Timing calculation for ~60 FPS modified by speed multiplier
                val speed = _speedMultiplier.value.coerceAtLeast(0.5f)
                val targetFrameNs = (1_000_000_000L / (60.0 * speed)).toLong()
                val now = System.nanoTime()
                val elapsed = now - lastTime
                val sleepNs = targetFrameNs - elapsed
                if (sleepNs > 2_000_000L) {
                    delay(sleepNs / 1_000_000L)
                } else {
                    delay(1)
                }
                lastTime = System.nanoTime()
            }
        }
    }

    fun setSpeed(multiplier: Float) {
        _speedMultiplier.value = multiplier
        activeCore?.setSpeed(multiplier)
        _isFastForwarding.value = multiplier > 1.0f
    }

    fun toggleFastForward(defaultMultiplier: Float = 2.0f) {
        if (_isFastForwarding.value) {
            setSpeed(1.0f)
        } else {
            setSpeed(defaultMultiplier)
        }
    }

    fun pause() {
        _isPaused.value = true
        activeCore?.pause()
    }

    fun resume() {
        _isPaused.value = false
        activeCore?.resume()
    }

    fun reset() {
        activeCore?.reset()
    }

    fun stop() {
        renderJob?.cancel()
        renderJob = null
        activeCore?.stop()
        activeCore = null
        primaryBitmap = null
        secondaryBitmap = null
        _isPaused.value = false
        _isFastForwarding.value = false
    }

    fun onButtonDown(button: ControllerButton, hapticsEnabled: Boolean = true) {
        activeCore?.setInput(button, true)
        if (hapticsEnabled) {
            triggerSubtleHaptic()
        }
    }

    fun onButtonUp(button: ControllerButton) {
        activeCore?.setInput(button, false)
    }

    fun onAnalogStick(stick: AnalogStickType, x: Float, y: Float) {
        activeCore?.setAnalog(stick, x, y)
    }

    fun onTouchScreen(x: Float, y: Float, isDown: Boolean) {
        activeCore?.handleTouch(x, y, isDown)
    }

    fun saveState(slot: Int): ByteArray? {
        return activeCore?.saveState(slot)
    }

    fun loadState(slot: Int, data: ByteArray): Boolean {
        return activeCore?.loadState(slot, data) ?: false
    }

    private fun triggerSubtleHaptic() {
        try {
            if (vibrator?.hasVibrator() == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(15)
                }
            }
        } catch (_: Exception) {}
    }
}
