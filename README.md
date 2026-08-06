<img src="https://github.com/qimuan7/ITM/blob/1/Assets/LocalGrid-ITM-ICON-Rounded.png" width="20%" alt="icon">

# LocalGrid - ITM

使用 iTunes 資料庫爲來源的安卓本地音樂播放器, 在任何安卓設備上恢復你已下載的播放清單, 隨心所欲的播放

[readme: 简体中文翻译](https://github.com/qimuan7/ITM/blob/1/Assets/README_zh-Hans.md)

[readme: EnglishTranslate](https://github.com/qimuan7/ITM/blob/1/Assets/README_EN.md)

[ITM Wiki](https://github.com/qimuan7/ITM/wiki)

[ITM 平板端](https://github.com/qimuan7/ITM-Tablet)

---

## 注意:

- ITM 在設計之初只能讀取 iTunes 資源庫作爲音樂來源, 且需要可以開啓"與其他應用共享 iTunes 資源庫 XML"

- ITM 無法掃描普通音頻, 如果你想要類似UI的普通播放器, 我更推薦 FlamingoHere, Accord-Beta, SaltPlayer, LuneMusic 或 Musicolet.

- ITM 90% 的代碼都由 Google AI 編寫, 儘管經過測試, 如果你擔心穩定性或者非常不喜歡 AI 寫的內容, 請尋找其他項目, 我爲此感到抱歉.

---

## 特別功能 & 界面:

### 功能:

- iTunes音樂資源庫解析, 恢復xml中的播放清單

- 自動音量平衡

- 音頻內嵌歌詞顯示

- 應用內獨立音量調節

- 隨機播放, 循環播放, 單曲播放

- 後臺播放, 以及接入原生安卓快速設定播放組件

- 橫豎屏切換不重載

### 界面 :

- 簡潔的界面和多語言 (3.0.20+ 目前只有 簡/繁/英)

- 本地的隨機首頁推薦卡片, 本地基於收聽次數的推薦列表 (列表功能於3.0.19修復)

- 自動分類和隱藏的播放列表頁面

- 帶有模糊的播放界面

- 淺色和深色模式適配和手動切換

- 美觀的橫屏播放頁

---

## 界面展示:

<table>
  <tr>
    <td align="center"><b>首頁, 媒體頁, 設定頁</b></td>
    <td align="center"><b>播放頁</b></td>
    <td align="center"><b>橫屏更新(3.0.18+)</b></td>
  </tr>
  <tr>
    <td align="center"><img src="https://github.com/qimuan7/ITM/blob/1/Assets/ITM3013-1.jpg" width="600" alt="ITM3013-1"></td>
    <td align="center"><img src="https://github.com/qimuan7/ITM/blob/1/Assets/ITM3013-2.jpg" width="600" alt="ITM3013-2"></td>
    <td align="center"><img src="https://github.com/qimuan7/ITM/blob/1/Assets/ITM3018-1.jpg" width="300" alt="ITM3018-1"></td>
  </tr>
</table>

**- 圖片所展示的歌曲均爲個人創建的離線測試歌單, 其名稱, 藝人, 專輯, 歌詞, 封面等均屬原作者, 不包含在項目中, 你需要匯入你自己的離線歌單方可收聽**

**- 該應用界面設計參考 Flamingo, Accord, SlatPlayer, 和 AppleMusic, 但未使用其源碼, 組件, 或其他任何內容**

---

## 使用:

> 詳細內容可以前往 [Wiki](https://github.com/qimuan7/ITM/wiki) 查看, 其中包含 ITM 的使用, 也會有 iTunes 本地音樂庫的建立方法參考

### 先決條件: 

1. 你的電腦系統版本需要是 Windows 或 OSX-10.14-及以下, 確定在 iTunes **進階設定**中已開啓 **"保持 iTunes Media 資料夾整理狀態" , "複製加入的歌曲到 iTunes Media" , "與其他應用程式共享 iTunes 資料庫 XML"**

2. 安卓設備建議在 Android 10+ , 已測試版本 Android 14 (OneUI6.1) , 我不確定 FFmpeg 組件需要的系統版本, 請自行嘗試

### 開始使用:

0. 確定上述先決條件滿足, 並檢查 iTunes 文件夾**是否包含所有** 音頻 和 (iTunes Music Library.xml) 

1. 將你的電腦中的整個 iTunes 文件夾複製到安卓設備上, 存放位置只要你喜歡都可以

2. 開啓 ITM , 點擊首頁右上角頭像標誌, 點擊後將會開啓設定頁面

3. 在設定頁面選擇最上方的 "重新選擇 iTunes 根目錄資料夾" 喚起文件選擇器

4. 選擇器中, 找到你剛剛複製到手機目錄的 iTunes 文件夾, 一直點擊到 iTunes 文件夾內, 此時文件選擇器頁面應該看到 iTunes Music Library.xml, 點擊底部"選擇"

5. 等待片刻, 資料庫會載入完成

6. 載入完成後你可以正常使用

7. **但是,** 此時直接操作你大概率會感到卡頓和發熱, 這是因**音量平衡**和**歌詞掃描**組件在後臺運行, 建議停在首頁等上幾分鐘 (我的400首歌大約用時4~5分鐘), 如果有logcat也可以查看它的進度
   > (請放心只會在每個資料庫首次載入時掃描, 平常只會讀取Database和暫存來運行)

---

## 自行編譯:

> 詳細內容可以前往 [Wiki](https://github.com/qimuan7/ITM/wiki) 查看

代碼相關我大概幫不上忙, 但我會盡力寫清楚每一部分在設計之初的運作方式, 提示詞就..字數太多有用的太少, 其實按整體思路來你可以寫一個一模一樣的.

關於翻譯, 其實這個App沒有多少可以被翻譯的界面內容, 只需要根據 res/values/strings 來添加即可

---

## 引用內容 & 版權聲明:

### 外觀參考:

- (豎屏界面) Flamingo Here: github.com/Yos-X/FlamingoHere

- (播放橫屏) Salt Player: moriafly.com/program/salt-player.html

- (媒體庫頁) Accord-Beta: github.com/FoedusProgramme (作者似乎改名了並且原項目沒有了)

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

## 更多 (作者廢話時間):

### 原諒我每次寫完readme都想在後面加一段個人想法之類的東西, 雖然沒用, 但湊了個字數, 也能讓我說說話 (?).

---

關於 ITM 本身, 其實不會有太多更新, 除非哪天發現哪裏出問題了, 並且克服不穩定的狀態和懶癌, 或許就冒出來修一下. 功能上一切保持極簡 (其實是啥也沒有), 因爲太複雜的話, 我煩躁起來說不定會想把手機炸了.

不可否認我幾乎完全不懂編程, 決定寫這個東西也是一時興起就做了, 這個想法的來源只是從前我很想要一個可以播放 iTunes 資料庫的播放器 (然後就可以讓我充電兩小時, 聽歌兩分鐘的 iPod Nano 4 退休了).

也該謝謝時代發展和 AI 進步, 從前有想法只能想想然後放棄, 現在只要保持思路清晰, 而且大概理解原理, 就嘗試讓 AI 做出來, 有點像數字版的 3D-Printer, 

下一步的話..想要做適配平板的版本 (因爲我還有個平板, 但現在的界面放上去很醜), ITM 3.1-Tablet, 應該會用 3.0.19 或者 3.0.20 做基礎? 目前 3.0 就只留給手機使用, 不過效果可能不會太好, 因爲我暫時想不出來該怎麼設計交互和排布, 日常狀態也比較差.

> 請放心, 平板端會改包名爲 lkg.itm.music.tablet, 你實在想在手機上裝倆也是可以的.

不過如果你不用 iTunes, 其實大可以去用其他播放器, 功能更完善, 界面更好看, 也不會有什麼怪bug, 作者更新也會更勤力.

---

說起來, 我可能是這個時代爲數不多還在用 iTunes + 離線樂庫聽歌的人? 至於爲什麼是 iTunes, 只是我喜歡它能把所有音樂塞一起, 還能自定排序和播放清單, 並且換電腦只要複製就行不用再排一次.
> (但OSX10.15以後自帶的音樂app真的很難用, 沒有xml共享就算了, 隔一個系統大版本就新舊不兼容是什麼超絕離譜兼容性 OAO)

離線給我的安全感始終高於線上, 無論什麼平臺, 如何承諾, 我焦慮起來就是會害怕, 害怕連一首歌都要失去, 雖然是個人選擇, 不過我也要說, 爲此耗費的精力是成倍增長的, 所以普通用戶也不必追求完全的電子離線化, 我更希望沒有人會像我一樣陷在無意義的焦慮裏.

就醬, 沒啦, 感謝你看到這裏, 無論你選擇 ITM 與否, 願你今日愉快ouo!.

---



