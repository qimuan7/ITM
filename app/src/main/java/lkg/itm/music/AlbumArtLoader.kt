package lkg.itm.music

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import android.util.LruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AlbumArtLoader {
    private const val TAG = "AlbumArtLoaderDebug"

    // 建立一個記憶體快取，最多快取 50 張圖片
    private val memoryCache = LruCache<String, Bitmap>(50)

    suspend fun getAlbumArtBitmap(
        context: Context,
        androidPath: String,
        reqWidth: Int = 150,
        reqHeight: Int = 150
    ): Bitmap? {

        if (androidPath.isEmpty()) return null

        val cacheKey = "${androidPath}_${reqWidth}x${reqHeight}"
        memoryCache.get(cacheKey)?.let { return it }

        return withContext(Dispatchers.IO) {
            try {
                val fileUri = Uri.parse(androidPath)
                val pfd = context.contentResolver.openFileDescriptor(fileUri, "r") ?: return@withContext null

                val retriever = MediaMetadataRetriever()
                val artBytes = try {
                    retriever.setDataSource(pfd.fileDescriptor)
                    retriever.embeddedPicture
                } finally {
                    runCatching { retriever.release() }
                    runCatching { pfd.close() }
                }

                if (artBytes != null) {
                    val options = BitmapFactory.Options().apply {
                        inJustDecodeBounds = true
                        BitmapFactory.decodeByteArray(artBytes, 0, artBytes.size, this)
                        inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)
                        inJustDecodeBounds = false
                    }

                    val bitmap = BitmapFactory.decodeByteArray(artBytes, 0, artBytes.size, options)
                    if (bitmap != null) {
                        memoryCache.put(cacheKey, bitmap)
                    }
                    bitmap
                } else null
            } catch (e: Exception) {
                Log.e(TAG, "❌ 讀取封面發生例外錯誤", e)
                null
            }
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val (height: Int, width: Int) = options.outHeight to options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}