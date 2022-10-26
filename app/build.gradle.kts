import java.util.Properties

plugins {
    kotlin("android")
    id("com.android.application")
    id("com.google.devtools.ksp")
}

android {
    compileSdk = 33

    defaultConfig {
        applicationId = "de.menkalian.barcodestore"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
            enableV4Signing = true

            val props = Properties()
            props.load(rootProject.file("keystore.local.properties").inputStream())

            storeFile = file(props["path"] ?: "")
            storePassword = props["password"]?.toString() ?: ""
            keyAlias = props["key"]?.toString() ?: ""
            keyPassword = props["keypass"]?.toString() ?: ""
        }
    }

    buildTypes {
        named("debug") {
            applicationIdSuffix = ".dbg"
        }
        named("release") {
            signingConfig = signingConfigs.getByName("release")

            isMinifyEnabled = true
            isDebuggable = false
            isShrinkResources = true

            setProguardFiles(listOf(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"))
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(AndroidX.core.ktx)

    implementation("de.menkalian.sagitta:loglib:_")

    // ZXing for Barcodes
    implementation("com.google.zxing:core:_")

    // Local libraries
    implementation(fileTree("${projectDir}/libs") { include("*.jar") })

    implementation(AndroidX.activity)
    implementation(AndroidX.activity.ktx)
    implementation(AndroidX.annotation)
    implementation(AndroidX.appCompat)
    implementation(AndroidX.constraintLayout)
    implementation(AndroidX.camera.camera2)
    implementation(AndroidX.camera.lifecycle)
    implementation(AndroidX.camera.video)
    implementation(AndroidX.camera.view)
    implementation(AndroidX.camera.extensions)
    implementation(AndroidX.fragment)
    implementation(AndroidX.lifecycle.liveData)
    implementation(AndroidX.lifecycle.liveDataKtx)
    implementation(AndroidX.lifecycle.viewModel)
    implementation(AndroidX.lifecycle.viewModelKtx)
    implementation(AndroidX.recyclerView)
    implementation(AndroidX.room.ktx)
    implementation(AndroidX.room.runtime)

    annotationProcessor(AndroidX.room.compiler)
    ksp(AndroidX.room.compiler)

    implementation(Google.android.material)

    testImplementation(Testing.junit4)
    androidTestImplementation(AndroidX.test.ext.junit)
    androidTestImplementation(AndroidX.test.espresso.core)
}

ksp {
    arg("room.schemaLocation", File(buildDir, "room/schema").absolutePath)
}