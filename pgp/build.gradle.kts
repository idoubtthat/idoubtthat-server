
plugins {
    alias(libs.plugins.kotlin.jvm)
}
repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}


dependencies {
    implementation(libs.bouncycastle)
    implementation(libs.jackson.jsr310)
    implementation(libs.jackson.kotlin)
    implementation(libs.ktor.client)
    implementation(libs.ktor.client.cio)

    testImplementation(libs.junit.jupiter.engine)
    testImplementation(libs.junit.kotlin)

    testRuntimeOnly(libs.junit.runtime)
}


tasks.named<Test>("test") {
    useJUnitPlatform()
}
