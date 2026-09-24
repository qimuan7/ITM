package lkg.itm.music

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var selectedNavItem by mutableIntStateOf(0)
    var isPlayerExpanded by mutableStateOf(false)
    var isSettingsOpen by mutableStateOf(false)

    // 音樂數據快取
    var songs by mutableStateOf<List<Song>>(emptyList())
    var playlists by mutableStateOf<List<Playlist>>(emptyList())
    var isLoading by mutableStateOf(true)
    var isLibraryLoaded by mutableStateOf(false) // 新增：標記是否已嘗試從資料庫加載
    
    // 媒體庫狀態
    var currentLibrarySubPage by mutableStateOf<String?>(null)
    var selectedPlaylistName by mutableStateOf<String?>(null)
    var selectedArtistName by mutableStateOf<String?>(null)
    var selectedAlbumName by mutableStateOf<String?>(null)
}
