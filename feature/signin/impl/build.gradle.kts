plugins {
    alias(libs.plugins.convention.android.feature.impl)
}

android {
    namespace = "live.lb_trip.feature.signin"
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.feature.signin.api)
    implementation(projects.domain)
}
