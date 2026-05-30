plugins {
    alias(libs.plugins.convention.android.library)
}

android {
    namespace = "live.lb_trip.domain"
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.javax.inject)
}
