plugins {
    alias(libs.plugins.convention.android.feature.impl)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "live.lb_trip.feature.settings"
}

dependencies {
    implementation(projects.feature.settings.api)
}
