plugins {
    java
}

group = "io.github.itamarbleiberg"
// The BACAP version. It also names the datapack zip below and is shown in the plugin and pack titles.
version = "1.20.2"

// The original, unmodified BACAP datapack. Its contents are copied into the plugin jar as-is.
val datapackZip = layout.projectDirectory.file("BlazeandCave's Advancements Pack $version.zip")

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
}

tasks.compileJava {
    options.encoding = "UTF-8"
    options.release.set(21)
}

tasks.processResources {
    val props = mapOf("version" to project.version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("paper-plugin.yml") {
        expand(props)
    }
    // BacapBootstrap registers this folder with Paper as a datapack.
    from(zipTree(datapackZip)) {
        into("bacap")
    }
}

tasks.jar {
    archiveFileName.set("BACAP-${project.version}.jar")
}
