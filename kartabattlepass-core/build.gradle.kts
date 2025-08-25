plugins {
    id("common-conventions")
    id("quality-conventions")
    `java-library`
}

dependencies {
    api(project(":kartabattlepass-api"))

    // Database
    implementation(libs.bundles.jdbi)
    implementation(libs.hikaricp)
    implementation(libs.flyway.core)
    runtimeOnly(libs.flyway.mysql)
    runtimeOnly(libs.flyway.postgres)

    // Caching
    implementation(libs.caffeine)

    // Serialization & Config
    implementation(libs.bundles.jackson)
    implementation(libs.snakeyaml)

    // Redis for network features
    implementation(libs.lettuce)

    // Annotations
    compileOnly(libs.jetbrains.annotations)
}
