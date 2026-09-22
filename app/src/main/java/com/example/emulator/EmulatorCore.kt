package com.example.emulator

import android.graphics.Bitmap
import com.example.model.AnalogStickType
import com.example.model.ConsoleSystem
import com.example.model.ControllerButton

data class FrameInfo(
    val frameNumber: Long,
    val fps: Float,
    val executionTimeMs: Long,
    val isNewFrame: Boolean
)

data class CoreInitResult(
    val success: Boolean,
    val message: String,
    val technicalDetails: String = ""
)

data class CoreStatus(
    val isFullyImplemented: Boolean,
    val description: String,
    val technicalDetails: String,
    val biosRequired: Boolean = false,
    val biosName: String? = null
)

interface EmulatorCore {
    val system: ConsoleSystem
    val name: String
    val status: CoreStatus

    fun loadRom(romBytes: ByteArray, romName: String): CoreInitResult
    fun start()
    fun pause()
    fun resume()
    fun reset()
    fun stop()

    fun setSpeed(multiplier: Float)
    fun setInput(button: ControllerButton, isPressed: Boolean)
    fun setAnalog(stick: AnalogStickType, x: Float, y: Float)
    fun handleTouch(x: Float, y: Float, isDown: Boolean)

    fun renderFrame(targetBitmap: Bitmap, secondScreenBitmap: Bitmap? = null): FrameInfo
    fun saveState(slot: Int): ByteArray?
    fun loadState(slot: Int, data: ByteArray): Boolean
}
