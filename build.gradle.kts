plugins {
    java
}

group = "lat.vytal"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.withType<JavaCompile>().configureEach {
    // Build with Java 21 while keeping Java 8 bytecode/API compatibility for older 1.16 servers.
    options.encoding = "UTF-8"
    options.release.set(8)
}

tasks.jar {
    archiveBaseName.set("VytalProtect")
}
