plugins {
    id("common-conventions")
    id("quality-conventions")
}

dependencies {
    implementation(project(":kartabattlepass-core"))
    implementation(project(":kartabattlepass-bukkit"))

    // Testing libraries
    testImplementation(libs.bundles.testing)
    testImplementation(libs.assertj)
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
