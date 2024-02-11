group = "info.idoubtthat"
version = "1.0-SNAPSHOT"


buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}

plugins {
    idea
    alias(libs.plugins.kotlin.jvm)
}

java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}