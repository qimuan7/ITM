<img src="https://github.com/qimuan7/ITM/blob/itm-3.2/Assets/LocalGrid-ITM-ICON-Rounded.png" width="20%" alt="icon">

# LocalGrid - ITM

使用 iTunes 资料库为来源的安卓本地音乐播放器, 在任何安卓设备上恢复你已下载的播放清单, 随心所欲的播放

[readme: 简体中文翻译](https://github.com/qimuan7/ITM/blob/itm-3.2/Assets/README_zh-Hans.md)

[readme: EnglishTranslate](https://github.com/qimuan7/ITM/blob/itm-3.2/Assets/README_EN.md)

[ITM Wiki](https://github.com/qimuan7/ITM/wiki)

ITM 新版下载: Release 3.2.1+

> ITM-New-3.2 合并 ITM-Phone 和 ITM-Tablet, 并做了一些小更新, 所以就不再分开维护原来的两个不同版本了

> ITM 手机端(旧版): github.com/qimuan7/ITM/tree/phone-3.0 ; Release 3.0.13 ~ 3.0.20

> ITM 平板端(旧版): github.com/qimuan7/ITM/tree/tablet-3.1 ; Release 3.1.12.FC ~ 3.1.20_11

---

## 注意:

- ITM 在设计之初只能读取 iTunes 资源库作为音乐来源, 且需要可以开启"与其他应用共享 iTunes 资源库 XML"

- ITM 无法扫描普通音频, 如果你想要类似UI的普通播放器, 我更推荐 FlamingoHere, Accord-Beta, SaltPlayer, LuneMusic 或 Musicolet.

- ITM 90% 的代码都由 Google AI 以及 Deepseek 辅助编写, 尽管经过测试, 如果你担心稳定性或者非常不喜欢 AI 写的内容, 请寻找其他项目, 我为此感到抱歉.

---

## 特别功能 & 界面:

### 功能:

- iTunes音乐资源库解析, 恢复xml中的播放清单

- 音量平衡, 可以开启或关闭, 并支援更多扫描选项

- 音频内嵌歌词显示

- 应用内独立音量调节

- 随机播放, 循环播放, 单曲播放

- 后台播放, 以及接入原生安卓快速设定播放组件

- 横竖屏切换不重载

### 界面 :

- 允许切换手机或平板视图, 配合简洁的界面设计

- 多语言 (3.0.20 和 3.1.20_11 有 简/繁/英, 新版本 3.2.1 已经移除多语言)

- 本地的随机首页推荐卡片, 本地基于收听次数的推荐列表 (列表功能于3.0.19修复)

- 自动分类和隐藏的播放列表页面

- 带有模糊的播放界面

- 浅色和深色模式适配和手动切换

- 美观的横屏播放页

---

## 界面展示:

### 手机视图展示:

<table>
<tr>
<td align="center"><b>首页, 媒体页, 设定页</b></td>
<td align="center"><b>播放页</b></td>
<td align="center"><b>横屏更新(3.0.18+)</b></td>
</tr>
<tr>
<td align="center"><img src="https://github.com/qimuan7/ITM/blob/itm-3.2/Assets/ITM3013-1.jpg" width="600" alt="ITM3013-1"></td>
<td align="center"><img src="https://github.com/qimuan7/ITM/blob/itm-3.2/Assets/ITM3013-2.jpg" width="600" alt="ITM3013-2"></td>
<td align="center"><img src="https://github.com/qimuan7/ITM/blob/itm-3.2/Assets/ITM3018-1.jpg" width="300" alt="ITM3018-1"></td>
</tr>
</table>

---

### 平板视图展示:

<table>
<tr>
<td align="center"><b>横屏首页</b></td>
<td align="center"><b>横屏播放页+歌词</b></td>
<td align="center"><b>横屏播放列表+音量条</b></td>
<td align="center"><b>横屏歌曲页面</b></td>
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
<td align="center"><b>竖屏首页</b></td>
<td align="center"><b>竖屏播放页</b></td>
<td align="center"><b>竖屏播放列表+音量条</b></td>
<td align="center"><b>竖屏歌曲页面</b></td>
</tr>
<tr>
<td align="center"><img src="https://github.com/qimuan7/ITM/blob/tablet-3.1/Assets/ITM-Tab-vert1.png" width="400" alt="vert-home"></td>
<td align="center"><img src="https://github.com/qimuan7/ITM/blob/tablet-3.1/Assets/ITM-Tab-vert2.png" width="400" alt="vert-player"></td>
<td align="center"><img src="https://github.com/qimuan7/ITM/blob/tablet-3.1/Assets/ITM-Tab-vert3.png" width="400" alt="vert-pylisvo"></td>
<td align="center"><img src="https://github.com/qimuan7/ITM/blob/tablet-3.1/Assets/ITM-Tab-vert4.png" width="400" alt="verrt-music"></td>
</tr>
</table>

---

### 新版更新内容:

<table>
<tr>
<td align="center"><b>音量平衡加载, 音量平衡扫描选项, 平板视图切换(手机端无法正常用)</b></td>
</tr>
<tr>
<td align="center"><img src="https://github.com/qimuan7/ITM/blob/itm-3.2/Assets/ITM3210-2.jpg" width="1000" alt="3210-2"></td>
</tr>
</table>

---

**- 图片所展示的歌曲均为个人创建的离线测试歌单, 其名称, 艺人, 专辑, 歌词, 封面等均属原作者, 不包含在项目中, 你需要汇入你自己的离线歌单方可收听**

**- 该应用界面设计参考 Flamingo, Accord, SlatPlayer, 和 AppleMusic, 但未使用其源码, 组件, 或其他任何内容**

---

## 使用:

> 详细内容可以前往 [Wiki](https://github.com/qimuan7/ITM/wiki) 查看, 其中包含 ITM 的使用, 构建, 也会有 iTunes 本地音乐库的建立方法参考

### 先决条件:

1. 你的电脑系统版本需要是 Windows 或 OSX-10.14-及以下 (10.15+ 请查看 Wiki), 确定在 iTunes **进阶设定**中已开启 **"保持 iTunes Media 资料夹整理状态" , "复制加入的歌曲到 iTunes Media" , "与其他应用程式共享 iTunes 资料库 XML"**

2. 安卓设备建议在 Android 10+ , 已测试版本 Android 14 (OneUI6.1) , 我不确定 FFmpeg 组件需要的系统版本, 请自行尝试

### 开始使用:

0. 确定上述先决条件满足, 并检查 iTunes 文件夹**是否包含所有** 音频 和 (iTunes Music Library.xml)

1. 将你的电脑中的整个 iTunes 文件夹复制到安卓设备上, 存放位置只要你喜欢都可以

2. 开启 ITM , 点击首页右上角, 或侧栏上方的头像标志, 点击后将会开启设定页面

3. 在设定页面选择最上方的 "重新选择 iTunes 根目录资料夹" 唤起文件选择器

4. 选择器中, 找到你刚刚复制到手机目录的 iTunes 文件夹, 一直点击到 iTunes 文件夹内, 此时文件选择器页面应该看到 iTunes Music Library.xml, 点击底部"选择"

5. 等待片刻, 资料库会载入完成

6. 载入完成后你可以正常使用

7. 如果你想使用音量平衡, 请切换到"开启", 使用默认数值并按下确认, 稍等一会, 可以去喝杯奶茶

8. 如果你想要切换不同的视图, 你可以在设定中找到, 手机视图主题色是红色, 平板视图是黑白, 只需要点一下即可切换, 但不要在手机上使用平板视图, 原因你试试就知道了...~

---

## 自行编译:

> 详细内容可以前往 [Wiki](https://github.com/qimuan7/ITM/wiki) 查看

代码相关我大概帮不上忙, 但我会尽力写清楚每一部分在设计之初的运作方式, 提示词就..字数太多有用的太少, 其实按整体思路来你可以写一个一模一样的.

关于翻译, 其实这个App没有多少可以被翻译的界面内容, 只需要根据 res/values/strings 来添加即可

---

## 引用内容 & 版权声明:

### 外观参考:

- (竖屏界面) Flamingo Here: github.com/Yos-X/FlamingoHere

- (播放横屏) Salt Player: moriafly.com/program/salt-player.html

- (媒体库页) Accord-Beta: github.com/FoedusProgramme (作者似乎改名了并且原项目没有了)

> 只下载和参考界面样式, 未使用其代码 (其实即使有我也看不懂)

### 使用组件 (部分):

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

## 更多 (作者废话时间+3):

### 原谅我每次写完readme都想在后面加一段个人想法之类的东西, 虽然没用, 但凑了个字数, 也能让我说说话 (?).

---

脑子一热想把两个项目合并, 嗯好处是以后只需要更新一版, 坏消息是没有更多好消息. 或许还在密谋更多奇怪小应用?..

我也在思考我写多一个键盘, 写多一个日记, 日历, 记账, 手机助理, 翻译, 这些真的会有人用么, 虽然像是废话, ITM 都不见得受众很广对吧.

不过我觉得我还是会耐不住性子写, 那就当完成心愿吧 ~. 你看, 想做什么就去做, 你也是 0w<.

---

Hmm, 最近 Gemini 又不知道做什么说我所在地不可用, 然后连历史记录都不给我看... 有点烦, 不过 DS 是真的很强, 用 GeminiAgent 十份一的时间就改完我想要的内容, 还是只有对话框的前提下.

好吧也再次提醒我, 其实没有 AI 我什么都不是, 或许只是有一堆没用想法的小废物.

生活不太好, 学习不太好, 最近还在开学第四礼拜请了长假, 很身体难受, 但请多一天就焦虑一天, 不知道怎么办 (摆烂_(:_/&)_, 医院排号排到下月了, 或许他们比我更值得去.

愿世界无灾吧, 虽然很空. 也希望我能开心, 不开心也可以, 但我想不再乱想...

愿你今日开心ouo, 有时间要好好照顾自己.

---
