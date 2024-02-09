import org.jooq.meta.jaxb.*

val ktor_version: String by project
val kotlin_version: String by project
val flyway_version: String by project
val exposed_version: String by project
val logback_version: String by project
val jooq_version: String by project
val h2_version: String by project
val junit_version: String by project

buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
    }

    dependencies {
        classpath("org.jooq:jooq-codegen:3.19.3")
        classpath("mysql:mysql-connector-java:8.0.33")
    }
}

plugins {
    application
    idea
    kotlin("jvm") version "1.9.22"
    id("io.ktor.plugin") version "2.3.7"
    id("org.jooq.jooq-codegen-gradle") version "3.19.3"
    id("org.openapi.generator") version "7.2.0"
}

sourceSets {
    main {
        kotlin {
            srcDirs("$buildDir/generated/openapi/server/src/main/kotlin", "src/database-bindings/kotlin")
        }
    }
    create("integrationTest") {
        kotlin.srcDirs("src/integration-test/kotlin", "$buildDir/generated/openapi/client/src/main/kotlin")
        resources.srcDir("src/integration-test/resources")
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += sourceSets.main.get().output
    }
}

idea {
    module {
        sourceDirs.add(file("$buildDir/generated/openapi/server/src/main/kotlin"))
        sourceDirs.add(file("$buildDir/generated/openapi/client/src/main/kotlin"))
        sourceDirs.add(file("src/database-bindings/kotlin"))
        testSources.from(sourceSets["integrationTest"].kotlin.srcDirs)
        testResources.from(sourceSets["integrationTest"].resources.srcDirs)
    }
}

openApiGenerate {
    generatorName.set("kotlin-server")
//    generatorName.set("jaxrs-spec")
    inputSpec.set("$rootDir/src/main/resources/openapi/documentation.yaml")
    outputDir.set("$buildDir/generated/openapi/server")
    apiPackage.set("info.idoubtthat.api")
    invokerPackage.set("info.idoubtthat.invoker")
    modelPackage.set("info.idoubtthat.model")
    configOptions.set(mapOf(
        "interfaceOnly" to "true",
        "packageName" to "info.idoubtthat",
        "library" to "jaxrs-spec"
    ))
}

val compileTypeScript by tasks.registering(org.gradle.api.DefaultTask::class) {
    group = "build"
    description = "Compile TypeScript files"
    dependsOn("clean")

    doLast {
        exec {
            workingDir = project.file("src/main/webapp/ts")
            commandLine("tsc")
        }
    }
}

tasks.compileKotlin.get().dependsOn(tasks.openApiGenerate)

jooq {
    configuration {
        jdbc {
            driver = "com.mysql.cj.jdbc.Driver"
            url = "jdbc:mysql://localhost/citation"

            // "username" is a valid synonym for "user"
            user = "root"
            password = "secret"
        }

        generator {
            name = "org.jooq.codegen.KotlinGenerator"

            database {
                name = "org.jooq.meta.mysql.MySQLDatabase"
                inputSchema = "citation"
            }

            target {
                packageName = "info.idoubtthat.db.schema"

                directory = "src/database-bindings/kotlin"
            }
        }
    }
}

application {
    mainClass.set("info.idoubtthat.ApplicationKt")
}
group = "info.idoubtthat"
version = "1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_21
java.targetCompatibility = JavaVersion.VERSION_21


repositories {
    mavenCentral()
}

val integrationTestImplementation by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

val integrationTestRuntimeOnly by configurations.getting {
    extendsFrom(configurations.runtimeOnly.get())
}

dependencies {
    implementation("io.swagger.codegen.v3:swagger-codegen-generators:1.0.46")
    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("ch.qos.logback:logback-classic:1.4.14")
    implementation("jakarta.ws.rs:jakarta.ws.rs-api:2.1.6")
    implementation("jakarta.annotation:jakarta.annotation-api:1.3.5")
    implementation("io.swagger:swagger-annotations:1.5.3")
    implementation("com.fasterxml.jackson.jaxrs:jackson-jaxrs-json-provider:2.16.1")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.1")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.flywaydb:flyway-core:$flyway_version")
    implementation("com.sksamuel.hoplite:hoplite-hocon:2.7.5")
    implementation("org.glassfish.jersey.containers:jersey-container-jetty-http:2.23.1")
    implementation("org.glassfish.jersey.media:jersey-media-json-jackson:2.23.1")
    implementation("org.jooq:jooq:$jooq_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testImplementation ("org.junit.jupiter:junit-jupiter-engine:5.7.1")
    integrationTestImplementation("com.squareup.okhttp3:okhttp:4.11.0")
    integrationTestImplementation("io.ktor:ktor-client:1.3.2-1.4-M2")
    integrationTestImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    integrationTestImplementation ("org.junit.jupiter:junit-jupiter-engine:5.7.1")
    // https://mvnrepository.com/artifact/com.fasterxml.jackson.module/jackson-module-kotlin
    integrationTestImplementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.1")

}

task<org.openapitools.generator.gradle.plugin.tasks.GenerateTask>("generateKotlinApiClient") {
    generatorName.set("kotlin")
    inputSpec.set("$rootDir/src/main/resources/openapi/documentation.yaml")
    outputDir.set("$buildDir/generated/openapi/client")
    apiPackage.set("info.idoubtthat.api")
    invokerPackage.set("info.idoubtthat.invoker")
    modelPackage.set("info.idoubtthat.model")
    configOptions.set(mapOf(
        "library" to "jvm-okhttp4",
        "serializationLibrary" to "jackson",
        "packageName" to "info.idoubtthat"
    ))
}

task<Test>("integration") {
    description = "Runs the integration tests"
    group = "verification"
    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    useJUnitPlatform()
}

tasks["compileIntegrationTestKotlin"].dependsOn(tasks["generateKotlinApiClient"])

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}