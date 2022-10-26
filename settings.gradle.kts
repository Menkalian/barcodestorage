pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven {
            name = "Menkalian"
            url = uri("https://artifactory.menkalian.de/artifactory/menkalian")
        }
    }
}

plugins {
    id("de.fayard.refreshVersions") version "0.40.2"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven {
            name = "Menkalian"
            url = uri("https://artifactory.menkalian.de/artifactory/menkalian")
        }
    }
}

rootProject.name = "Barcode Storage"
include(":app")
