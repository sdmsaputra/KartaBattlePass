plugins {
    id("common-conventions")
    id("quality-conventions")
}

dependencies {
    compileOnly(libs.paper.api)
    compileOnly(libs.jetbrains.annotations)

    // Adventure API is part of Paper, but we declare it for clarity and standalone compilation
    compileOnly(libs.bundles.adventure.api.bundle)
}
