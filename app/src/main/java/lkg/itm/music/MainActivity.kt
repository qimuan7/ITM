package lkg.itm.music

import android.content.Intent
import android.graphics.Color as AndroidColor // 避免與 Compose Color 衝突
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.runtime.collectAsState
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.ExistingWorkPolicy
import androidx.work.workDataOf
import lkg.itm.music.ui.MainScreen
import lkg.itm.music.MainViewModel
import lkg.itm.music.ui.MusicState
import lkg.itm.music.ui.SettingsScreen
import lkg.itm.music.ui.theme.ITMTheme
import java.io.InputStream

class MainActivity : ComponentActivity() {
    private lateinit var playerManager: MusicPlayerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        // 1. 啟用 Edge-to-Edge
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        // 2. 請求通知權限 (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val requestPermissionLauncher = registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { }
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        // 3. 強制將系統狀態欄與導航欄設為完全透明
        window.statusBarColor = AndroidColor.TRANSPARENT
        window.navigationBarColor = AndroidColor.TRANSPARENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }

        playerManager = MusicPlayerManager(this)

        setContent {
            var songs by remember { mutableStateOf<List<Song>>(emptyList()) }
            var playlists by remember { mutableStateOf<List<Playlist>>(emptyList()) }
            var isLoading by remember { mutableStateOf(true) } // 一開始先讀取快取

            // 💡 今日推薦重新整理的觸發狀態
            var refreshDailyTrigger by remember { mutableIntStateOf(0) }

            // 建立 Room Repository
            val repository = remember { MusicRepository(applicationContext) }

            // 🎨 主題偏好設定
            val themePreferences = remember { ThemePreferences(this@MainActivity) }
            var currentThemeMode by remember { mutableStateOf(themePreferences.getThemeMode()) }

            // 判斷是否為暗黑模式
            val systemDark = isSystemInDarkTheme()
            val useDarkTheme = when (currentThemeMode) {
                "light" -> false
                "dark" -> true
                else -> systemDark // "system" 或預設
            }

            // 資料夾選取器 Launcher (可供設定頁面等地方觸發)
            val directoryPickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocumentTree()
            ) { treeUri: Uri? ->
                treeUri?.let { selectedTreeUri ->
                    isLoading = true

                    // 啟動 Coroutine 在背景執行緒處理檔案與解析
                    lifecycleScope.launch(Dispatchers.Main) {
                        try {
                            // 1. 取得資料夾的持久授權
                            contentResolver.takePersistableUriPermission(
                                selectedTreeUri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            )

                            val rootDir = DocumentFile.fromTreeUri(this@MainActivity, selectedTreeUri)

                            // 2. 在背景執行緒中尋找並解析 XML
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
                                    } else {
                                        null
                                    }
                                } else {
                                    null
                                }
                            }

                            if (result != null) {
                                songs = result.first
                                playlists = result.second

                                // 成功解析後，立刻存入 Room 資料庫快取！
                                withContext(Dispatchers.IO) {
                                    repository.saveLibrary(songs, playlists)
                                    // 啟動背景響度掃描
                                    // 在 MainActivity.kt 的 saveLibrary 呼叫後
                                    if (result != null) {
                                        songs = result.first
                                        playlists = result.second

                                        withContext(Dispatchers.IO) {
                                            repository.saveLibrary(songs, playlists)

                                            // 【核心修正】在這裡為每一首沒有響度與歌詞數據的歌，排程背景掃描！
                                            val workManager = WorkManager.getInstance(this@MainActivity)
                                            
                                            // 1. 啟動一個批量歌詞掃描任務
                                            val lyricsBatchRequest = OneTimeWorkRequestBuilder<LyricsAnalysisWorker>()
                                                .setInputData(workDataOf("isBatch" to true))
                                                .build()
                                            workManager.enqueueUniqueWork("LyricsBatchScan", ExistingWorkPolicy.KEEP, lyricsBatchRequest)

                                            // 2. 響度掃描
                                            songs.forEach { song ->
                                                if (song.loudness == null) {
                                                    val request = OneTimeWorkRequestBuilder<LoudnessAnalysisWorker>()
                                                        .setInputData(workDataOf("trackId" to song.trackId))
                                                        .build()
                                                    workManager.enqueue(request)
                                                }
                                            }
                                        }
                                        // ...
                                    }


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

            // 頁面初次載入時：優先從 Room 資料庫秒讀快取
            LaunchedEffect(Unit) {
                // 啟動背景響度掃描 (處理之前沒掃完的)
            val workManager = WorkManager.getInstance(this@MainActivity)
            val lyricsBatchRequest = OneTimeWorkRequestBuilder<LyricsAnalysisWorker>()
                .setInputData(workDataOf("isBatch" to true))
                .build()
            workManager.enqueueUniqueWork("LyricsBatchScan", ExistingWorkPolicy.KEEP, lyricsBatchRequest)
            
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

            // 統一在這裡套用 ITMTheme 並開啟動態取色 (Material You)
            ITMTheme(
                darkTheme = useDarkTheme,
                dynamicColor = true
            ) {
                // 關鍵修改：直接讓 Surface 填滿，背景色交由內部的主畫面與設定頁面控制
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
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

                        // 呼叫 MainAppRoot
                        val currentSong by playerManager.currentSong.collectAsState()
                        MainAppRoot(
                            songs = songs,
                            playlists = playlists,
                            playerManager = playerManager,
                            currentSong = currentSong,
                            onSelectDirectory = {
                                directoryPickerLauncher.launch(null)
                            },
                            currentThemeMode = currentThemeMode,
                            onThemeModeChanged = { newMode ->
                                themePreferences.setThemeMode(newMode)
                                currentThemeMode = newMode
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
    onRefreshDailyRecommendations: () -> Unit,
    recommendationRefreshTrigger: Int,
    // 注入 viewModel
    viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val isNormalizationEnabled by playerManager.isNormalizationEnabled.collectAsState()

    // 構建最新的狀態
    val musicState = remember(currentSong) {
        MusicState(
            songTitle = currentSong?.title ?: "未知",
            artistName = currentSong?.artist ?: "未知",
            artwork = currentSong?.androidPath,
            lyrics = currentSong?.lyrics
        )
    }

    // 將 BackHandler 改為使用 ViewModel 的狀態
    BackHandler(enabled = viewModel.isSettingsOpen) { viewModel.isSettingsOpen = false }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        MainScreen(
            playlists = playlists,
            allSongs = songs,
            playerManager = playerManager,
            musicState = musicState,
            onSelectDirectory = onSelectDirectory,
            currentThemeMode = currentThemeMode,
            onThemeModeChanged = onThemeModeChanged,
            onOpenSettings = { viewModel.isSettingsOpen = true }, // 修改這裡
            isSettingsOpen = viewModel.isSettingsOpen,            // 修改這裡
            recommendationRefreshTrigger = recommendationRefreshTrigger
        )

        // 2. 上層：全局覆蓋的設定頁面
        if (viewModel.isSettingsOpen) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                SettingsScreen(
                    currentThemeMode = currentThemeMode,
                    onThemeModeChanged = onThemeModeChanged,
                    isNormalizationEnabled = isNormalizationEnabled,
                    onNormalizationChanged = { playerManager.setNormalizationEnabled(it) },
                    onSelectFolderClick = onSelectDirectory,
                    onRefreshDailyRecommendations = onRefreshDailyRecommendations,
                    onBackClick = { viewModel.isSettingsOpen = false } // 修改這裡
                )
            }
        }
    }
}
