<img src="https://github.com/qimuan7/ITM/blob/itm-3.2/Assets/LocalGrid-ITM-ICON-Rounded.png" width="20%" alt="icon">

# LocalGrid - ITM

使用 iTunes 資料庫爲來源的安卓本地音樂播放器, 在任何安卓設備上恢復你已下載的播放清單, 隨心所欲的播放

[readme: 简体中文翻译](https://github.com/qimuan7/ITM/blob/itm-3.2/Assets/README_zh-Hans.md)

[readme: EnglishTranslate](https://github.com/qimuan7/ITM/blob/itm-3.2/Assets/README_EN.md)

[ITM Wiki](https://github.com/qimuan7/ITM/wiki)

ITM 新版下載: Release 3.2.1+

> ITM-New-3.2 合併 ITM-Phone 和 ITM-Tablet, 並做了一些小更新, 所以就不再分開維護原來的兩個不同版本了

> ITM 手機端(舊版): github.com/qimuan7/ITM/tree/phone-3.0 ; Release 3.0.13 ~ 3.0.20

> ITM 平板端(舊版): github.com/qimuan7/ITM/tree/tablet-3.1 ; Release 3.1.12.FC ~ 3.1.20_11

---

## 注意:

- ITM 在設計之初只能讀取 iTunes 資源庫作爲音樂來源, 且需要可以開啓"與其他應用共享 iTunes 資源庫 XML"

- ITM 無法掃描普通音頻, 如果你想要類似UI的普通播放器, 我更推薦 FlamingoHere, Accord-Beta, SaltPlayer, LuneMusic 或 Musicolet.

- ITM 90% 的代碼都由 Google AI 以及 Deepseek 輔助編寫, 儘管經過測試, 如果你擔心穩定性或者非常不喜歡 AI 寫的內容, 請尋找其他項目, 我爲此感到抱歉.

---

## 特別功能 & 界面:

### 功能:

- iTunes音樂資源庫解析, 恢復xml中的播放清單

- 音量平衡, 可以開啓或關閉, 並支援更多掃描選項

- 音頻內嵌歌詞顯示

- 應用內獨立音量調節

- 隨機播放, 循環播放, 單曲播放

- 後臺播放, 以及接入原生安卓快速設定播放組件

- 橫豎屏切換不重載

### 界面 :

- 允許切換手機或平板視圖, 配合簡潔的界面設計

- 多語言 (3.0.20 和 3.1.20_11 有 簡/繁/英, 新版本 3.2.1 已經移除多語言)

- 本地的隨機首頁推薦卡片, 本地基於收聽次數的推薦列表 (列表功能於3.0.19修復)

- 自動分類和隱藏的播放列表頁面

- 帶有模糊的播放界面

- 淺色和深色模式適配和手動切換

- 美觀的橫屏播放頁

---

## 界面展示:

### 手機視圖展示:

<table>
  <tr>
    <td align="center"><b>首頁, 媒體頁, 設定頁</b></td>
    <td align="center"><b>播放頁</b></td>
    <td align="center"><b>橫屏更新(3.0.18+)</b></td>
  </tr>
  <tr>
    <td align="center"><img src="https://github.com/qimuan7/ITM/blob/itm-3.2/Assets/ITM3013-1.jpg" width="600" alt="ITM3013-1"></td>
    <td align="center"><img src="https://github.com/qimuan7/ITM/blob/itm-3.2/Assets/ITM3013-2.jpg" width="600" alt="ITM3013-2"></td>
    <td align="center"><img src="https://github.com/qimuan7/ITM/blob/itm-3.2/Assets/ITM3018-1.jpg" width="300" alt="ITM3018-1"></td>
  </tr>
</table>

---

### 平板視圖展示:

<table>
  <tr>
    <td align="center"><b>橫屏首頁</b></td>
    <td align="center"><b>橫屏播放頁+歌詞</b></td>
    <td align="center"><b>橫屏播放列表+音量條</b></td>
    <td align="center"><b>橫屏歌曲頁面</b></td>
  </tr>
  <tr>
    <td align="center"><img src="https://github.com/qimuan7/ITM/blob/tablet-3.1/Assets/ITM-Tab-land1.png" width="300" alt="land-home"></td>
    <td align="center"><img src="https://github.com/qimuan7/ITM/blob/tablet-3.1/Assets/ITM-Tab-land2.png" width="300" alt="land-pylrc"></td>
    <td align="center"><img src="https://github.com/qimuan7/ITM/blob/tablet-3.1/Assets/ITM-Tab-land3.png" width="300" alt="land-pylisvo"></td>
    <td align="center"><img src="https://github.com/qimuan7/ITM/blob/tablet-3.1/Assets/ITM-Tab-land4.png" width="300" alt="land-music"></td>
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
    <td align="center"><img src="https://github.com/qimuan7/ITM/blob/tablet-3.1/Assets/ITM-Tab-vert1.png" width="400" alt="vert-home"></td>
    <td align="center"><img src="https://github.com/qimuan7/ITM/blob/tablet-3.1/Assets/ITM-Tab-vert2.png" width="400" alt="vert-player"></td>
    <td align="center"><img src="https://github.com/qimuan7/ITM/blob/tablet-3.1/Assets/ITM-Tab-vert3.png" width="400" alt="vert-pylisvo"></td>
    <td align="center"><img src="https://github.com/qimuan7/ITM/blob/tablet-3.1/Assets/ITM-Tab-vert4.png" width="400" alt="verrt-music"></td>
  </tr>
</table>

---

### 新版更新內容:

<table>
  <tr>
    <td align="center"><b>音量平衡加載, 音量平衡掃描選項, 平板視圖切換(手機端無法正常用)</b></td>
  </tr>
  <tr>
    <td align="center"><img src="https://github.com/qimuan7/ITM/blob/itm-3.2/Assets/ITM3210-2.jpg" width="1000" alt="3210-2"></td>
  </tr>
</table>

---

**- 圖片所展示的歌曲均爲個人創建的離線測試歌單, 其名稱, 藝人, 專輯, 歌詞, 封面等均屬原作者, 不包含在項目中, 你需要匯入你自己的離線歌單方可收聽**

**- 該應用界面設計參考 Flamingo, Accord, SlatPlayer, 和 AppleMusic, 但未使用其源碼, 組件, 或其他任何內容**

---

## 使用:

> 詳細內容可以前往 [Wiki](https://github.com/qimuan7/ITM/wiki) 查看, 其中包含 ITM 的使用, 構建, 也會有 iTunes 本地音樂庫的建立方法參考

### 先決條件: 

1. 你的電腦系統版本需要是 Windows 或 OSX-10.14-及以下 (10.15+ 請查看 Wiki), 確定在 iTunes **進階設定**中已開啓 **"保持 iTunes Media 資料夾整理狀態" , "複製加入的歌曲到 iTunes Media" , "與其他應用程式共享 iTunes 資料庫 XML"**

2. 安卓設備建議在 Android 10+ , 已測試版本 Android 14 (OneUI6.1) , 我不確定 FFmpeg 組件需要的系統版本, 請自行嘗試

### 開始使用:

0. 確定上述先決條件滿足, 並檢查 iTunes 文件夾**是否包含所有** 音頻 和 (iTunes Music Library.xml) 

1. 將你的電腦中的整個 iTunes 文件夾複製到安卓設備上, 存放位置只要你喜歡都可以

2. 開啓 ITM , 點擊首頁右上角, 或側欄上方的頭像標誌, 點擊後將會開啓設定頁面

3. 在設定頁面選擇最上方的 "重新選擇 iTunes 根目錄資料夾" 喚起文件選擇器

4. 選擇器中, 找到你剛剛複製到手機目錄的 iTunes 文件夾, 一直點擊到 iTunes 文件夾內, 此時文件選擇器頁面應該看到 iTunes Music Library.xml, 點擊底部"選擇"

5. 等待片刻, 資料庫會載入完成

6. 載入完成後你可以正常使用

7. 如果你想使用音量平衡, 請切換到"開啓", 使用默認數值並按下確認, 稍等一會, 可以去喝杯奶茶

8. 如果你想要切換不同的視圖, 你可以在設定中找到, 手機視圖主題色是紅色, 平板視圖是黑白, 只需要點一下即可切換, 但不要在手機上使用平板視圖, 原因你試試就知道了...~

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

## 更多 (作者廢話時間+3):

### 原諒我每次寫完readme都想在後面加一段個人想法之類的東西, 雖然沒用, 但湊了個字數, 也能讓我說說話 (?).

---

腦子一熱想把兩個項目合併, 嗯好處是以後只需要更新一版, 壞消息是沒有更多好消息. 或許還在密謀更多奇怪小應用?..

我也在思考我寫多一個鍵盤, 寫多一個日記, 日曆, 記賬, 手機助理, 翻譯, 這些真的會有人用麼, 雖然像是廢話, ITM 都不見得受衆很廣對吧.

不過我覺得我還是會耐不住性子寫, 那就當完成心願吧 ~. 你看, 想做什麼就去做, 你也是 0w<.

---

Hmm, 最近 Gemini 又不知道做什麼說我所在地不可用, 然後連歷史記錄都不給我看... 有點煩, 不過 DS 是真的很強, 用 GeminiAgent 十份一的時間就改完我想要的內容, 還是只有對話框的前提下.

好吧也再次提醒我, 其實沒有 AI 我什麼都不是, 或許只是有一堆沒用想法的小廢物.

生活不太好, 學習不太好, 最近還在開學第四禮拜請了長假, 很身體難受, 但請多一天就焦慮一天, 不知道怎麼辦 (擺爛_(:_/&)_, 醫院排號排到下月了, 或許他們比我更值得去.

願世界無災吧, 雖然很空. 也希望我能開心, 不開心也可以, 但我想不再亂想...

願你今日開心ouo, 有時間要好好照顧自己.

---



