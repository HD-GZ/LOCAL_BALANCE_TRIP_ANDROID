plugins {
    alias(libs.plugins.convention.android.feature.impl)
}

android {
    namespace = "live.lb_trip.feature.signup"
}

dependencies {
    implementation(projects.feature.signup.api)
    implementation(projects.domain)
}
