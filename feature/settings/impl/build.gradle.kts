plugins {
    alias(libs.plugins.convention.android.feature.impl)
}

android {
    namespace = "live.lb_trip.feature.settings"
}

dependencies {
    implementation(projects.feature.settings.api)
    implementation(projects.domain)
}
