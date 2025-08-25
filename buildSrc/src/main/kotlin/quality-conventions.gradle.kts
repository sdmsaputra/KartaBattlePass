import net.ltgt.gradle.errorprone.errorprone
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.the
import org.gradle.kotlin.dsl.withType

plugins {
    id("common-conventions")
    id("com.diffplug.spotless")
    id("checkstyle")
    id("net.ltgt.errorprone")
}

configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    java {
        googleJavaFormat().aosp()
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlin {
        ktlint()
        trimTrailingWhitespace()
        endWithNewline()
    }
    format("misc") {
        target("*.md", "*.gitignore", ".editorconfig")
        trimTrailingWhitespace()
        endWithNewline()
    }
}

checkstyle {
    toolVersion = "10.17.0"
    configFile = rootProject.file("config/checkstyle/checkstyle.xml")
    isIgnoreFailures = false
    maxWarnings = 0
}

tasks.withType<Checkstyle>().configureEach {
    classpath = project.the<JavaPluginExtension>().sourceSets.getByName("main").output
}

dependencies {
    "errorprone"("com.google.errorprone:error_prone_core:2.27.0")
    "errorproneJavac"("com.google.errorprone:javac:9+181-r4173-1")
}

tasks.withType<JavaCompile> {
    options.errorprone.isEnabled.set(true)
}
