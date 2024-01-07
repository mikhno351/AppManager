import java.util.Date
import java.text.SimpleDateFormat

plugins {
    id("com.android.application")
}

val versionNameDate = SimpleDateFormat("dd-MM-yy").format(Date()).toString()
var buildNumber = 1

val buildNumberFile = file("buildnumber.txt")
if (buildNumberFile.exists()) buildNumber = buildNumberFile.readText().toIntOrNull() ?: 1

buildNumberFile.writeText((buildNumber + 1).toString())

android {
    namespace = "com.mixno35.appmanager"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mixno35.appmanager"
        minSdk = 27
        targetSdk = 34
        versionCode = buildNumber
        versionName = "1.0.$buildNumber.$versionNameDate"

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
    implementation("com.jaredrummler:apk-parser:1.0.2")
    implementation("com.getkeepsafe.taptargetview:taptargetview:1.13.3")
    implementation("androidx.core:core:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}