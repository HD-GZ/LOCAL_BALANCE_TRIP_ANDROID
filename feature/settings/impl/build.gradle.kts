plugins {
    alias(libs.plugins.convention.android.feature.impl)
}

android {
    namespace = "live.lb_trip.feature.settings"
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.core)
    implementation(projects.domain)
}
