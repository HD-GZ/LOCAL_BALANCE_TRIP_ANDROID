plugins {
    alias(libs.plugins.convention.android.feature.impl)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "live.lb_trip.feature.home"
}

dependencies {
    implementation(projects.feature.home.api)
    implementation(projects.core.designsystem)
    implementation(projects.core)
    implementation(projects.domain)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
}
