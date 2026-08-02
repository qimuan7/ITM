package lkg.itm.music

import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.audio.BaseAudioProcessor
import androidx.media3.common.audio.AudioProcessor.AudioFormat
import androidx.media3.common.util.UnstableApi
import java.nio.ByteBuffer
import java.nio.ByteOrder

@OptIn(UnstableApi::class)
class ReplayGainAudioProcessor : BaseAudioProcessor() {
    private var gainFactor = 1.0f

    fun setGainFactor(factor: Float) {
        // 限制增益在合理範圍內 (0.01x ~ 5.0x)
        val safeFactor = factor.coerceIn(0.01f, 5.0f)
        if (this.gainFactor != safeFactor) {
            this.gainFactor = safeFactor
            Log.d("ReplayGainProcessor", "增益因子已更新為: $gainFactor")
        }
    }

    override fun queueInput(inputBuffer: ByteBuffer) {
        val remaining = inputBuffer.remaining()
        if (remaining == 0) return

        // 必須獲取輸出 Buffer
        val outputBuffer = replaceOutputBuffer(remaining)

        if (gainFactor == 1.0f) {
            // 如果不需要增益，直接透傳數據，避免靜音
            outputBuffer.put(inputBuffer)
        } else {
            // 進行 PCM 16bit 增益處理
            inputBuffer.order(ByteOrder.LITTLE_ENDIAN)
            outputBuffer.order(ByteOrder.LITTLE_ENDIAN)

            while (inputBuffer.hasRemaining()) {
                val sample = inputBuffer.getShort()
                val amplified = (sample * gainFactor).toInt()
                val processed = amplified.coerceIn(
                    Short.MIN_VALUE.toInt(),
                    Short.MAX_VALUE.toInt()
                )
                outputBuffer.putShort(processed.toShort())
            }
        }
        // 必須調用 flip() 讓 Media3 讀取數據
        outputBuffer.flip()
    }

    override fun onConfigure(inputAudioFormat: AudioFormat): AudioFormat {
        return inputAudioFormat
    }

    // 保持始終開啟，以避免動態切換導致的播放中斷或 Codec 錯誤
    override fun isActive(): Boolean = true
}
