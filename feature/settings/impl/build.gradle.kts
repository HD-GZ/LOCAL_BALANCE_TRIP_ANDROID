plugins {
    alias(libs.plugins.convention.android.feature.impl)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "live.lb_trip.feature.settings"
}

dependencies {
    implementation(projects.core.designsystem)
    implementation(projects.core)
    implementation(projects.domain)
    implementation(libs.aboutlibraries.compose.m3)
    implementation(libs.kotlinx.collections.immutable)
}
