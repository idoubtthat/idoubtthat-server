plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":storage"))

    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.openapi)
    implementation(libs.ktor.server.serialization.jackson)
    implementation(libs.ktor.server.swagger)
    implementation(libs.logback)

    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.junit.jupiter.engine)
}

application {
    mainClass.set("idoubtthat.server.ApplicationKt")
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}