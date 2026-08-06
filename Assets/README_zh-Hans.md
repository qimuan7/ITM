<img src="https://github.com/qimuan7/ITM/blob/1/Assets/LocalGrid-ITM-ICON-Rounded.png" width="20%" alt="icon">

# LocalGrid - ITM

使用 iTunes 资料库为来源的安卓本地音乐播放器, 在任何安卓设备上恢复你已下载的播放清单, 随心所欲的播放

[readme: EnglishTranslate](https://github.com/qimuan7/ITM/blob/1/Assets/README_EN.md)

[ITM Wiki](https://github.com/qimuan7/ITM/wiki)

[ITM 平板端](https://github.com/qimuan7/ITM-Tablet)

---

## 注意:

- ITM 在设计之初只能读取 iTunes 资源库作为音乐来源, 且需要可以开启"与其他应用共享 iTunes 资源库 XML"

- ITM 无法扫描普通音频, 如果你想要类似UI的普通播放器, 我更推荐 FlamingoHere, Accord-Beta, 椒盐音乐, LuneMusic 或 Musicolet.

- ITM 90% 的代码都由 Google AI 编写, 尽管经过测试, 如果你担心稳定性或者非常不喜欢 AI 写的内容, 请寻找其他项目, 我为此感到抱歉.

---

## 特别功能 & 界面:

### 功能:

- iTunes音乐资源库解析, 恢复xml中的播放清单

- 自动音量平衡

- 音频内嵌歌词显示

- 应用内独立音量调节

- 随机播放, 循环播放, 单曲播放

- 后台播放, 以及接入原生安卓快速设定播放组件

- 横竖屏切换不重载 (3.0.18+)

### 界面 :

- 简洁的界面和多语言 (3.0.20+ 目前只有 简/繁/英)

- 本地的随机首页推荐卡片, 本地基于收听次数的推荐列表 (列表功能于3.0.19修复)

- 自动分类和隐藏的播放列表页面

- 带有模糊的播放界面

- 浅色和深色模式适配和手动切换

- 美观的横屏播放页

---

## 界面展示:

<table>
<tr>
<td align="center"><b>首页, 媒体页, 设定页</b></td>
<td align="center"><b>播放页</b></td>
<td align="center"><b>横屏更新(3.0.18+)</b></td>
</tr>
<tr>
<td align="center"><img src="https://github.com/qimuan7/ITM/blob/1/Assets/ITM3013-1.jpg" width="600" alt="ITM3013-1"></td>
<td align="center"><img src="https://github.com/qimuan7/ITM/blob/1/Assets/ITM3013-2.jpg" width="600" alt="ITM3013-2"></td>
<td align="center"><img src="https://github.com/qimuan7/ITM/blob/1/Assets/ITM3018-1.jpg" width="300" alt="ITM3018-1"></td>
</tr>
</table>

**- 图片所展示的歌曲均为个人创建的离线测试歌单, 其名称, 艺人, 专辑, 歌词, 封面等均属原作者, 不包含在项目中, 你需要汇入你自己的离线歌单方可收听**

**- 该应用界面设计参考 Flamingo, Accord, 椒盐音乐, 和 AppleMusic, 但未使用其源码, 组件, 或其他任何内容**

---

## 使用:

> 详细内容可以前往 [Wiki](https://github.com/qimuan7/ITM/wiki) 查看, 其中包含 ITM 的使用, 也会有 iTunes 本地音乐库的建立方法参考

### 先决条件:

1. 你的电脑系统版本需要是 Windows 或 OSX-10.14 及以下, 确定在 iTunes **高级选项**中已开启 **"保持 iTunes Media 资料夹整理状态" , "复制加入的歌曲到 iTunes Media" , "与其他应用程式共享 iTunes 资料库 XML"**

2. 安卓设备建议在 Android 10+ , 已测试版本 Android 14 (OneUI6.1) , 我不确定 FFmpeg 组件需要的系统版本, 请自行尝试

### 开始使用:

0. 确定上述先决条件满足, 并检查 iTunes 文件夹**是否包含所有** 音频 和 (iTunes Music Library.xml)

1. 将你的电脑中的整个 iTunes 文件夹复制到安卓设备上, 存放位置只要你喜欢都可以

2. 开启 ITM , 点击首页右上角头像标志, 点击后将会开启设定页面

3. 在设定页面选择最上方的 "重新选择 iTunes 根目录资料夹" 唤起文件选择器

4. 选择器中, 找到你刚刚复制到手机目录的 iTunes 文件夹, 一直点击到 iTunes 文件夹内, 此时文件选择器页面应该看到 iTunes Music Library.xml, 点击底部"选择"

5. 等待片刻, 资料库会载入完成

6. 载入完成后你可以正常使用

7. **但是,** 此时直接操作你大概率会感到卡顿和发热, 这是因**音量平衡**和**歌词扫描**组件在后台运行, 建议停在首页等上几分钟 (我的400首歌大约用时4~5分钟), 如果有logcat也可以查看它的进度
> (请放心只会在每个资料库首次载入时扫描, 平常只会读取Database和暂存来运行)

---

## 自行编译:

> 详细内容可以前往 [Wiki](https://github.com/qimuan7/ITM/wiki) 查看

代码相关我大概帮不上忙, 但我会尽力写清楚每一部分在设计之初的运作方式, 提示词就..字数太多有用的太少, 其实按整体思路来你可以写一个一模一样的.

关于翻译, 其实这个App没有多少可以被翻译的界面内容, 只需要根据 res/values/strings 来添加即可

---

## 引用内容 & 版权声明:

### 外观参考:

- (竖屏界面) Flamingo Here: github.com/Yos-X/FlamingoHere

- (播放横屏) 椒盐音乐: moriafly.com/program/salt-player.html

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

## 更多 (作者废话时间):

### 原谅我每次写完readme都想在后面加一段个人想法之类的东西, 虽然没用, 但凑了个字数, 也能让我说说话 (?).

---

关于 ITM 本身, 其实不会有太多更新, 除非哪天发现哪里出问题了, 并且克服不稳定的状态和懒癌, 或许就冒出来修一下. 功能上一切保持极简 (其实是啥也没有), 因为太复杂的话, 我烦躁起来说不定会想把手机炸了.

不可否认我几乎完全不懂编程, 决定写这个东西也是一时兴起就做了, 这个想法的来源只是从前我很想要一个可以播放 iTunes 资料库的播放器 (然后就可以让我充电两小时, 听歌两分钟的 iPod Nano 4 退休了).

也该谢谢时代发展和 AI 进步, 从前有想法只能想想然后放弃, 现在只要保持思路清晰, 而且大概理解原理, 就尝试让 AI 做出来, 有点像数字版的 3D 打印机,

下一步的话..想要做适配平板端 (因为我还有个平板, 但现在的界面放上去很丑), ITM 3.1-Tablet, 应该会用 3.0.19 或者 3.0.20 做基础? 目前 3.0 就只留给手机使用, 不过平板的效果可能不会太好, 因为我暂时想不出来该怎么设计交互和排布, 日常状态也比较差.

> 请放心, 平板端会改包名为 lkg.itm.music.tablet, 你实在想在手机上装俩也是可以的.

不过如果你不用 iTunes, 其实大可以去用其他播放器, 功能更完善, 界面更好看, 也不会有什么怪bug, 作者更新也会更快.

---

说起来, 我可能是这个时代为数不多还在用 iTunes + 离线乐库听歌的人? 至于为什么是 iTunes, 只是我喜欢它能把所有音乐塞一起, 还能自定排序和播放清单, 并且换电脑只要复制就行不用再排一次.
> (但OSX10.15以后自带的音乐app真的很难用, 没有xml共享就算了, 隔一个系统大版本就新旧不兼容是什么超绝离谱兼容性 OAO)

离线给我的安全感始终高于线上, 无论什么平台, 如何承诺, 我焦虑起来就是会害怕, 害怕连一首歌都要失去, 虽然是个人选择, 不过我也要说, 为此耗费的精力是成倍增长的, 所以普通用户也不必追求完全的电子离线化, 我更希望没有人会像我一样陷在无意义的焦虑里.

就酱, 没啦, 感谢你看到这里, 无论你选择 ITM 与否, 愿你今日愉快ouo!.

---
