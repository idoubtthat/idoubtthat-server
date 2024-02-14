plugins {
    alias(libs.plugins.docker.compose)
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

    testImplementation(libs.junit.jupiter.engine)
    testImplementation(libs.junit.kotlin)
    testImplementation(libs.ktor.client)
    testImplementation(libs.ktor.client.cio)
    testImplementation(libs.ktor.client.logging)
    testImplementation(libs.ktor.client.content.negotiation)
    testImplementation(libs.ktor.client.serialization.json)

    testRuntimeOnly(libs.junit.runtime)
}

application {
    mainClass.set("idoubtthat.server.ApplicationKt")
}

tasks.test {
    useJUnitPlatform()
}

tasks.test.get().dependsOn(tasks.composeUp.get())
tasks.test.get().finalizedBy(tasks.composeDownForced.get())