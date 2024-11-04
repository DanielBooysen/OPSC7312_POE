plugins {
    alias(libs.plugins.androidApplication)
    id("com.google.gms.google-services") // Google Services plugin for Firebase
    alias(libs.plugins.jetbrainsKotlinAndroid)
}

android {
    namespace = "com.example.opsc7312_poe"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.opsc7312_poe"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_17 // Updated to Java 17
        targetCompatibility = JavaVersion.VERSION_17 // Updated to Java 17
    }

    kotlinOptions {
        jvmTarget = "17" // Consistent Kotlin JVM target version
    }
}

// Declare explicit task dependencies to ensure correct order
tasks.whenTaskAdded {
    if (name == "mergeDebugResources") {
        dependsOn("processDebugGoogleServices")
    }
    if (name == "mergeReleaseResources") {
        dependsOn("processReleaseGoogleServices")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Firebase BoM - Bill of Materials
    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))

    // Firebase Libraries
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-messaging:23.1.2")
    implementation("com.google.firebase:firebase-analytics:21.4.0")

    // Google Sign-In
    implementation("com.google.android.gms:play-services-auth:20.0.1")
    implementation(libs.firebase.storage.ktx)

    // Testing Libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Retrofit for HTTP calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")


    // Biometric Authentication
    implementation ("androidx.biometric:biometric:1.2.0-alpha05")



}