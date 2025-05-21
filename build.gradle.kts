import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.1.21"
    `maven-publish`
    signing
}

val http4kVersion = "6.9.1.0"
val junitVersion = "5.4.2"
val junitPlatformVersion = "1.4.2"

version = if (project.hasProperty("-version")) project.property("-version")!! else "SNAPSHOT"
group = "com.natpryce"

println("building version $version")

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    implementation("org.http4k:http4k-core:$http4kVersion") {
        exclude(group = "org.jetbrains.kotlin")
    }

    testImplementation(kotlin("test"))
    testImplementation("com.natpryce:hamkrest:1.4.2.0")
    testImplementation("org.http4k:http4k-testing-hamkrest:$http4kVersion") {
        exclude(group = "org.jetbrains.kotlin")
    }

    testImplementation("org.junit.platform:junit-platform-runner:$junitPlatformVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("dev.minutest:minutest:1.13.0")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher:$junitPlatformVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kotlin {
    // Use Gradle's JVM Toolchain feature: https://docs.gradle.org/current/userguide/toolchains.html
    jvmToolchain(21)
}


tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_21
    }
}

tasks.named<Jar>("jar") {
    manifest {
        attributes(
            "Implementation-Title" to "krouton",
            "Implementation-Vendor" to "com.natpryce",
            "Implementation-Version" to version
        )
    }
}

val sourcesJar by tasks.registering(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
    from("build/javadoc")
}

artifacts {
    add("archives", sourcesJar)
    add("archives", javadocJar)
}

tasks.withType<Test> {
    useJUnitPlatform {
        includeEngines("junit-jupiter")
    }
    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
    }
}

tasks.register("ossrhAuthentication") {
    doLast {
        if (!(project.hasProperty("ossrh.username") && project.hasProperty("ossrh.password"))) {
            throw GradleException("Missing OSSRH credentials!")
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])

            pom {
                name.set("Krouton")
                description.set("Type-safe and compositional URL routing and reverse routing")
                url.set("https://github.com/npryce/krouton")

                licenses {
                    license {
                        name.set("Apache 2.0")
                        url.set("http://opensource.org/licenses/Apache-2.0")
                    }
                }

                developers {
                    developer {
                        id.set("npryce")
                        name.set("Nat Pryce")
                    }
                }

                scm {
                    connection.set("scm:git:git@github.com:npryce/krouton.git")
                    url.set("https://github.com/npryce/krouton")
                }
            }
        }
    }

    repositories {
        maven {
            val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl

            credentials {
                username = findProperty("ossrh.username") as String?
                password = findProperty("ossrh.password") as String?
            }
        }
    }
}

tasks.named("publish") {
    dependsOn("ossrhAuthentication")
}

signing {
    sign(publishing.publications["mavenJava"])
}
