package lkg.itm.music

data class Song(
    val trackId: String,
    val title: String,
    val artist: String,
    val album: String,
    val androidPath: String,
    val ffmpegLoudness: Double? = null,
    val ffmpegPeak: Double? = null,
    val lyrics: String? = null
)