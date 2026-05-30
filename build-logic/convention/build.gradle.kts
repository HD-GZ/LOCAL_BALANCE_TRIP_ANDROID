import org.gradle.kotlin.dsl.compileOnly

plugins {
    `kotlin-dsl`
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    compileOnly(libs.kotlin.gradle)
    compileOnly(libs.android.gradle.tool)
    compileOnly(libs.ksp.gradle)
    compileOnly(libs.hilt.gradle)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "live.lb_trip.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "live.lb_trip.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "live.lb_trip.android.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("androidHilt") {
            id = "live.lb_trip.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("androidFeatureImpl") {
            id = "live.lb_trip.android.feature.impl"
            implementationClass = "AndroidFeatureImplConventionPlugin"
        }
        register("androidFeatureApi") {
            id = "live.lb_trip.android.feature.api"
            implementationClass = "AndroidFeatureApiConventionPlugin"
        }
    }
}