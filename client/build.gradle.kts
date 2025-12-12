plugins {
    java
    id ("application")
    id ("org.openjfx.javafxplugin") version "0.0.13"
    id ("org.beryx.jlink") version "2.25.0"
}

group = "gal.usc.etse"
version = "0.0.1-SNAPSHOT"
description = "ShareCloud"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.17.0")
    implementation("com.fasterxml.jackson.core:jackson-core:2.17.0")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.17.0")

    implementation("io.jsonwebtoken:jjwt-api:0.13.0")
    implementation(platform("org.mongodb:mongodb-driver-bom:5.6.1"))
    implementation ("org.mongodb:mongodb-driver-sync")
    implementation("org.json:json:20210307")

    //JAVAFX
    implementation ("org.openjfx:javafx-controls:21.0.2")
    implementation ("org.openjfx:javafx-fxml:21.0.2")
    implementation("org.openjfx:javafx-web:21.0.2")

    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.13.0")
}

javafx {
    version = "21.0.2"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.web")
}

jlink {
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    launcher {
        name = "Boot"
    }
}

application {
    mainClass.set("gal.usc.etse.sharecloud.FachadaGUI")
    mainModule.set("ShareCloud.client.main")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
