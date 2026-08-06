package lkg.itm.music

import android.content.Context
import android.util.Log
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File
import java.io.InputStream

object LyricsExtractor {
    private const val TAG = "LyricsExtractor"

    /**
     * 從指定 URI 或路徑提取歌詞
     */
    fun getLyrics(context: Context, androidPath: String): String? {
        return try {
            // 對於 SAF URI，可能無法直接用 File(path)
            // 如果 androidPath 是一個內容 URI，我們需要將其複製到緩存文件
            val file = if (androidPath.startsWith("content://")) {
                val cacheFile = File(context.cacheDir, "temp_song.mp3")
                context.contentResolver.openInputStream(android.net.Uri.parse(androidPath))?.use { input ->
                    cacheFile.outputStream().use { output -> input.copyTo(output) }
                }
                cacheFile
            } else {
                File(androidPath)
            }

            if (!file.exists()) return null

            val audioFile = AudioFileIO.read(file)
            val tag = audioFile.tag ?: return null

            val lyrics = tag.getFirst(FieldKey.LYRICS).takeIf { it.isNotBlank() }
                ?: tag.getFirst("UNSYNCEDLYRICS").takeIf { it.isNotBlank() }

            lyrics
        } catch (e: Exception) {
            Log.e(TAG, "解析歌詞失敗: $androidPath", e)
            null
        }
    }
}
