val ktor_version: String by project
val kotlin_version: String by project

plugins {
    java
    `maven-publish`
    signing
    id("org.jetbrains.dokka") version "1.4.32"
    kotlin("jvm") version "1.5.20"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.5.20"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

group = "io.github.dseelp"
version = "0.3"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-client-serialization:$ktor_version")
    implementation("io.ktor:ktor-server-host-common:$ktor_version")
    implementation("io.ktor:ktor-serialization:$ktor_version")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.2.1")
}

val implementationVersion = version

tasks {
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveBaseName.set("server")
        mergeServiceFiles()
        manifest {
            attributes(mapOf("Implementation-Version" to implementationVersion))
        }
    }
}

tasks {
    build {
        //dependsOn(shadowJar)
    }
}


val dokkaHtml by tasks.getting(org.jetbrains.dokka.gradle.DokkaTask::class)

val javadocJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
    dependsOn(dokkaHtml)
    archiveClassifier.set("javadoc")
    from(dokkaHtml.outputDirectory)
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val isDeployingToCentral = System.getenv().containsKey("DEPLOY_CENTRAL")

if (isDeployingToCentral) println("Deploying to central...")

publishing {
    repositories {
        if (isDeployingToCentral) maven(url = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/") {
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        } else mavenLocal()
    }
    publications {
        register(project.name, MavenPublication::class) {
            from(components["kotlin"])
            artifact(javadocJar.get())
            artifact(sourcesJar.get())

            pom {
                url.set("https://github.com/DSeeLP/Kommon")
                val prefix = "pom.${project.name}"
                val pomName = project.property("$prefix.name")
                val pomDescription = project.property("$prefix.description")
                name.set(pomName as String)
                description.set(pomDescription as String)
                developers {
                    developer {
                        name.set("DSeeLP")
                        organization.set("com.github.dseelp")
                        organizationUrl.set("https://www.github.com/DSeeLP")
                    }
                }
                licenses {
                    license {
                        name.set("MIT LICENSE")
                        url.set("https://www.opensource.org/licenses/mit-license.php")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/DSeeLP/Kommon.git")
                    developerConnection.set("scm:git:git://github.com/DSeeLP/Kommon.git")
                    url.set("https://github.com/DSeeLP/Kommon/")
                }
            }
        }
    }
}

signing {
    if (!isDeployingToCentral) return@signing
    useInMemoryPgpKeys(
        //System.getenv("SIGNING_ID"),
        System.getenv("SIGNING_KEY"),
        System.getenv("SIGNING_PASSWORD")
    )
    publishing.publications.onEach {
        sign(it)
    }
}