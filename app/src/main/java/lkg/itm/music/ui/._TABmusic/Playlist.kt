package lkg.itm.music

data class Playlist(
    val name: String,
    val songs: List<Song>,
    val isSysDefault: Boolean = false
)