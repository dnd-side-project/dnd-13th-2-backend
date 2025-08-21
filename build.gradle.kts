plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"
    id("org.springframework.boot") version "3.5.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.diffplug.spotless") version "7.2.1"
}

group = "org.example"

version = "0.0.1-SNAPSHOT"

spotless {
    kotlin {
        ktfmt().kotlinlangStyle()
        targetExclude("**/build/**")
    }
    kotlinGradle { ktfmt().kotlinlangStyle() }
}

java { toolchain { languageVersion = JavaLanguageVersion.of(17) } }

repositories { mavenCentral() }

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Environment Variable (.env)
    implementation("io.github.cdimascio:dotenv-kotlin:6.5.1")

    // Spring Data JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Swagger UI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9")

    // MySQL Connector/J
    runtimeOnly("com.mysql:mysql-connector-j")

    // Spring Batch
    implementation("org.springframework.boot:spring-boot-starter-batch")

    // Spring Webflux
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // H2
    testImplementation("com.h2database:h2")

    // Netty Resolver DNS (macOS)
    val isMac = System.getProperty("os.name").startsWith("Mac OS X")
    val architecture = System.getProperty("os.arch")
    if (isMac && architecture == "aarch64") {
        developmentOnly("io.netty:netty-resolver-dns-native-macos:4.1.123.Final:osx-aarch_64")
    }
}

kotlin { compilerOptions { freeCompilerArgs.addAll("-Xjsr305=strict") } }

tasks.withType<Test> { useJUnitPlatform() }
