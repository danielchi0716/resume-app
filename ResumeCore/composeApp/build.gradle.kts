import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

val localProps = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}

fun requireBuildConfig(key: String): String =
    System.getenv(key)
        ?: localProps.getProperty(key)
        ?: throw GradleException(
            "Missing required build config '$key'. Set it as an env var (CI) or in ResumeCore/local.properties (local).",
        )

fun optionalConfig(key: String): String? =
    System.getenv(key) ?: localProps.getProperty(key)

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.material.iconsExtended)
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.core.ktx)
            implementation(libs.coil.network.okhttp)
            implementation(libs.hilt.android)
            implementation(libs.androidx.hilt.navigationCompose)
            implementation(libs.androidx.navigation.compose)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.coil.compose)
            implementation(projects.shared)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.danielchi0716.resume.core"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.danielchi0716.resume.core"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = optionalConfig("ANDROID_VERSION_CODE")?.toIntOrNull() ?: 1
        versionName = optionalConfig("ANDROID_VERSION_NAME") ?: "1.0-local"

        buildConfigField(
            "String",
            "RESUME_DATA_HOST",
            "\"${requireBuildConfig("RESUME_DATA_HOST")}\"",
        )
        buildConfigField(
            "String",
            "RESUME_SHARE_URL",
            "\"${requireBuildConfig("RESUME_SHARE_URL")}\"",
        )
    }
    buildFeatures {
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    signingConfigs {
        create("release") {
            val storePath = optionalConfig("ANDROID_KEYSTORE_PATH")
            val storePw = optionalConfig("ANDROID_KEYSTORE_PASSWORD")
            val alias = optionalConfig("ANDROID_KEY_ALIAS")
            val keyPw = optionalConfig("ANDROID_KEY_PASSWORD")
            if (storePath != null && storePw != null && alias != null && keyPw != null) {
                storeFile = file(storePath)
                storePassword = storePw
                keyAlias = alias
                keyPassword = keyPw
            }
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            val sc = signingConfigs.getByName("release")
            if (sc.storeFile != null) {
                signingConfig = sc
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
    add("kspAndroid", libs.hilt.compiler)
}

