plugins {
    alias(libs.plugins.convention.android.feature.impl)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "live.lb_trip.feature.propensity"
}

dependencies {
    implementation(projects.feature.propensity.api)
    implementation(projects.core.designsystem)
    implementation(projects.core)
    implementation(projects.domain)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
}
