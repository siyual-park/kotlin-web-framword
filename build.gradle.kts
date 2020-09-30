plugins {
    kotlin("jvm") version "1.4.10"
}

group = "org.example"
version = "1.0-SNAPSHOT"

val kotlin_version = "1.4.10"
val junit_version = "5.7.0"
val junit_platform_version = "1.7.0"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    implementation("org.jetbrains.kotlin", "kotlin-reflect", kotlin_version)

    testImplementation("org.junit.jupiter", "junit-jupiter-api", junit_version)
    testImplementation("org.jetbrains.kotlin", "kotlin-test", kotlin_version)
    testImplementation("org.jetbrains.kotlin", "kotlin-test-junit", kotlin_version)

    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", junit_version)

    testRuntimeOnly("org.junit.platform", "junit-platform-launcher", junit_platform_version)
    testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", junit_version)
    testRuntimeOnly("org.junit.vintage", "junit-vintage-engine", junit_version)
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}