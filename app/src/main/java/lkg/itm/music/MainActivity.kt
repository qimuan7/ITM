package lkg.itm.music

import android.content.Intent
import android.graphics.Color as AndroidColor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import lkg.itm.music.ui.LoudnessEngineDialog
import lkg.itm.music.ui.LoudnessScanScreen
import lkg.itm.music.ui.MainScreen
import lkg.itm.music.ui.MusicState
import lkg.itm.music.ui.SettingsScreen
import lkg.itm.music.ui.TabletUI.TabletMainScreen
import lkg.itm.music.ui.theme.ITMTheme
import java.io.InputStream
import java.util.UUID

class MainActivity : ComponentActivity() {
    private lateinit var playerManager: MusicPlayerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { }
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        window.statusBarColor = AndroidColor.TRANSPARENT
        window.navigationBarColor = AndroidColor.TRANSPARENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        playerManager = MusicPlayerManager(this)

        setContent {
            var songs by remember { mutableStateOf<List<Song>>(emptyList()) }
            var playlists by remember { mutableStateOf<List<Playlist>>(emptyList()) }
            var isLoading by remember { mutableStateOf(true) }
            var refreshDailyTrigger by remember { mutableIntStateOf(0) }

            val repository = remember { MusicRepository(applicationContext) }

            val themePreferences = remember { ThemePreferences(this@MainActivity) }
            var currentThemeMode by remember { mutableStateOf(themePreferences.getThemeMode()) }
            var currentUiMode by remember { mutableStateOf(themePreferences.getUiMode()) }

            // ── 音量平衡相關狀態 ───────────────────────
            var currentLoudnessEngine by remember { mutableStateOf(themePreferences.getLoudnessEngine()) }
            var currentLoudnessConcurrency by remember { mutableStateOf(themePreferences.getLoudnessConcurrency()) }   // ← 加这行
            var pendingEngine by remember { mutableStateOf<String?>(null) }
            var scanWorkId by remember { mutableStateOf<UUID?>(null) }

            val systemDark = isSystemInDarkTheme()
            val useDarkTheme = when (currentThemeMode) {
                "light" -> false
                "dark" -> true
                else -> systemDark
            }

            val isTabletDevice = LocalConfiguration.current.smallestScreenWidthDp >= 600
            val effectiveUiMode = when (currentUiMode) {
                "phone" -> "phone"
                "tablet" -> "tablet"
                else -> if (isTabletDevice) "tablet" else "phone"
            }

            // ── 音量平衡掃描啟動器 ─────────────────────
            fun startLoudnessScan(engine: String, fullScan: Boolean) {
                lifecycleScope.launch(Dispatchers.Main) {

                    // 1. 決定要掃的 trackIds
                    val targetIds: List<String> = withContext(Dispatchers.IO) {
                        if (fullScan) {
                            repository.clearAllLoudness()
                            repository.getCachedSongs().map { it.trackId }
                        } else {
                            repository.getSongsWithoutLoudness().map { it.trackId }
                        }
                    }

                    // 2. 更新偏好
                    themePreferences.setLoudnessEngine(engine)
                    currentLoudnessEngine = engine

                    // 3. 沒東西掃就不用開 Worker
                    if (targetIds.isEmpty()) {
                        Toast.makeText(this@MainActivity, "沒有需要掃描的歌曲", Toast.LENGTH_SHORT).show()
                        pendingEngine = null
                        return@launch
                    }

                    // 4. Enqueue worker
                    val concurrency = themePreferences.getLoudnessConcurrency()
                    val request = OneTimeWorkRequestBuilder<LoudnessAnalysisWorker>()
                        .setInputData(workDataOf(
                            "engine" to engine,
                            "trackIds" to targetIds.toTypedArray(),
                            "concurrency" to concurrency
                        ))
                        .build()

                    WorkManager.getInstance(this@MainActivity).enqueue(request)
                    scanWorkId = request.id
                    pendingEngine = null
                }
            }

            // ── 資料夾選取器 ─────────────────────────────
            val directoryPickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocumentTree()
            ) { treeUri: Uri? ->
                treeUri?.let { selectedTreeUri ->
                    isLoading = true

                    lifecycleScope.launch(Dispatchers.Main) {
                        try {
                            contentResolver.takePersistableUriPermission(
                                selectedTreeUri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            )

                            val rootDir = DocumentFile.fromTreeUri(this@MainActivity, selectedTreeUri)

                            val result = withContext(Dispatchers.IO) {
                                val possibleNames = listOf(
                                    "iTunes Music Library.xml",
                                    "itunes.xml",
                                    "Library.xml"
                                )
                                val xmlFile = possibleNames.firstNotNullOfOrNull { rootDir?.findFile(it) }

                                if (xmlFile != null && xmlFile.isFile) {
                                    val inputStream: InputStream? = contentResolver.openInputStream(xmlFile.uri)
                                    if (inputStream != null && rootDir != null) {
                                        ITunesParser.parseITunesLibraryWithUri(
                                            inputStream = inputStream,
                                            documentFileRoot = rootDir
                                        )
                                    } else null
                                } else null
                            }

                            if (result != null) {
                                songs = result.first
                                playlists = result.second

                                withContext(Dispatchers.IO) {
                                    repository.saveLibrary(songs, playlists)

                                    val workManager = WorkManager.getInstance(this@MainActivity)
                                    val lyricsBatchRequest = OneTimeWorkRequestBuilder<LyricsAnalysisWorker>()
                                        .setInputData(workDataOf("isBatch" to true))
                                        .build()
                                    workManager.enqueueUniqueWork(
                                        "LyricsBatchScan",
                                        ExistingWorkPolicy.KEEP,
                                        lyricsBatchRequest
                                    )
                                }

                                Toast.makeText(
                                    this@MainActivity,
                                    "成功載入並快取 ${songs.size} 首歌曲！",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@MainActivity,
                                    "在選取的資料夾中找不到有效的 iTunes XML 檔案！",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(
                                this@MainActivity,
                                "讀取失敗: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        } finally {
                            isLoading = false
                        }
                    }
                }
            }

            // ── 首次載入：從 Room 讀快取 ──────────────────
            LaunchedEffect(Unit) {
                withContext(Dispatchers.IO) {
                    try {
                        val cachedSongs = repository.getCachedSongs()
                        if (cachedSongs.isNotEmpty()) {
                            val cachedPlaylists = repository.getCachedPlaylists(cachedSongs)
                            withContext(Dispatchers.Main) {
                                songs = cachedSongs
                                playlists = cachedPlaylists
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        withContext(Dispatchers.Main) {
                            isLoading = false
                        }
                    }
                }
            }

            // ── 判斷彈窗的「首次使用」狀態 ──────────────
            val isFirstTimeForPending: Boolean = pendingEngine?.let {
                songs.all { song -> song.ffmpegLoudness == null }
            } ?: false

            // ── 主題 + 內容 ─────────────────────────────
            ITMTheme(darkTheme = useDarkTheme, dynamicColor = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {

                        if (isLoading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(if (useDarkTheme) Color.Black else Color(0xFFF5F5F5)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(
                                        color = if (useDarkTheme) Color.White else Color.Black
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "正在載入音樂庫...",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (useDarkTheme) Color.White else Color.Black
                                    )
                                }
                            }
                        } else {
                            val currentSong by playerManager.currentSong.collectAsState()
                            MainAppRoot(
                                songs = songs,
                                playlists = playlists,
                                playerManager = playerManager,
                                currentSong = currentSong,
                                onSelectDirectory = { directoryPickerLauncher.launch(null) },
                                currentThemeMode = currentThemeMode,
                                onThemeModeChanged = { newMode ->
                                    themePreferences.setThemeMode(newMode)
                                    currentThemeMode = newMode
                                },
                                uiMode = effectiveUiMode,
                                currentUiModePreference = currentUiMode,
                                onUiModeChanged = { newMode ->
                                    themePreferences.setUiMode(newMode)
                                    currentUiMode = newMode
                                },
                                currentLoudnessEngine = currentLoudnessEngine,
                                onLoudnessEngineChange = { newEngine ->
                                    when {
                                        newEngine == currentLoudnessEngine -> { /* no-op */ }
                                        newEngine == "off" -> {
                                            themePreferences.setLoudnessEngine("off")
                                            currentLoudnessEngine = "off"
                                        }
                                        else -> pendingEngine = newEngine
                                    }
                                },
                                onRefreshDailyRecommendations = {
                                    val prefs = getSharedPreferences("itm_music_prefs", MODE_PRIVATE)
                                    val currentCount = prefs.getInt("refresh_count", 0)
                                    prefs.edit().putInt("refresh_count", currentCount + 1).apply()
                                    refreshDailyTrigger += 1
                                },
                                recommendationRefreshTrigger = refreshDailyTrigger
                            )
                        }

                        // ── 引擎切換彈窗 ────────────────────
                        pendingEngine?.let { eng ->
                            LoudnessEngineDialog(
                                isFirstTime = isFirstTimeForPending,
                                currentThemeMode = currentThemeMode,
                                uiMode = effectiveUiMode,
                                concurrency = currentLoudnessConcurrency,                    // ← 新增
                                onConcurrencyChange = { newValue ->                          // ← 新增
                                    themePreferences.setLoudnessConcurrency(newValue)
                                    currentLoudnessConcurrency = newValue
                                },
                                onFullScan = { startLoudnessScan(eng, fullScan = true) },
                                onIncrementalScan = { startLoudnessScan(eng, fullScan = false) },
                                onDismiss = { pendingEngine = null }
                            )
                        }

                        // ── 掃描進度畫面（最上層） ──────────
                        scanWorkId?.let { id ->
                            LoudnessScanScreen(
                                workId = id,
                                currentThemeMode = currentThemeMode,
                                onCancel = {
                                    WorkManager.getInstance(this@MainActivity).cancelWorkById(id)
                                    scanWorkId = null
                                },
                                onFinished = {
                                    scanWorkId = null
                                    lifecycleScope.launch(Dispatchers.Main) {
                                        val refreshedSongs = withContext(Dispatchers.IO) {
                                            repository.getCachedSongs()
                                        }
                                        val refreshedPlaylists = withContext(Dispatchers.IO) {
                                            repository.getCachedPlaylists(refreshedSongs)
                                        }
                                        songs = refreshedSongs
                                        playlists = refreshedPlaylists
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        playerManager.release()
    }
}

@Composable
fun MainAppRoot(
    songs: List<Song>,
    playlists: List<Playlist>,
    playerManager: MusicPlayerManager,
    currentSong: Song?,
    onSelectDirectory: () -> Unit,
    currentThemeMode: String,
    onThemeModeChanged: (String) -> Unit,
    uiMode: String,
    currentUiModePreference: String,
    onUiModeChanged: (String) -> Unit,
    currentLoudnessEngine: String,
    onLoudnessEngineChange: (String) -> Unit,
    onRefreshDailyRecommendations: () -> Unit,
    recommendationRefreshTrigger: Int,
    viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val isNormalizationEnabled by playerManager.isNormalizationEnabled.collectAsState()

    val musicState = remember(currentSong) {
        MusicState(
            songTitle = currentSong?.title ?: "未知",
            artistName = currentSong?.artist ?: "未知",
            artwork = currentSong?.androidPath,
            lyrics = currentSong?.lyrics
        )
    }

    BackHandler(enabled = viewModel.isSettingsOpen) { viewModel.isSettingsOpen = false }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        if (uiMode == "tablet") {
            TabletMainScreen(
                playlists = playlists,
                allSongs = songs,
                playerManager = playerManager,
                musicState = musicState,
                onSelectDirectory = onSelectDirectory,
                currentThemeMode = currentThemeMode,
                onThemeModeChanged = onThemeModeChanged,
                onOpenSettings = { viewModel.isSettingsOpen = true },
                isSettingsOpen = viewModel.isSettingsOpen,
                recommendationRefreshTrigger = recommendationRefreshTrigger
            )
        } else {
            MainScreen(
                playlists = playlists,
                allSongs = songs,
                playerManager = playerManager,
                musicState = musicState,
                onSelectDirectory = onSelectDirectory,
                currentThemeMode = currentThemeMode,
                onThemeModeChanged = onThemeModeChanged,
                onOpenSettings = { viewModel.isSettingsOpen = true },
                isSettingsOpen = viewModel.isSettingsOpen,
                recommendationRefreshTrigger = recommendationRefreshTrigger
            )
        }

        if (viewModel.isSettingsOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                SettingsScreen(
                    currentThemeMode = currentThemeMode,
                    onThemeModeChanged = onThemeModeChanged,
                    currentUiModePreference = currentUiModePreference,
                    onUiModeChanged = onUiModeChanged,
                    isNormalizationEnabled = isNormalizationEnabled,
                    onNormalizationChanged = { playerManager.setNormalizationEnabled(it) },
                    onSelectFolderClick = onSelectDirectory,
                    onRefreshDailyRecommendations = onRefreshDailyRecommendations,
                    onBackClick = { viewModel.isSettingsOpen = false },
                    uiMode = uiMode,
                    currentLoudnessEngine = currentLoudnessEngine,
                    onLoudnessEngineChange = onLoudnessEngineChange
                )
            }
        }
    }
}