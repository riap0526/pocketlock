import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val keystorePropsFile = rootProject.file("keystore.properties")
val keystoreProps = Properties().apply {
    if (keystorePropsFile.exists()) {
        keystorePropsFile.inputStream().use { load(it) }
    }
}

android {
    namespace = "pl.iskri.pocketlock"
    compileSdk = 35

    defaultConfig {
        applicationId = "pl.iskri.pocketlock"
        minSdk = 27
        targetSdk = 34
        versionCode = 80
        versionName = "1.4"

        // GitHub Actions builds of this fork: derive an always-increasing versionCode from the
        // upstream one and the build number, so every CI build installs over the previous one.
        // Release builds take the number from the tag (FORK_BUILD_NUMBER, see release.yml).
        (System.getenv("FORK_BUILD_NUMBER") ?: System.getenv("GITHUB_RUN_NUMBER"))
            ?.toIntOrNull()?.let { run ->
                versionCode = versionCode!! * 10000 + run
                versionName = "$versionName-fork.$run"
            }
    }

    signingConfigs {
        if (keystorePropsFile.exists()) {
            create("release") {
                storeFile = rootProject.file(keystoreProps.getProperty("storeFile"))
                storePassword = keystoreProps.getProperty("storePassword")
                keyAlias = keystoreProps.getProperty("keyAlias")
                keyPassword = keystoreProps.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            if (keystorePropsFile.exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}
