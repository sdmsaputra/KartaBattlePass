allprojects {
    group = "com.kartabattlepass"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://jitpack.io") // For Vault API
        maven("https://repo.citizensnpcs.co/") // For Citizens
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/") // For ProtocolLib
        maven("https://maven.enginehub.org/repo/") // For WorldGuard
    }
}
