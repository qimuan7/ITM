package lkg.itm.music

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.ReturnCode
import java.io.File
import java.io.FileOutputStream

class LoudnessAnalysisWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    private val repository = MusicRepository(context)

    override suspend fun doWork(): Result {
        val trackId = inputData.getString("trackId") ?: return Result.failure()
        val song = repository.getSong(trackId) ?: return Result.failure()

        val tempFile = File(applicationContext.cacheDir, "analysis_${System.currentTimeMillis()}.tmp")
        
        return try {
            // 1. 複製檔案到快取
            if (song.androidPath.startsWith("content://")) {
                applicationContext.contentResolver.openInputStream(Uri.parse(song.androidPath))?.use { input ->
                    FileOutputStream(tempFile).use { output -> input.copyTo(output) }
                } ?: return Result.failure()
            } else {
                val originalFile = File(song.androidPath)
                if (!originalFile.exists()) return Result.failure()
                originalFile.copyTo(tempFile, overwrite = true)
            }

            // 2. 執行 FFmpeg 分析 (使用 ebur128)
            val cmd = "-i \"${tempFile.absolutePath}\" -vn -sn -af ebur128 -f null -"
            val session = FFmpegKit.execute(cmd)
            val output = session.allLogsAsString
            Log.d("LoudnessScanner", "FFmpeg Output for ${song.title}: $output")

            if (ReturnCode.isSuccess(session.returnCode)) {
                val inputI = parseLoudness(output)
                val inputTp = parsePeak(output)

                if (inputI != null) {
                    repository.updateSongLoudness(trackId, inputI, inputTp ?: -1.0)
                    Log.d("LoudnessScanner", "分析成功: ${song.title}, LUFS: $inputI, Peak: $inputTp")
                    Result.success()
                } else {
                    Log.e("LoudnessScanner", "無法從輸出中解析響度")
                    Result.failure()
                }
            } else {
                Log.e("LoudnessScanner", "FFmpeg 執行失敗: ${session.failStackTrace}")
                Result.failure()
            }
        } catch (e: Exception) {
            Log.e("LoudnessScanner", "分析過程錯誤: ${e.message}")
            Result.failure()
        } finally {
            if (tempFile.exists()) tempFile.delete()
        }
    }

    private fun parseLoudness(output: String): Double? {
        // 匹配 Summary 中的 "I:         -17.9 LUFS"
        val regex = "I:\\s+(-?\\d+\\.?\\d+)\\s+LUFS".toRegex()
        return regex.findAll(output).lastOrNull()?.groupValues?.get(1)?.toDoubleOrNull()
    }

    private fun parsePeak(output: String): Double? {
        val regex = "True peak:\\s+(-?\\d+\\.?\\d+)".toRegex()
        return regex.find(output)?.groupValues?.get(1)?.toDoubleOrNull()
    }
}
