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
        val hiltVersion = "2.51.1"
        val kotlinVersion = "2.0.0" // use the same version here and in plugins

        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navVersion")
        classpath( "com.google.dagger:hilt-android-gradle-plugin:$hiltVersion")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.0")
        classpath ("com.google.gms:google-services:4.4.2")

    }
}

plugins {
    id("com.android.application") version "8.1.4" apply false

   // id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false

    id("com.google.devtools.ksp") version "2.0.0-1.0.21" apply false

}