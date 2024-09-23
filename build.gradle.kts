// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
}
/*
buildscript {
    repositories {
        google() // Ensure Google repository is included
        mavenCentral()
    }
    dependencies {
        classpath("com.google.gms:google-services:4.3.10") // Firebase Google Services classpath
    }
}
 */