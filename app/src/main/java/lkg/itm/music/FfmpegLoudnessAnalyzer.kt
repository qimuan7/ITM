package lkg.itm.music

import android.content.Context
import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class FfmpegLoudnessAnalyzer(private val context: Context) : LoudnessAnalyzer {

    override suspend fun analyze(file: File): Double? = analyzeWithPeak(file)?.first

    override suspend fun analyzeWithPeak(file: File): Pair<Double, Double?>? =
        withContext(Dispatchers.IO) {
            val cmd = "-i \"${file.absolutePath}\" -vn -sn -af ebur128 -f null -"
            try {
                val session = FFmpegKit.execute(cmd)
                val output = session.allLogsAsString
                Log.d("LoudnessScanner", "FFmpeg output: $output")

                if (ReturnCode.isSuccess(session.returnCode)) {
                    val i = parseLoudness(output) ?: return@withContext null
                    val tp = parsePeak(output)
                    i to tp
                } else {
                    Log.e("LoudnessScanner", "FFmpeg failed: ${session.failStackTrace}")
                    null
                }
            } catch (e: Exception) {
                Log.e("LoudnessScanner", "FFmpeg exception: ${e.message}")
                null
            }
        }

    private fun parseLoudness(output: String): Double? {
        val regex = "I:\\s+(-?\\d+\\.?\\d+)\\s+LUFS".toRegex()
        return regex.findAll(output).lastOrNull()?.groupValues?.get(1)?.toDoubleOrNull()
    }

    private fun parsePeak(output: String): Double? {
        val regex = "True peak:\\s+(-?\\d+\\.?\\d+)".toRegex()
        return regex.find(output)?.groupValues?.get(1)?.toDoubleOrNull()
    }
}