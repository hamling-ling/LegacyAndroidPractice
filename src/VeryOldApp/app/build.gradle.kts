plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    kotlin("kapt")
    alias(libs.plugins.dagger.hilt.android)
}

android {
    namespace = "com.example.veryoldapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.veryoldapp"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.example.veryoldapp.CustomTestRunner"
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        dataBinding = true
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)

    // ViewModel and LiveData
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Hilt
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.hilt.android)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.androidx.ui.test.junit4.android)
    testImplementation(libs.androidx.core.testing)
    testImplementation(libs.androidx.runner)
    androidTestImplementation(libs.hilt.android.testing)
    kapt(libs.hilt.android.compiler)

    // Json
    implementation(libs.gson)

    // Unit Test
    testImplementation(libs.hilt.android.testing)
    testImplementation(libs.junit)
    testImplementation(libs.androidx.core.testing)

    // Android Unit Test
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.hilt.android.testing)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.core.testing)
    androidTestImplementation("androidx.test:runner:1.5.0")
}
