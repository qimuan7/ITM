// "資源庫"頁面
package lkg.itm.music.ui

import android.graphics.Bitmap
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import lkg.itm.music.AlbumArtLoader
import lkg.itm.music.MainViewModel
import lkg.itm.music.MusicPlayerManager
import lkg.itm.music.Playlist
import lkg.itm.music.Song


@Composable
fun LibraryScreen(
    songs: List<Song>,
    playlists: List<Playlist>,
    playerManager: MusicPlayerManager,
    onSelectDirectory: () -> Unit,
    currentThemeMode: String,
    onThemeModeChanged: (String) -> Unit,
    onOpenSettings: () -> Unit,
    isOverlayOpen: Boolean,
    viewModel: MainViewModel = viewModel()
) {
    val context = LocalContext.current

    val currentSong by playerManager.currentSong.collectAsState()
    val isPlaying by playerManager.isPlaying.collectAsState()

    var isSearchExpanded by remember { mutableStateOf(false) }
    var detailSearchQuery by remember { mutableStateOf("") }

    // 為了避免命名衝突，將內部的 isDark 重新命名
    val currentIsDark = when (currentThemeMode) {
        "light" -> false
        "dark" -> true
        else -> androidx.compose.foundation.isSystemInDarkTheme()
    }

    @Composable
    fun LibraryDetailHeader(
        count: Int,
        currentThemeMode: String,
        onPlayClick: (() -> Unit)? = null,
        onShuffleClick: (() -> Unit)? = null,
        onSearchClick: (() -> Unit)? = null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 0.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$count 首歌曲",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )

            if (onPlayClick != null || onShuffleClick != null || onSearchClick != null) {
                val iconColor = when (currentThemeMode) {
                    "light" -> Color.Black
                    "dark" -> Color.White
                    else -> if (androidx.compose.foundation.isSystemInDarkTheme()) Color.White else Color.Black
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    onPlayClick?.let { IconButton(onClick = it) { Icon(Icons.Default.PlayArrow, contentDescription = "播放", tint = iconColor) } }
                    onShuffleClick?.let { IconButton(onClick = it) { Icon(Icons.Default.Shuffle, contentDescription = "隨機播放", tint = iconColor) } }
                    onSearchClick?.let { IconButton(onClick = it) { Icon(Icons.Default.Search, contentDescription = "搜尋", tint = iconColor) } }
                }
            }
        }
    }

    @Composable
    fun RenderPlaylistContent(songs: List<Song>, showHeaderButtons: Boolean = true) {
        val displaySongs = remember(songs, detailSearchQuery) {
            if (detailSearchQuery.isBlank()) songs
            else songs.filter { it.title.contains(detailSearchQuery, true) || it.artist.contains(detailSearchQuery, true) }
        }

        // 使用 Box 讓搜尋框懸浮在內容上方
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (showHeaderButtons) {
                    LibraryDetailHeader(
                        count = displaySongs.size,
                        currentThemeMode = currentThemeMode,
                        onPlayClick = { if (displaySongs.isNotEmpty()) playerManager.setPlaylist(displaySongs, 0) },
                        onShuffleClick = { if (displaySongs.isNotEmpty()) playerManager.setPlaylist(displaySongs.shuffled(), 0) },
                        onSearchClick = { isSearchExpanded = !isSearchExpanded; if (!isSearchExpanded) detailSearchQuery = "" }
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp, top = if (isSearchExpanded) 70.dp else 10.dp) // 將 top 改為 0.dp
                ) {
                    items(items = displaySongs, key = { it.trackId }) { song ->
                        SongListItem(
                            song = song,
                            isSelected = currentSong?.trackId == song.trackId,
                            onClick = { playerManager.setPlaylist(displaySongs, displaySongs.indexOf(song)) }
                        )
                    }
                }
            }

            // 搜尋框置於最上層
            if (isSearchExpanded) {
                Box(modifier = Modifier.padding(top = 60.dp)) {
                    ModernSearchField(
                        query = detailSearchQuery,
                        onQueryChange = { detailSearchQuery = it },
                        isDark = currentIsDark
                    )
                }
            }
        }
    }

    // 👈 更新：支援藝人與專輯細節頁的返回手勢，並檢查是否被覆蓋
    BackHandler(enabled = !isOverlayOpen && (viewModel.currentLibrarySubPage != null || viewModel.selectedPlaylistName != null || viewModel.selectedArtistName != null || viewModel.selectedAlbumName != null)) {
        when {
            viewModel.selectedArtistName != null -> viewModel.selectedArtistName = null
            viewModel.selectedAlbumName != null -> viewModel.selectedAlbumName = null
            viewModel.selectedPlaylistName != null && viewModel.currentLibrarySubPage == "播放清單詳情" -> {
                viewModel.selectedPlaylistName = null
                viewModel.currentLibrarySubPage = null
            }
            viewModel.selectedPlaylistName != null -> viewModel.selectedPlaylistName = null
            else -> viewModel.currentLibrarySubPage = null
        }
    }

    val animationScale = remember {
        try {
            Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1.0f
            )
        } catch (e: Exception) {
            1.0f
        }
    }
    val animDuration = (300 * animationScale).toInt()
    val appleEaseCurve = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1.0f)

    val libraryCategories = listOf(
        Pair("已下載", Icons.Default.Download),
        Pair("歌曲", Icons.Default.MusicNote),
        Pair("藝人", Icons.Default.Person),
        Pair("專輯", Icons.Default.Album)
    )

    val userPlaylists = remember(playlists) {
        playlists.filter { !it.isSysDefault }
    }

    // 👈 新增：透過 songs 自動分組計算藝人與專輯資料
    val artistMap = remember(songs) {
        songs.groupBy { if (it.artist.isBlank()) "未知藝人" else it.artist }.toSortedMap()
    }

    val albumMap = remember(songs) {
        songs.groupBy { if (it.album.isNullOrBlank()) "未知專輯" else it.album!! }.toSortedMap()
    }

    val isDark = when (currentThemeMode) {
        "light" -> false
        "dark" -> true
        else -> androidx.compose.foundation.isSystemInDarkTheme()
    }
    
    val backgroundColor = if (isDark) Color.Black else Color(0xFFF5F5F5)
    val groupCardBackgroundColor = if (isDark) Color(0xFF0D0F12) else Color(0xFFEDEDED)
    val cardBorderColor = if (isDark) Color(0xFF2C2C2C) else Color(0xFFB0B3BD)
    val dividerColor = if (isDark) Color.White.copy(alpha = 0.15f) else Color(0xFFB0B3BD)
    val cardShape = RoundedCornerShape(16.dp)
    val borderModifier = androidx.compose.ui.Modifier.border(1.dp, cardBorderColor, cardShape)
    val textColor = if (isDark) Color.White else Color.Black

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        AnimatedContent(
            targetState = viewModel.currentLibrarySubPage,
            transitionSpec = {
                if (targetState != null) {
                    slideInHorizontally(
                        animationSpec = tween(durationMillis = animDuration, easing = appleEaseCurve),
                        initialOffsetX = { fullWidth -> fullWidth }
                    ).togetherWith(
                        slideOutHorizontally(
                            animationSpec = tween(durationMillis = animDuration, easing = appleEaseCurve),
                            targetOffsetX = { fullWidth -> -fullWidth }
                        )
                    )
                } else {
                    slideInHorizontally(
                        animationSpec = tween(durationMillis = animDuration, easing = appleEaseCurve),
                        initialOffsetX = { fullWidth -> -fullWidth }
                    ).togetherWith(
                        slideOutHorizontally(
                            animationSpec = tween(durationMillis = animDuration, easing = appleEaseCurve),
                            targetOffsetX = { fullWidth -> fullWidth }
                        )
                    )
                }
            },
            label = "PageTransition",
            contentAlignment = Alignment.TopStart,
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
        ) { subPage ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp)
            ) {
                // ================= 頂部標題列邏輯 =================
                if (subPage == null) {
                    Spacer(modifier = Modifier.height(65.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 0.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "媒體庫",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            fontSize = 34.sp,
                            color = when (currentThemeMode) {
                                "light" -> Color.Black
                                "dark" -> Color.White
                                else -> if (androidx.compose.foundation.isSystemInDarkTheme()) Color.White else Color.Black
                            }
                        )

                        IconButton(onClick = onOpenSettings) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "設定",
                                tint = Color(0xFFFA2D48),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                } else {
                    when (subPage) {
                        "播放清單詳情", "已下載", "藝人", "專輯" -> {
                            Column {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                            contentDescription = "返回",
                            tint = Color(0xFFFA2D48),
                            modifier = Modifier
                                .offset(x = (-13).dp)
                                .size(48.dp)
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                                ) {
                                    isSearchExpanded = false
                                    detailSearchQuery = ""
                                    when {
                                        viewModel.selectedArtistName != null -> viewModel.selectedArtistName = null
                                        viewModel.selectedAlbumName != null -> viewModel.selectedAlbumName = null
                                        viewModel.selectedPlaylistName != null -> {
                                            viewModel.selectedPlaylistName = null
                                            if (subPage == "播放清單詳情") viewModel.currentLibrarySubPage = null
                                        }
                                        else -> viewModel.currentLibrarySubPage = null
                                    }
                                }
                                .padding(13.dp)
                        )
                                Spacer(modifier = Modifier.height(17.dp))
                                Text(
                                    text = if (subPage == "播放清單詳情") (viewModel.selectedPlaylistName ?: "播放清單") 
                                           else if (subPage == "藝人") (viewModel.selectedArtistName ?: "藝人")
                                           else if (subPage == "專輯") (viewModel.selectedAlbumName ?: "專輯")
                                           else subPage,
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 34.sp,
                                    color = if (isDark) Color.White else Color.Black
                                )
                            }
                        }
                        else -> {
                            Column {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                                    contentDescription = "返回",
                                    tint = Color(0xFFFA2D48),
                                    modifier = Modifier
                                        .offset(x = (-13).dp)
                                        .size(48.dp)
                                        .clickable(
                                            indication = null,
                                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                                        ) {
                                            isSearchExpanded = false; detailSearchQuery =
                                            ""; viewModel.currentLibrarySubPage = null
                                        }
                                        .padding(13.dp)
                                )
                                Spacer(modifier = Modifier.height(17.dp))
                                Text(
                                    text = subPage,
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 34.sp,
                                    color = if (isDark) Color.White else Color.Black
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(0.dp))

                // ================= 內容區域分發 =================
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    if (subPage == null) {
                        // 1. 媒體庫主目錄
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(28.dp),
                            contentPadding = PaddingValues(bottom = 120.dp)
                        ) {
                            item {
                                Spacer(modifier = Modifier.height(6.dp))
                            }

                            // ---------------- 區塊一：資料庫 ----------------
                            item {
                                Text(
                                    text = "資料庫",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .then(borderModifier),
                                    colors = CardDefaults.cardColors(containerColor = groupCardBackgroundColor),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                    shape = cardShape
                                ) {
                                    Column {
                                        libraryCategories.forEachIndexed { index, (title, icon) ->
                                            LibraryMenuItem(
                                                title = title,
                                                icon = icon,
                                                iconSize = 24.dp,
                                                fontSize = 20.dp.value.sp,
                                                textColor = textColor,
                                                onClick = {
                                                    viewModel.currentLibrarySubPage = title
                                                    viewModel.selectedPlaylistName = null
                                                    viewModel.selectedArtistName = null
                                                    viewModel.selectedAlbumName = null
                                                }
                                            )
                                            if (index < libraryCategories.size - 1) {
                                                HorizontalDivider(
                                                    modifier = Modifier.padding(start = 52.dp),
                                                    thickness = 0.5.dp,
                                                    color = dividerColor
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // ---------------- 區塊二：播放清單 ----------------
                            item {
                                Text(
                                    text = "播放清單",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .then(borderModifier),
                                    colors = CardDefaults.cardColors(containerColor = groupCardBackgroundColor),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                    shape = cardShape
                                ) {
                                    Column {
                                        userPlaylists.forEachIndexed { index, playlist ->
                                            LibraryMenuItem(
                                                title = "${playlist.name} (${playlist.songs.size}首)",
                                                icon = Icons.Default.QueueMusic,
                                                iconSize = 24.dp,
                                                fontSize = 18.sp,
                                                textColor = textColor,
                                                onClick = {
                                                    viewModel.selectedPlaylistName = playlist.name
                                                    viewModel.currentLibrarySubPage = "播放清單詳情"
                                                }
                                            )
                                            if (index < userPlaylists.size - 1) {
                                                HorizontalDivider(
                                                    modifier = Modifier.padding(start = 52.dp),
                                                    thickness = 0.5.dp,
                                                    color = dividerColor
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // 2. 子頁面內容
                        when (subPage) {
                            "播放清單詳情" -> {
                                val songsToShow = remember(viewModel.selectedPlaylistName, songs, playlists) {
                                    if (viewModel.selectedPlaylistName == "全部") songs
                                    else playlists.find { it.name == viewModel.selectedPlaylistName }?.songs ?: emptyList()
                                }
                                RenderPlaylistContent(songsToShow)
                            }

                            "已下載" -> {
                                val sysPlaylists = remember(playlists) { playlists.filter { it.isSysDefault } }
                                if (viewModel.selectedPlaylistName == null) {
                                    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(top = 30.dp, bottom = 100.dp)) {
                                        item {
                                            Card(modifier = Modifier
                                                .fillMaxWidth()
                                                .then(borderModifier), colors = CardDefaults.cardColors(containerColor = groupCardBackgroundColor), shape = cardShape) {
                                                Column {
                                                    sysPlaylists.forEachIndexed { index, playlist ->
                                                        LibraryMenuItem(
                                                            title = "${playlist.name} (${playlist.songs.size}首)",
                                                            icon = Icons.Default.Folder,
                                                            iconSize = 24.dp,
                                                            fontSize = 18.sp,
                                                            textColor = textColor,
                                                            onClick = { viewModel.selectedPlaylistName = playlist.name }
                                                        )
                                                        if (index < sysPlaylists.size - 1) HorizontalDivider(modifier = Modifier.padding(start = 52.dp), thickness = 0.5.dp, color = dividerColor)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    val songsToShow = remember(viewModel.selectedPlaylistName, playlists) { playlists.find { it.name == viewModel.selectedPlaylistName }?.songs ?: emptyList() }
                                    RenderPlaylistContent(songsToShow)
                                }
                            }

                            "藝人" -> {
                                if (viewModel.selectedArtistName == null) {
                                    val artists = artistMap.keys.toList()
                                    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(top = 20.dp, bottom = 100.dp)) {
                                        items(artists) { artist ->
                                            LibraryMenuItem(title = "$artist (${artistMap[artist]?.size ?: 0} 首)", icon = Icons.Default.Person, onClick = { viewModel.selectedArtistName = artist })
                                            HorizontalDivider(modifier = Modifier.padding(start = 52.dp), thickness = 0.5.dp, color = dividerColor)
                                        }
                                    }
                                } else {
                                    RenderPlaylistContent(artistMap[viewModel.selectedArtistName] ?: emptyList())
                                }
                            }

                            "專輯" -> {
                                if (viewModel.selectedAlbumName == null) {
                                    LazyVerticalGrid(columns = GridCells.Fixed(2), contentPadding = PaddingValues(top = 30.dp, bottom = 100.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                        items(albumMap.keys.toList()) { album ->
                                            val firstSong = albumMap[album]?.firstOrNull()
                                            AlbumGridItem(title = album, artist = firstSong?.artist ?: "未知藝人", imagePath = firstSong?.androidPath, currentThemeMode = currentThemeMode, onClick = { viewModel.selectedAlbumName = album })
                                        }
                                    }
                                } else {
                                    RenderPlaylistContent(albumMap[viewModel.selectedAlbumName] ?: emptyList())
                                }
                            }

                            "歌曲" -> RenderPlaylistContent(songs.sortedBy { it.title.lowercase() })

                            else -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("詳細內容", color = Color.Gray) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ModernSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    isDark: Boolean
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }

    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 6.dp)
            .height(42.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                if (isDark) Color(0xFF0D0F12).copy(alpha = 0.85f) else Color(0xFFEDEDED).copy(
                    alpha = 0.85f
                )
            )
            .border(
                1.dp,
                if (isDark) Color(0xFF2C2C2C) else Color(0xFFB0B3BD),
                RoundedCornerShape(12.dp)
            ),
        textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp, color = if (isDark) Color.White else Color.Black),
        singleLine = true,
        interactionSource = interactionSource,
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text("搜尋...", color = Color.Gray, fontSize = 14.sp)
                    }
                    innerTextField()
                }
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color(0xFFFA2D48),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    )
}

// 👈 新增：用於專輯頁面的網格項目卡片
@Composable
fun AlbumGridItem(
    title: String,
    artist: String,
    imagePath: String?,
    currentThemeMode: String,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    var bitmap by remember(imagePath) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(imagePath) {
        if (!imagePath.isNullOrBlank()) {
            try {
                bitmap = withContext(Dispatchers.IO) {
                    AlbumArtLoader.getAlbumArtBitmap(context, imagePath)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF222222)),
            contentAlignment = Alignment.Center
        ) {
            bitmap?.let { loadedBitmap ->
                Image(
                    bitmap = loadedBitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } ?: run {
                Icon(
                    imageVector = Icons.Default.Album,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = when (currentThemeMode) {
                "light" -> Color.Black
                "dark" -> Color.White
                else -> if (androidx.compose.foundation.isSystemInDarkTheme()) Color.White else Color.Black
            },
            maxLines = 1
        )
        Text(
            text = artist,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            maxLines = 1
        )
    }
}

@Composable
fun LibraryBackButton(
    title: String = "‹ 媒體庫",
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFFFA2D48)
        )
    }
}

@Composable
fun LibraryMenuItem(
    title: String,
    icon: ImageVector,
    iconSize: Dp = 30.dp,
    fontSize: TextUnit = 16.sp,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = Color(0xFFFA2D48),
            modifier = Modifier.size(iconSize)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = fontSize,
            fontWeight = FontWeight.Medium,
            color = textColor,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "›",
            fontSize = 20.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun SongListItem(
    song: Song,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    var bitmap by remember(song.trackId) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(song.trackId) {
        try {
            bitmap = withContext(Dispatchers.IO) {
                AlbumArtLoader.getAlbumArtBitmap(context, song.androidPath)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            bitmap?.let { loadedBitmap ->
                Image(
                    bitmap = loadedBitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } ?: run {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) Color(0xFFFA2D48) else MaterialTheme.colorScheme.onBackground,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
fun FloatingPlayerBar(
    song: Song,
    isPlaying: Boolean,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.95f),
        tonalElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFA2D48)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            IconButton(onClick = onPlayPauseClick) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }

            IconButton(onClick = onNextClick) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
