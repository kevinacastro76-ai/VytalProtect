plugins { java }

group = "lat.vytalprotect"
version = "1.0.0"

repositories { mavenCentral(); maven("https://repo.papermc.io/repository/maven-public/") }

dependencies { compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT") }

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

// JDK 21 compiler, Java 8 bytecode/API surface for legacy server support.
tasks.withType<JavaCompile>().configureEach {
    options.release.set(8)
    options.encoding = "UTF-8"
}

 tasks.processResources { filteringCharset = "UTF-8"; filesMatching("plugin.yml") { expand("version" to project.version) } }
