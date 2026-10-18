package lkg.itm.music

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

class LoudnessAnalysisWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val repository = MusicRepository(context)

    override suspend fun doWork(): Result {
        val engine = inputData.getString("engine") ?: return Result.failure()
        val trackIds = inputData.getStringArray("trackIds")?.toList() ?: return Result.failure()
        val concurrency = inputData.getInt("concurrency", 3).coerceIn(1, 20)
        val total = trackIds.size
        if (total == 0) return Result.success()

        val analyzer: LoudnessAnalyzer = when (engine) {
            "ffmpeg" -> FfmpegLoudnessAnalyzer(applicationContext)
            else -> return Result.failure()
        }

        // 併發數, 對低端設備友好, 同時保留大部分並行速度
        val semaphore = Semaphore(concurrency)

        val completedCount = AtomicInteger(0)
        val successCount = AtomicInteger(0)
        val failCount = AtomicInteger(0)
        val lastTitle = AtomicReference<String>("準備中…")

        setProgress(workDataOf(
            "current" to 0,
            "total" to total,
            "success" to 0,
            "failed" to 0,
            "title" to lastTitle.get()
        ))

        coroutineScope {
            trackIds.map { trackId ->
                async(Dispatchers.IO) {
                    semaphore.withPermit {
                        if (isStopped) return@withPermit
                        processOne(trackId, engine, analyzer, successCount, failCount, lastTitle)
                        val done = completedCount.incrementAndGet()
                        setProgress(workDataOf(
                            "current" to done,
                            "total" to total,
                            "success" to successCount.get(),
                            "failed" to failCount.get(),
                            "title" to lastTitle.get()
                        ))
                    }
                }
            }.awaitAll()
        }

        Log.d("LoudnessScanner", "掃描完成: 成功 ${successCount.get()}, 失敗 ${failCount.get()} / 共 $total")
        return Result.success(workDataOf(
            "success" to successCount.get(),
            "failed" to failCount.get(),
            "total" to total
        ))
    }

    private suspend fun processOne(
        trackId: String,
        engine: String,
        analyzer: LoudnessAnalyzer,
        successCount: AtomicInteger,
        failCount: AtomicInteger,
        lastTitle: AtomicReference<String>
    ) {
        val song = repository.getSong(trackId)
        if (song == null) {
            failCount.incrementAndGet()
            return
        }
        lastTitle.set(song.title)

        val localFile = resolveToFile(song.androidPath)
        if (localFile == null) {
            Log.w("LoudnessScanner", "無法解析檔案: ${song.androidPath}")
            failCount.incrementAndGet()
            return
        }

        val tempCreated = localFile.absolutePath.startsWith(applicationContext.cacheDir.absolutePath)

        try {

            val result = analyzer.analyzeWithPeak(localFile)
            if (result != null) {
                repository.updateFfmpegLoudness(trackId, result.first, result.second ?: -1.0)
                successCount.incrementAndGet()
            } else {
                failCount.incrementAndGet()
            }

        } catch (e: Exception) {
            Log.e("LoudnessScanner", "分析異常: ${song.title}, ${e.message}")
            failCount.incrementAndGet()
        } finally {
            if (tempCreated && localFile.exists()) localFile.delete()
        }
    }

    private fun resolveToFile(path: String): File? {
        return try {
            if (path.startsWith("content://")) {
                val tmp = File(applicationContext.cacheDir, "ld_${UUID.randomUUID()}.tmp")
                applicationContext.contentResolver.openInputStream(Uri.parse(path))?.use { input ->
                    FileOutputStream(tmp).use { output -> input.copyTo(output) }
                } ?: return null
                tmp
            } else {
                File(path).takeIf { it.exists() }
            }
        } catch (e: Exception) {
            Log.e("LoudnessScanner", "resolveToFile 失敗: $path, ${e.message}")
            null
        }
    }
}