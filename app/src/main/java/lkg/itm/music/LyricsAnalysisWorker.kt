package lkg.itm.music

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import android.util.Log

class LyricsAnalysisWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    private val repository = MusicRepository(context)

    override suspend fun doWork(): Result {
        val isBatch = inputData.getBoolean("isBatch", false)
        val songsToScan = if (isBatch) {
            repository.getCachedSongs().filter { it.lyrics.isNullOrBlank() }
        } else {
            val trackId = inputData.getString("trackId") ?: return Result.failure()
            listOfNotNull(repository.getSong(trackId)).filter { it.lyrics.isNullOrBlank() }
        }

        Log.d("LyricsAnalysisWorker", "準備掃描 ${songsToScan.size} 首歌曲")

        for (song in songsToScan) {
            val lyrics = LyricsExtractor.getLyrics(applicationContext, song.androidPath)
            if (!lyrics.isNullOrBlank()) {
                repository.updateSongLyrics(song.trackId, lyrics)
                Log.d("LyricsAnalysisWorker", "成功寫入: ${song.title}")
            } else {
                Log.d("LyricsAnalysisWorker", "未找到歌詞: ${song.title}")
            }
        }
        return Result.success()
    }
}
