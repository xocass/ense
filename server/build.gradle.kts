plugins {
    java
    id("org.springframework.boot") version "3.5.6"
    id("io.spring.dependency-management") version "1.1.7"
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
    maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.session:spring-session-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-security")
    //implementation("org.springframework.boot:spring-boot-starter-hateoas")
    implementation ("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("io.jsonwebtoken:jjwt-api:0.13.0")
    implementation(platform("org.mongodb:mongodb-driver-bom:5.6.1"))
    implementation ("org.mongodb:mongodb-driver-sync")
    implementation("org.json:json:20210307")

    //Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

    // Resend
    implementation("com.resend:resend-java:+")

    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("io.lettuce:lettuce-core")

    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.13.0")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.13.0")
}

jlink {
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    launcher {
        name = "Boot"
    }
}

application {
    mainClass.set("gal.usc.etse.sharecloud.Boot")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
