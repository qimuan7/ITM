package lkg.itm.music

data class Song(
    val trackId: String,
    val title: String,
    val artist: String,
    val album: String,
    val androidPath: String,
    val loudness: Double? = null,
    val peak: Double? = null,
    val lyrics: String? = null
)
