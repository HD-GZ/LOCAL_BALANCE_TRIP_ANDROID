plugins {
    alias(libs.plugins.convention.android.feature.impl)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "live.lb_trip.feature.savedcourses"
}

dependencies {
    implementation(projects.feature.savedcourses.api)
    implementation(projects.core.designsystem)
    implementation(projects.core)
    implementation(projects.domain)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.androidx.activity.compose)
}
