import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.xiaoxisi"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.xiaoxisi"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }

        val properties = Properties()
        val localFile = rootProject.file("local.properties")
        if (localFile.exists()) {
            properties.load(localFile.inputStream())
        }

        buildConfigField("String", "ASR_API_KEY", "\"${properties.getProperty("xiaoxisi.asr.apiKey", "")}\"")
        buildConfigField("String", "ASR_API_SECRET", "\"${properties.getProperty("xiaoxisi.asr.apiSecret", "")}\"")
        buildConfigField("String", "ASR_APP_ID", "\"${properties.getProperty("xiaoxisi.asr.appId", "")}\"")
        buildConfigField("String", "LLM_PROVIDER", "\"${properties.getProperty("xiaoxisi.llm.provider", "qwen")}\"")
        buildConfigField("String", "LLM_API_KEY", "\"${properties.getProperty("xiaoxisi.llm.apiKey", "")}\"")
        buildConfigField("String", "LLM_BASE_URL", "\"${properties.getProperty("xiaoxisi.llm.baseUrl", "https://dashscope.aliyuncs.com/compatible-mode/v1")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.activity.compose)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons)
    debugImplementation(libs.compose.ui.tooling)

    implementation(libs.lifecycle.runtime)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.navigation.compose)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation)

    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    implementation(libs.coroutines)
    implementation(libs.gson)
}
