plugins {
    alias(libs.plugins.convention.android.feature.impl)
}

android {
    namespace = "live.lb_trip.feature.home"
}

dependencies {
    implementation(projects.feature.home.api)
    implementation(projects.feature.settings.api)
    implementation(projects.domain)
}
