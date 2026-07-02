plugins {
    alias(libs.plugins.convention.android.application)
    alias(libs.plugins.convention.android.compose)
    alias(libs.plugins.convention.android.hilt)
}

android {
    namespace = "live.lb_trip.localbalancetrip"

    defaultConfig {
        applicationId = "live.lb_trip.localbalancetrip"
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_URL", "\"https://api.stage.lb-trip.live\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            buildConfigField("String", "BASE_URL", "\"https://api.lb-trip.live\"")
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
    implementation(projects.feature.signup.api)
    implementation(projects.feature.signup.impl)
    implementation(projects.feature.signin.api)
    implementation(projects.feature.signin.impl)
    implementation(projects.feature.settings.api)
    implementation(projects.feature.settings.impl)
    implementation(projects.core.designsystem)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)

    implementation(libs.circuit.foundation)
    implementation(libs.circuit.runtime)

    implementation(libs.hilt.navigation.compose)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}
