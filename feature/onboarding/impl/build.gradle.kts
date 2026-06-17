plugins {
    alias(libs.plugins.convention.android.feature.impl)
}

android {
    namespace = "live.lb_trip.feature.onboarding"
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.feature.onboarding.api)
    implementation(projects.feature.signin.api)
    implementation(projects.feature.signup.api)
}
