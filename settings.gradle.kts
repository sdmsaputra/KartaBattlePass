// Root settings for KartaBattlePass
// Using Gradle 8.8+

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "KartaBattlePass"

include(
    "kartabattlepass-api",
    "kartabattlepass-core",
    "kartabattlepass-bukkit",
    "kartabattlepass-tests"
)
