// "設定" 頁面 首頁和資源庫右上角 用戶圖標進入

package lkg.itm.music.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import lkg.itm.music.R
import androidx.work.WorkManager
import androidx.compose.foundation.isSystemInDarkTheme

@Composable
fun SettingsScreen(
    currentThemeMode: String, // 接收目前的主題模式 ("system", "light", "dark")
    onThemeModeChanged: (String) -> Unit, // 切換主題時觸發
    isNormalizationEnabled: Boolean,
    onNormalizationChanged: (Boolean) -> Unit,
    onSelectFolderClick: () -> Unit, // 點擊時觸發 MainActivity 的資料夾選取器
    onRefreshDailyRecommendations: () -> Unit, // 更換今日推薦的動作
    onBackClick: () -> Unit
) {
    val isDark = when (currentThemeMode) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }
    val backgroundColor = if (isDark) Color.Black else Color(0xFFF5F5F5)
    val cardBackground = if (isDark) Color(0xFF0D0F12) else Color(0xFFEDEDED)
    val cardBorderColor = if (isDark) Color(0xFF2C2C2C) else Color(0xFFB0B3BD)
    val cardShape = RoundedCornerShape(16.dp)
    val borderModifier = androidx.compose.ui.Modifier.border(1.dp, cardBorderColor, cardShape)

    val context = LocalContext.current
    val workManager = remember { WorkManager.getInstance(context) }

    // 追蹤 Worker 狀態的狀態變數

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
            .navigationBarsPadding() // 確保內容安全顯示，背景色仍可透過背景延伸實現
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // 將點擊事件從 Row 移至 Icon，並限制大小
        Icon(
            imageVector = Icons.Default.ArrowBackIosNew,
            contentDescription = "返回",
            tint = Color(0xFFFA2D48),
            modifier = Modifier
                .offset(x = (-13).dp) // 向左偏移圖標
                .size(48.dp) // 增加觸摸區域至 48dp
                .clickable(
                    indication = null,
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                ) { onBackClick() }
                .padding(13.dp) // 內部間距保持圖標看起來正常
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = stringResource(id = R.string.settings),
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



        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(34.dp)) // 在這裡新增空位
                SettingsSectionHeader(title = stringResource(id = R.string.library_section))
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().then(borderModifier),
                    colors = CardDefaults.cardColors(containerColor = cardBackground),
                    shape = cardShape,
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Button(
                            onClick = { onSelectFolderClick() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFA2D48)),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CreateNewFolder,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(id = R.string.reselect_itunes_folder),
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = stringResource(id = R.string.select_itunes_folder_desc),
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                SettingsSectionHeader(title = stringResource(id = R.string.refresh_recommendation_section))
                Spacer(modifier = Modifier.height(8.dp))

                // 「更換今日推薦」的獨立卡片按鈕
                Card(
                    modifier = Modifier.fillMaxWidth().then(borderModifier),
                    colors = CardDefaults.cardColors(containerColor = cardBackground),
                    shape = cardShape,
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Button(
                            onClick = {
                                android.util.Log.d("TestRefresh", "【SettingsScreen】更換今日推薦按鈕被點擊了！")
                                onRefreshDailyRecommendations()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFD9D9D9)
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                tint = if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else Color.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(id = R.string.refresh_recommendation),
                                color = if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(id = R.string.refresh_recommendation_desc),
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            item {
                SettingsSectionHeader(title = stringResource(id = R.string.appearance_section))
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().then(borderModifier),
                    colors = CardDefaults.cardColors(containerColor = cardBackground),
                    shape = cardShape,
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        //  外觀主題模式切換
                        Text(
                            text = stringResource(id = R.string.appearance_theme),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ThemeOptionButton(
                                text = stringResource(id = R.string.theme_system),
                                isSelected = currentThemeMode == "system",
                                onClick = { onThemeModeChanged("system") },
                                isDark = isDark,
                                modifier = Modifier.weight(1f)
                            )
                            ThemeOptionButton(
                                text = stringResource(id = R.string.theme_light),
                                isSelected = currentThemeMode == "light",
                                onClick = { onThemeModeChanged("light") },
                                isDark = isDark,
                                modifier = Modifier.weight(1f)
                            )
                            ThemeOptionButton(
                                text = stringResource(id = R.string.theme_dark),
                                isSelected = currentThemeMode == "dark",
                                onClick = { onThemeModeChanged("dark") },
                                isDark = isDark,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeOptionButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFFFA2D48) else if (isDark) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFD9D9D9),
            contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(text = text, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        modifier = Modifier.padding(start = 4.dp)
    )
}

@Composable
fun SettingsSwitchItem(
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFFFA2D48)
            )
        )
    }
}