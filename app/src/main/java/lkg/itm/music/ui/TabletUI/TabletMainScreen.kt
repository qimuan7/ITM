// 底欄 組件
package lkg.itm.music.ui.TabletUI

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import lkg.itm.music.MainViewModel
import lkg.itm.music.MusicPlayerManager
import lkg.itm.music.Playlist
import lkg.itm.music.Song
import lkg.itm.music.ui.MusicState

@Composable
fun TabletMainScreen(
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
    val iconColor = if (isDark) Color.White else Color.Black
    val borderColor = if (isDark) Color.White else Color.Black

    Box(modifier = Modifier.fillMaxSize().background(if (isDark) Color.Black else Color(0xFFF5F5F5))) {
        Row(modifier = Modifier.fillMaxSize()) {
            // 左側 Sidebar
            val sidebarBg = if (isDark) Color(0xFF000000) else Color(0xFFF5F5F5)
            val textColor = if (isDark) Color.White else Color.Black
            val accentColor = Color(0xFFFA2D48)

            Column(
                modifier = Modifier
                    .width(260.dp)
                    .fillMaxHeight()
                    .background(sidebarBg)
                    .drawWithContent {
                        drawContent()
                        // 繪製右側分隔線
                        drawLine(
                            color = borderColor,
                            start = Offset(size.width, 0f),
                            end = Offset(size.width, size.height),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp)
            ) {
                // 設定按鈕 (移至頂部右側)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 76.dp, end = 8.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "設定",
                        tint = iconColor,
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { onOpenSettings() }
                    )
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    item {
                        Text(
                            "主頁",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
                        )
                        SidebarItem(
                            title = "首頁",
                            icon = Icons.Default.Home,
                            isSelected = viewModel.selectedNavItem == 0,
                            onClick = {
                                viewModel.selectedNavItem = 0
                                viewModel.currentLibrarySubPage = null
                            },
                            textColor = textColor,
                            accentColor = accentColor,
                            iconColor = iconColor,
                            borderColor = borderColor
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        Text(
                            "媒體庫",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
                        )
                    }

                    val categories = listOf(
                        Triple("已下載", Icons.Default.Download, "已下載"),
                        Triple("歌曲", Icons.Default.MusicNote, "歌曲"),
                        Triple("藝人", Icons.Default.Person, "藝人"),
                        Triple("專輯", Icons.Default.Album, "專輯")
                    )

                    items(categories) { (title, icon, subPage) ->
                        SidebarItem(
                            title = title,
                            icon = icon,
                            isSelected = viewModel.selectedNavItem == 1 && viewModel.currentLibrarySubPage == subPage && viewModel.selectedPlaylistName == null,
                            onClick = {
                                viewModel.selectedNavItem = 1
                                viewModel.currentLibrarySubPage = subPage
                                viewModel.selectedPlaylistName = null
                            },
                            textColor = textColor,
                            accentColor = accentColor,
                            iconColor = iconColor,
                            borderColor = borderColor
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "播放清單",
                            color = Color.Gray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
                        )
                    }

                    items(playlists.filter { !it.isSysDefault }) { playlist ->
                        SidebarItem(
                            title = playlist.name,
                            icon = Icons.AutoMirrored.Filled.QueueMusic,
                            isSelected = viewModel.selectedNavItem == 1 && viewModel.selectedPlaylistName == playlist.name,
                            onClick = {
                                viewModel.selectedNavItem = 1
                                viewModel.currentLibrarySubPage = "播放清單詳情"
                                viewModel.selectedPlaylistName = playlist.name
                            },
                            textColor = textColor,
                            accentColor = accentColor,
                            iconColor = iconColor,
                            borderColor = borderColor
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(54.dp))
                    }
                }
            }

            // 右側內容區
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                // 🌟 核心修改：使用 Box 疊加分頁，透過 alpha 顯示/隱藏，保證頁面常駐不銷毀，滾動位置絕對不丟失！
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { alpha = if (viewModel.selectedNavItem == 0) 1f else 0f }
                ) {
                    TabletHomeScreen(
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

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { alpha = if (viewModel.selectedNavItem == 1) 1f else 0f }
                ) {
                    TabletLibraryScreen(
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

        // 🌟 核心修改：將播放器移到最外層 Box，使其展開時能覆蓋全螢幕（包含 Sidebar）
        TabletPlayerOverlayScreen(
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

@Composable
private fun SidebarItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    textColor: Color,
    accentColor: Color,
    iconColor: Color,
    borderColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .then(if (isSelected) Modifier.border(1.dp, borderColor, RoundedCornerShape(10.dp)) else Modifier)
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 17.sp,
            color = textColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
