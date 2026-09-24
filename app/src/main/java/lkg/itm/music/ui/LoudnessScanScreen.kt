package lkg.itm.music.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.work.WorkInfo
import androidx.work.WorkManager
import java.util.UUID

@Composable
fun LoudnessScanScreen(
    workId: UUID,
    currentThemeMode: String,
    onCancel: () -> Unit,
    onFinished: () -> Unit
) {
    val context = LocalContext.current
    val workManager = remember { WorkManager.getInstance(context) }

    val workInfo by workManager.getWorkInfoByIdFlow(workId)
        .collectAsState(initial = null)

    LaunchedEffect(workInfo?.state) {
        when (workInfo?.state) {
            WorkInfo.State.SUCCEEDED,
            WorkInfo.State.FAILED,
            WorkInfo.State.CANCELLED -> onFinished()
            else -> Unit
        }
    }

    val isDark = when (currentThemeMode) {
        "light" -> false
        "dark" -> true
        else -> isSystemInDarkTheme()
    }
    val backgroundColor = if (isDark) Color.Black else Color(0xFFF5F5F5)
    val textColor       = if (isDark) Color.White else Color.Black
    val subTextColor    = Color.Gray
    val trackColor      = if (isDark) Color(0xFF2C2C2C) else Color(0xFFD9D9D9)
    val accentColor     = if (isDark) Color.White else Color.Black

    val current = workInfo?.progress?.getInt("current", 0) ?: 0
    val total = workInfo?.progress?.getInt("total", 0) ?: 0
    val title = workInfo?.progress?.getString("title") ?: ""
    val progress = if (total > 0) current.toFloat() / total else 0f
    val percent = (progress * 100).toInt()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            Text(
                text = "正在掃描音量平衡…",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$current / $total",
                fontSize = 14.sp,
                color = subTextColor
            )
            Spacer(modifier = Modifier.height(28.dp))

            // 大號百分比
            Text(
                text = "$percent%",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Spacer(modifier = Modifier.height(24.dp))

            // 線性進度條
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                color = accentColor,
                trackColor = trackColor,
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "當前：$title",
                fontSize = 13.sp,
                color = subTextColor,
                maxLines = 1,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onCancel,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = trackColor,
                    contentColor = textColor
                )
            ) {
                Text("取消", fontWeight = FontWeight.Medium)
            }
        }
    }
}