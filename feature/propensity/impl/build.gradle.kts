plugins {
    alias(libs.plugins.convention.android.feature.impl)
}

android {
    namespace = "live.lb_trip.feature.propensity"
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.feature.propensity.api)
    implementation(projects.domain)
}
