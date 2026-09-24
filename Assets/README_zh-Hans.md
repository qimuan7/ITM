<img src="https://github.com/qimuan7/ITM-Tablet/blob/1/Assets/LocalGrid-ITM-Tablet-ICON-Rounded.png" width="20%" alt="icon">

# LocalGrid - ITM Tablet

---

使用 iTunes 资料库为来源的安卓本地音乐播放器, 专为安卓平板设计的极简界面, 方便的恢复你所有的 iTunes 离线播放清单, 随心所欲的播放

[readme: 简体中文翻译](https://github.com/qimuan7/ITM/blob/tablet-3.1/Assets/README_zh-Hans.md)

[readme: EnglishTranslate](https://github.com/qimuan7/ITM/blob/tablet-3.1/Assets/README_EN.md)

[ITM Wiki](https://github.com/qimuan7/ITM/wiki)

[ITM 新版下载](https://github.com/qimuan7/ITM) Release 3.2.1+

> ITM-New-3.2 合併 ITM-Phone 和 ITM-Tablet, 並做了一些小更新, 所以就不再分开维护原來的两个不同版本了

> ITM 手机端(停更): github.com/qimuan7/ITM/tree/phone-3.0 ; Release 3.0.13 ~ 3.0.20

> ITM 平板端(停更): github.com/qimuan7/ITM/tree/tablet-3.1 ; Release 3.1.12.FC ~ 3.1.20_11

---

## 注意:

- ITM 在设计之初只能读取 iTunes 资源库作为音乐来源, 且需要可以开启"与其他应用共享 iTunes 资源库 XML"

- ITM 无法扫描普通音频, 如果你想要类似UI的普通播放器, 我更推荐 FlamingoHere, SaltPlayer, 或 Musicolet.

- ITM 90% 的代码都由 Google AI 编写, 尽管经过测试, 如果你担心稳定性或者非常不喜欢 AI 写的内容, 请寻找其他项目, 我为此感到抱歉.

---

## 特别功能 & 界面:

### 功能:

> - iTunes音乐资源库解析, 恢复xml中的播放清单

> - 自动音量平衡

> - 音频内嵌歌词显示

> - 应用内独立音量调节

> - 随机播放, 循环播放, 单曲播放

> - 后台播放, 以及接入原生安卓快速设定播放组件

> - 横竖屏切换不重载

### 界面 :

- 重新设计的平板 UI, 带有侧边栏, 以及更多为平板优化的显示细节

- 改用黑白主色的界面, 内含多语言 (3.1.20_11+ 目前只有 简/繁/英)

> - 本地的随机首页推荐卡片, 本地基于收听次数的推荐列表 (列表功能于3.0.19修复)

> - 自动分类和隐藏的播放列表页面

- 为横竖屏分别重新设计的, 带有模糊的播放界面

> - 浅色和深色模式适配和手动切换

> - 美观的横屏播放页

---

## 界面展示:

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

**- 展示的设备由于界面缩放较小, 显示的样式可能与你的设备有所不同**

**- 图片所展示的歌曲均为个人创建的离线测试歌单, 其名称, 艺人, 专辑, 歌词, 封面等均属原作者, 不包含在项目中, 你需要汇入你自己的离线歌单方可收听**

**- 该应用界面设计参考 Flamingo, SlatPlayer, 和 AppleMusic, 但未使用其源码, 组件, 或其他任何内容**

---

## 使用:

> 详细内容可以前往 [ITM Wiki](https://github.com/qimuan7/ITM/wiki) 查看, 其中包含 ITM 的使用, 也会有 iTunes 本地音乐库的建立方法参考

### 先决条件:

1. 你的电脑系统版本需要是 Windows 或 OSX-10.14-及以下 (OSX 10.15+ 可以查看 Wiki), 确定在 iTunes **进阶设定**中已开启 **"保持 iTunes Media 资料夹整理状态" , "复制加入的歌曲到 iTunes Media" , "与其他应用程式共享 iTunes 资料库 XML"**

2. 安卓设备建议在 Android 10+ , 已测试版本 11inch: Android 14 (OneUI-Tab-6.1) 以及 8.0inch: Android 16 (LineageOS-Tab-23) , 我不确定 FFmpeg 组件需要的系统版本, 请自行尝试

### 开始使用:

0. 确定上述先决条件满足, 并检查 iTunes 文件夹**是否包含所有** 音频 和 (iTunes Music Library.xml)

1. 将你的电脑中的整个 iTunes 文件夹复制到安卓设备上, 存放位置只要你喜欢都可以

2. 开启 ITM Tablet , 点击**位于侧栏**右上角的头像标志, 点击后将会开启设定页面

3. 在设定页面选择最上方的 "重新选择 iTunes 根目录资料夹" 唤起文件选择器

4. 选择器中, 找到你刚刚复制到手机目录的 iTunes 文件夹, 一直点击到 iTunes 文件夹内, 此时文件选择器页面应该看到 iTunes Music Library.xml, 点击底部"选择"

5. 等待片刻, 资料库会载入完成

6. 载入完成后你可以正常使用

7. **但是,** 此时直接操作你大概率会感到卡顿和发热, 这是因**音量平衡**和**歌词扫描**组件在后台运行, 建议停在首页等上几分钟 (我的400首歌大约用时4~5分钟), 如果有logcat也可以查看它的进度
> (请放心只会在每个资料库首次载入时扫描, 平常只会读取Database和暂存来运行)

---

## 自行编译:

> 详细内容可以前往 ITM [Wiki](https://github.com/qimuan7/ITM/wiki) 查看

代码相关我大概帮不上忙, 但我会尽力写清楚每一部分在设计之初的运作方式, 提示词就..字数太多有用的太少, 其实按整体思路来你可以写一个一模一样的.

**平板端 (ITM-Tablet) 基于手机端 (ITM) 更改, 只是 UI 不一样, 其余一切通用**

关于翻译, 其实这个App没有多少可以被翻译的界面内容, 只需要根据 res/values/strings 来添加即可

---

## 引用内容 & 版权声明:

### 外观参考:

- (竖屏界面) Flamingo Here: github.com/Yos-X/FlamingoHere

- (播放横屏) Salt Player: moriafly.com/program/salt-player.html

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

## 更多 (作者废话时间+1):

### 原谅我每次写完readme都想在后面加一段个人想法之类的东西, 虽然没用, 但凑了个字数, 也能让我说说话 (?).

---

继 ITM 后不到一个星期我又闲不住, 给自己的平板也写了一个, 现在手机平板都看的顺眼多了.

同时我也找到了一个可以在 MacOS X 10.15+ 安装 iTunes 的办法, 在 Github 上有一个项目叫 [Retroactive](https://github.com/cormiertyshawn895/Retroactive) , 你可以点击查看它.

它会修改 iTunes 并能让你成功在高版本 OSX 装上 iTunes, 我的 MBP 2017 x64 OSX11 测试是可以的, 最新版本在 iTunes 12.9.5.5, 如果直接把 Windows 的 iTunes_12.13.1.3+ 资料库移过来会提升版本过低, hmm唯一办法就是在这边再重建一次资料库了 0.0)

不过至少是能用了, 感谢项目大佬 ouo! .

然后关于设计 UI 改动, 其实本来想做类 AppleMusic 在 iPad 上的风格, 但我调整完功能实在太累, 一转头看到想拿来做电子书的小平板, 所以就改成了有点像水墨屏的黑白+外框风格, 简洁倒是简洁了, 不过可能会有点单调, 不喜欢的话只要把边框颜色从白色调成透明, 加上选择框背景颜色, 再给图标换个色就好 (其实就是改色).

对于这个版本, 这次依旧决定基于 ITM_3.0.19, 因为我不需要多语言, 所以直接写 UI 里是最稳定的做法, 但 ITM-Tab_3.1.20_11 不是基于 ITM_3.0.20 做的, 只是 19_10 后的一个改版, 它把文字换成了字符串, 所以支持多语言 (说起来 ITM_3.0.20 也是这么做的), 不过侧栏似乎有一部分没被翻译 (恼), 不过也不太影响使用 (其实是我懒).

然后就没了?.. 噢对, 最近南方地区的天气终于天晴了 ouo! 不过很热 0n0 , 各位处在世界各地都记得照顾好自己. 最后无论你是否使用 ITM, 愿你今日愉快 owo!

---
