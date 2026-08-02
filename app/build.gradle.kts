plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp) // 啟用 ksp 外掛
}

android {
    namespace = "lkg.itm.music"
    compileSdk = 37 // 根據您之前的內容調整

    defaultConfig {
        applicationId = "lkg.itm.music"
        minSdk = 26
        targetSdk = 36
        versionCode = 260802
        versionName = "3.0.18"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 正確的 NDK 設定位置
        ndk {
            abiFilters.addAll(listOf("arm64-v8a", "armeabi-v7a"))
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    // material icons
    implementation("androidx.compose.material:material-icons-extended")

    // document file
    implementation("androidx.documentfile:documentfile:1.0.1")

    // dd-plist
    implementation("com.googlecode.plist:dd-plist:1.29")

    // Exo Player (Media3)
    implementation("androidx.media3:media3-exoplayer:1.3.0")
    implementation("androidx.media3:media3-ui:1.3.0")
    implementation("androidx.media3:media3-session:1.3.0")

    // Room DataBase
    val roomVersion = "2.7.0-rc01"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")

    // Coil ImgLoader
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Palette Color Picker
    implementation("androidx.palette:palette-ktx:1.0.0")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // FFmpeg-Kit-Min 全庫音頻響度計算引用庫 (原:FFmpegX)
    dependencies {
        // 社群繼續維護的版本（目前支援至 FFmpeg 6.x / 7.x 以上）
        implementation("io.github.maitrungduc1410:ffmpeg-kit-min:6.0.6")
    }

    // JAudioTagger 歌詞解析
    implementation("net.jthink:jaudiotagger:3.0.1")

    // Media3 系統播放組件 (MusicService.kt)
    val media3Version = "1.3.0"
    implementation("androidx.media3:media3-exoplayer:$media3Version")
    implementation("androidx.media3:media3-ui:$media3Version")
    implementation("androidx.media3:media3-session:$media3Version")
    implementation("androidx.media3:media3-common:$media3Version")

    // ViewModel 頁面狀態記憶, 取代原來 AndroidManifest.xml 強制保留活動
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    // original items below
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}