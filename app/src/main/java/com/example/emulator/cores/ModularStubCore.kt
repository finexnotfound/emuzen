package com.example.emulator.cores

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import com.example.emulator.CoreInitResult
import com.example.emulator.CoreStatus
import com.example.emulator.EmulatorCore
import com.example.emulator.FrameInfo
import com.example.model.AnalogStickType
import com.example.model.ConsoleSystem
import com.example.model.ControllerButton
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import kotlin.math.sin

class ModularStubCore(override val system: ConsoleSystem) : EmulatorCore {

    override val name: String = "EmuZen ${system.shortName} Modular Core"

    override val status: CoreStatus = CoreStatus(
        isFullyImplemented = false,
        description = "Modular Interface Ready (Dynamic JNI Core slot)",
        technicalDetails = getConsoleTechnicalSpecs(system),
        biosRequired = isBiosRecommended(system),
        biosName = getRecommendedBiosName(system)
    )

    private var isRunning = false
    private var isPaused = false
    private var speedMultiplier = 1.0f

    private var romTitle = "${system.fullName} ROM"
    private var romSizeBytes = 0L
    private var frameCount = 0L
    private var lastFrameTime = System.currentTimeMillis()
    private var currentFps = 60.0f

    private val pressedButtons = mutableSetOf<ControllerButton>()
    private var stickX = 0f
    private var stickY = 0f

    // Touch input (for NDS / 3DS bottom screen)
    private val touchPoints = mutableListOf<Pair<Float, Float>>()
    private var isTouching = false

    override fun loadRom(romBytes: ByteArray, romName: String): CoreInitResult {
        romSizeBytes = romBytes.size.toLong()
        romTitle = romName.substringBeforeLast('.')
        reset()

        val biosNote = if (status.biosRequired) "\nNote: ${status.biosName} recommended for full system features." else ""
        return CoreInitResult(
            success = true,
            message = "ROM '${romTitle}' attached to ${system.fullName} modular engine.$biosNote",
            technicalDetails = "Core Type: Modular Emulation Interface\nFile: $romName (${romBytes.size / 1024} KB)\nResolution: ${system.nativeWidth}x${system.nativeHeight}"
        )
    }

    override fun start() {
        isRunning = true
        isPaused = false
    }

    override fun pause() {
        isPaused = true
    }

    override fun resume() {
        isPaused = false
    }

    override fun reset() {
        frameCount = 0L
        touchPoints.clear()
        pressedButtons.clear()
        stickX = 0f
        stickY = 0f
    }

    override fun stop() {
        isRunning = false
    }

    override fun setSpeed(multiplier: Float) {
        speedMultiplier = multiplier
    }

    override fun setInput(button: ControllerButton, isPressed: Boolean) {
        if (isPressed) {
            pressedButtons.add(button)
        } else {
            pressedButtons.remove(button)
        }
    }

    override fun setAnalog(stick: AnalogStickType, x: Float, y: Float) {
        stickX = x
        stickY = y
    }

    override fun handleTouch(x: Float, y: Float, isDown: Boolean) {
        isTouching = isDown
        if (isDown) {
            touchPoints.add(x to y)
            if (touchPoints.size > 200) {
                touchPoints.removeAt(0)
            }
        }
    }

    override fun renderFrame(targetBitmap: Bitmap, secondScreenBitmap: Bitmap?): FrameInfo {
        val now = System.currentTimeMillis()
        val dt = (now - lastFrameTime).coerceAtLeast(1)
        lastFrameTime = now
        currentFps = (1000f / dt) * speedMultiplier
        frameCount++

        val w = targetBitmap.width
        val h = targetBitmap.height

        // 1. Render Main Screen
        renderMainScreen(targetBitmap, w, h)

        // 2. Render Second Screen (for NDS / 3DS touch screen)
        if (secondScreenBitmap != null && (system.hasDualScreen || system.hasTouchScreen)) {
            renderTouchScreen(secondScreenBitmap, secondScreenBitmap.width, secondScreenBitmap.height)
        }

        return FrameInfo(
            frameNumber = frameCount,
            fps = currentFps,
            executionTimeMs = dt,
            isNewFrame = true
        )
    }

    private fun renderMainScreen(bitmap: Bitmap, w: Int, h: Int) {
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Sleek dark retro background
        canvas.drawColor(Color.rgb(18, 20, 26))

        // Grid lines
        paint.color = Color.rgb(28, 32, 42)
        paint.strokeWidth = 1f
        val step = 24
        for (x in 0 until w step step) {
            canvas.drawLine(x.toFloat(), 0f, x.toFloat(), h.toFloat(), paint)
        }
        for (y in 0 until h step step) {
            canvas.drawLine(0f, y.toFloat(), w.toFloat(), y.toFloat(), paint)
        }

        // Animated sine wave waveform (emulating audio/video signal)
        paint.color = Color.rgb(99, 102, 241)
        paint.strokeWidth = 2f
        paint.style = Paint.Style.STROKE
        val wavePath = Path()
        val waveY = h * 0.45f
        for (x in 0 until w step 4) {
            val offset = sin((x * 0.04f) + (frameCount * 0.1f * speedMultiplier)) * 14f
            if (x == 0) wavePath.moveTo(x.toFloat(), waveY + offset)
            else wavePath.lineTo(x.toFloat(), waveY + offset)
        }
        canvas.drawPath(wavePath, paint)

        // Text Info & Status
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        paint.textSize = (w * 0.055f).coerceIn(10f, 18f)
        paint.isFakeBoldText = true
        canvas.drawText(romTitle.take(18), 12f, 24f, paint)

        paint.color = Color.rgb(148, 163, 184)
        paint.textSize = (w * 0.04f).coerceIn(8f, 13f)
        paint.isFakeBoldText = false
        canvas.drawText("${system.fullName} • ${system.nativeWidth}x${system.nativeHeight}", 12f, 42f, paint)

        // Input visualizer
        val activeButtonsStr = if (pressedButtons.isEmpty()) "IDLE" else pressedButtons.joinToString(" ") { it.label }
        paint.color = Color.rgb(56, 189, 248)
        canvas.drawText("Input: $activeButtonsStr", 12f, h - 32f, paint)

        if (stickX != 0f || stickY != 0f) {
            paint.color = Color.rgb(245, 158, 11)
            canvas.drawText("Stick: (${String.format("%.2f", stickX)}, ${String.format("%.2f", stickY)})", 12f, h - 16f, paint)
        } else {
            paint.color = Color.rgb(100, 116, 139)
            canvas.drawText("Modular Core Active • Frame $frameCount", 12f, h - 16f, paint)
        }
    }

    private fun renderTouchScreen(bitmap: Bitmap, w: Int, h: Int) {
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Soft dark touch screen background
        canvas.drawColor(Color.rgb(22, 25, 34))

        // Center touch prompt
        paint.color = Color.rgb(71, 85, 105)
        paint.textSize = 12f
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("TOUCH SCREEN / STYLUS INPUT", w / 2f, h / 2f - 10f, paint)
        paint.textSize = 9f
        canvas.drawText("Tap or drag to interact", w / 2f, h / 2f + 8f, paint)

        // Draw touch strokes / points
        paint.color = Color.rgb(56, 189, 248)
        paint.strokeWidth = 3f
        paint.style = Paint.Style.STROKE

        for (i in 0 until touchPoints.size - 1) {
            val p1 = touchPoints[i]
            val p2 = touchPoints[i + 1]
            canvas.drawLine(p1.first * w, p1.second * h, p2.first * w, p2.second * h, paint)
        }

        // Draw current cursor / stylus position if active
        touchPoints.lastOrNull()?.let { last ->
            paint.style = Paint.Style.FILL
            paint.color = Color.rgb(244, 63, 94)
            canvas.drawCircle(last.first * w, last.second * h, 6f, paint)
        }
    }

    override fun saveState(slot: Int): ByteArray? {
        val json = JSONObject().apply {
            put("slot", slot)
            put("frameCount", frameCount)
            put("romTitle", romTitle)
            put("timestamp", System.currentTimeMillis())
            put("system", system.id)
        }
        return json.toString().toByteArray(StandardCharsets.UTF_8)
    }

    override fun loadState(slot: Int, data: ByteArray): Boolean {
        return try {
            val str = String(data, StandardCharsets.UTF_8)
            val json = JSONObject(str)
            frameCount = json.optLong("frameCount", 0L)
            true
        } catch (_: Exception) {
            false
        }
    }

    companion object {
        private fun getConsoleTechnicalSpecs(system: ConsoleSystem): String {
            return when (system) {
                ConsoleSystem.NES -> "CPU: Ricoh 2A03 (8-bit @ 1.79 MHz)\nPPU: 256x240 @ 60.0985 Hz\nAudio: 5-channel APU (2 pulse, 1 triangle, 1 noise, 1 DPCM)"
                ConsoleSystem.SNES -> "CPU: Ricoh 5A22 (16-bit 65C816 @ 3.58 MHz)\nPPU: 256x224 (Mode 7 affine matrix)\nAudio: Sony SPC700 8-channel 16-bit DSP"
                ConsoleSystem.N64 -> "CPU: MIPS VR4300 (64-bit @ 93.75 MHz)\nReality Coprocessor (RCP) RSP & RDP\nUnified RDRAM: 4MB / 8MB with Expansion Pak"
                ConsoleSystem.NDS -> "Dual Core: ARM946E-S @ 67 MHz & ARM7TDMI @ 33 MHz\nScreens: Dual 256x192 LCD with resistive touch\nMemory: 4MB Main RAM + VRAM"
                ConsoleSystem.NINTENDO_3DS -> "CPU: Dual-Core ARM11 MPCore @ 268 MHz\nGPU: DMP PICA200\nTop: 800x240 Autostereoscopic, Bottom: 320x240 Touch"
                ConsoleSystem.PS1 -> "CPU: MIPS R3000A (32-bit @ 33.8688 MHz)\nGPU: 1MB VRAM (320x240 to 640x480)\nSPU: 24-channel ADPCM Audio"
                ConsoleSystem.PSP -> "CPU: MIPS R4000 (Allegrex @ 333 MHz)\nGPU: 2MB eDRAM @ 166 MHz\nDisplay: 4.3\" 16:9 LCD (480x272)"
                ConsoleSystem.ATARI -> "CPU: MOS Technology 6507 @ 1.19 MHz\nTIA (Television Interface Adaptor)\nRAM: 128 bytes (inside RIOT chip)"
                ConsoleSystem.GAME_GEAR -> "CPU: Zilog Z80 @ 3.58 MHz\nVDP: 160x144 (Palette: 4096 colors, 32 on screen)\nAudio: Texas Instruments SN76489 PSG"
                ConsoleSystem.GAME_BOY_COLOR -> "CPU: Sharp LR35902 (Dual Speed 8.388 MHz)\nPPU: 160x144, 56 simultaneous colors\nMemory: 32KB Work RAM + 16KB VRAM"
                ConsoleSystem.GAME_BOY_ADVANCE -> "CPU: ARM7TDMI (32-bit RISC @ 16.78 MHz)\nDisplay: 240x160 15-bit color LCD\nMemory: 32KB internal + 256KB external WRAM"
                else -> "Console Architecture: ${system.fullName}\nNative: ${system.nativeWidth}x${system.nativeHeight}"
            }
        }

        private fun isBiosRecommended(system: ConsoleSystem): Boolean {
            return when (system) {
                ConsoleSystem.PS1, ConsoleSystem.NDS, ConsoleSystem.NINTENDO_3DS, ConsoleSystem.GAME_BOY_ADVANCE -> true
                else -> false
            }
        }

        private fun getRecommendedBiosName(system: ConsoleSystem): String? {
            return when (system) {
                ConsoleSystem.PS1 -> "scph1001.bin (PS1 BIOS)"
                ConsoleSystem.NDS -> "bios7.bin / bios9.bin (DS ARM BIOS)"
                ConsoleSystem.NINTENDO_3DS -> "boot9.bin / aes_keys.txt"
                ConsoleSystem.GAME_BOY_ADVANCE -> "gba_bios.bin"
                else -> null
            }
        }
    }
}
