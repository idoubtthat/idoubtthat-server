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
    java
    libs.plugins.kotlin.jvm
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}