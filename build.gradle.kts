plugins {
    java
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
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
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation(platform("org.mongodb:mongodb-driver-bom:5.6.1"))
    implementation ("org.mongodb:mongodb-driver-sync")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
