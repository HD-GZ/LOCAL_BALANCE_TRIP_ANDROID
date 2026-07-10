plugins {
    alias(libs.plugins.convention.android.feature.impl)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "live.lb_trip.feature.signin"
}

dependencies {
    implementation(projects.feature.signin.api)
    implementation(projects.core.designsystem)
    implementation(projects.domain)
}
