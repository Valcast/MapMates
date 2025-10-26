pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "SocialMeetingApp"
include(":app")
include(":modules:feature-login:impl")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":modules:core-navigation")
include(":modules:feature-login:api")
include(":modules:core-navigation:api")
include(":modules:core-navigation:impl")
include(":modules:feature-account")
include(":modules:feature-account:impl")
