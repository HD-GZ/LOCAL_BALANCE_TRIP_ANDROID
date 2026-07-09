plugins {
    alias(libs.plugins.convention.android.feature)
}

android {
    namespace = "live.lb_trip.feature.propensity"
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.domain)
}
