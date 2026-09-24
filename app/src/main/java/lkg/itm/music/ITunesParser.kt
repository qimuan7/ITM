package lkg.itm.music

import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import com.dd.plist.NSDictionary
import com.dd.plist.NSArray
import com.dd.plist.PropertyListParser
import java.io.File
import java.io.InputStream
import java.net.URLDecoder

object ITunesParser {

    private const val TAG = "ITunesParserDebug"

    fun resolvePathFromiTunesAnchor(xmlLocation: String, androidStorageRoot: File): String? {
        val decodedUrl = try {
            URLDecoder.decode(xmlLocation, "UTF-8")
        } catch (_: Exception) {
            xmlLocation
        }

        val anchor = "/iTunes/"
        val anchorIndex = decodedUrl.indexOf(anchor, ignoreCase = true)

        if (anchorIndex != -1) {
            val relativePath = decodedUrl.substring(anchorIndex + 1)
            val targetFile = File(androidStorageRoot, relativePath)
            return targetFile.absolutePath
        }
        return null
    }

    fun parseITunesLibrary(
        inputStream: InputStream,
        androidStorageRoot: File
    ): Pair<List<Song>, List<Playlist>> {
        val tracksMap = mutableMapOf<String, Song>()
        val playlists = mutableListOf<Playlist>()

        try {
            // 1. 用 dd-plist 解析整份 XML
            val rootDict = PropertyListParser.parse(inputStream) as NSDictionary

            // 2. 解析 Tracks (使用 .objectForKey 和 .allKeys())
            val tracksDict = rootDict.objectForKey("Tracks") as? NSDictionary
            if (tracksDict != null) {
                Log.d(TAG, "=> 開始解析 Tracks，共 ${tracksDict.count()} 首")
                for (trackIdKey in tracksDict.allKeys()) {
                    val trackDict = tracksDict.objectForKey(trackIdKey) as? NSDictionary ?: continue
                    val trackId = trackDict.objectForKey("Track ID")?.toString() ?: trackIdKey
                    val title = trackDict.objectForKey("Name")?.toString() ?: "未知歌曲"
                    val artist = trackDict.objectForKey("Artist")?.toString() ?: "未知歌手"
                    val album = trackDict.objectForKey("Album")?.toString() ?: "未知專輯"
                    val location = trackDict.objectForKey("Location")?.toString()

                    val androidPath = location?.let {
                        resolvePathFromiTunesAnchor(it, androidStorageRoot)
                    } ?: ""

                    // 嘗試從 iTunes XML 獲取預計的音量調整 (Normalization)
                    // iTunes 的 Normalization 是一個整數值，代表音量偏移
                    // 目前優先依賴 LoudnessAnalysisWorker 進行精確掃描

                    tracksMap[trackId] = Song(
                        trackId = trackId,
                        title = title,
                        artist = artist,
                        album = album,
                        androidPath = androidPath,
                        loudness = null, // 初始化為空，等待掃描或資料庫查詢
                        peak = null
                    )
                }
                Log.d(TAG, "=> Tracks 解析完畢，成功讀取 ${tracksMap.size} 首")
            }

            // 3. 解析 Playlists (使用 .objectForKey)
            val playlistsArray = rootDict.objectForKey("Playlists") as? NSArray
            if (playlistsArray != null) {
                Log.d(TAG, "=> 開始解析 Playlists，共 ${playlistsArray.count()} 個")
                for (i in 0 until playlistsArray.count()) {
                    val playlistDict = playlistsArray.objectAtIndex(i) as? NSDictionary ?: continue
                    val name = playlistDict.objectForKey("Name")?.toString() ?: "未命名播放列表"

                    // 過濾掉主資料庫總表
                    if (name == "資料庫" || name == "Library" || name == "Music") continue

                    // 檢查是否為資料夾本身 (Master 或 Folder)
                    val isMaster = playlistDict.objectForKey("Master")?.toString()?.toBoolean() ?: false
                    val isFolder = playlistDict.objectForKey("Folder")?.toString()?.toBoolean() ?: false
                    if (isMaster || isFolder) continue

                    // 檢查是否為智慧播放清單或系統預設清單
                    val hasSmartInfo = playlistDict.objectForKey("Smart Info") != null
                    val distinguishedKind = playlistDict.objectForKey("Distinguished Kind")?.toString()

                    // 判定規則：有 Smart Info、有指定 Kind，或名稱符合常見系統內建清單
                    val isSysDefault = hasSmartInfo || distinguishedKind != null || name in listOf("已購商品", "頂級 25 首", "最近播放")

                    val playlistSongs = mutableListOf<Song>()
                    val itemsArray = playlistDict.objectForKey("Playlist Items") as? NSArray
                    if (itemsArray != null) {
                        for (j in 0 until itemsArray.count()) {
                            val itemDict = itemsArray.objectAtIndex(j) as? NSDictionary ?: continue
                            val trackId = itemDict.objectForKey("Track ID")?.toString() ?: continue
                            tracksMap[trackId]?.let { playlistSongs.add(it) }
                        }
                    }

                    playlists.add(Playlist(name = name, songs = playlistSongs, isSysDefault = isSysDefault))
                    Log.d(TAG, "成功讀取播放列表 [$name]，包含 ${playlistSongs.size} 首歌曲 (系統預設: $isSysDefault)")
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "解析 iTunes 庫失敗", e)
        }

        return Pair(tracksMap.values.toList(), playlists)
    }

    fun parseITunesLibraryWithUri(
        inputStream: InputStream,
        documentFileRoot: DocumentFile
    ): Pair<List<Song>, List<Playlist>> {
        val tempRoot = File("")
        val (songs, playlists) = parseITunesLibrary(inputStream, tempRoot)

        Log.d(TAG, "=> 開始建立檔案路徑快取 (Map)...")
        val uriCache = mutableMapOf<String, Uri>()
        // 一次性把整個文件樹的檔案掃描進記憶體（極速！）
        cacheDocumentFiles(documentFileRoot, "", uriCache)
        Log.d(TAG, "=> 檔案路徑快取建立完畢，共快取 ${uriCache.size} 個檔案")

        // 將 songs 裡面的 androidPath 快速對應到快取中的 content:// Uri
        val updatedSongs = songs.map { song ->
            val relativePath = extractRelativePath(song.androidPath)
                .replace("\\", "/") // 統一斜線方向
                .lowercase()        // 轉小寫以防大小寫不匹配

            // 從快取中直接取得 Uri，省去繁重的 findFile 查詢
            val contentUri = uriCache[relativePath]

            song.copy(androidPath = contentUri?.toString() ?: "")
        }

        val updatedSongsMap = updatedSongs.associateBy { it.trackId }
        val updatedPlaylists = playlists.map { playlist ->
            playlist.copy(songs = playlist.songs.mapNotNull { updatedSongsMap[it.trackId] })
        }

        return Pair(updatedSongs, updatedPlaylists)
    }

    /**
     * 一次性遞迴掃描整個 DocumentFile 樹，建立相對路徑對應表
     */
    private fun cacheDocumentFiles(dir: DocumentFile, currentPath: String, cache: MutableMap<String, Uri>) {
        val files = dir.listFiles()
        for (file in files) {
            val name = file.name ?: continue
            val relativePath = if (currentPath.isEmpty()) name else "$currentPath/$name"

            if (file.isDirectory) {
                cacheDocumentFiles(file, relativePath, cache)
            } else if (file.isFile) {
                // 以小寫的相對路徑作為 Key 存入 Map
                cache[relativePath.lowercase()] = file.uri
            }
        }
    }

    private fun extractRelativePath(xmlLocation: String): String {
        val decoded = try { URLDecoder.decode(xmlLocation, "UTF-8") } catch (_: Exception) { xmlLocation }
        val anchor = "/iTunes/"
        val index = decoded.indexOf(anchor, ignoreCase = true)
        return if (index != -1) {
            decoded.substring(index + anchor.length)
        } else {
            // 如果找不到 /iTunes/ 錨點，嘗試直接抓最後幾層路徑
            val lastMusicIndex = decoded.indexOf("Music/", ignoreCase = true)
            if (lastMusicIndex != -1) {
                decoded.substring(lastMusicIndex)
            } else {
                decoded.substringAfterLast("/")
            }
        }
    }
}