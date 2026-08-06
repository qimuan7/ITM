package lkg.itm.music

import android.content.Context
import androidx.room.*

// 1. 歌曲資料表
@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey val trackId: String,
    val title: String,
    val artist: String,
    val album: String,
    val androidPath: String,
    val loudness: Double? = null,
    val peak: Double? = null,
    val lyrics: String? = null
) {
    fun toSong() = Song(
        trackId = trackId,
        title = title,
        artist = artist,
        album = album,
        androidPath = androidPath,
        loudness = loudness,
        peak = peak,
        lyrics = lyrics
    )
}

fun Song.toEntity() = SongEntity(
    trackId = trackId,
    title = title,
    artist = artist,
    album = album,
    androidPath = androidPath,
    loudness = loudness,
    peak = peak,
    lyrics = lyrics
)

// 2. 播放清單資料表
@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey val name: String,
    val isSysDefault: Boolean = false
) {
    fun toPlaylist(songs: List<Song>) = Playlist(
        name = name,
        songs = songs,
        isSysDefault = isSysDefault
    )
}

fun Playlist.toEntity() = PlaylistEntity(
    name = name,
    isSysDefault = isSysDefault
)

// 3. 播放清單與歌曲的多對多關聯
@Entity(
    tableName = "playlist_song_cross_ref",
    primaryKeys = ["playlistName", "trackId"]
)
data class PlaylistSongCrossRef(
    val playlistName: String,
    val trackId: String
)

// 4. 播放次數統計資料表
@Entity(tableName = "song_play_stats")
data class SongPlayStatEntity(
    @PrimaryKey val trackId: String,
    val playCount: Int = 1,
    val lastPlayedTime: Long = System.currentTimeMillis()
)

// 5. 播放次數 DAO
@Dao
interface SongPlayStatDao {
    @Query("SELECT * FROM song_play_stats ORDER BY playCount DESC")
    suspend fun getAllStats(): List<SongPlayStatEntity>

    @Upsert
    suspend fun upsertPlayStat(stat: SongPlayStatEntity)

    @Query("SELECT * FROM song_play_stats WHERE trackId = :trackId")
    suspend fun getStatById(trackId: String): SongPlayStatEntity?
}

// 6. 資料庫管理者 / Repository
class MusicRepository(context: Context) {
    private val db = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "itm_music_database"
    )
        .fallbackToDestructiveMigration(true) // 👈 加上 true 參數
        .build()


    private val musicDao = db.musicDao()
    private val playStatDao = db.songPlayStatDao()

    suspend fun getCachedSongs(): List<Song> {
        return musicDao.getAllSongs().map { it.toSong() }
    }

    suspend fun getCachedPlaylists(allSongs: List<Song>): List<Playlist> {
        val playlistEntities = musicDao.getAllPlaylists()
        val crossRefs = musicDao.getAllCrossRefs()

        val songMap = allSongs.associateBy { it.trackId }

        return playlistEntities.map { pEntity ->
            val songIds = crossRefs.filter { it.playlistName == pEntity.name }.map { it.trackId }
            val playlistSongs = songIds.mapNotNull { songMap[it] }
            pEntity.toPlaylist(playlistSongs)
        }
    }

    suspend fun saveLibrary(songs: List<Song>, playlists: List<Playlist>) {
        // 先獲取舊的歌曲數據，保留音量信息
        val existingSongs = musicDao.getAllSongs().associateBy { it.trackId }
        
        musicDao.clearSongs()
        musicDao.clearPlaylists()
        musicDao.clearCrossRefs()

        val songsToInsert = songs.map { song ->
            val entity = song.toEntity()
            val existing = existingSongs[song.trackId]
            if (existing != null && entity.loudness == null) {
                entity.copy(loudness = existing.loudness, peak = existing.peak)
            } else {
                entity
            }
        }

        musicDao.insertSongs(songsToInsert)
        musicDao.insertPlaylists(playlists.map { it.toEntity() })

        val crossRefs = mutableListOf<PlaylistSongCrossRef>()
        for (playlist in playlists) {
            for (song in playlist.songs) {
                crossRefs.add(PlaylistSongCrossRef(playlist.name, song.trackId))
            }
        }
        musicDao.insertCrossRefs(crossRefs)
    }

    // 播放次數相關方法
    suspend fun recordPlay(trackId: String) {
        val existing = playStatDao.getStatById(trackId)
        val newStat = if (existing != null) {
            existing.copy(playCount = existing.playCount + 1, lastPlayedTime = System.currentTimeMillis())
        } else {
            SongPlayStatEntity(trackId = trackId, playCount = 1, lastPlayedTime = System.currentTimeMillis())
        }
        playStatDao.upsertPlayStat(newStat)
    }

    suspend fun getTopPlayedTrackIds(): List<String> {
        return playStatDao.getAllStats().take(20).map { it.trackId }
    }

    suspend fun updateSongLoudness(trackId: String, loudness: Double, peak: Double) {
        val song = musicDao.getSongById(trackId)
        if (song != null) {
            musicDao.updateSong(song.copy(loudness = loudness, peak = peak))
        }
    }

    suspend fun updateSongLyrics(trackId: String, lyrics: String) {
        val song = musicDao.getSongById(trackId)
        if (song != null) {
            musicDao.updateSong(song.copy(lyrics = lyrics))
        }
    }

    suspend fun getSong(trackId: String): Song? {
        return musicDao.getSongById(trackId)?.toSong()
    }
}

// 7. Room DAO
@Dao
interface MusicDao {
    @Query("SELECT * FROM songs")
    suspend fun getAllSongs(): List<SongEntity>

    @Query("SELECT * FROM playlists")
    suspend fun getAllPlaylists(): List<PlaylistEntity>

    @Query("SELECT * FROM playlist_song_cross_ref")
    suspend fun getAllCrossRefs(): List<PlaylistSongCrossRef>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<SongEntity>)

    @Update
    suspend fun updateSong(song: SongEntity)

    @Query("SELECT * FROM songs WHERE trackId = :trackId")
    suspend fun getSongById(trackId: String): SongEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylists(playlists: List<PlaylistEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRefs(crossRefs: List<PlaylistSongCrossRef>)

    @Query("DELETE FROM songs")
    suspend fun clearSongs()

    @Query("DELETE FROM playlists")
    suspend fun clearPlaylists()

    @Query("DELETE FROM playlist_song_cross_ref")
    suspend fun clearCrossRefs()
}

// 8. Room Database 主體
@Database(
    entities = [SongEntity::class, PlaylistEntity::class, PlaylistSongCrossRef::class, SongPlayStatEntity::class],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun musicDao(): MusicDao
    abstract fun songPlayStatDao(): SongPlayStatDao
}