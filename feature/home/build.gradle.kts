plugins {
    alias(libs.plugins.convention.android.feature)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "live.lb_trip.feature.home"
}

dependencies {
    implementation(projects.core.designsystem)
}
