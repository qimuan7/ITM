package lkg.itm.music.ui

import android.graphics.Bitmap
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeDown
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import lkg.itm.music.R
import lkg.itm.music.Song
import lkg.itm.music.AlbumArtLoader

/**
 * 輔助函數：線性插值
 */
private fun lerp(start: Float, stop: Float, fraction: Float): Float =
    (1 - fraction) * start + fraction * stop

/**
 * 1. 狀態管理與數據結構
 */
enum class PlayerMode {
    DEFAULT, LYRICS, QUEUE
}

data class MusicState(
    val songTitle: String = "Unknown Title",
    val artistName: String = "Unknown Artist",
    val artwork: Any? = null,
    val isPlaying: Boolean = false,
    val progress: Float = 0f,
    val currentPositionText: String = "0:00",
    val durationText: String = "0:00",
    val queue: List<Song> = emptyList(),
    val currentSongId: String? = null,
    val volume: Int = 100,
    val albumName: String = "Unknown Album",
    val isShuffleEnabled: Boolean = false,
    val repeatMode: Int = 0,
    val lyrics: String? = null
)

@Composable
fun AlbumArtImage(
    artwork: Any?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    reqWidth: Int = 200,
    reqHeight: Int = 200
) {
    val context = LocalContext.current
    var bitmap by remember(artwork, reqWidth, reqHeight) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(artwork, reqWidth, reqHeight) {
        if (artwork is String && artwork.isNotEmpty()) {
            bitmap = AlbumArtLoader.getAlbumArtBitmap(context, artwork, reqWidth, reqHeight)
        } else if (artwork is Bitmap) {
            bitmap = artwork
        }
    }

    AsyncImage(
        model = bitmap ?: artwork,
        contentDescription = null,
        contentScale = contentScale,
        modifier = modifier
    )
}

@Composable
fun PlayerOverlayScreen(
    musicState: MusicState,
    isExpanded: Boolean,
    onExpandToggle: (Boolean) -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onSeek: (Float) -> Unit,
    onSongClick: (Song) -> Unit = {},
    onVolumeChange: (Int) -> Unit = {},
    onShuffleToggle: (Boolean) -> Unit = {},
    onRepeatModeChange: (Int) -> Unit = {}
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val screenHeightPx = constraints.maxHeight.toFloat()
        val screenWidthPx = constraints.maxWidth.toFloat()
        val scope = rememberCoroutineScope()
        val density = LocalDensity.current

        val transitionProgress = remember { Animatable(if (isExpanded) 1f else 0f) }

        LaunchedEffect(isExpanded) {
            transitionProgress.animateTo(
                if (isExpanded) 1f else 0f,
                animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMediumLow)
            )
        }

        if (transitionProgress.value < 0.99f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
                    .padding(bottom = 24.dp), // 移除水平 padding，讓 MiniPlayer 獨立控制
                contentAlignment = Alignment.BottomCenter
            ) {
                MiniPlayer(
                    state = musicState,
                    onClick = { onExpandToggle(true) },
                    onPlayPauseClick = onPlayPauseClick,
                    onNextClick = onNextClick,
                    onPreviousClick = onPreviousClick,
                    onShuffleToggle = onShuffleToggle,
                    onRepeatModeChange = onRepeatModeChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 288.dp, end = 24.dp) // 保持所需的左右間距
                        .pointerInput(Unit) {
                            detectVerticalDragGestures(
                                onDragEnd = {
                                    scope.launch {
                                        if (transitionProgress.value > 0.3f) onExpandToggle(true)
                                        else transitionProgress.animateTo(0f, spring(stiffness = Spring.StiffnessMediumLow))
                                    }
                                },
                                onVerticalDrag = { _, dragAmount ->
                                    val delta = -dragAmount / screenHeightPx
                                    scope.launch { transitionProgress.snapTo((transitionProgress.value + delta).coerceIn(0f, 1f)) }
                                }
                            )
                        }
                )
            }
        }

        if (transitionProgress.value > 0.001f) {
            val progress = transitionProgress.value.coerceIn(0f, 1f)
            val collapseGestureModifier = Modifier.pointerInput(Unit) {
                detectVerticalDragGestures(
                    onDragEnd = {
                        scope.launch {
                            if (transitionProgress.value < 0.7f) onExpandToggle(false)
                            else transitionProgress.animateTo(1f, spring(stiffness = Spring.StiffnessMediumLow))
                        }
                    },
                    onVerticalDrag = { change, dragAmount ->
                        if (dragAmount != 0f) {
                            val delta = -dragAmount / screenHeightPx
                            scope.launch { transitionProgress.snapTo((transitionProgress.value + delta).coerceIn(0f, 1.05f)) }
                            change.consume()
                        }
                    }
                )
            }

            val miniPaddingEndPx = with(density) { 24.dp.toPx() }
            val miniPaddingStartPx = with(density) { 288.dp.toPx() } // 更新為 288dp
            val miniPaddingBottomPx = with(density) { 24.dp.toPx() }
            val navBottomPaddingPx = with(density) { WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding().toPx() }
            val navEndPaddingPx = with(density) { WindowInsets.navigationBars.asPaddingValues().calculateEndPadding(androidx.compose.ui.platform.LocalLayoutDirection.current).toPx() }
            val miniHeight = with(density) { 58.dp.toPx() }

            val miniLeft = miniPaddingStartPx
            val miniTop = screenHeightPx - miniPaddingBottomPx - navBottomPaddingPx - miniHeight
            val miniRight = screenWidthPx - miniPaddingEndPx - navEndPaddingPx
            val miniBottom = screenHeightPx - miniPaddingBottomPx - navBottomPaddingPx
            val currentLeft = lerp(miniLeft, 0f, progress)
            val currentTop = lerp(miniTop, 0f, progress)
            val currentRight = lerp(miniRight, screenWidthPx, progress)
            val currentBottom = lerp(miniBottom, screenHeightPx, progress)
            val currentWidth = (currentRight - currentLeft).coerceAtLeast(0f)
            val currentHeight = (currentBottom - currentTop).coerceAtLeast(0f)

            Box(
                modifier = Modifier
                    .offset { IntOffset(currentLeft.toInt(), currentTop.toInt()) }
                    .size(with(density) { currentWidth.toDp() }, with(density) { currentHeight.toDp() })
                    .shadow(
                        elevation = lerp(12f, 0f, (1f - progress).coerceIn(0f, 1f)).dp,
                        shape = RoundedCornerShape(lerp(16f, 0f, progress).coerceAtLeast(0f).dp)
                    )
                    .clip(RoundedCornerShape(lerp(16f, 0f, progress).coerceAtLeast(0f).dp))
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Box(
                    modifier = Modifier.requiredSize(with(density) { screenWidthPx.toDp() }, with(density) { screenHeightPx.toDp() })
                ) {
                    FullScreenPlayer(
                        state = musicState,
                        dragProgress = progress,
                        collapseModifier = collapseGestureModifier,
                        onCollapse = { onExpandToggle(false) },
                        onPlayPauseClick = onPlayPauseClick,
                        onNextClick = onNextClick,
                        onPreviousClick = onPreviousClick,
                        onSeek = onSeek,
                        onSongClick = onSongClick,
                        onVolumeChange = onVolumeChange,
                        onShuffleToggle = onShuffleToggle,
                        onRepeatModeChange = onRepeatModeChange
                    )
                }
            }
        }
    }
}

@Composable
fun MiniPlayer(
    state: MusicState,
    onClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onShuffleToggle: (Boolean) -> Unit,
    onRepeatModeChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val outerCornerRadius = 12.dp
    val innerCornerRadius = 8.dp
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    Surface(
        modifier = modifier
            .height(58.dp)
            .shadow(12.dp, RoundedCornerShape(outerCornerRadius))
            .clip(RoundedCornerShape(outerCornerRadius))
            .clickable { onClick() },
        color = if (isDark) Color(0xFF1C1C1E).copy(alpha = 0.9f) else Color(0xFFFFFFFF).copy(alpha = 0.9f),
        tonalElevation = 8.dp
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(start = 6.dp, end = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(46.dp).clip(RoundedCornerShape(innerCornerRadius)), color = Color.LightGray) {
                AlbumArtImage(artwork = state.artwork, modifier = Modifier.fillMaxSize())
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = state.songTitle,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = state.artistName,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 隨機播放
            IconButton(onClick = { onShuffleToggle(!state.isShuffleEnabled) }, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Rounded.Shuffle, null, tint = if (state.isShuffleEnabled) (if (isDark) Color.White else Color.Black) else Color.Gray.copy(alpha = 0.7f), modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            // 上一首
            IconButton(onClick = onPreviousClick, modifier = Modifier.size(52.dp)) {
                Icon(Icons.Rounded.SkipPrevious, null, modifier = Modifier.size(40.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            // 暫停播放
            IconButton(onClick = onPlayPauseClick, modifier = Modifier.size(52.dp)) {
                Icon(imageVector = if (state.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(46.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            // 下一首
            IconButton(onClick = onNextClick, modifier = Modifier.size(52.dp)) {
                Icon(Icons.Rounded.SkipNext, null, modifier = Modifier.size(40.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            // 循環或單曲播放
            IconButton(onClick = { onRepeatModeChange((state.repeatMode + 1) % 3) }, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = when(state.repeatMode) {
                        1 -> Icons.Rounded.Repeat
                        2 -> Icons.Rounded.RepeatOne
                        else -> Icons.Rounded.Repeat
                    },
                    contentDescription = stringResource(id = R.string.repeat),
                    tint = if (state.repeatMode != 0) (if (isDark) Color.White else Color.Black) else Color.Gray.copy(alpha = 0.7f),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
        }
    }
}

@Composable
fun ControlPanel(
    state: MusicState,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onShuffleToggle: (Boolean) -> Unit,
    onRepeatModeChange: (Int) -> Unit,
    isLandscape: Boolean = false,
    modifier: Modifier = Modifier,
    scale: Float = 1f
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isLandscape) Arrangement.SpaceBetween else Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isLandscape) {
            IconButton(onClick = { onShuffleToggle(!state.isShuffleEnabled) }, modifier = Modifier.size(48.dp)) {
                Icon(Icons.Rounded.Shuffle, null, tint = if (state.isShuffleEnabled) Color(0xFFFFFFFF) else Color.White.copy(alpha = 0.6f))
            }
        }

        IconButton(onClick = onPreviousClick, modifier = Modifier.size((64 * scale).dp)) {
            Icon(Icons.Rounded.SkipPrevious, null, modifier = Modifier.size((54 * scale).dp), tint = Color.White)
        }
        IconButton(onClick = onPlayPauseClick, modifier = Modifier.size((80 * scale).dp)) {
            Icon(
                imageVector = if (state.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size((70 * scale).dp),
                tint = Color.White
            )
        }
        IconButton(onClick = onNextClick, modifier = Modifier.size((64 * scale).dp)) {
            Icon(Icons.Rounded.SkipNext, null, modifier = Modifier.size((54 * scale).dp), tint = Color.White)
        }

        if (isLandscape) {
            IconButton(onClick = { onRepeatModeChange((state.repeatMode + 1) % 3) }, modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = when(state.repeatMode) {
                        1 -> Icons.Rounded.Repeat
                        2 -> Icons.Rounded.RepeatOne
                        else -> Icons.Rounded.Repeat
                    },
                    contentDescription = stringResource(id = R.string.repeat),
                    tint = if (state.repeatMode != 0) Color(0xFFFFFFFF) else Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun FullScreenPlayer(
    state: MusicState,
    dragProgress: Float,
    collapseModifier: Modifier,
    onCollapse: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit,
    onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier,
    onSongClick: (Song) -> Unit = {},
    onVolumeChange: (Int) -> Unit = {},
    onShuffleToggle: (Boolean) -> Unit = {},
    onRepeatModeChange: (Int) -> Unit = {}
) {
    var currentMode by remember { mutableStateOf(PlayerMode.DEFAULT) }
    var showVolumePanel by remember { mutableStateOf(false) }
    var appVolume by remember { mutableIntStateOf(state.volume) }
    val haptic = LocalHapticFeedback.current
    val scale = 1.5f

    LaunchedEffect(state.volume) { appVolume = state.volume }
    LaunchedEffect(appVolume) { onVolumeChange(appVolume) }

    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    val animatedScale by animateFloatAsState(
        targetValue = if (state.isPlaying) 1f else 0.85f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessLow),
        label = "AlbumArtScale"
    )
    val finalScale = lerp(0.7f, 1f, dragProgress) * animatedScale

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = dragProgress))
            .then(collapseModifier)
    ) {
        // 毛玻璃背景
        Box(modifier = Modifier.fillMaxSize().graphicsLayer { alpha = dragProgress }) {
            AlbumArtImage(
                artwork = state.artwork,
                modifier = Modifier.fillMaxSize().blur(50.dp).graphicsLayer { alpha = 0.4f },
                reqWidth = 100,
                reqHeight = 100
            )
            Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.6f)))))
        }

        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .displayCutoutPadding()
                    .padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 左側區域：封面、標題、控制項 (對齊紅框，向右偏移 150dp)
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(start = 150.dp) // 向右偏移
                        .width(400.dp)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    // 2. 封面
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxSize(finalScale)
                                .shadow(elevation = 20.dp * finalScale, shape = RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            color = Color.DarkGray
                        ) {
                            AlbumArtImage(artwork = state.artwork, modifier = Modifier.fillMaxSize())
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 3. 歌名藝人
                    Text(state.songTitle, style = MaterialTheme.typography.headlineMedium, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(state.artistName, style = MaterialTheme.typography.titleLarge, color = Color.White.copy(alpha = 0.6f), maxLines = 1, overflow = TextOverflow.Ellipsis)

                    Spacer(modifier = Modifier.height(24.dp))

                    // 4. 進度條 與 時間顯示
                    var isDragging by remember { mutableStateOf(false) }
                    var dragProgress by remember { mutableFloatStateOf(0f) }
                    LaunchedEffect(state.progress, isDragging) { if (!isDragging) dragProgress = state.progress }
                    
                    fun formatTime(progress: Float): String {
                        val durationParts = state.durationText.split(":")
                        val totalSeconds = if (durationParts.size >= 2) {
                            val m = durationParts[0].replace("-", "").toInt()
                            val s = durationParts[1].toInt()
                            m * 60 + s
                        } else 0
                        val currentSeconds = (progress * totalSeconds).toInt()
                        return String.format(java.util.Locale.getDefault(), "%d:%02d", currentSeconds / 60, currentSeconds % 60)
                    }

                    BoxWithConstraints(modifier = Modifier.fillMaxWidth().height(24.dp)) {
                        val maxWidthPx = with(LocalDensity.current) { this@BoxWithConstraints.maxWidth.toPx() }
                        Box(modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.3f))
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { isDragging = true },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        val delta = dragAmount.x / maxWidthPx
                                        dragProgress = (dragProgress + delta).coerceIn(0f, 1f)
                                    },
                                    onDragEnd = { onSeek(dragProgress); isDragging = false },
                                    onDragCancel = { isDragging = false }
                                )
                            })
                        Box(modifier = Modifier.fillMaxWidth(dragProgress.coerceIn(0f, 1f)).height(6.dp).clip(CircleShape).background(Color.White))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(formatTime(dragProgress), style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.6f))
                        Text(state.durationText, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.6f))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 5. 播放按鍵
                    ControlPanel(state, onPlayPauseClick, onNextClick, onPreviousClick, onShuffleToggle, onRepeatModeChange, isLandscape = true)
                }

                // 右側區域：歌詞/列表容器 (紅框) + 控制按鈕列 (黃框)
                Box(modifier = Modifier.weight(1f).fillMaxHeight().padding(start = 50.dp)) {
                    // 內容容器 (保持邊距，背景透明/無模糊)
                    Box(modifier = Modifier.fillMaxSize().padding(top = 44.dp, end = 80.dp)) { // 添加 end padding 避開功能鍵
                        if (currentMode != PlayerMode.DEFAULT) {
                            // 使用 Box 包裹以限制內容，並根據模式調整縮放
                            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                                if (currentMode == PlayerMode.LYRICS) {
                                    LyricsView(state.lyrics, isLandscape = true) // 橫屏原大小
                                } else {
                                    QueueView(state, onSongClick, isLandscape = true) // 橫屏原大小
                                }
                            }
                        }
                    }

                    // 音量面板 (橫屏) - 使用 Box 的 Alignment 定位以覆蓋在上方
                    androidx.compose.animation.AnimatedVisibility(
                        visible = showVolumePanel,
                        enter = fadeIn(tween(250)) + slideInHorizontally { it },
                        exit = fadeOut(tween(200)) + slideOutHorizontally { it },
                        modifier = Modifier
                            .padding(end = 60.dp, top = 20.dp) // 避開右側功能按鈕列，並下移20.dp
                            .align(Alignment.CenterEnd)
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(56.dp, 300.dp)
                                .shadow(20.dp, RoundedCornerShape(28.dp)),
                            shape = RoundedCornerShape(28.dp),
                            color = if (isDark) Color(0xFF1C1C1E).copy(alpha = 0.98f) else Color(0xFFF2F2F7).copy(alpha = 0.98f),
                            border = BorderStroke(0.5.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.12f))
                        ) {
                            var dragAccumulator by remember { mutableFloatStateOf(appVolume.toFloat()) }
                            Column(
                                modifier = Modifier.padding(vertical = 20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.VolumeUp,
                                    contentDescription = null,
                                    tint = if (isDark) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.5f),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                BoxWithConstraints(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(vertical = 16.dp)
                                        .width(48.dp),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    val trackHeightPx = constraints.maxHeight.toFloat()
                                    Box(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .width(4.dp)
                                            .clip(CircleShape)
                                            .background(if (isDark) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.15f))
                                            .pointerInput(Unit) {
                                                detectDragGestures(
                                                    onDragStart = { dragAccumulator = appVolume.toFloat() },
                                                    onDrag = { change, dragAmount ->
                                                        change.consume()
                                                        val deltaPercent = (-dragAmount.y / trackHeightPx) * 100f
                                                        dragAccumulator = (dragAccumulator + deltaPercent).coerceIn(0f, 100f)
                                                        val newVol = dragAccumulator.toInt()
                                                        if (newVol != appVolume) {
                                                            if (newVol % 5 == 0 || newVol == 0 || newVol == 100) {
                                                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                            }
                                                            appVolume = newVol
                                                        }
                                                    }
                                                )
                                            }
                                    )
                                    val trackHeight = maxHeight
                                    Box(
                                        modifier = Modifier
                                            .height(trackHeight * (appVolume / 100f))
                                            .width(4.dp)
                                            .clip(CircleShape)
                                            .background(if (isDark) Color.White else Color.Black)
                                    )
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.VolumeDown,
                                    contentDescription = null,
                                    tint = if (isDark) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.5f),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    // 功能控制列 (黃框區)
                    Column(
                        modifier = Modifier.width(60.dp).fillMaxHeight().align(Alignment.CenterEnd).padding(top = 20.dp), // 向下移動 6.dp
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(onClick = { currentMode = if (currentMode == PlayerMode.LYRICS) PlayerMode.DEFAULT else PlayerMode.LYRICS }) {
                            Icon(Icons.Rounded.Article, null, tint = if (currentMode == PlayerMode.LYRICS) Color.White else Color.White.copy(alpha = 0.6f))
                        }
                        IconButton(onClick = { showVolumePanel = !showVolumePanel }) {
                            Icon(imageVector = if (appVolume >= 51) Icons.AutoMirrored.Outlined.VolumeUp else Icons.AutoMirrored.Outlined.VolumeDown, contentDescription = null, tint = if (showVolumePanel) Color.White else Color.White.copy(alpha = 0.6f))
                        }
                        IconButton(onClick = { currentMode = if (currentMode == PlayerMode.QUEUE) PlayerMode.DEFAULT else PlayerMode.QUEUE }) {
                            Icon(Icons.AutoMirrored.Rounded.QueueMusic, null, tint = if (currentMode == PlayerMode.QUEUE) Color.White else Color.White.copy(alpha = 0.6f))
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 將整體豎屏內容縮小到 80%，並置中
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .graphicsLayer(
                            scaleX = 0.8f,
                            scaleY = 0.8f
                        )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            AnimatedContent(
                                targetState = currentMode,
                                transitionSpec = {
                                    (fadeIn(tween(400)) + scaleIn(initialScale = 0.92f))
                                        .togetherWith(fadeOut(tween(300)))
                                },
                                label = "PlayerModeTransition"
                            ) { mode ->
                                when (mode) {
                                    PlayerMode.DEFAULT -> {
                                        Column(
                                            modifier = Modifier.fillMaxSize(),
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .weight(1f)
                                                    .padding(top = 14.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Surface(
                                                    modifier = Modifier
                                                        .fillMaxWidth(if (state.isPlaying) 0.95f else 0.9f)
                                                        .aspectRatio(1f)
                                                        .graphicsLayer {
                                                            scaleX = finalScale
                                                            scaleY = finalScale
                                                            alpha = dragProgress
                                                        }
                                                        .shadow(elevation = (20 * dragProgress).dp, shape = RoundedCornerShape(16.dp)),
                                                    shape = RoundedCornerShape(16.dp),
                                                    color = Color.DarkGray
                                                ) {
                                                    AlbumArtImage(
                                                        artwork = state.artwork,
                                                        modifier = Modifier.fillMaxSize(),
                                                        reqWidth = 900,
                                                        reqHeight = 900
                                                    )
                                                }
                                            }

                                            // 移除 Spacer 以達到向上貼近效果
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .offset(y = (-20 * scale).dp) // 使用偏移量移動，不直接影響佈局佔位
                                                    .padding(start = 12.dp, end = 24.dp)
                                            ) {
                                                Column(
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    Text(state.songTitle, style = MaterialTheme.typography.headlineSmall.copy(fontSize = (24 * scale).sp), fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                    Text(state.artistName, style = MaterialTheme.typography.titleMedium.copy(fontSize = (16 * scale).sp), color = Color.White.copy(alpha = 0.6f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                }
                                            }
                                        }
                                    }
                                    else -> {
                                        Column(modifier = Modifier.fillMaxSize()) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 16.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Surface(
                                                    modifier = Modifier.size((56 * scale).dp).shadow(4.dp, RoundedCornerShape(8.dp)),
                                                    shape = RoundedCornerShape(8.dp),
                                                    color = Color.DarkGray
                                                ) {
                                                    AlbumArtImage(
                                                        artwork = state.artwork,
                                                        modifier = Modifier.fillMaxSize(),
                                                        reqWidth = 900,
                                                        reqHeight = 900
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(16.dp))
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(state.songTitle, style = MaterialTheme.typography.bodyLarge.copy(fontSize = (16 * scale).sp), fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                    Text(state.artistName, style = MaterialTheme.typography.bodySmall.copy(fontSize = (14 * scale).sp), color = Color.White.copy(alpha = 0.6f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                }
                                                IconButton(onClick = { onShuffleToggle(!state.isShuffleEnabled) }, modifier = Modifier.size((40 * scale).dp)) {
                                                    Icon(
                                                        imageVector = Icons.Rounded.Shuffle,
                                                        contentDescription = "Shuffle",
                                                        tint = if (state.isShuffleEnabled) Color(0xFFFA2D48) else Color.White.copy(alpha = 0.6f),
                                                        modifier = Modifier.size((20 * scale).dp)
                                                    )
                                                }
                                                IconButton(onClick = { onRepeatModeChange((state.repeatMode + 1) % 3) }, modifier = Modifier.size((40 * scale).dp)) {
                                                    Icon(
                                                        imageVector = when(state.repeatMode) {
                                                            1 -> Icons.Rounded.Repeat // Repeat All
                                                            2 -> Icons.Rounded.RepeatOne // Repeat One
                                                            else -> Icons.Rounded.Repeat // Off
                                                        },
                                                        contentDescription = stringResource(id = R.string.repeat),
                                                        tint = if (state.repeatMode != 0) Color(0xFFFA2D48) else Color.White.copy(alpha = 0.6f),
                                                        modifier = Modifier.size((20 * scale).dp)
                                                    )
                                                }
                                            }
                                            Box(modifier = Modifier.weight(1f)) {
                                                if (mode == PlayerMode.LYRICS) {
                                                    LyricsView(state.lyrics)
                                                } else {
                                                    QueueView(state, onSongClick)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 32.dp)
                                .graphicsLayer { alpha = dragProgress },
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                var isDragging by remember { mutableStateOf(false) }
                                var dragProgress by remember { mutableFloatStateOf(0f) }

                                LaunchedEffect(state.progress, isDragging) {
                                    if (!isDragging) {
                                        dragProgress = state.progress
                                    }
                                }

                                fun formatTime(progress: Float): String {
                                    val durationParts = state.durationText.split(":")
                                    val totalSeconds = if (durationParts.size >= 2) {
                                        val m = durationParts[0].replace("-", "").toInt()
                                        val s = durationParts[1].toInt()
                                        m * 60 + s
                                    } else 0

                                    val currentSeconds = (progress * totalSeconds).toInt()
                                    val minutes = currentSeconds / 60
                                    val seconds = currentSeconds % 60
                                    return String.format(java.util.Locale.getDefault(), "%d:%02d", minutes, seconds)
                                }

                                BoxWithConstraints(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp)
                                        .height((24 * scale).dp)
                                ) {
                                    val maxWidth = this.maxWidth
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height((24 * scale).dp)
                                            .pointerInput(Unit) {
                                                detectDragGestures(
                                                    onDragStart = { isDragging = true },
                                                    onDrag = { change, dragAmount ->
                                                        change.consume()
                                                        val deltaPercent = dragAmount.x / maxWidth.toPx()
                                                        dragProgress = (dragProgress + deltaPercent).coerceIn(0f, 1f)
                                                    },
                                                    onDragEnd = {
                                                        onSeek(dragProgress)
                                                        isDragging = false
                                                    },
                                                    onDragCancel = { isDragging = false }
                                                )
                                            },
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height((6 * scale).dp)
                                                .clip(CircleShape)
                                                .background(Color.White.copy(alpha = 0.3f))
                                        )
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(dragProgress.coerceIn(0f, 1f))
                                                .height((6 * scale).dp)
                                                .clip(CircleShape)
                                                .background(Color.White)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height((12 * scale).dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (isDragging) formatTime(dragProgress) else state.currentPositionText,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = (12 * scale).sp),
                                        color = if (isDragging) Color.White else Color.White.copy(alpha = 0.6f)
                                    )
                                    Text(
                                        text = state.durationText,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = (12 * scale).sp),
                                        color = Color.White.copy(alpha = 0.6f)
                                    )
                                }
                                Spacer(modifier = Modifier.height((24 * scale).dp))
                                ControlPanel(state, onPlayPauseClick, onNextClick, onPreviousClick, onShuffleToggle, onRepeatModeChange, isLandscape = false, scale = scale)
                                Spacer(modifier = Modifier.height((30 * scale).dp))
                                HorizontalDivider(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                                    thickness = (0.5 * scale).dp,
                                    color = Color.White.copy(alpha = 0.2f)
                                )
                                Spacer(modifier = Modifier.height((20 * scale).dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth().height((60 * scale).dp),
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(onClick = {
                                        currentMode = if (currentMode == PlayerMode.LYRICS) PlayerMode.DEFAULT else PlayerMode.LYRICS
                                        if (currentMode != PlayerMode.DEFAULT) showVolumePanel = false
                                    }, modifier = Modifier.size((40 * scale).dp)) {
                                        Icon(
                                            imageVector = Icons.Rounded.Article,
                                            contentDescription = null,
                                            modifier = Modifier.size((26 * scale).dp),
                                            tint = if (currentMode == PlayerMode.LYRICS) Color.White else Color.White.copy(alpha = 0.5f)
                                        )
                                    }
                                    IconButton(onClick = { showVolumePanel = !showVolumePanel }, modifier = Modifier.size((40 * scale).dp)) {
                                        Icon(
                                            imageVector = if (appVolume >= 51) Icons.AutoMirrored.Outlined.VolumeUp else Icons.AutoMirrored.Outlined.VolumeDown,
                                            contentDescription = null,
                                            modifier = Modifier.size((28 * scale).dp),
                                            tint = if (showVolumePanel) Color.White else Color.White.copy(alpha = 0.5f)
                                        )
                                    }
                                    IconButton(onClick = {
                                        currentMode = if (currentMode == PlayerMode.QUEUE) PlayerMode.DEFAULT else PlayerMode.QUEUE
                                        if (currentMode != PlayerMode.DEFAULT) showVolumePanel = false
                                    }, modifier = Modifier.size((40 * scale).dp)) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Rounded.QueueMusic,
                                            contentDescription = null,
                                            modifier = Modifier.size((28 * scale).dp),
                                            tint = if (currentMode == PlayerMode.QUEUE) Color.White else Color.White.copy(alpha = 0.5f)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height((26 * scale).dp))
                            }
                            androidx.compose.animation.AnimatedVisibility(
                                visible = showVolumePanel,
                                enter = fadeIn(tween(250)) + scaleIn(initialScale = 0.95f, animationSpec = spring(stiffness = Spring.StiffnessLow)),
                                exit = fadeOut(tween(200)) + scaleOut(targetScale = 0.95f),
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .offset(y = (-80 * scale).dp)
                                    .padding(horizontal = 16.dp)
                            ) {
                                var dragAccumulator by remember { mutableFloatStateOf(appVolume.toFloat()) }
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height((56 * scale).dp)
                                        .shadow(30.dp, RoundedCornerShape(28.dp)),
                                    shape = RoundedCornerShape(28.dp),
                                    color = if (isDark) Color(0xFF1C1C1E).copy(alpha = 0.98f) else Color(0xFFF2F2F7).copy(alpha = 0.98f),
                                    border = androidx.compose.foundation.BorderStroke(0.5.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.12f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = (20 * scale).dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Outlined.VolumeDown,
                                            contentDescription = null,
                                            tint = if (isDark) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.5f),
                                            modifier = Modifier.size((16 * scale).dp)
                                        )
                                        BoxWithConstraints(
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(horizontal = (16 * scale).dp)
                                                .height((48 * scale).dp),
                                            contentAlignment = Alignment.CenterStart
                                        ) {
                                            val trackWidthPx = constraints.maxWidth.toFloat()
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height((4 * scale).dp)
                                                    .clip(CircleShape)
                                                    .background(if (isDark) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.15f))
                                                    .pointerInput(Unit) {
                                                        detectDragGestures(
                                                            onDragStart = { dragAccumulator = appVolume.toFloat() },
                                                            onDrag = { change, dragAmount ->
                                                                change.consume()
                                                                val deltaPercent = (dragAmount.x / trackWidthPx) * 100f
                                                                dragAccumulator = (dragAccumulator + deltaPercent).coerceIn(0f, 100f)
                                                                val newVol = dragAccumulator.toInt()
                                                                if (newVol != appVolume) {
                                                                    if (newVol % 5 == 0 || newVol == 0 || newVol == 100) {
                                                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                                                    }
                                                                    appVolume = newVol
                                                                }
                                                            }
                                                        )
                                                    }
                                            )
                                            val trackWidth = maxWidth
                                            Box(
                                                modifier = Modifier
                                                    .width(trackWidth * (appVolume / 100f))
                                                    .height((4 * scale).dp)
                                                    .clip(CircleShape)
                                                    .background(if (isDark) Color.White else Color.Black)
                                            )
                                        }
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Outlined.VolumeUp,
                                            contentDescription = null,
                                            tint = if (isDark) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.5f),
                                            modifier = Modifier.size((16 * scale).dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LyricsView(lyrics: String?, isLandscape: Boolean = false) {
    Box(
        modifier = Modifier.fillMaxSize().padding(0.dp),
        contentAlignment = if (lyrics.isNullOrBlank()) Alignment.Center else Alignment.TopStart
    ) {
        if (lyrics.isNullOrBlank()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.lyrics_not_ready),
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(id = R.string.lyrics_not_available),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            val scrollState = rememberScrollState()
            val fontSize = if (isLandscape) 26.sp else (26 * 1.5).sp
            val lineHeight = if (isLandscape) 30.sp else (30 * 1.5).sp
            Text(
                text = lyrics.replace("\r\n", "\n").replace("\r", "\n"),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = fontSize,
                    lineHeight = lineHeight
                ),
                color = Color.White,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(start = 10.dp, bottom = 25.dp)
            )
        }
    }
}

@Composable
fun QueueView(state: MusicState, onSongClick: (Song) -> Unit, isLandscape: Boolean = false) {
    val scale = if (isLandscape) 1f else 1.5f
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(0.dp),
        contentPadding = PaddingValues(bottom = 20.dp),
        state = rememberLazyListState()
    ) {
        itemsIndexed(state.queue) { index, song ->
            val isCurrent = song.trackId == state.currentSongId
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isCurrent) Color.White.copy(alpha = 0.1f) else Color.Transparent)
                    .clickable { onSongClick(song) }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size((50 * scale).dp),
                    shape = RoundedCornerShape(4.dp),
                    color = Color.DarkGray
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        AlbumArtImage(artwork = song.androidPath, modifier = Modifier.fillMaxSize())
                        if (isCurrent && state.isPlaying) {
                            Box(
                                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.Equalizer, null, tint = Color.White)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.width((18 * scale).dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.bodyLarge.copy(fontSize = (18 * scale).sp),
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                        color = if (isCurrent) Color(0xFFFA2D48) else Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = song.artist,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = (16 * scale).sp),
                        color = Color.White.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (isCurrent) {
                    Icon(Icons.AutoMirrored.Outlined.VolumeUp, null, tint = Color(0xFFFA2D48), modifier = Modifier.size((24 * scale).dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlayerPreviewLight() {
    MaterialTheme(colorScheme = lightColorScheme()) {
        PlayerOverlayScreen(
            musicState = MusicState(
                songTitle = "17さい的うた",
                artistName = "Yuika",
                isPlaying = true,
                progress = 0.3f,
                currentPositionText = "0:02",
                durationText = "-5:07"
            ),
            isExpanded = true,
            onExpandToggle = {},
            onPlayPauseClick = {},
            onNextClick = {},
            onPreviousClick = {},
            onSeek = {}
        )
    }
}
