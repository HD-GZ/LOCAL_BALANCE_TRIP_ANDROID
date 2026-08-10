plugins {
    alias(libs.plugins.convention.android.library)
    alias(libs.plugins.convention.android.compose)
}

android {
    namespace = "live.lb_trip.core.designsystem"
}

dependencies {
    implementation(libs.androidx.compose.material3.adaptive.navigation.suite)
}
