import org.jooq.meta.jaxb.*

val ktor_version: String by project
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
    id("org.jooq.jooq-codegen-gradle") version "3.19.3"
}

sourceSets {
    main {
        kotlin {
            srcDirs("src/database-bindings/kotlin")
        }
    }
}

idea {
    module {
        sourceDirs.add(file("src/database-bindings/kotlin"))
    }
}



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


dependencies {
    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("ch.qos.logback:logback-classic:1.4.14")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.flywaydb:flyway-core:10.7.2")
    implementation("com.sksamuel.hoplite:hoplite-hocon:2.7.5")
    implementation("org.jooq:jooq:3.19.3")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testImplementation ("org.junit.jupiter:junit-jupiter-engine:5.7.1")

}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}