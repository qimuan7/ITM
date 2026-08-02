<img src="https://github.com/qimuan7/ITM/blob/1/Assets/LocalGrid-ITM-ICON-Rounded.png" width="20%" alt="icon">

# LocalGrid - ITM

使用 iTunes 資料庫爲來源的安卓本地音樂播放器, 在任何安卓設備上恢復你已下載的播放清單, 隨心所欲的播放

[readme: 简体中文翻译]()

[readme: EnglishTranslate]()

---

## 注意:

- ITM 在設計之初只能讀取 iTunes 資源庫作爲音樂來源, 且需要可以開啓"與其他應用共享 iTunes 資源庫 XML"

- ITM 無法掃描普通音頻, 如果你想要類似UI的普通播放器, 我更推薦 FlamingoHere, Accord-Beta, SaltPlayer, LuneMusic 或 Musicolet.

- ITM 內目前只有繁體中文, 需要界面多語言的用戶可以考慮自行修改添加後編譯, 或者等我研究一下 (orz.

- ITM 90% 的代碼都由 Google AI 編寫, 儘管經過測試, 如果你擔心穩定性或者非常不喜歡 AI 寫的內容, 請尋找其他項目, 我爲此感到抱歉.

---

## 特別功能 & 界面:

- 解析 iTunes Music Library.xml 並準確恢復你所有的本地歌單

- 自動音量平衡

- 應用內額外單獨調節音量

- 音頻(MP3)內嵌歌詞顯示

- 隨機播放, 列表循環, 單曲循環

- 自動歸類播放清單

- 基於本地可更換的首頁推薦卡片, 根據習慣列出聽得最多的歌曲

- 帶有模糊背景的播放頁

- 淺色和深色模式適配, 允許手動切換

- 簡潔的類 AppleMusic 風格界面

- 更多...(其實沒了只是湊字數)

---

## 界面展示:

<table>
  <tr>
    <td align="center"><b>首頁, 媒體頁, 設定頁</b></td>
    <td align="center"><b>播放頁</b></td>
    <td align="center"><b>橫屏更新(3.0.18+)</b></td>
  </tr>
  <tr>
    <td align="center"><img src="https://github.com/qimuan7/ITM/blob/1/Assets/ITM3013-1.jpg" width="500" alt="ITM3013-1"></td>
    <td align="center"><img src="https://github.com/qimuan7/ITM/blob/1/Assets/ITM3013-2.jpg" width="500" alt="ITM3013-2"></td>
    <td align="center"><img src="https://github.com/qimuan7/ITM/blob/1/Assets/ITM3018-1.jpg" width="400" alt="ITM3018-1"></td>
  </tr>
</table>

**- 圖片所展示的歌曲均爲個人創建的離線測試歌單, 其名稱, 藝人, 專輯, 歌詞, 封面等均屬原作者, 不包含在項目中, 你需要匯入你自己的離線歌單方可收聽**

**- 該應用界面設計參考 Flamingo, Accord, SlatPlayer, 和 AppleMusic, 但未使用其源碼, 組件, 或其他任何內容**

---

## 使用:

### 先決條件: 

1. 你的電腦系統版本需要是 Windows 或 OSX-10.13-及以下, 確定在 iTunes **進階設定**中已開啓 **"保持 iTunes Media 資料夾整理狀態" , "複製加入的歌曲到 iTunes Media" , "與其他應用程式共享 iTunes 資料庫 XML"**

2. 安卓設備建議在 Android 10+ , 已測試版本 Android 14 (OneUI6.1) , 我不確定 FFmpeg 組件需要的系統版本, 請自行嘗試

### 獲取 iTunes Music Library.xml :

1. 






