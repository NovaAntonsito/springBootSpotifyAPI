

plugins {
    java
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
}

group = "com.springbackend"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

extra["snippetsDir"] = file("build/generated-snippets")

dependencies {
    implementation("se.michaelthelin.spotify:spotify-web-api-java:8.3.4")
    implementation("org.springframework:spring-webflux:6.1.1")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-rest")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    implementation("com.sachinhandiekar:jMusixMatch:1.1.4")
    //Developement extension
    implementation("com.slack.api:slack-api-client:1.36.1")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
    runtimeOnly("com.mysql:mysql-connector-j")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("mysql:mysql-connector-java:5.1.6")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")


}

tasks.withType<Test> {
    useJUnitPlatform()
}
val snippetsDir by extra { file("build/generated-snippets") }

tasks.test {
    outputs.dir(snippetsDir)
}


tasks.asciidoctor {
    inputs.dir(snippetsDir)
    dependsOn(tasks.test)
}
