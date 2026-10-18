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
            val viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            
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

            // 資料夾選取器 Launcher
            val directoryPickerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.OpenDocumentTree()
            ) { treeUri: Uri? ->
                treeUri?.let { selectedTreeUri ->
                    viewModel.isLoading = true

                    lifecycleScope.launch(Dispatchers.Main) {
                        try {
                            contentResolver.takePersistableUriPermission(
                                selectedTreeUri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                            )

                            val rootDir = DocumentFile.fromTreeUri(this@MainActivity, selectedTreeUri)

                            val result = withContext(Dispatchers.IO) {
                                val possibleNames = listOf("iTunes Music Library.xml", "itunes.xml", "Library.xml")
                                val xmlFile = possibleNames.firstNotNullOfOrNull { rootDir?.findFile(it) }

                                if (xmlFile != null && xmlFile.isFile) {
                                    val inputStream: InputStream? = contentResolver.openInputStream(xmlFile.uri)
                                    if (inputStream != null && rootDir != null) {
                                        ITunesParser.parseITunesLibraryWithUri(inputStream, rootDir)
                                    } else null
                                } else null
                            }

                            if (result != null) {
                                viewModel.songs = result.first
                                viewModel.playlists = result.second

                                withContext(Dispatchers.IO) {
                                    repository.saveLibrary(viewModel.songs, viewModel.playlists)
                                    
                                    val workManager = WorkManager.getInstance(this@MainActivity)
                                    val lyricsBatchRequest = OneTimeWorkRequestBuilder<LyricsAnalysisWorker>()
                                        .setInputData(workDataOf("isBatch" to true))
                                        .build()
                                    workManager.enqueueUniqueWork("LyricsBatchScan", ExistingWorkPolicy.KEEP, lyricsBatchRequest)

                                    viewModel.songs.forEach { song ->
                                        if (song.loudness == null) {
                                            val request = OneTimeWorkRequestBuilder<LoudnessAnalysisWorker>()
                                                .setInputData(workDataOf("trackId" to song.trackId))
                                                .build()
                                            workManager.enqueue(request)
                                        }
                                    }
                                }

                                Toast.makeText(this@MainActivity, "成功載入並快取 ${viewModel.songs.size} 首歌曲！", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@MainActivity, "找不到有效的 iTunes XML 檔案！", Toast.LENGTH_LONG).show()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(this@MainActivity, "讀取失敗: ${e.message}", Toast.LENGTH_LONG).show()
                        } finally {
                            viewModel.isLoading = false
                        }
                    }
                }
            }

            // 頁面初次載入時：優先從 Room 資料庫秒讀快取
            LaunchedEffect(Unit) {
                if (viewModel.isLibraryLoaded) return@LaunchedEffect

                val workManager = WorkManager.getInstance(this@MainActivity)
                val lyricsBatchRequest = OneTimeWorkRequestBuilder<LyricsAnalysisWorker>()
                    .setInputData(workDataOf("isBatch" to true))
                    .build()
                workManager.enqueueUniqueWork("LyricsBatchScan", ExistingWorkPolicy.KEEP, lyricsBatchRequest)
                
                withContext(Dispatchers.IO) {
                    try {
                        val cachedSongs = repository.getCachedSongs()
                        android.util.Log.d("ITM_DEBUG", "從資料庫讀取到 ${cachedSongs.size} 首歌曲")
                        if (cachedSongs.isNotEmpty()) {
                            val cachedPlaylists = repository.getCachedPlaylists(cachedSongs)
                            withContext(Dispatchers.Main) {
                                viewModel.songs = cachedSongs
                                viewModel.playlists = cachedPlaylists
                                viewModel.isLibraryLoaded = true
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("ITM_DEBUG", "讀取資料庫出錯", e)
                    } finally {
                        withContext(Dispatchers.Main) {
                            viewModel.isLoading = false
                        }
                    }
                }
            }

            ITMTheme(darkTheme = useDarkTheme, dynamicColor = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (viewModel.isLoading) {
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
                            songs = viewModel.songs,
                            playlists = viewModel.playlists,
                            playerManager = playerManager,
                            currentSong = currentSong,
                            onSelectDirectory = { directoryPickerLauncher.launch(null) },
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
                            recommendationRefreshTrigger = refreshDailyTrigger,
                            viewModel = viewModel
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
