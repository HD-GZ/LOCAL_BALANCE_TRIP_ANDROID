plugins {
    alias(libs.plugins.convention.android.feature.impl)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "live.lb_trip.feature.tour"
}

dependencies {
    implementation(projects.feature.tour.api)
    implementation(projects.core.designsystem)
    implementation(projects.core)
    implementation(projects.domain)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.naver.map.compose)
}
