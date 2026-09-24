package lkg.itm.music.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.work.WorkManager

@Composable
fun SettingsScreen(
    currentThemeMode: String,
    onThemeModeChanged: (String) -> Unit,
    currentUiModePreference: String,
    onUiModeChanged: (String) -> Unit,
    isNormalizationEnabled: Boolean,
    onNormalizationChanged: (Boolean) -> Unit,
    onSelectFolderClick: () -> Unit,
    onRefreshDailyRecommendations: () -> Unit,
    onBackClick: () -> Unit,
    uiMode: String,
    currentLoudnessEngine: String,
    onLoudnessEngineChange: (String) -> Unit
) {
    val isDark = when (currentThemeMode) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }
    val isTablet = uiMode == "tablet"

    val accentColor        = if (isTablet) (if (isDark) Color.White else Color.Black) else Color(0xFFFA2D48)
    val onAccentColor      = if (isTablet) (if (isDark) Color.Black else Color.White) else Color.White
    val backIconTint       = accentColor

    val backgroundColor  = if (isDark) Color.Black else Color(0xFFF5F5F5)
    val cardBackground   = if (isDark) Color(0xFF0D0F12) else Color(0xFFEDEDED)
    val cardBorderColor  = if (isDark) Color(0xFF2C2C2C) else Color(0xFFB0B3BD)
    val cardShape        = RoundedCornerShape(16.dp)
    val borderModifier   = Modifier.border(1.dp, cardBorderColor, cardShape)

    val context = LocalContext.current
    val workManager = remember { WorkManager.getInstance(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
            .navigationBarsPadding()
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Icon(
            imageVector = Icons.Default.ArrowBackIosNew,
            contentDescription = "返回",
            tint = backIconTint,
            modifier = Modifier
                .offset(x = (-13).dp)
                .size(48.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                ) { onBackClick() }
                .padding(13.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "設定",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 34.sp,
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
            // ── 資源庫 & 更換推薦 ────────────────
            item {
                Spacer(modifier = Modifier.height(34.dp))
                SettingsSectionHeader(title = "資源庫")
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
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CreateNewFolder,
                                contentDescription = null,
                                tint = onAccentColor
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "重新選擇 iTunes 根目錄資料夾",
                                color = onAccentColor,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "選擇包含 iTunes Music Library.xml 的 iTunes 資料夾",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                SettingsSectionHeader(title = "更換推薦")
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().then(borderModifier),
                    colors = CardDefaults.cardColors(containerColor = cardBackground),
                    shape = cardShape,
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Button(
                            onClick = { onRefreshDailyRecommendations() },
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
                                text = "更換今日推薦",
                                color = if (isDark) MaterialTheme.colorScheme.onSurfaceVariant else Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "點擊可隨機更換首頁「今日推薦」的 6 首歌曲",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // ── 音量平衡 ────────────────────────
            item {
                SettingsSectionHeader(title = "音量平衡")
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().then(borderModifier),
                    colors = CardDefaults.cardColors(containerColor = cardBackground),
                    shape = cardShape,
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "響度標準化引擎",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "掃描後播放時自動調整音量，減少歌曲間響度差異",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ThemeOptionButton(
                                text = "關閉",
                                isSelected = currentLoudnessEngine == "off",
                                onClick = { onLoudnessEngineChange("off") },
                                isDark = isDark, accentColor = accentColor, onAccentColor = onAccentColor,
                                modifier = Modifier.weight(1f)
                            )
                            ThemeOptionButton(
                                text = "開啓",
                                isSelected = currentLoudnessEngine == "ffmpeg",
                                onClick = { onLoudnessEngineChange("ffmpeg") },
                                isDark = isDark, accentColor = accentColor, onAccentColor = onAccentColor,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "使用 FFmpeg 分析歌曲響度，播放時自動調整音量",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            // ── 外觀選擇 ────────────────────────
            item {
                SettingsSectionHeader(title = "外觀選擇")
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().then(borderModifier),
                    colors = CardDefaults.cardColors(containerColor = cardBackground),
                    shape = cardShape,
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // 主題模式
                        Text(
                            text = "外觀主題",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "選擇顯示爲亮色或暗色",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ThemeOptionButton(
                                text = "跟隨",
                                isSelected = currentThemeMode == "system",
                                onClick = { onThemeModeChanged("system") },
                                isDark = isDark, accentColor = accentColor, onAccentColor = onAccentColor,
                                modifier = Modifier.weight(1f)
                            )
                            ThemeOptionButton(
                                text = "明亮",
                                isSelected = currentThemeMode == "light",
                                onClick = { onThemeModeChanged("light") },
                                isDark = isDark, accentColor = accentColor, onAccentColor = onAccentColor,
                                modifier = Modifier.weight(1f)
                            )
                            ThemeOptionButton(
                                text = "暗黑",
                                isSelected = currentThemeMode == "dark",
                                onClick = { onThemeModeChanged("dark") },
                                isDark = isDark, accentColor = accentColor, onAccentColor = onAccentColor,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // 界面佈局
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                            text = "界面佈局",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "選擇手機或平板版的操作介面",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ThemeOptionButton(
                                text = "跟隨",
                                isSelected = currentUiModePreference == "auto",
                                onClick = { onUiModeChanged("auto") },
                                isDark = isDark, accentColor = accentColor, onAccentColor = onAccentColor,
                                modifier = Modifier.weight(1f)
                            )
                            ThemeOptionButton(
                                text = "手機",
                                isSelected = currentUiModePreference == "phone",
                                onClick = { onUiModeChanged("phone") },
                                isDark = isDark, accentColor = accentColor, onAccentColor = onAccentColor,
                                modifier = Modifier.weight(1f)
                            )
                            ThemeOptionButton(
                                text = "平板",
                                isSelected = currentUiModePreference == "tablet",
                                onClick = { onUiModeChanged("tablet") },
                                isDark = isDark, accentColor = accentColor, onAccentColor = onAccentColor,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "「跟隨」會依裝置螢幕寬度自動選擇",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun ThemeOptionButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    isDark: Boolean,
    accentColor: Color,
    onAccentColor: Color,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) accentColor
            else if (isDark) MaterialTheme.colorScheme.surfaceVariant
            else Color(0xFFD9D9D9),
            contentColor = if (isSelected) onAccentColor
            else MaterialTheme.colorScheme.onSurfaceVariant
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