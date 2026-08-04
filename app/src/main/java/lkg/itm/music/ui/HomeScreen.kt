// "首頁"頁面
package lkg.itm.music.ui

import android.content.Context
import android.graphics.Bitmap
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.palette.graphics.Palette
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lkg.itm.music.MusicPlayerManager
import lkg.itm.music.MusicRepository
import lkg.itm.music.Song
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

@Composable
fun HomeScreen(
    songs: List<Song>,
    playerManager: MusicPlayerManager,
    verticalListState: LazyListState,     // 來自 MainScreen 的狀態
    horizontalListState: LazyListState,   // 來自 MainScreen 的狀態
    onSelectDirectory: () -> Unit,
    currentThemeMode: String,
    onThemeModeChanged: (String) -> Unit,
    onOpenSettings: () -> Unit,
    isSettingsOpen: Boolean,
    isOverlayOpen: Boolean,
    recommendationRefreshTrigger: Int = 0
) {
    val context = LocalContext.current
    val repository = remember { MusicRepository(context) }
    val isDark = when (currentThemeMode) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }
    val backgroundColor = if (isDark) Color.Black else Color(0xFFF5F5F5)
    val coroutineScope = rememberCoroutineScope()

    // 1️⃣ 處理返回鍵：如果在首頁且垂直滾動不在頂部，按返回鍵自動滾動回頂部
    BackHandler(enabled = !isOverlayOpen && !isSettingsOpen && (verticalListState.firstVisibleItemIndex > 0 || verticalListState.firstVisibleItemScrollOffset > 0)) {
        coroutineScope.launch {
            verticalListState.animateScrollToItem(0)
        }
    }

    var currentRefreshCount by remember { mutableIntStateOf(0) }
    val todayStr = remember { SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date()) }

    LaunchedEffect(todayStr, recommendationRefreshTrigger) {
        val prefs = context.getSharedPreferences("itm_music_prefs", Context.MODE_PRIVATE)
        val savedDate = prefs.getString("recommend_date", "")
        val savedRefreshCount = prefs.getInt("refresh_count", 0)

        if (savedDate != todayStr) {
            prefs.edit().putString("recommend_date", todayStr).putInt("refresh_count", 0).apply()
            currentRefreshCount = 0
        } else {
            currentRefreshCount = savedRefreshCount
        }
    }

    val dailyRecommendedSongs = remember(songs, todayStr, currentRefreshCount) {
        if (songs.isEmpty()) return@remember emptyList<Song>()
        val seed = (todayStr.toLong() * 100) + currentRefreshCount
        val shuffled = songs.shuffled(Random(seed))
        shuffled.take(6.coerceAtMost(songs.size))
    }

    var topPlayedSongs by remember { mutableStateOf<List<Song>>(emptyList()) }
    LaunchedEffect(songs) {
        withContext(Dispatchers.IO) {
            try {
                val topIds = repository.getTopPlayedTrackIds()
                val songMap = songs.associateBy { it.trackId }
                
                // 1. 取得熱門歌曲 (確保存在於目前的歌曲庫中)
                val hotSongs = topIds.mapNotNull { songMap[it] }
                
                // 2. 取得其餘歌曲 (過濾掉熱門歌曲)
                val otherSongs = songs.filter { it.trackId !in topIds }
                
                // 3. 合併：熱門在前，其餘在後，最多取 20 首
                topPlayedSongs = (hotSongs + otherSongs).take(20)
            } catch (e: Exception) {
                e.printStackTrace()
                topPlayedSongs = songs.take(20)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(65.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "首頁",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    fontSize = 34.sp,
                    // 邏輯修改：根據傳入的 currentThemeMode 判斷
                    // 如果是 system，則檢查系統主題；否則根據 light/dark 強制指定
                    color = when (currentThemeMode) {
                        "light" -> Color.Black
                        "dark" -> Color.White
                        else -> if (isSystemInDarkTheme()) Color.White else Color.Black
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

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = verticalListState,
                verticalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                item { Spacer(modifier = Modifier.height(6.dp)) }

                if (dailyRecommendedSongs.isNotEmpty()) {
                    item {
                        Column {
                            Text(
                                text = "今日推薦",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                fontSize = 23.sp,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            LazyRow(
                                state = horizontalListState,
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            ) {
                                items(
                                    items = dailyRecommendedSongs,
                                    key = { song -> song.trackId }
                                ) { song ->
                                    FlamingoBigCard(
                                        song = song,
                                        onClick = {
                                            playerManager.setPlaylist(
                                                dailyRecommendedSongs,
                                                dailyRecommendedSongs.indexOf(song)
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                if (topPlayedSongs.isNotEmpty()) {
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(1.dp),
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = "猜你想聽",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            topPlayedSongs.forEach { song ->
                                HomeSongItemForHome(
                                    song = song,
                                    onClick = {
                                        playerManager.setPlaylist(
                                            topPlayedSongs,
                                            topPlayedSongs.indexOf(song)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FlamingoBigCardPreview() {
    val sampleSong = Song(
        trackId = "1",
        title = "Sample Song",
        artist = "Sample Artist",
        album = "Sample Album",
        androidPath = ""
    )
    FlamingoBigCard(
        song = sampleSong,
        onClick = {}
    )
}

@Composable
fun FlamingoBigCard(
    song: Song,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    var extractedBackgroundColor by remember(song.trackId) { mutableStateOf(Color(0xFF2C2C2E)) }
    var bitmap by remember(song.trackId) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(song.trackId) {
        try {
            val artBitmap = lkg.itm.music.AlbumArtLoader.getAlbumArtBitmap(context, song.androidPath, 800, 800)
            if (artBitmap != null) {
                bitmap = artBitmap

                withContext(Dispatchers.Default) {
                    val palette = Palette.from(artBitmap).generate()
                    val swatches = listOfNotNull(
                        palette.darkVibrantSwatch,
                        palette.vibrantSwatch,
                        palette.darkMutedSwatch,
                        palette.mutedSwatch,
                        palette.dominantSwatch
                    )

                    val selectedColor = if (swatches.isNotEmpty()) {
                        val bestSwatch = swatches.maxByOrNull { swatch ->
                            val rgb = swatch.rgb
                            val red = android.graphics.Color.red(rgb)
                            val green = android.graphics.Color.green(rgb)
                            val blue = android.graphics.Color.blue(rgb)
                            val luminance = (0.299 * red + 0.587 * green + 0.114 * blue)
                            val darknessScore = if (luminance < 128) (128 - luminance) * 3 else -(luminance - 128)
                            kotlin.math.ln(swatch.population.toDouble()) * 100 + darknessScore
                        }
                        bestSwatch?.rgb ?: palette.getDominantColor(android.graphics.Color.parseColor("#2C2C2E"))
                    } else {
                        palette.getDominantColor(android.graphics.Color.parseColor("#2C2C2E"))
                    }

                    extractedBackgroundColor = Color(selectedColor)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    Column(
        modifier = Modifier
            .width(260.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .background(extractedBackgroundColor)
    ) {
        Box(
            modifier = Modifier
                .size(260.dp)
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            bitmap?.let { loadedBitmap ->
                Image(
                    bitmap = loadedBitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = 1.03f,
                            scaleY = 1.03f
                        ),
                    contentScale = ContentScale.Crop
                )
            } ?: run {
                Text("♪", color = Color.White, fontSize = 32.sp)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = song.album,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun HomeSongItemForHome(
    song: Song,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    var bitmap by remember(song.androidPath) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(song.androidPath) {
        bitmap = lkg.itm.music.AlbumArtLoader.getAlbumArtBitmap(context, song.androidPath)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } ?: Text("♪", fontSize = 20.sp, color = Color.White)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = song.artist,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                maxLines = 1
            )
        }
        Text("›", fontSize = 20.sp, color = Color.Gray)
    }
}
