package lkg.itm.music

import android.content.Context
import kotlin.math.pow

class VolumePreferences(context: Context) {
    private val prefs = context.getSharedPreferences("volume_prefs", Context.MODE_PRIVATE)

    fun getVolume(): Int {
        return prefs.getInt("app_volume", 100)
    }

    fun setVolume(volume: Int) {
        prefs.edit().putInt("app_volume", volume).apply()
    }

    fun isNormalizationEnabled(): Boolean {
        return prefs.getBoolean("volume_normalization", false)
    }

    fun setNormalizationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("volume_normalization", enabled).apply()
    }

    fun isShuffleEnabled(): Boolean {
        return prefs.getBoolean("shuffle_enabled", false)
    }

    fun setShuffleEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("shuffle_enabled", enabled).apply()
    }

    fun getRepeatMode(): Int {
        return prefs.getInt("repeat_mode", 0) // 0: None, 1: All, 2: One (Media3 constants match these values)
    }

    fun setRepeatMode(mode: Int) {
        prefs.edit().putInt("repeat_mode", mode).apply()
    }

    /**
     * 【核心相乘邏輯】
     * @param songLoudnessDb 當前歌曲的響度值 (dB)，例如從資料庫讀取的 loudness
     * 計算公式：最終輸出音量 = (用戶設定音量 / 100f) × 歌曲平衡增益倍數
     */
    fun calculateEffectiveVolume(songLoudnessDb: Double?): Float {
        // 1. 取得用戶手動設定的音量 (0 ~ 100 轉為 0.0f ~ 1.0f)
        val userVolume = getVolume() / 100f

        // 如果音量平衡關閉，直接返回用戶音量
        if (!isNormalizationEnabled()) {
            return userVolume
        }

        // 2. 計算歌曲平衡增益倍數 (將 dB 轉為線性乘數: 10^(dB / 20))
        val trackGainMultiplier = if (songLoudnessDb != null) {
            10.0.pow(songLoudnessDb / 20.0).toFloat()
        } else {
            1.0f // 沒有數據時預設不增減
        }

        // 3. 【用戶音量 × 歌曲平衡增益】相乘後限制在 0.0f ~ 1.0f 之間
        val finalVolume = userVolume * trackGainMultiplier
        return finalVolume.coerceIn(0.0f, 1.0f)
    }
}