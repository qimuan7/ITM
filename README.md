<img src="(https://github.com/qimuan7/ITM/blob/tablet-3.1/Assets/LocalGrid-ITM-Tablet-ICON-Rounded.png)" width="20%" alt="icon">

# LocalGrid - ITM Tablet

---

使用 iTunes 資料庫爲來源的安卓本地音樂播放器, 專爲安卓平板設計的極簡界面, 方便的恢復你所有的 iTunes 離線播放清單, 隨心所欲的播放

[readme: 简体中文翻译](https://github.com/qimuan7/ITM/blob/tablet-3.1/Assets/README_zh-Hans.md)

[readme: EnglishTranslate](https://github.com/qimuan7/ITM/blob/tablet-3.1/Assets/README_EN.md)

[ITM Wiki](https://github.com/qimuan7/ITM/wiki)

[ITM 3.2 New](https://github.com/qimuan7/ITM)

> ITM-3.2 將原來的 ITM-Phone 和 ITM-Tablet 合併爲同一個項目, 保留兩種界面以便隨時切換

[ITM 手機端 (舊版Readme)](https://github.com/qimuan7/ITM/tree/phone-3.0)

---

## 注意:

- ITM 在設計之初只能讀取 iTunes 資源庫作爲音樂來源, 且需要可以開啓"與其他應用共享 iTunes 資源庫 XML"

- ITM 無法掃描普通音頻, 如果你想要類似UI的普通播放器, 我更推薦 FlamingoHere, SaltPlayer, 或 Musicolet.

- ITM 90% 的代碼都由 Google AI 編寫, 儘管經過測試, 如果你擔心穩定性或者非常不喜歡 AI 寫的內容, 請尋找其他項目, 我爲此感到抱歉.

---

## 特別功能 & 界面:

### 功能:

> - iTunes音樂資源庫解析, 恢復xml中的播放清單

> - 自動音量平衡

> - 音頻內嵌歌詞顯示

> - 應用內獨立音量調節

> - 隨機播放, 循環播放, 單曲播放

> - 後臺播放, 以及接入原生安卓快速設定播放組件

> - 橫豎屏切換不重載

### 界面 :

- 重新設計的平板 UI, 帶有側欄, 以及更多爲平板優化的顯示細節

- 改用黑白主色的界面, 內含多語言 (3.1.20_11+ 目前只有 簡/繁/英)

> - 本地的隨機首頁推薦卡片, 本地基於收聽次數的推薦列表 (列表功能於3.0.19修復)

> - 自動分類和隱藏的播放列表頁面

- 爲橫豎屏分別重新設計的, 帶有模糊的播放界面

> - 淺色和深色模式適配和手動切換

> - 美觀的橫屏播放頁

---

## 界面展示:

<table>
  <tr>
    <td align="center"><b>橫屏首頁</b></td>
    <td align="center"><b>橫屏播放頁+歌詞</b></td>
    <td align="center"><b>橫屏播放列表+音量條</b></td>
    <td align="center"><b>橫屏歌曲頁面</b></td>
  </tr>
  <tr>
    <td align="center"><img src="https://github.com/qimuan7/ITM-Tablet/blob/1/Assets/ITM-Tab-land1.png" width="300" alt="land-home"></td>
    <td align="center"><img src="https://github.com/qimuan7/ITM-Tablet/blob/1/Assets/ITM-Tab-land2.png" width="300" alt="land-pylrc"></td>
    <td align="center"><img src="https://github.com/qimuan7/ITM-Tablet/blob/1/Assets/ITM-Tab-land3.png" width="300" alt="land-pylisvo"></td>
    <td align="center"><img src="https://github.com/qimuan7/ITM-Tablet/blob/1/Assets/ITM-Tab-land4.png" width="300" alt="land-music"></td>
  </tr>
</table>

<table>
  <tr>
    <td align="center"><b>豎屏首頁</b></td>
    <td align="center"><b>豎屏播放頁</b></td>
    <td align="center"><b>豎屏播放列表+音量條</b></td>
    <td align="center"><b>豎屏歌曲頁面</b></td>
  </tr>
  <tr>
    <td align="center"><img src="https://github.com/qimuan7/ITM-Tablet/blob/1/Assets/ITM-Tab-vert1.png" width="400" alt="vert-home"></td>
    <td align="center"><img src="https://github.com/qimuan7/ITM-Tablet/blob/1/Assets/ITM-Tab-vert2.png" width="400" alt="vert-player"></td>
    <td align="center"><img src="https://github.com/qimuan7/ITM-Tablet/blob/1/Assets/ITM-Tab-vert3.png" width="400" alt="vert-pylisvo"></td>
    <td align="center"><img src="https://github.com/qimuan7/ITM-Tablet/blob/1/Assets/ITM-Tab-vert4.png" width="400" alt="verrt-music"></td>
  </tr>
</table>

**- 展示的設備由於界面縮放較小, 顯示的樣式可能與你的設備有所不同**

**- 圖片所展示的歌曲均爲個人創建的離線測試歌單, 其名稱, 藝人, 專輯, 歌詞, 封面等均屬原作者, 不包含在項目中, 你需要匯入你自己的離線歌單方可收聽**

**- 該應用界面設計參考 Flamingo, SlatPlayer, 和 AppleMusic, 但未使用其源碼, 組件, 或其他任何內容**

---

## 使用:

> 詳細內容可以前往 [ITM Wiki](https://github.com/qimuan7/ITM/wiki) 查看, 其中包含 ITM 的使用, 也會有 iTunes 本地音樂庫的建立方法參考

### 先決條件: 

1. 你的電腦系統版本需要是 Windows 或 OSX-10.14-及以下 (OSX 10.15+ 可以看 Wiki), 確定在 iTunes **進階設定**中已開啓 **"保持 iTunes Media 資料夾整理狀態" , "複製加入的歌曲到 iTunes Media" , "與其他應用程式共享 iTunes 資料庫 XML"**

2. 安卓設備建議在 Android 10+ , 已測試版本 11inch: Android 14 (OneUI-Tab-6.1) 以及 8.0inch: Android 16 (LineageOS-Tab-23) , 我不確定 FFmpeg 組件需要的系統版本, 請自行嘗試

### 開始使用:

0. 確定上述先決條件滿足, 並檢查 iTunes 文件夾**是否包含所有** 音頻 和 (iTunes Music Library.xml) 

1. 將你的電腦中的整個 iTunes 文件夾複製到安卓設備上, 存放位置只要你喜歡都可以

2. 開啓 ITM Tablet , 點擊**位於側欄**右上角的頭像標誌, 點擊後將會開啓設定頁面

3. 在設定頁面選擇最上方的 "重新選擇 iTunes 根目錄資料夾" 喚起文件選擇器

4. 選擇器中, 找到你剛剛複製到手機目錄的 iTunes 文件夾, 一直點擊到 iTunes 文件夾內, 此時文件選擇器頁面應該看到 iTunes Music Library.xml, 點擊底部"選擇"

5. 等待片刻, 資料庫會載入完成

6. 載入完成後你可以正常使用

7. **但是,** 此時直接操作你大概率會感到卡頓和發熱, 這是因**音量平衡**和**歌詞掃描**組件在後臺運行, 建議停在首頁等上幾分鐘 (我的400首歌大約用時4~5分鐘), 如果有logcat也可以查看它的進度
   > (請放心只會在每個資料庫首次載入時掃描, 平常只會讀取Database和暫存來運行)

---

## 自行編譯:

> 詳細內容可以前往 ITM [Wiki](https://github.com/qimuan7/ITM/wiki) 查看

代碼相關我大概幫不上忙, 但我會盡力寫清楚每一部分在設計之初的運作方式, 提示詞就..字數太多有用的太少, 其實按整體思路來你可以寫一個一模一樣的.

**平板端 (ITM-Tablet) 基於手機端 (ITM) 更改, 只是 UI 不一樣, 其餘一切通用**

關於翻譯, 其實這個App沒有多少可以被翻譯的界面內容, 只需要根據 res/values/strings 來添加即可

---

## 引用內容 & 版權聲明:

### 外觀參考:

- (豎屏界面) Flamingo Here: github.com/Yos-X/FlamingoHere

- (播放橫屏) Salt Player: moriafly.com/program/salt-player.html

  > 只下載和參考界面樣式, 未使用其代碼 (其實即使有我也看不懂)

### 使用組件 (部分):

- Material-Icons

- DocumentFiles

- ExoPlayer (Media3)

- RoomDB

- Coil

- Palettle

- FFmpeg-kit-min: io.github.maitrungduc1410

- JAudioTagger

- Jetpack Compose ViewModel

- MORE...

---

[README END]

---

## 更多 (作者廢話時間+1):

### 原諒我每次寫完readme都想在後面加一段個人想法之類的東西, 雖然沒用, 但湊了個字數, 也能讓我說說話 (?).

---

繼 ITM 後不到一個星期我又閒不住, 給自己的平板也寫了一個, 現在手機平板都看的順眼多了.

同時我也找到了一個可以在 MacOS X 10.15+ 安裝 iTunes 的辦法, 在 Github 上有一個項目叫 [Retroactive](https://github.com/cormiertyshawn895/Retroactive) , 你可以點擊查看它.

它會修改 iTunes 並能讓你成功在高版本 OSX 裝上 iTunes, 我的 MBP 2017 x64 OSX11 測試是可以的, 最新版本在 iTunes 12.9.5.5, 如果直接把 Windows 的 iTunes_12.13.1.3+ 資料庫移過來會提升版本過低, hmm唯一辦法就是在這邊再重建一次資料庫了 0.0)

不過至少是能用了, 感謝項目大佬 ouo! .

然後關於設計 UI 改動, 其實本來想做類 AppleMusic 在 iPad 上的風格, 但我調整完功能實在太累, 一轉頭看到想拿來做電子書的小平板, 所以就改成了有點像水墨屏的黑白+外框風格, 簡潔倒是簡潔了, 不過可能會有點單調, 不喜歡的話只要把邊框顏色從白色調成透明, 加上選擇框背景顏色, 再給圖標換個色就好 (其實就是改色).

對於這個版本, 這次依舊決定基於 ITM_3.0.19, 因爲我不需要多語言, 所以直接寫 UI 裏是最穩定的做法, 但 ITM-Tab_3.1.20_11 不是基於 ITM_3.0.20 做的, 只是 19_10 後的一個改版, 它把文字換成了字符串, 所以支持多語言 (說起來 ITM_3.0.20 也是這麼做的), 不過側欄似乎有一部分沒被翻譯 (惱), 不過也不太影響使用 (其實是我懶).

然後就沒了?.. 噢對, 最近南方地區的天氣終於天晴了 ouo! 不過很熱 0n0 , 各位處在世界各地都記得照顧好自己. 最後無論你是否使用 ITM, 願你今日愉快 owo!

---



