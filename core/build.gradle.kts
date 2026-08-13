plugins {
    alias(libs.plugins.convention.android.library)
    alias(libs.plugins.convention.android.hilt)
}

android {
    namespace = "live.lb_trip.core"

    buildFeatures {
        buildConfig = true
    }
    buildTypes {
        debug {
            buildConfigField("String", "WEB_URL", "\"https://stage.lb-trip.live\"")
        }
        release {
            buildConfigField("String", "WEB_URL", "\"https://lb-trip.live\"")
        }
    }
}

dependencies {
    implementation(libs.androidx.activity)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kakao.share)
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.common)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.javax.inject)
}
