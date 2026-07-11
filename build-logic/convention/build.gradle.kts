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
    compileOnly(libs.ktlint.gradle)
    compileOnly(libs.detekt.gradle)
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
        register("jvmLibrary") {
            id = "live.lb_trip.jvm.library"
            implementationClass = "JvmLibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "live.lb_trip.android.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("androidHilt") {
            id = "live.lb_trip.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("androidFeatureApi") {
            id = "live.lb_trip.android.feature.api"
            implementationClass = "AndroidFeatureApiConventionPlugin"
        }
        register("androidFeatureImpl") {
            id = "live.lb_trip.android.feature.impl"
            implementationClass = "AndroidFeatureImplConventionPlugin"
        }
        register("ktlint") {
            id = "live.lb_trip.ktlint"
            implementationClass = "KtlintConventionPlugin"
        }
        register("detekt") {
            id = "live.lb_trip.detekt"
            implementationClass = "DetektConventionPlugin"
        }
    }
}