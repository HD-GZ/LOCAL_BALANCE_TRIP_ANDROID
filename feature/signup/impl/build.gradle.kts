plugins {
    alias(libs.plugins.convention.android.feature.impl)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "live.lb_trip.feature.signup"
}

dependencies {
    implementation(projects.feature.signup.api)
    implementation(projects.core.designsystem)
    implementation(projects.core)
    implementation(projects.domain)
}
