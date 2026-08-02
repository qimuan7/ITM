// 底欄 組件
package lkg.itm.music.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import lkg.itm.music.MainViewModel
import lkg.itm.music.MusicPlayerManager
import lkg.itm.music.Playlist
import lkg.itm.music.Song

@Composable
fun MainScreen(
    playlists: List<Playlist>,
    allSongs: List<Song>,
    playerManager: MusicPlayerManager,
    musicState: MusicState,
    onSelectDirectory: () -> Unit,
    currentThemeMode: String,
    onThemeModeChanged: (String) -> Unit,
    onOpenSettings: () -> Unit,
    isSettingsOpen: Boolean,
    recommendationRefreshTrigger: Int = 0,
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current

    // 🌟 直接在 MainScreen 記憶狀態，因為頁面常駐不銷毀，這裡的 remember 永遠有效
    val homeVerticalListState = rememberLazyListState()
    val homeHorizontalListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var backPressedTime by remember { mutableLongStateOf(0L) }

    val currentSong by playerManager.currentSong.collectAsState()
    val isPlaying by playerManager.isPlaying.collectAsState()
    val currentPosition by playerManager.currentPosition.collectAsState()
    val duration by playerManager.duration.collectAsState()
    val queue by playerManager.queue.collectAsState()
    val volume by playerManager.volume.collectAsState()
    val shuffleMode by playerManager.shuffleMode.collectAsState()
    val repeatMode by playerManager.repeatMode.collectAsState()

    // 格式化播放時間
    fun formatTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return "%d:%02d".format(minutes, seconds)
    }

    // 構建播放器狀態
    val musicStateInstance = MusicState(
        songTitle = currentSong?.title ?: "未在播放",
        artistName = currentSong?.artist ?: "",
        artwork = currentSong?.androidPath, // AlbumArtLoader 會處理路徑
        isPlaying = isPlaying,
        progress = if (duration > 0) currentPosition.toFloat() / duration else 0f,
        currentPositionText = formatTime(currentPosition),
        durationText = if (duration > 0) "-${formatTime(duration - currentPosition)}" else "0:00",
        queue = queue,
        currentSongId = currentSong?.trackId,
        volume = volume,
        isShuffleEnabled = shuffleMode,
        repeatMode = repeatMode,
        lyrics = currentSong?.lyrics
    )

    // 全螢幕播放器展開時，返回鍵關閉播放器
    BackHandler(enabled = viewModel.isPlayerExpanded) {
        viewModel.isPlayerExpanded = false
    }

    // 當在首頁且向下滾動時，按返回鍵讓頁面滾動回頂部
    BackHandler(
        enabled = !viewModel.isPlayerExpanded && !isSettingsOpen && viewModel.selectedNavItem == 0 &&
                (homeVerticalListState.firstVisibleItemIndex > 0 || homeVerticalListState.firstVisibleItemScrollOffset > 0)
    ) {
        coroutineScope.launch {
            homeVerticalListState.animateScrollToItem(0)
        }
    }

    // 雙擊返回鍵退出 App
    BackHandler(enabled = !viewModel.isPlayerExpanded && !isSettingsOpen &&
            !(viewModel.selectedNavItem == 0 && (homeVerticalListState.firstVisibleItemIndex > 0 || homeVerticalListState.firstVisibleItemScrollOffset > 0))) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backPressedTime < 2000) {
            (context as? android.app.Activity)?.finish()
        } else {
            backPressedTime = currentTime
        }
    }

    // 判斷是否為深色模式
    val isDark = when (currentThemeMode) {
        "light" -> false
        "dark" -> true
        else -> androidx.compose.foundation.isSystemInDarkTheme()
    }

    Box(modifier = Modifier.fillMaxSize().background(if (isDark) Color.Black else Color(0xFFF5F5F5))) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                val bottomBarBackground = if (isDark) {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f),
                            Color.Black
                        )
                    )
                } else {
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFF5F5F5).copy(alpha = 0.0f),
                            Color(0xFFF5F5F5).copy(alpha = 0.85f),
                            Color(0xFFF5F5F5)
                        )
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(bottomBarBackground)
                        .navigationBarsPadding()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 首頁按鈕
                        val isHomeSelected = viewModel.selectedNavItem == 0
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { viewModel.selectedNavItem = 0 },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "首頁",
                                tint = if (isHomeSelected) Color(0xFFFA2D48) else Color.Gray,
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "首頁",
                                fontSize = 12.sp,
                                color = if (isHomeSelected) Color(0xFFFA2D48) else Color.Gray
                            )
                        }

                        // 媒體庫按鈕
                        val isLibrarySelected = viewModel.selectedNavItem == 1
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) { viewModel.selectedNavItem = 1 },
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LibraryMusic,
                                contentDescription = "媒體庫",
                                tint = if (isLibrarySelected) Color(0xFFFA2D48) else Color.Gray,
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "媒體庫",
                                fontSize = 12.sp,
                                color = if (isLibrarySelected) Color(0xFFFA2D48) else Color.Gray
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // 🌟 核心修改：使用 Box 疊加分頁，透過 alpha 顯示/隱藏，保證頁面常駐不銷毀，滾動位置絕對不丟失！
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { alpha = if (viewModel.selectedNavItem == 0) 1f else 0f }
                ) {
                    if (viewModel.selectedNavItem == 0 || true) { // 保持組合但控制顯示
                        HomeScreen(
                            songs = allSongs,
                            playerManager = playerManager,
                            verticalListState = homeVerticalListState,
                            horizontalListState = homeHorizontalListState,
                            onSelectDirectory = onSelectDirectory,
                            currentThemeMode = currentThemeMode,
                            onThemeModeChanged = onThemeModeChanged,
                            onOpenSettings = onOpenSettings,
                            isSettingsOpen = isSettingsOpen,
                            isOverlayOpen = viewModel.isPlayerExpanded || isSettingsOpen,
                            recommendationRefreshTrigger = recommendationRefreshTrigger
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { alpha = if (viewModel.selectedNavItem == 1) 1f else 0f }
                ) {
                    if (viewModel.selectedNavItem == 1) {
                        LibraryScreen(
                            playlists = playlists,
                            songs = allSongs,
                            playerManager = playerManager,
                            onSelectDirectory = onSelectDirectory,
                            currentThemeMode = currentThemeMode,
                            onThemeModeChanged = onThemeModeChanged,
                            onOpenSettings = onOpenSettings,
                            isOverlayOpen = viewModel.isPlayerExpanded || isSettingsOpen
                        )
                    }
                }
            }
        }

        PlayerOverlayScreen(
            musicState = musicStateInstance,
            isExpanded = viewModel.isPlayerExpanded,
            onExpandToggle = { viewModel.isPlayerExpanded = it },
            onPlayPauseClick = { playerManager.togglePlayPause() },
            onNextClick = { playerManager.playNext() },
            onPreviousClick = { playerManager.playPrevious() },
            onSeek = { progress ->
                val seekPos = (progress * duration).toLong()
                playerManager.seekTo(seekPos)
            },
            onSongClick = { song ->
                playerManager.playSong(song)
            },
            onVolumeChange = { volume ->
                playerManager.setVolume(volume)
            },
            onShuffleToggle = { enabled ->
                playerManager.setShuffleMode(enabled)
            },
            onRepeatModeChange = { mode ->
                playerManager.setRepeatMode(mode)
            }
        )
    }
}
