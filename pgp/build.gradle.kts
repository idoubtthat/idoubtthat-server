
plugins {
    alias(libs.plugins.kotlin.jvm)
}
repositories {
    mavenCentral()
}


dependencies {
    implementation(libs.bouncycastle)
    implementation(libs.jackson.jsr310)
    implementation(libs.jackson.kotlin)
    implementation(libs.ktor.client)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.serialization.json)

    testImplementation(libs.junit.jupiter.engine)
    testImplementation(libs.junit.kotlin)

    testRuntimeOnly(libs.junit.runtime)
}


tasks.test {
    useJUnitPlatform()
}
