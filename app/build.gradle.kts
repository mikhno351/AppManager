import java.util.Date
import java.text.SimpleDateFormat

plugins {
    id("com.android.application")
    id("com.google.android.gms.oss-licenses-plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

val versionNameDate = SimpleDateFormat("dd-MM-yy").format(Date()).toString()
var buildNumber = 1

val buildNumberFile = file("buildnumber.txt")
if (buildNumberFile.exists()) buildNumber = buildNumberFile.readText().toIntOrNull() ?: 1

buildNumberFile.writeText((buildNumber + 1).toString())

android {
    namespace = "com.mixno35.app_manager"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mixno35.app_manager"
        minSdk = 27
        targetSdk = 35
        versionCode = buildNumber
        versionName = "1.0.${buildNumber}.${versionNameDate}"

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
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.android.gms:play-services-base:18.5.0")
    implementation("com.google.android.gms:play-services-oss-licenses:17.1.0")
    implementation("androidx.preference:preference:1.2.1")
    implementation("androidx.fragment:fragment:1.8.5")
    implementation("com.jaredrummler:apk-parser:1.0.2")
    implementation("androidx.core:core:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}