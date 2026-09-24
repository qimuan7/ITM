<img src="https://github.com/qimuan7/ITM/blob/1/Assets/LocalGrid-ITM-ICON-Rounded.png" width="20%" alt="icon">

# LocalGrid - ITM

An Android local music player that uses iTunes libraries as its source. Restore your downloaded playlists on any Android device and play them as you like.

[readme: Simplified Chinese Translation](https://github.com/qimuan7/ITM/blob/1/Assets/README_zh-Hans.md)

[ITM Wiki](https://github.com/qimuan7/ITM/wiki)

[ITM for Tablet](https://github.com/qimuan7/ITM-Tablet)

---

## Note:

- ITM was designed to only read iTunes libraries as its music source and requires "Share iTunes Library XML with other apps" to be enabled.

- ITM CANNOT scan the normal audio in ur device. If you want a player with a similar UI, u can try: FlamingoHere, Accord-Beta, SaltPlayer, LuneMusic, or Musicolet.

- 90% of ITM's code was written by Google AI, although it has been tested. If you are concerned about stability or strongly dislike AI-generated content, please look for other projects. I apologize for this.

---

## Special Features & Interface:

### Features:

- iTunes music library parsing, restoring all playlists from iTunes XML

- Automatic volume balancing

- Embedded lyrics display in audio

- Independent volume adjustment within the app

- Shuffle, loop, and single-track playback

- Background playback, and integration with native Android quick playback settings

- No reload when switching between portrait and landscape modes (3.0.18+)

### Interface:

- Simple interface and multi-language support (currently only Simplified/Traditional/English in version 3.0.20+)

- Local random homepage recommendation cards, local recommendation lists based on listen counts (list functionality fixed in 3.0.19)

- Automatically categorized system-created playlist in "Downlaoded" page

- Playback interface with blurring

- Light and dark mode adaptation and manual switching

- Aesthetically pleasing landscape playback page

---

## Interface Display:

<table>

<tr>

<td align="center"><b>Home, Media Page, Settings Page</b></td>

<td align="center"><b>Playback Page</b></td>

<td align="center"><b>Landscape Update (3.0.18+)</b></td>

</tr>

<tr>

<td align="center"><img src="https://github.com/qimuan7/ITM/blob/1/Assets/ITM3013-1.jpg" width="600" alt="ITM3013-1"></td>

<td align="center"><img src="https://github.com/qimuan7/ITM/blob/1/Assets/ITM3013-2.jpg" width="600" <alt="ITM3013-2"></td>

<td align="center"><img src="https://github.com/qimuan7/ITM/blob/1/Assets/ITM3018-1.jpg" width="300" alt="ITM3018-1"></td>

</tr>

</table>

**- The songs shown in the image are personal offline test playlists. Their names, artists, albums, lyrics, covers, etc., belong to their original authors and are not included in the project. You need to import your own offline playlists to listen to them.**

**- The application's interface design references Flamingo, Accord, SlatPlayer, and AppleMusic, but does not use their source code, components, or any other content.**

---
## Usage:

> For detailed information, please visit [Wiki](https://github.com/qimuan7/ITM/wiki), which includes the use of ITM. There will also be a reference on how to create a local iTunes music library.

### Prerequisites:

1. Your computer system needs to be Windows or OSX-10.14 or earlier (10.15+ can read the wiki). Ensure that **"Keep iTunes Media folder organized", "Copy added songs to iTunes Media", and "Share iTunes Library XML with other applications" are enabled in iTunes **Advanced Settings**.

2. Android devices are recommended to be Android 10+. Android 14 (OneUI 6.1) has been tested. I am unsure about the system version required for the FFmpeg component; please try it yourself.

### Getting Started:

0. Ensure the above prerequisites are met and check that the iTunes folder **contains all** audio files and (iTunes Music Library.xml).

1. Copy the entire iTunes folder from your computer to your Android device. The location is up to you.

2. Open ITM, click the avatar icon in the upper right corner of the homepage... Clicking will open the settings page.

3. On the settings page, select "Reselect iTunes Root Folder" at the top to bring up the file selector.

4. In the selector, locate the iTunes folder you just copied to your phone's directory. Click until you reach the iTunes folder. You should now see iTunes Music Library.xml in the file selector. Click "Select" at the bottom.

5. Wait a moment for the library to load.

6. Once loaded, you can use it normally.

7. **However,** you will likely experience lag and overheating at this point. This is because the **volume balancing** and **lyrics scanning** components are running in the background. It's recommended to wait a few minutes on the home screen (it took me about 4-5 minutes for 400 songs). If you have logcat, you can check its progress.

> (Don't worry, it only scans the library the first time it's loaded; normally it only reads the database and temporary files.)

---

## Build it urself:

> For details, please visit [Wiki](https://github.com/qimuan7/ITM/wiki) View

I probably can't help with the code, but I'll try my best to explain how each part worked in the initial design. As for the prompts... there are too many words and too few useful ones. Actually, you can write an identical one based on the overall concept.

Regarding translation, this app doesn't have much translatable interface content; you just need to add it based on res/values/strings.

---

## Referenced Content & Copyright Notice:

### Appearance References:

- (Portrait Interface) Flamingo Here: github.com/Yos-X/FlamingoHere

- (Landscape Playback) Salt Player: moriafly.com/program/salt-player.html

- (Media Library) Accord-Beta: github.com/FoedusProgramme (The author seems to have changed their name and the original project is gone)

> Only downloaded and referenced the interface style; the code was not used. (Even if I did, I wouldn't understand it)

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

## More (Author's Ramblings):

### Forgive me for always add some personal reflection at the end of the readme, even though it's useless, it just adds up to the word count and lets me say something (?).

---

Regarding ITM itself, there won't be many updates unless I discover a problem and overcome the instability and laziness, then maybe I'll fix it. Functionally, everything will be kept extremely simple (actually, there's nothing Lol), because if it's too complex... When I get frustrated, I might want to blow up my phone.

Admittedly, I know almost nothing about programming. Deciding to write this was a spur-of-the-moment decision. The idea stemmed from my past desire for a media player that could play my iTunes library (so my iPod Nano 4, which only charged for two hours and allowed me to listen to music for two minutes, could retire).

And also thank for the advancements in technology and AI. Before, I could only think about ideas and then abandon & forget them. Now, as long as u maintain a clear thought process and roughly understand the principles, anyone can try to get AI to create it, somewhat like a digital version of a 3D printer?

Next step... I want to create a tablet-compatible version (because I also have a tablet, but the current interface looks terrible on it), ITM 3.1-Tablet. I'll probably use 3.0.19 or 3.0.20 as a base? Currently, 3.0 is only for mobile use, but the effect might not be very good because I can't think of a design for the interaction and layout yet, and my daily performance is also quite poor.

> Don't worry, the tablet version will have a different package name. You can install both of the tablet-ver and phone-ver on your device if you really want.

However, if you don't use iTunes, you can definitely use other music players. They have more complete functions, better interfaces, fewer weird bugs, and the developers update them more frequently.

---

Btw, I might be one of the few people in this era who still uses iTunes + an offline music library to listen to music? As for why iTunes, it's simply because I like that it can put all my music together, and I can customize the sorting and playlists. Also when I switch computers, I just copy the music without having to sort & setting it again.

(But the built-in music app on OSX 10.15 and later is really hard to use. Not only is there no XML sharing, but every major system version there's incompatibility—it's ridiculously incompatible! OAO)

Offline music gives me more security than online music. No matter the platform or its promises, when I get anxious, I get scared—scared of losing even a single song. Although it's a personal choice, I have to say that the effort required for this is multiplied. Therefore, ordinary users don't need to pursue a completely offline digital experience. I hope no one gets trapped in pointless anxiety like I have.

No more~ , thanks for reading this far. Whether you choose ITM or not, have a wonderful day!

---
