import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.yaml:snakeyaml:2.2")
    }
}

plugins {
    id("common-conventions")
    id("quality-conventions")
    alias(libs.plugins.shadow)
}

dependencies {
    implementation(project(":kartabattlepass-core"))

    // Paper API
    compileOnly(libs.paper.api)
    // Folia API (for scheduler detection)
    compileOnly(libs.folia.api)

    // Plugin integrations
    compileOnly(libs.vault.api)
    compileOnly(libs.placeholderapi)
    compileOnly(libs.citizens)
    compileOnly(libs.mythicmobs.api)
    compileOnly(libs.protocollib)
    compileOnly(libs.luckperms.api)
    compileOnly(libs.worldguard)

    // bStats
    implementation(libs.bstats)

    // Adventure Platform
    implementation(libs.adventure.platform.bukkit)
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("") // Produce a single, non-classified JAR
    relocate("org.jdbi", "com.kartabattlepass.libs.jdbi")
    relocate("com.zaxxer.hikari", "com.kartabattlepass.libs.hikaricp")
    relocate("org.flywaydb", "com.kartabattlepass.libs.flywaydb")
    relocate("com.github.benmanes.caffeine", "com.kartabattlepass.libs.caffeine")
    relocate("com.fasterxml.jackson", "com.kartabattlepass.libs.jackson")
    relocate("org.yaml.snakeyaml", "com.kartabattlepass.libs.snakeyaml")
    relocate("io.lettuce.core", "com.kartabattlepass.libs.lettuce")
    relocate("org.bstats", "com.kartabattlepass.libs.bstats")

    // We don't want to shade the API module into the plugin
    configurations = listOf(project.configurations.runtimeClasspath.get())
    dependencies {
        exclude(dependency("com.kartabattlepass:kartabattlepass-api:.*"))
    }
}

// This task generates the plugin.yml file.
tasks.register("generatePluginYml") {
    val pluginYmlFile = layout.buildDirectory.file("resources/main/plugin.yml")
    outputs.file(pluginYmlFile)

    doLast {
        pluginYmlFile.get().asFile.apply {
            parentFile.mkdirs()
            val yaml = Yaml(DumperOptions().apply {
                defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
                isPrettyFlow = true
                indent = 2
            })
            val pluginData = mapOf(
                "name" to "KartaBattlePass",
                "main" to "com.karta.battlepass.bukkit.KartaBattlePassPlugin",
                "version" to project.version,
                "apiVersion" to "1.21",
                "authors" to listOf("Karta Team"),
                "website" to "https://minekarta.com",
                "softdepend" to listOf(
                    "Vault",
                    "PlaceholderAPI",
                    "Citizens",
                    "MythicMobs",
                    "ProtocolLib",
                    "ItemsAdder",
                    "Oraxen",
                    "LuckPerms",
                    "WorldGuard"
                ),
                "folia-supported" to true,
                "commands" to mapOf(
                    "kartabattlepass" to mapOf(
                        "description" to "Main command for KartaBattlePass.",
                        "aliases" to listOf("kbp", "bp")
                    )
                ),
                "permissions" to mapOf(
                    "kbp.user.*" to mapOf("default" to true, "children" to mapOf(
                        "kbp.user.open" to true,
                        "kbp.user.quests" to true,
                        "kbp.user.rewards" to true,
                        "kbp.user.buy" to true,
                        "kbp.user.gift" to true,
                        "kbp.user.boosters" to true,
                        "kbp.user.leaderboard" to true
                    )),
                    "kbp.admin.*" to mapOf("default" to "op", "children" to mapOf(
                        "kbp.admin.reload" to true,
                        "kbp.admin.setpoints" to true,
                        "kbp.admin.addpoints" to true,
                        "kbp.admin.settier" to true,
                        "kbp.admin.givepass" to true,
                        "kbp.admin.season.*" to true,
                        "kbp.admin.quest.*" to true,
                        "kbp.admin.booster.*" to true,
                        "kbp.admin.leaderboard.*" to true,
                        "kbp.admin.import" to true,
                        "kbp.admin.migrate" to true
                    ))
                )
            )
            writeText(yaml.dump(pluginData))
        }
    }
}

// Make sure plugin.yml is generated before resources are processed.
tasks.processResources {
    dependsOn("generatePluginYml")
}

// Add the generated resources to the shadowJar
tasks.shadowJar {
    from(layout.buildDirectory.file("resources/main"))
}
