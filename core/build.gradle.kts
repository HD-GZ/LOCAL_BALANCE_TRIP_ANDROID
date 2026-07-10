plugins {
    alias(libs.plugins.convention.android.library)
}

android {
    namespace = "live.lb_trip.core.viewmodel"
}

dependencies {
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.kotlinx.coroutines.core)
}
