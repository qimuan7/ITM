package lkg.itm.music

import java.io.File

/**
 * 統一的響度分析介面。
 * - analyze(): 只取 LUFS
 * - analyzeWithPeak(): 取 LUFS + True Peak（Tetsu 不支持 peak，回傳 null）
 */
interface LoudnessAnalyzer {
    suspend fun analyze(file: File): Double?
    suspend fun analyzeWithPeak(file: File): Pair<Double, Double?>?
}