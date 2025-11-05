plugins {
    java
    id ("application")
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
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
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.session:spring-session-data-mongodb")
    //developmentOnly("org.springframework.boot:spring-boot-devtools")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation(platform("org.mongodb:mongodb-driver-bom:5.6.1"))
    implementation ("org.mongodb:mongodb-driver-sync")
    implementation ("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation ("org.springframework.boot:spring-boot-starter-webflux")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    //JAVAFX
    implementation ("org.openjfx:javafx-controls:21.0.2")
    implementation ("org.openjfx:javafx-fxml:21.0.2")

    implementation("com.github.java-json-tools:json-patch:1.13")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
}

javafx {
    version = "21.0.2"
    modules = listOf("javafx.controls", "javafx.fxml")
}

jlink {
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    launcher {
        name = "Boot"
    }
}

application {
    mainClass.set("gal.usc.etse.sharecloud.Boot")
    mainModule.set("ShareCloud.main")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
