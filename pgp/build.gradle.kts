
plugins {
    alias(libs.plugins.kotlin.jvm)
}
repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}


dependencies {
    testImplementation(libs.junit.kotlin)
    testImplementation(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.runtime)

    implementation(libs.jackson.jsr310)
    implementation(libs.jackson.kotlin)
    implementation(libs.bouncycastle)
}


tasks.named<Test>("test") {
    useJUnitPlatform()
}
