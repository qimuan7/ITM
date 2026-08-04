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
    
    // 媒體庫狀態
    var currentLibrarySubPage by mutableStateOf<String?>(null)
    var selectedPlaylistName by mutableStateOf<String?>(null)
    var selectedArtistName by mutableStateOf<String?>(null)
    var selectedAlbumName by mutableStateOf<String?>(null)
}
