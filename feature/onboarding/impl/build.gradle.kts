plugins {
    alias(libs.plugins.convention.android.feature.impl)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "live.lb_trip.feature.onboarding"
}

dependencies {
    implementation(projects.feature.onboarding.api)
    implementation(projects.core.designsystem)
}
