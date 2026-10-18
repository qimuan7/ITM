package lkg.itm.music

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

@OptIn(UnstableApi::class)
class MusicPlayerManager(private val context: Context) {
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null

    private val repository = MusicRepository(context)
    private val volumePrefs = VolumePreferences(context)

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    private val _queue = MutableStateFlow<List<Song>>(emptyList())
    val queue: StateFlow<List<Song>> = _queue

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration

    private val _volume = MutableStateFlow(volumePrefs.getVolume())
    val volume: StateFlow<Int> = _volume

    private val _shuffleMode = MutableStateFlow(volumePrefs.isShuffleEnabled())
    val shuffleMode: StateFlow<Boolean> = _shuffleMode

    private val _repeatMode = MutableStateFlow(volumePrefs.getRepeatMode())
    val repeatMode: StateFlow<Int> = _repeatMode

    private val _isNormalizationEnabled = MutableStateFlow(volumePrefs.isNormalizationEnabled())
    val isNormalizationEnabled: StateFlow<Boolean> = _isNormalizationEnabled

    private var playlist: List<Song> = emptyList()

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var progressJob: Job? = null

    init {
        initializeController()
    }

    private fun initializeController() {
        val sessionToken = SessionToken(context, ComponentName(context, MusicService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener({
            try {
                val mediaController = controllerFuture?.get()
                controller = mediaController
                setupController(mediaController)
            } catch (e: Exception) {
                Log.e("MusicPlayerManager", "Failed to connect to MediaController", e)
            }
        }, MoreExecutors.directExecutor())
    }

    private fun setupController(controller: MediaController?) {
        controller?.let { ctrl ->
            ctrl.repeatMode = when (_repeatMode.value) {
                2 -> Player.REPEAT_MODE_ONE
                1 -> Player.REPEAT_MODE_ALL
                else -> Player.REPEAT_MODE_OFF
            }
            ctrl.shuffleModeEnabled = _shuffleMode.value

            // 【核心修復】連接後立即從 Controller 同步狀態
            coroutineScope.launch {
                syncWithService(ctrl)
                // 連接後同步 Normalization 狀態
                val bundle = Bundle().apply { putBoolean("enabled", _isNormalizationEnabled.value) }
                ctrl.sendCustomCommand(SessionCommand("ACTION_SET_NORMALIZATION", Bundle.EMPTY), bundle)
            }

            ctrl.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                    if (isPlaying) startProgressTicker() else stopProgressTicker()
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    mediaItem?.let { item ->
                        coroutineScope.launch {
                            // 紀錄播放次數
                            repository.recordPlay(item.mediaId)

                            val song = repository.getSong(item.mediaId) ?: playlist.find { it.trackId == item.mediaId }
                            _currentSong.value = song
                            _queue.value = getActualPlayingOrder()
                        }
                    }
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_READY) {
                        _duration.value = ctrl.duration.coerceAtLeast(0L)
                    }
                }
            })
        }
    }

    private suspend fun syncWithService(ctrl: MediaController) {
        val mediaItem = ctrl.currentMediaItem ?: return
        
        // 1. 同步當前歌曲
        val song = repository.getSong(mediaItem.mediaId)
        _currentSong.value = song
        
        // 2. 同步播放狀態
        _isPlaying.value = ctrl.isPlaying
        _duration.value = ctrl.duration.coerceAtLeast(0L)
        _currentPosition.value = ctrl.currentPosition
        
        // 3. 同步隊列 (重建 playlist)
        val restoredPlaylist = mutableListOf<Song>()
        for (i in 0 until ctrl.mediaItemCount) {
            repository.getSong(ctrl.getMediaItemAt(i).mediaId)?.let {
                restoredPlaylist.add(it)
            }
        }
        playlist = restoredPlaylist
        _queue.value = restoredPlaylist

        if (ctrl.isPlaying) startProgressTicker()
    }

    private fun updatePlayerVolume() {
        val userVol = _volume.value / 100f
        val ctrl = controller ?: return

        ctrl.volume = userVol
    }

    fun setNormalizationEnabled(enabled: Boolean) {
        _isNormalizationEnabled.value = enabled
        volumePrefs.setNormalizationEnabled(enabled)
        val bundle = Bundle().apply { putBoolean("enabled", enabled) }
        controller?.sendCustomCommand(SessionCommand("ACTION_SET_NORMALIZATION", Bundle.EMPTY), bundle)
    }

    fun setPlaylist(songs: List<Song>, startIndex: Int = 0) {
        playlist = songs
        _queue.value = songs
        val ctrl = controller ?: return

        val mediaItems = songs.map { song ->
            val uri = if (song.androidPath.startsWith("content://")) Uri.parse(song.androidPath) else Uri.fromFile(File(song.androidPath))
            val extras = Bundle().apply {
                song.loudness?.let { putDouble("loudness", it) }
            }
            MediaItem.Builder()
                .setUri(uri)
                .setMediaId(song.trackId)
                .setMediaMetadata(
                    androidx.media3.common.MediaMetadata.Builder()
                        .setTitle(song.title)
                        .setArtist(song.artist)
                        .setAlbumTitle(song.album)
                        .setExtras(extras)
                        .build()
                )
                .build()
        }

        ctrl.setMediaItems(mediaItems, startIndex, 0L)
        ctrl.prepare()
        ctrl.play()
    }

    fun playSong(song: Song) {
        val ctrl = controller ?: return
        for (i in 0 until ctrl.mediaItemCount) {
            if (ctrl.getMediaItemAt(i).mediaId == song.trackId) {
                ctrl.seekTo(i, 0L)
                ctrl.play()
                return
            }
        }
    }

    fun togglePlayPause() {
        val ctrl = controller ?: return
        if (ctrl.isPlaying) ctrl.pause() else ctrl.play()
    }

    fun playNext() {
        controller?.seekToNextMediaItem()
    }

    fun playPrevious() {
        controller?.seekToPreviousMediaItem()
    }

    fun seekTo(positionMs: Long) {
        controller?.seekTo(positionMs)
        _currentPosition.value = positionMs
    }

    fun setVolume(volume: Int) {
        _volume.value = volume.coerceIn(0, 100)
        volumePrefs.setVolume(_volume.value)
        updatePlayerVolume()
    }

    fun setShuffleMode(enabled: Boolean) {
        _shuffleMode.value = enabled
        controller?.shuffleModeEnabled = enabled
        volumePrefs.setShuffleEnabled(enabled)
        _queue.value = getActualPlayingOrder()
    }

    fun setRepeatMode(mode: Int) {
        _repeatMode.value = mode
        controller?.repeatMode = when (mode) {
            1 -> Player.REPEAT_MODE_ALL
            2 -> Player.REPEAT_MODE_ONE
            else -> Player.REPEAT_MODE_OFF
        }
        volumePrefs.setRepeatMode(mode)
    }

    fun getActualPlayingOrder(): List<Song> {
        val ctrl = controller ?: return playlist
        val order = mutableListOf<Song>()
        for (i in 0 until ctrl.mediaItemCount) {
            val mediaId = ctrl.getMediaItemAt(i).mediaId
            playlist.find { it.trackId == mediaId }?.let { order.add(it) }
        }
        return order
    }

    private fun startProgressTicker() {
        progressJob?.cancel()
        progressJob = coroutineScope.launch {
            while (isActive) {
                controller?.let {
                    _currentPosition.value = it.currentPosition.coerceAtLeast(0L)
                }
                delay(500L)
            }
        }
    }

    private fun stopProgressTicker() {
        progressJob?.cancel()
        progressJob = null
    }

    fun release() {
        stopProgressTicker()
        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }
    }
}
