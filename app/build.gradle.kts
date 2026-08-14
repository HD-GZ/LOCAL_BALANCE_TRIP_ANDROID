import java.util.Properties

plugins {
    alias(libs.plugins.convention.android.application)
    alias(libs.plugins.convention.android.compose)
    alias(libs.plugins.convention.android.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.aboutlibraries)
}

val localProperties =
    Properties().apply {
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { load(it) }
        }
    }
val naverMapClientId: String = localProperties.getProperty("NCP_KEY_ID", "REPLACE_WITH_NCP_KEY_ID")
val kakaoKey: String = localProperties.getProperty("KAKAO_KEY", "REPLACE_WITH_KAKAO_KEY")
val facebookAppId: String = localProperties.getProperty("FACEBOOK_APP_ID", "REPLACE_WITH_FACEBOOK_APP_ID")

android {
    namespace = "live.lb_trip.localbalancetrip"

    defaultConfig {
        applicationId = "live.lb_trip.localbalancetrip"
        versionCode = (project.findProperty("versionCode") as String?)?.toIntOrNull() ?: 1
        versionName = project.findProperty("versionName") as String? ?: "0.0.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }

    signingConfigs {
        create("release") {
            storeFile = rootProject.file(localProperties["KEYSTORE_FILE"] ?: "")
            storePassword = localProperties["KEYSTORE_PASSWORD"] as String? ?: ""
            keyAlias = localProperties["KEY_ALIAS"] as String? ?: ""
            keyPassword = localProperties["KEY_PASSWORD"] as String? ?: ""
        }
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"https://api.stage.lb-trip.live\"")
            buildConfigField("String", "NCP_KEY_ID", "\"$naverMapClientId\"")
            buildConfigField("String", "KAKAO_KEY", "\"$kakaoKey\"")
            buildConfigField("String", "FACEBOOK_APP_ID", "\"$facebookAppId\"")
            manifestPlaceholders["KAKAO_KEY"] = kakaoKey
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            buildConfigField("String", "BASE_URL", "\"https://api.lb-trip.live\"")
            buildConfigField("String", "NCP_KEY_ID", "\"$naverMapClientId\"")
            buildConfigField("String", "KAKAO_KEY", "\"$kakaoKey\"")
            buildConfigField("String", "FACEBOOK_APP_ID", "\"$facebookAppId\"")
            manifestPlaceholders["KAKAO_KEY"] = kakaoKey

            signingConfig = signingConfigs.getByName("release")
        }
    }
}

dependencies {
    implementation(projects.domain)
    implementation(projects.data)
    implementation(projects.feature.home.api)
    implementation(projects.feature.home.impl)
    implementation(projects.feature.onboarding.api)
    implementation(projects.feature.onboarding.impl)
    implementation(projects.feature.propensity.api)
    implementation(projects.feature.propensity.impl)
    implementation(projects.feature.recommendation.api)
    implementation(projects.feature.recommendation.impl)
    implementation(projects.feature.tour.api)
    implementation(projects.feature.tour.impl)
    implementation(projects.feature.savedcourses.api)
    implementation(projects.feature.savedcourses.impl)
    implementation(projects.feature.signup.api)
    implementation(projects.feature.signup.impl)
    implementation(projects.feature.signin.api)
    implementation(projects.feature.signin.impl)
    implementation(projects.feature.settings.impl)
    implementation(projects.core)
    implementation(projects.core.designsystem)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.naver.map.compose)
    implementation(libs.kakao.common)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}
