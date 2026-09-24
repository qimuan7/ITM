<img src="https://github.com/qimuan7/ITM/blob/tablet-3.1/Assets/LocalGrid-ITM-Tablet-ICON-Rounded.png" width="20%" alt="icon">

# LocalGrid - ITM for Tablet (Legacy)

---

An Android local music player sourced from the iTunes library, featuring a minimalist interface designed specifically for Android tablets. Easily restore all your iTunes offline playlists and play them as you please.

[readme: 简体中文翻译](https://github.com/qimuan7/ITM/blob/tablet-3.1/Assets/README_zh-Hans.md)

[readme: EnglishTranslate](https://github.com/qimuan7/ITM/blob/tablet-3.1/Assets/README_EN.md)

[ITM Wiki](https://github.com/qimuan7/ITM/wiki)

[ITM NewVer. Download](https://github.com/qimuan7/ITM) Release 3.2.1+

> ITM-New-3.2 merge ITM-Phone & ITM-Tablet and take some little changes, so now the old-two ver. stoped updating.

> ITM Phone (Legacy): github.com/qimuan7/ITM/tree/phone-3.0 ; Release 3.0.13 ~ 3.0.20

> ITM Tablet (Legacy): github.com/qimuan7/ITM/tree/tablet-3.1 ; Release 3.1.12.FC ~ 3.1.20_11

---
## Note:

- ITM was initially designed to only read iTunes libraries as music sources, and requires "Sharing iTunes Library XML with other applications" to be enabled.

- ITM cannot scan regular audio. If you want a regular media player with a similar UI, I recommend FlamingoHere, SaltPlayer, or Musicolet.

- 90% of ITM's code is written by Google AI. Although it has been tested, if you are concerned about stability or strongly dislike AI-written content, please look for other projects. I apologize for this.

---
## Special Features & Interface:

### Features:

> - iTunes music library parsing, restoring playlists from XML

> - Automatic volume balancing

> - Embedded lyrics display in audio

> - Independent volume adjustment within the app

> - Shuffle, Loop, Single Track Playback

> - Background playback, and integration with native Android quick playback settings

> - No reload when switching between portrait and landscape modes

### Interface:

- Redesigned tablet UI with a sidebar and more display details optimized for tablets

- Black and white color scheme, multi-language support (currently only Simplified/Traditional/English in version 3.1.20_11+)

> - Local random homepage recommendation cards, local recommendation list based on listen count (list functionality fixed in 3.0.19)

> - Automatically categorized and hidden playlist pages

- Redesigned playback interface with blurring for both portrait and landscape modes

> - Light and dark mode adaptation and manual switching

> - Beautiful landscape playback page

---
## Interface Display:

<table>
<tr>
<td align="center"><b>Landscape Homepage</b></td>
<td <td align="center"><b>Landscape playback page + lyrics</b></td>
<td align="center"><b>Landscape playlist + volume bar</b></td>
<td align="center"><b>Landscape song page</b></td>
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
<td align="center"><b>Portrait Homepage</b></td>
<td align="center"><b>Portrait Playback Page</b></td>
<td align="center"><b>Portrait Playlist + Volume Bar</b></td>
<td align="center"><b>Portrait Song Page</b></td>
</tr>
<tr>
    <td align="center"><img src="https://github.com/qimuan7/ITM/blob/tablet-3.1/Assets/ITM-Tab-vert1.png" width="400" alt="vert-home"></td>
    <td align="center"><img src="https://github.com/qimuan7/ITM/blob/tablet-3.1/Assets/ITM-Tab-vert2.png" width="400" alt="vert-player"></td>
    <td align="center"><img src="https://github.com/qimuan7/ITM/blob/tablet-3.1/Assets/ITM-Tab-vert3.png" width="400" alt="vert-pylisvo"></td>
    <td align="center"><img src="https://github.com/qimuan7/ITM/blob/tablet-3.1/Assets/ITM-Tab-vert4.png" width="400" alt="verrt-music"></td>
</tr>
</table>

**- Due to the smaller screen size on the device shown, the display style may differ from your device.**

**- The songs shown in the images are personal offline test playlists. Their names, artists, albums, lyrics, and covers belong to their original authors and are not included in the project. You need to import your own offline playlists to listen to them.**

**- The application's interface design references Flamingo, SlatPlayer, and AppleMusic, but does not use their source code, components, or any other content.**

---
## Usage:

> For detailed information, please visit [ITM Wiki](https://github.com/qimuan7/ITM/wiki), which includes information on the use of ITM and iTunes. Methods for creating a local music library (see reference below)

### Prerequisites:

1. Your computer system needs to be Windows or OSX-10.14 or earlier (OSX 10.15+ can read the wiki or below). Ensure that **"Keep iTunes Media folder organized," "Copy added songs to iTunes Media," and "Share iTunes Library XML with other applications"** are enabled in iTunes **Advanced Settings**.

2. For Android devices, Android 10+ is recommended. Tested versions: 11-inch: Android 14 (OneUI-Tab-6.1) and 8.0-inch: Android 16 (LineageOS-Tab-23). ​​I am unsure about the system version required for the FFmpeg component; please try it yourself.

### Getting Started:

0. Ensure the above prerequisites are met and check that the iTunes folder **contains all** audio files and (iTunes Music Library.xml).

1. Copy the entire iTunes folder from your computer to your Android device. 1. You can store the files wherever you like.

2. Open the ITM Tablet and click the avatar icon in the upper right corner of the sidebar. This will open the settings page.

3. In the settings page, select "Reselect iTunes Root Folder" at the top to bring up the file selector.

4. In the selector, locate the iTunes folder you just copied to your phone's directory. Click until you reach the iTunes folder. You should now see iTunes Music Library.xml in the file selector. Click "Select" at the bottom.

5. Wait a moment for the library to load.

6. Once loaded, you can use it normally.

7. **However,** you will likely experience lag and overheating if you operate it directly at this point. This is because the **volume balance** and **lyrics scanning** components are running in the background. It is recommended to wait a few minutes on the home screen (it took me about 4-5 minutes for 400 songs). If you have logcat, you can also check its progress.

> (Don't worry, the scanning only happens the first time each library is loaded.) Normally, it only reads from the database and temporary storage to run.

---

## Build it urself:

> For details, please visit ITM [Wiki](https://github.com/qimuan7/ITM/wiki).

I probably can't help with the code, but I will try my best to explain how each part works in the initial design. As for the prompts... there are too many words and too few useful ones. Actually, you can write an identical one based on the overall concept.

**Tablet version (ITM-Tablet) is based on the mobile version (ITM), only the UI is different; everything else is the same.**

Regarding translation, this app doesn't actually have much content that can be translated. You only need to add it based on res/values/strings.

---

## Referenced Content & Copyright Notice:

### Appearance References:

- (Portrait screen) Flamingo Here: github.com/Yos-X/FlamingoHere

- (Landscape playback) Salt Player: moriafly.com/program/salt-player.html

> Only downloaded and referenced the interface style, did not use its code (actually, even if I did, I wouldn't understand it).

### Components Used (Partial):

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

## More (Author's rambling time +1):

### Forgive me that always add sth what i tk at the end of every readme. Although it's useless (?)

---

Less than a week after ITM, I couldn't stay idle and wrote another version for my tablet, and now phones and tablets both look much better :D.

I also found a way to install iTunes on macOS X 10.15+. There's a project on Github called [Retroactive](https://github.com/cormiertyshawn895/Retroactive), you can check it out (its readme will guide u to do almost thing).

It modifies iTunes and allows you to successfully install iTunes on higher versions of OSX. It worked on my MPB 2017 x64 OSX11. The latest version is iTunes 12.9.5.5. But if you directly move your Windows iTunes_12.13.1.3+ library over, it will upgrade to an outdated version. Hmm, the only solution is to rebuild the library here 0.0)

But at least it works! Thanks to the project creator ouo!

And then about the UI design changes were initially intended to resemble the Apple Music style on the iPad, but adjusting the functionality proved too tiring. Then I saw a small tablet I wanted to use as an e-reader, so I changed it to a black-and-white style with a border, somewhat like an e-ink screen. It's simple, but perhaps a bit monotonous. If you don't like it, simply change the border color from white to transparent, add a background color to the selection boxes, and recolor the icons (basically, just a color change).

For this version, I've decided to base it on ITM_3.0.19 again, because I don't need multiple languages, so writing it directly in the UI is the most stable approach. However, ITM-Tab_3.1.20_11 has multi-lang, it is NOT based on ITM_3.0.20, but just a fix-ver on 19_10. It replaces text with strings, thus supporting multiple languages ​​(which is what ITM_3.0.20 did too). However, it seems some parts of the sidebar haven't been translated :( , but it doesn't significantly affect usability. (Actually, I'm just lazy so not fix it before send it xd).

And that's it?... Oh right, the weather in the south of China has finally cleared up recently! But it also gets very hot 0n0. Everyone, wherever you are in the world, remember to take care of yourselves. Finally, whether you use ITM or not, have a nice day! owo!

---
