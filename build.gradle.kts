// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
}

buildscript {
    dependencies {
        // Google Services plugin for Firebase
        classpath("com.google.gms:google-services:4.3.10")
    }
}


// Repositories should no longer be specified here due to the repositories mode in settings.gradle.kts



