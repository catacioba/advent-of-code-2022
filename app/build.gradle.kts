plugins {
    id("org.jetbrains.kotlin.jvm") version "1.7.10"

    application
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.1")

    implementation("com.google.guava:guava:31.1-jre")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.21")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")
}

application {
    mainClass.set("aoc.AppKt")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
