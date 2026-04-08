plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.pantrypal"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.pantrypal"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        isCoreLibraryDesugaringEnabled = true
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    // UI / Material
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")

    // Charts
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // 🔥 SPEEDOMETER GAUGE (NEW)
    implementation("com.github.anastr:speedviewlib:1.6.0")

    // Networking
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.9.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.9.2")

    // Navigation
    implementation("androidx.navigation:navigation-fragment:2.9.3")
    implementation("androidx.navigation:navigation-ui:2.9.3")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")

    // Barcode
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("com.google.zxing:core:3.4.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")

    // Desugaring
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
}