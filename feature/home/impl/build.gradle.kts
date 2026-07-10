plugins {
    alias(libs.plugins.convention.android.feature.impl)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "live.lb_trip.feature.home"
}

dependencies {
    implementation(projects.feature.home.api)
    implementation(projects.core.designsystem)
}
