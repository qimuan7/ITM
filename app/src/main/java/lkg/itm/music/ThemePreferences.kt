package lkg.itm.music

import android.content.Context

class ThemePreferences(context: Context) {
    private val prefs = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)

    // 儲存選項: "system", "light", "dark"
    fun getThemeMode(): String {
        return prefs.getString("theme_mode", "system") ?: "system"
    }

    fun setThemeMode(mode: String) {
        prefs.edit().putString("theme_mode", mode).apply()
    }

    // 儲存選項: "auto", "phone", "tablet"
    fun getUiMode(): String {
        return prefs.getString("ui_mode", "auto") ?: "auto"
    }

    fun setUiMode(mode: String) {
        prefs.edit().putString("ui_mode", mode).apply()
    }

    // 儲存選項: "off", "ffmpeg"
    fun getLoudnessEngine(): String {
        return prefs.getString("loudness_engine", "off") ?: "off"
    }

    fun setLoudnessEngine(engine: String) {
        prefs.edit().putString("loudness_engine", engine).apply()
    }

    // 掃描並發數（1~20），預設 3
    fun getLoudnessConcurrency(): Int {
        return prefs.getInt("loudness_concurrency", 3)
    }

    fun setLoudnessConcurrency(value: Int) {
        prefs.edit().putInt("loudness_concurrency", value.coerceIn(1, 20)).apply()
    }
}