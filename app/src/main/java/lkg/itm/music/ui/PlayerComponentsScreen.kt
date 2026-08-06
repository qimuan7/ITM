package lkg.itm.music.ui

import android.graphics.Bitmap
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
                    .graphicsLayer { alpha = (1f - transitionProgress.value * 4f).coerceIn(0f, 1f) }
                    .padding(bottom = 105.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                MiniPlayer(
                    state = musicState,
                    onClick = { onExpandToggle(true) },
                    onPlayPauseClick = onPlayPauseClick,
                    onNextClick = onNextClick,
                    modifier = Modifier.pointerInput(Unit) {
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

            val miniHeight = with(density) { 58.dp.toPx() }
            val miniPaddingH = with(density) { 12.dp.toPx() }
            val miniPaddingV = with(density) { 8.dp.toPx() }
            val miniBottomMargin = with(density) { 105.dp.toPx() }
            val miniLeft = miniPaddingH
            val miniRight = screenWidthPx - miniPaddingH
            val miniBottom = screenHeightPx - miniBottomMargin - miniPaddingV
            val miniTop = miniBottom - miniHeight
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
fun MiniPlayer(state: MusicState, onClick: () -> Unit, onPlayPauseClick: () -> Unit, onNextClick: () -> Unit, modifier: Modifier = Modifier) {
    val outerCornerRadius = 12.dp
    val innerCornerRadius = 8.dp
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
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
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = state.songTitle, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = state.artistName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            IconButton(onClick = onPlayPauseClick) {
                Icon(imageVector = if (state.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, contentDescription = null, modifier = Modifier.size(40.dp))
            }
            IconButton(onClick = onNextClick) {
                Icon(imageVector = Icons.Rounded.SkipNext, contentDescription = null, modifier = Modifier.size(40.dp))
            }
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
    modifier: Modifier = Modifier
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

        IconButton(onClick = onPreviousClick, modifier = Modifier.size(64.dp)) {
            Icon(Icons.Rounded.SkipPrevious, null, modifier = Modifier.size(54.dp), tint = Color.White)
        }
        IconButton(onClick = onPlayPauseClick, modifier = Modifier.size(80.dp)) {
            Icon(
                imageVector = if (state.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(70.dp),
                tint = Color.White
            )
        }
        IconButton(onClick = onNextClick, modifier = Modifier.size(64.dp)) {
            Icon(Icons.Rounded.SkipNext, null, modifier = Modifier.size(54.dp), tint = Color.White)
        }

        if (isLandscape) {
            IconButton(onClick = { onRepeatModeChange((state.repeatMode + 1) % 3) }, modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = when(state.repeatMode) {
                        1 -> Icons.Rounded.Repeat
                        2 -> Icons.Rounded.RepeatOne
                        else -> Icons.Rounded.Repeat
                    },
                    contentDescription = "Repeat",
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
                // 左側封面與內容層疊區
                Surface(
                    modifier = Modifier
                        .weight(0.9f)
                        .aspectRatio(1f)
                        .shadow(20.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.DarkGray
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AlbumArtImage(artwork = state.artwork, modifier = Modifier.fillMaxSize())

                        // 層疊顯示歌詞或列表
                        AnimatedContent(
                            targetState = currentMode,
                            transitionSpec = { fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut() },
                            label = "LayerContent"
                        ) { mode ->
                            if (mode != PlayerMode.DEFAULT) {
                                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f)).padding(16.dp)) {
                                    if (mode == PlayerMode.LYRICS) LyricsView(state.lyrics)
                                    else QueueView(state, onSongClick)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(32.dp))

                // 右側內容：佔據 1 的權重，讓其空間更寬裕
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    // 標題與功能按鈕
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(state.songTitle, style = MaterialTheme.typography.headlineSmall, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(state.artistName, style = MaterialTheme.typography.titleMedium, color = Color.White.copy(alpha = 0.6f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        Row {
                            IconButton(onClick = {
                                currentMode = if (currentMode == PlayerMode.LYRICS) PlayerMode.DEFAULT else PlayerMode.LYRICS
                                if (currentMode != PlayerMode.DEFAULT) showVolumePanel = false
                            }) {
                                Icon(Icons.Rounded.Article, null, tint = if (currentMode == PlayerMode.LYRICS) Color(0xFFFFFFFF) else Color.White.copy(alpha = 0.6f))
                            }
                            IconButton(onClick = { showVolumePanel = !showVolumePanel }) {
                                Icon(
                                    imageVector = if (appVolume >= 51) Icons.AutoMirrored.Outlined.VolumeUp else Icons.AutoMirrored.Outlined.VolumeDown,
                                    contentDescription = null,
                                    tint = if (showVolumePanel) Color.White else Color.White.copy(alpha = 0.6f)
                                )
                            }
                            IconButton(onClick = {
                                currentMode = if (currentMode == PlayerMode.QUEUE) PlayerMode.DEFAULT else PlayerMode.QUEUE
                                if (currentMode != PlayerMode.DEFAULT) showVolumePanel = false
                            }) {
                                Icon(Icons.AutoMirrored.Rounded.QueueMusic, null, tint = if (currentMode == PlayerMode.QUEUE) Color(0xFFFFFFFF) else Color.White.copy(alpha = 0.6f))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 進度條與音量調整層疊區 - 固定高度為 56.dp 以防止音量面板呼出時導致位移
                    Box(
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // 進度條與時間 (保持位置固定)
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
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
                                val density = LocalDensity.current
                                val maxWidthPx = with(density) { this@BoxWithConstraints.maxWidth.toPx() }
                                Box(modifier = Modifier.fillMaxWidth().height(24.dp).pointerInput(Unit) {
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
                                }, contentAlignment = Alignment.CenterStart) {
                                    Box(modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.3f)))
                                    Box(modifier = Modifier.fillMaxWidth(dragProgress.coerceIn(0f, 1f)).height(6.dp).clip(CircleShape).background(Color.White))
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = formatTime(dragProgress),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = state.durationText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            }
                        }

                        // 音量面板 (橫屏) - 佔據相同區域，使用 absolute 定位
                        androidx.compose.animation.AnimatedVisibility(
                            visible = showVolumePanel,
                            enter = fadeIn(tween(250)) + scaleIn(initialScale = 0.95f, animationSpec = spring(stiffness = Spring.StiffnessLow)),
                            exit = fadeOut(tween(200)) + scaleOut(targetScale = 0.95f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            var dragAccumulator by remember { mutableFloatStateOf(appVolume.toFloat()) }
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .shadow(30.dp, RoundedCornerShape(28.dp)),
                                shape = RoundedCornerShape(28.dp),
                                color = if (isDark) Color(0xFF1C1C1E).copy(alpha = 0.98f) else Color(0xFFF2F2F7).copy(alpha = 0.98f),
                                border = androidx.compose.foundation.BorderStroke(0.5.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.12f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 20.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.VolumeDown,
                                        contentDescription = null,
                                        tint = if (isDark) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.5f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    BoxWithConstraints(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 16.dp)
                                            .height(48.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        val trackWidthPx = constraints.maxWidth.toFloat()
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(4.dp)
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
                                                .height(4.dp)
                                                .clip(CircleShape)
                                                .background(if (isDark) Color.White else Color.Black)
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.VolumeUp,
                                        contentDescription = null,
                                        tint = if (isDark) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.5f),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    ControlPanel(state, onPlayPauseClick, onNextClick, onPreviousClick, onShuffleToggle, onRepeatModeChange, isLandscape = true)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp),
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

                                    Spacer(modifier = Modifier.height(24.dp))

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(90.dp)
                                            .padding(start = 12.dp, end = 24.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(state.songTitle, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                            Text(state.artistName, style = MaterialTheme.typography.titleMedium, color = Color.White.copy(alpha = 0.6f), maxLines = 1, overflow = TextOverflow.Ellipsis)
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
                                            modifier = Modifier.size(56.dp).shadow(4.dp, RoundedCornerShape(8.dp)),
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
                                            Text(state.songTitle, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                            Text(state.artistName, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.6f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }
                                        IconButton(onClick = { onShuffleToggle(!state.isShuffleEnabled) }) {
                                            Icon(
                                                imageVector = Icons.Rounded.Shuffle,
                                                contentDescription = "Shuffle",
                                                tint = if (state.isShuffleEnabled) Color(0xFFFA2D48) else Color.White.copy(alpha = 0.6f),
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        IconButton(onClick = { onRepeatModeChange((state.repeatMode + 1) % 3) }) {
                                            Icon(
                                                imageVector = when(state.repeatMode) {
                                                    1 -> Icons.Rounded.Repeat // Repeat All
                                                    2 -> Icons.Rounded.RepeatOne // Repeat One
                                                    else -> Icons.Rounded.Repeat // Off
                                                },
                                                contentDescription = "Repeat",
                                                tint = if (state.repeatMode != 0) Color(0xFFFA2D48) else Color.White.copy(alpha = 0.6f),
                                                modifier = Modifier.size(20.dp)
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
                                .height(24.dp)
                        ) {
                            val maxWidth = this.maxWidth
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(24.dp)
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
                                        .height(6.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.3f))
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(dragProgress.coerceIn(0f, 1f))
                                        .height(6.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isDragging) formatTime(dragProgress) else state.currentPositionText,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isDragging) Color.White else Color.White.copy(alpha = 0.6f)
                            )
                            Text(
                                text = state.durationText,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        ControlPanel(state, onPlayPauseClick, onNextClick, onPreviousClick, onShuffleToggle, onRepeatModeChange, isLandscape = false)
                        Spacer(modifier = Modifier.height(30.dp))
                        HorizontalDivider(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = Color.White.copy(alpha = 0.2f)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth().height(60.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {
                                currentMode = if (currentMode == PlayerMode.LYRICS) PlayerMode.DEFAULT else PlayerMode.LYRICS
                                if (currentMode != PlayerMode.DEFAULT) showVolumePanel = false
                            }) {
                                Icon(
                                    imageVector = Icons.Rounded.Article,
                                    contentDescription = null,
                                    modifier = Modifier.size(26.dp),
                                    tint = if (currentMode == PlayerMode.LYRICS) Color.White else Color.White.copy(alpha = 0.5f)
                                )
                            }
                            IconButton(onClick = { showVolumePanel = !showVolumePanel }) {
                                Icon(
                                    imageVector = if (appVolume >= 51) Icons.AutoMirrored.Outlined.VolumeUp else Icons.AutoMirrored.Outlined.VolumeDown,
                                    contentDescription = null,
                                    modifier = Modifier.size(28.dp),
                                    tint = if (showVolumePanel) Color.White else Color.White.copy(alpha = 0.5f)
                                )
                            }
                            IconButton(onClick = {
                                currentMode = if (currentMode == PlayerMode.QUEUE) PlayerMode.DEFAULT else PlayerMode.QUEUE
                                if (currentMode != PlayerMode.DEFAULT) showVolumePanel = false
                            }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.QueueMusic,
                                    contentDescription = null,
                                    modifier = Modifier.size(28.dp),
                                    tint = if (currentMode == PlayerMode.QUEUE) Color.White else Color.White.copy(alpha = 0.5f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(26.dp))
                    }
                    androidx.compose.animation.AnimatedVisibility(
                        visible = showVolumePanel,
                        enter = fadeIn(tween(250)) + scaleIn(initialScale = 0.95f, animationSpec = spring(stiffness = Spring.StiffnessLow)),
                        exit = fadeOut(tween(200)) + scaleOut(targetScale = 0.95f),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = (-80).dp)
                            .padding(horizontal = 16.dp)
                    ) {
                        var dragAccumulator by remember { mutableFloatStateOf(appVolume.toFloat()) }
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .shadow(30.dp, RoundedCornerShape(28.dp)),
                            shape = RoundedCornerShape(28.dp),
                            color = if (isDark) Color(0xFF1C1C1E).copy(alpha = 0.98f) else Color(0xFFF2F2F7).copy(alpha = 0.98f),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.12f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 20.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.VolumeDown,
                                    contentDescription = null,
                                    tint = if (isDark) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.5f),
                                    modifier = Modifier.size(16.dp)
                                )
                                BoxWithConstraints(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 16.dp)
                                        .height(48.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    val trackWidthPx = constraints.maxWidth.toFloat()
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(4.dp)
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
                                            .height(4.dp)
                                            .clip(CircleShape)
                                            .background(if (isDark) Color.White else Color.Black)
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.VolumeUp,
                                    contentDescription = null,
                                    tint = if (isDark) Color.White.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.5f),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LyricsView(lyrics: String?) {
    LaunchedEffect(lyrics) {
        android.util.Log.d("UI_DEBUG", "LyricsView 收到的歌詞: ${lyrics?.take(20) ?: "NULL"}")
    }

    Box(
        modifier = Modifier.fillMaxSize().padding(0.dp),
        contentAlignment = if (lyrics.isNullOrBlank()) Alignment.Center else Alignment.TopStart
    ) {
        if (lyrics.isNullOrBlank()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "歌詞未就緒",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "目前無可用歌詞",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            val scrollState = rememberScrollState()
            Text(
                text = lyrics.replace("\r\n", "\n").replace("\r", "\n"),
                style = MaterialTheme.typography.bodyLarge,
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
fun QueueView(state: MusicState, onSongClick: (Song) -> Unit) {
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
                    modifier = Modifier.size(40.dp),
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
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                        color = if (isCurrent) Color(0xFFFA2D48) else Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = song.artist,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (isCurrent) {
                    Icon(Icons.AutoMirrored.Outlined.VolumeUp, null, tint = Color(0xFFFA2D48), modifier = Modifier.size(16.dp))
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
