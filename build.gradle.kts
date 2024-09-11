import org.springframework.boot.gradle.tasks.bundling.BootJar
import java.net.URI

plugins {
    java
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
    id("maven-publish")

}

group = "com.pepej"
version = "0.0.2-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.withType<BootJar> {
    enabled = false
}

repositories {
    mavenCentral()
    maven { url = URI("https://hub.spigotmc.org/nexus/content/groups/public/") }
    maven { url = URI("https://oss.squareland.ru/repository/minecraft") }
}

dependencies {
    compileOnly("com.pepej:papi-core:2.6.4")

    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT")
    implementation("org.springframework.boot:spring-boot-starter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.pepej"
            artifactId = "papi-spring"
            version = "0.0.15"
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
        }
    }

    repositories {
        maven {
            name = "minecraft"
            url = uri("https://oss.squareland.ru/repository/minecraft")
            credentials {
                username = "dev"
                password = "XPzCnFqG93ZuWUA"

            }
        }
    }
}




tasks.withType<Test> {
    useJUnitPlatform()
}
