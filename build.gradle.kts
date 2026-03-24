// Top-level build file where you can add configuration options common to all sub-projects/modules.
//plugins {
//    id("com.android.application") version "8.1.4" apply false
//    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
//}


// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()

        mavenCentral()
        // Added For Image Crop : Required in yalantis:ucrop library
       maven { url = uri("https://jitpack.io") }   // for Crop Library
        // maven { url "https://jitpack.io" }

        flatDir {
            dirs("app/libs") // This is the default libs folder inside the app module
        }
    }
    dependencies {
        val navVersion = "2.7.7"
      //  val hiltVersion = "2.51.1"
        val hiltVersion = "2.52"
        val kotlinVersion = "2.0.0" // use the same version here and in plugins

//        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")
//        classpath( "com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")
//        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.0")
//        classpath ("com.google.gms:google-services:4.4.2")


        // Navigation Safe Args
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")

        // Hilt Gradle Plugin
        classpath("com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")

        // Kotlin Gradle Plugin (Required for Kotlin 2.0)
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")

        // Google Services (Firebase)
        classpath("com.google.gms:google-services:4.4.2")

    }
}

plugins {

//    id("com.android.application") version "8.1.4" apply false
//
//   // id("org.jetbrains.kotlin.android") version "1.9.0" apply false
//    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
//
//    id("com.google.devtools.ksp") version "2.0.0-1.0.21" apply false


    // 🔴 CHANGED FROM 8.1.4 → 8.7.3
    // ✅ REQUIRED for 16 KB memory page size support
    id("com.android.application") version "8.7.3" apply false

    // Kotlin 2.0 – fully supported by AGP 8.3.x
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false

    // KSP compatible with Kotlin 2.0
    id("com.google.devtools.ksp") version "2.0.0-1.0.21" apply false

}