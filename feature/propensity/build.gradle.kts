plugins {
    alias(libs.plugins.convention.android.feature)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "live.lb_trip.feature.propensity"
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.domain)
}
