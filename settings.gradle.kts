pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "LocalBalanceTrip"
include(":app")
include(":core:designsystem")
include(":domain")
include(":data")
include(":feature:home:api")
include(":feature:home")
include(":feature:settings:api")
include(":feature:settings:impl")
include(":feature:signup:api")
include(":feature:signup:impl")
include(":feature:signin:api")
include(":feature:signin:impl")
include(":feature:onboarding:api")
include(":feature:onboarding:impl")
include(":feature:propensity")
