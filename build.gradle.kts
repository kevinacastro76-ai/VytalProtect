plugins {
java
}

group = "lat.vytalprotect"
version = "1.0.0"

repositories {
mavenCentral()

maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")

}

dependencies {
compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
}

java {
toolchain {
languageVersion.set(JavaLanguageVersion.of(8))
}
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}

tasks.jar {
archiveBaseName.set("VytalProtect")
archiveVersion.set(version.toString())
}
