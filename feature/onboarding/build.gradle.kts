plugins {
    alias(libs.plugins.convention.android.feature)
}

android {
    namespace = "live.lb_trip.feature.onboarding"
}

dependencies {
    implementation(projects.core.designsystem)
}
