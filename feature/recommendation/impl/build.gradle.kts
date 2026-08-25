plugins {
    alias(libs.plugins.convention.android.feature.impl)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "live.lb_trip.feature.recommendation"
}

dependencies {
    implementation(projects.feature.recommendation.api)
    implementation(projects.feature.savedcourses.api)
    implementation(projects.core.designsystem)
    implementation(projects.core)
    implementation(projects.domain)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.naver.map.compose)
}
