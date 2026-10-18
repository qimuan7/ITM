package lkg.itm.music.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun LoudnessEngineDialog(
    isFirstTime: Boolean,
    currentThemeMode: String,
    uiMode: String,
    concurrency: Int,
    onConcurrencyChange: (Int) -> Unit,
    onFullScan: () -> Unit,
    onIncrementalScan: () -> Unit,
    onDismiss: () -> Unit
) {
    val isDark = when (currentThemeMode) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }

    val dialogBackground = if (isDark) Color(0xFF0D0F12) else Color(0xFFEDEDED)
    val cardBackground   = if (isDark) Color(0xFF16181D) else Color(0xFFF7F7F7)
    val borderColor      = if (isDark) Color(0xFF2C2C2C) else Color(0xFFB0B3BD)
    val textColor        = if (isDark) Color.White else Color.Black
    val subTextColor     = Color.Gray

    val isTablet = uiMode == "tablet"
    val accentColor   = if (isTablet) (if (isDark) Color.White else Color.Black) else Color(0xFFFA2D48)
    val onAccentColor = if (isTablet) (if (isDark) Color.Black else Color.White) else Color.White

    val shape = RoundedCornerShape(20.dp)

    val engineName = "響度掃描"
    val description = "使用 FFmpeg 分析所有歌曲的響度，每首約 3–10 秒。"

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .widthIn(max = 400.dp)                       // ← 限制最大寬度
                .background(dialogBackground, shape)
                .border(1.dp, borderColor, shape)
                .padding(20.dp)
        ) {
            // 標題
            Text(
                text = engineName,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                fontSize = 13.sp,
                color = subTextColor
            )

            // ── 並發數選擇 ──────────────────────────
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "並發數",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "同時分析的歌曲數量。越高越快，但對設備負擔也越大。",
                fontSize = 12.sp,
                color = subTextColor
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(1, 3, 5, 10).forEach { n ->
                    ConcurrencyButton(
                        value = n,
                        isSelected = concurrency == n,
                        accentColor = accentColor,
                        onAccentColor = onAccentColor,
                        isDark = isDark,
                        modifier = Modifier.weight(1f)
                    ) { onConcurrencyChange(n) }
                }
            }

            // ── 三個選項 ────────────────────────────
            Spacer(modifier = Modifier.height(20.dp))

            DialogOptionRow(
                icon = Icons.Default.Refresh,
                text = "全量分析",
                enabled = true,
                textColor = textColor,
                backgroundColor = cardBackground,
                borderColor = borderColor
            ) { onFullScan() }

            Spacer(modifier = Modifier.height(10.dp))

            DialogOptionRow(
                icon = Icons.Default.Add,
                text = "新增分析",
                enabled = !isFirstTime,
                textColor = textColor,
                backgroundColor = cardBackground,
                borderColor = borderColor
            ) { onIncrementalScan() }

            Spacer(modifier = Modifier.height(10.dp))

            DialogOptionRow(
                icon = Icons.Default.Close,
                text = "不使用",
                enabled = true,
                textColor = textColor,
                backgroundColor = cardBackground,
                borderColor = borderColor
            ) { onDismiss() }
        }
    }
}

@Composable
private fun ConcurrencyButton(
    value: Int,
    isSelected: Boolean,
    accentColor: Color,
    onAccentColor: Color,
    isDark: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) accentColor
            else if (isDark) Color(0xFF22252B) else Color(0xFFD9D9D9),
            contentColor = if (isSelected) onAccentColor
            else if (isDark) Color.White else Color.Black
        ),
        shape = RoundedCornerShape(10.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = 0.dp, vertical = 6.dp
        )
    ) {
        Text(
            text = value.toString(),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun DialogOptionRow(
    icon: ImageVector,
    text: String,
    enabled: Boolean,
    textColor: Color,
    backgroundColor: Color,
    borderColor: Color,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    val alpha = if (enabled) 1f else 0.4f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha)
            .background(backgroundColor, shape)
            .border(1.dp, borderColor, shape)
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}