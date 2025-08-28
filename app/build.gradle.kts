import java.util.Date
import java.text.SimpleDateFormat

plugins {
    id("com.android.application")
}

var buildNumber = 1

val buildNumberFile = file("build.number.txt")

if (buildNumberFile.exists()) {
    buildNumber = buildNumberFile.readText().toIntOrNull() ?: 1
}

buildNumberFile.writeText((buildNumber + 1).toString())

android {
    namespace = "com.mikhno.appmanager"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.mikhno.appmanager"
        minSdk = 27
        targetSdk = 35
        versionCode = buildNumber
        versionName = "1.0.${buildNumber}"

        buildConfigField("Boolean", "IS_RUSTORE", "true")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("androidx.preference:preference:1.2.1")
    implementation("androidx.fragment:fragment:1.8.9")
    implementation("com.jaredrummler:apk-parser:1.0.2")
    implementation("androidx.core:core:1.17.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.14.0-alpha04")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
}