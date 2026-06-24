plugins {
    id("java")
}

group = "assignment4.ex3"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:6.0.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("com.rabbitmq:amqp-client:5.31.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<JavaExec>("runClient") {
    group = "application"
    description = "Run program A"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("assignment4.ex3.ClientImpl")
}