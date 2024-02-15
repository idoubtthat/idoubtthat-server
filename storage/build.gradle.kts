import org.jooq.meta.jaxb.*

buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
    }

    dependencies {
        classpath(libs.jooq.codegen)
        classpath(libs.mysql)
    }
}

plugins {
    alias(libs.plugins.docker.compose)
    alias(libs.plugins.flyway)
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.jooq.codegen)
}

flyway {
    driver = "com.mysql.cj.jdbc.Driver"
    url = "jdbc:mysql://localhost:9217/citation"
    user = "root"
    password = "secret"
}

tasks.flywayMigrate.get().dependsOn(tasks.composeUp.get())

jooq {
    configuration {
        jdbc {
            driver = "com.mysql.cj.jdbc.Driver"
            url = "jdbc:mysql://localhost:9217/citation"

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
                packageName = "db.schema"

                directory = "src/main/kotlin"
            }
        }
    }
}

tasks.jooqCodegen.get().dependsOn(tasks.flywayMigrate.get())
tasks.jooqCodegen.get().finalizedBy(tasks.composeDownForced.get())

group = "info.idoubtthat"
version = "1.0-SNAPSHOT"


repositories {
    mavenCentral()
}


dependencies {
    implementation(libs.flyway)
    implementation(libs.hikari)
    implementation(libs.jooq)
    implementation(libs.logback)
    implementation(libs.mysql)

    testImplementation(libs.junit.jupiter.engine)
    testImplementation(libs.junit.kotlin)

    testRuntimeOnly(libs.junit.runtime)
}

tasks.test {
    useJUnitPlatform()
}

tasks.test.get().dependsOn(tasks.composeUp.get())
tasks.test.get().finalizedBy(tasks.composeDownForced.get())

kotlin {
    jvmToolchain(21)
}
