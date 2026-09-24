<img src="https://github.com/qimuan7/ITM/blob/itm-3.2/Assets/LocalGrid-ITM-ICON-Rounded.png" width="20%" alt="icon">

# LocalGrid - ITM

An Android local music player sourced from the iTunes library. Restore your downloaded playlists on any Android device and play them as you like.

[readme: Simplified Chinese Translation](https://github.com/qimuan7/ITM/blob/itm-3.2/Assets/README_zh-Hans.md)

[readme: English Translation](https://github.com/qimuan7/ITM/blob/itm-3.2/Assets/README_EN.md)

[ITM Wiki](https://github.com/qimuan7/ITM/wiki)

ITM New Version Download: Release 3.2.1+

> ITM-New-3.2 merges ITM-Phone and ITM-Tablet, and makes some minor updates, so the two different versions are no longer maintained separately.

> ITM Mobile App (Old Version): github.com/qimuan7/ITM/tree/phone-3.0; Release 3.0.13 ~ 3.0.20

> ITM Tablet App (Old Version): github.com/qimuan7/ITM/tree/tablet-3.1; Release 3.1.12.FC ~ 3.1.20_11

---
## Note:

- ITM was designed to only read iTunes libraries as music sources, and requires "Share iTunes Library XML with other applications" to be enabled.

- ITM cannot scan regular audio. If you want a regular media player with a similar UI, I recommend FlamingoHere, Accord-Beta, or SaltPlayer. LuneMusic or Musicolet.

- 90% of ITM's code was written with the assistance of Google AI and Deepseek. Although tested, if you are concerned about stability or strongly dislike AI-generated content, please look for other projects. I apologize for this.

---

## Special Features & Interface:

### Features:

- iTunes music library parsing, restoring playlists from XML files

- Volume balancing, can be turned on or off, and supports more scanning options

- Embedded lyrics display in audio

- Independent volume adjustment within the app

- Shuffle, loop, and single-track playback

- Background playback, and integration with native Android quick playback settings

- No reload when switching between portrait and landscape modes

### Interface:

- Allows switching between phone and tablet views, with a clean and simple interface design

- Multilingual (Simplified/Traditional/English available in versions 3.0.20 and 3.1.20_11; newer versions...) 3.2.1 (Multi-language support removed)

- Local random homepage recommendation cards, local recommendation lists based on listen counts (list functionality fixed in 3.0.19)

- Automatically categorized and hidden playlist pages

- Playback interface with blurring

- Light and dark mode adaptation and manual switching

- Beautiful landscape playback page

---

## Interface Display:

### Mobile View Display:

<table>

<tr>

<td align="center"><b>Home, Media Page, Settings Page</b></td>

<td align="center"><b>Playback Page</b></td>

<td align="center"><b>Landscape Update (3.0.18+)</b></td>

</tr>

<tr>

<td align="center"><img src="https://github.com/qimuan7/ITM/blob/itm-3.2/Assets/ITM3013-1.jpg" width="600" alt="ITM3013-1"></td>
<td align="center"><img src="https://github.com/qimuan7/ITM/blob/itm-3.2/Assets/ITM3013-2.jpg" width="600" alt="ITM3013-2"></td>
<td align="center"><img src="https://github.com/qimuan7/ITM/blob/itm-3.2/Assets/ITM3018-1.jpg" width="300" alt="ITM3018-1"></td>
</tr>
</table>

---

### Tablet View Display:

<table>

<tr>

<td align="center"><b>Landscape Homepage</b></td>

<td align="center"><b>Landscape Playback Page + Lyrics</b></td>

<td align="center"><b>Landscape Playlist + Volume Bar</b></td>

<td align="center"><b>Landscape Song Page</b></td>

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
<td align="center"><b>Vertical homepage</b></td>
<td align="center"><b>Vertical screen play page</b></td>
<td <b>Vertical Playlist + Volume Bar</b></td>

<td align="center"><b>Vertical Song Page</b></td>

</tr>

<tr>

<td align="center"><img src="https://github.com/qimuan7/ITM/blob/tablet-3.1/Assets/ITM-Tab-vert1.png" width="400" alt="vert-home"></td>

<td align="center"><img src="https://github.com/qimuan7/ITM/blob/tablet-3.1/Assets/ITM-Tab-vert2.png" width="400" alt="vert-player"></td>

<td align="center"><img <img src="https://github.com/qimuan7/ITM/blob/tablet-3.1/Assets/ITM-Tab-vert3.png" width="400" alt="vert-pylisvo"></td>

<td align="center"><img src="https://github.com/qimuan7/ITM/blob/tablet-3.1/Assets/ITM-Tab-vert4.png" width="400" alt="verrt-music"></td>

</tr>

</table>

---

### New Version Update Content:

<table>

<tr>

<td align="center"><b>Volume balance loading, Volume balance scan options, Tablet view switching (not working properly on mobile devices)</b></td>

</tr>

<tr>

<td align="center"><img src="https://github.com/qimuan7/ITM/blob/itm-3.2/Assets/ITM3210-2.jpg" width="1000" alt="3210-2"></td>

</tr>

</table>

---

**- The songs shown in the image are personal offline test playlists. Their names, artists, albums, lyrics, covers, etc., belong to their original authors and are not included in the project. You need to import your own offline playlists to listen to them.**

**- The application's interface design references Flamingo, Accord, SlatPlayer, and AppleMusic, but does not use their source code, components, or any other content.**

---

## Usage:

> For detailed information, please visit [Wiki](https://github.com/qimuan7/ITM/wiki), which includes the use and construction of ITM, and will also include iTunes. Methods for creating a local music library (see reference below)

### Prerequisites:

1. Your computer system needs to be Windows or OSX 10.14 or earlier (for 10.15+, please see the Wiki). Ensure that **"Keep iTunes Media folder organized", "Copy added songs to iTunes Media", and "Share iTunes Library XML with other applications" are enabled in iTunes **Advanced Settings**.

2. For Android devices, Android 10+ is recommended. Android 14 (OneUI 6.1) has been tested. I am unsure about the system version required for the FFmpeg component; please try it yourself.

### Getting Started:

0. Ensure the above prerequisites are met and check that the iTunes folder **contains all** audio files and (iTunes Music Library.xml).

1. Copy the entire iTunes folder from your computer to your Android device. The location is up to you.

2. Open ITM, click the upper right corner of the homepage... Or click the avatar icon at the top of the sidebar to open the settings page.

3. In the settings page, select "Reselect iTunes Root Folder" at the top to bring up the file selector.

4. In the selector, find the iTunes folder you just copied to your phone's directory. Click until you reach the iTunes folder. You should now see iTunes Music Library.xml in the file selector. Click "Select" at the bottom.

5. Wait a moment for the library to load.

6. Once loaded, you can use it normally.

7. If you want to use volume balance, switch to "On," use the default value, and press OK. Wait a while; you can go grab a bubble tea while you wait.

8. If you want to switch between different views, you can find it in the settings. The phone view theme color is red, and the tablet view is black and white. Just tap to switch, but don't use the tablet view on your phone. You'll understand why once you try it...~

---

## Self-compiled:

> For more details, please visit [Wiki](https://github.com/qimuan7/ITM/wiki) View

I probably can't help with the code, but I'll try my best to explain how each part worked in the initial design. As for the hints... there are too many words and too few useful ones. Actually, you could write one based on the overall concept.

Exactly the same.

Regarding translation, this app doesn't actually have much translatable interface content; you just need to add it based on res/values/strings.

---

## Referenced Content & Copyright Notice:

### Appearance References:

- (Portrait Interface) Flamingo Here: github.com/Yos-X/FlamingoHere

- (Landscape Playback) Salt Player: moriafly.com/program/salt-player.html

- (Media Library Page) Accord-Beta: github.com/FoedusProgramme (The author seems to have changed their name and the original project is no longer available)

> Only downloaded and referenced the interface style; did not use the code (actually, even if I did, I wouldn't understand it).

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

## More (Author's rambling time +3):

### Forgive me for always wanting to add a personal reflection at the end of my README, even though it's useless, it just adds up the word count and lets me say something (?).

---

On a whim, I decided to merge two projects. The good thing is that I only need to update one version from now on. The bad news is there's no more good news. Maybe I'm still plotting more strange little applications?...

I'm also thinking about writing a keyboard, a diary, a calendar, an expense tracker, a phone assistant, a translator—would anyone actually use these? It sounds obvious, even ITM doesn't have a very wide audience, right?

But I think I'll still be unable to resist writing, so let's consider it fulfilling a wish~. You see, do what you want to do. You're also 0w<.

---

Hmm, recently Gemini has been saying my location is unavailable again, and it won't even show me my history... It's a bit annoying. But DS is really powerful; it changed what I wanted in a tenth of the time using GeminiAgent, and that was with only the dialog box.

Okay, this also reminds me again that without AI, I'm nothing, maybe just a useless little piece of trash with a bunch of useless ideas.

Life isn't good, studies aren't good, and I recently took a long leave of absence in the fourth week of school. I feel really unwell, but every extra day I take leave brings me anxiety. I don't know what to do (just give up _(:_/&)_). The hospital appointment is booked until next month; maybe they deserve to go more than I do.

May the world be free of disasters, even though I feel empty. I also hope I can be happy, or not, but I want to stop overthinking...

Wishing you happiness today, ouo. Take good care of yourself when you have time also.

---
