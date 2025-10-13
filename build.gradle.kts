plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"
    id("org.springframework.boot") version "3.5.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.diffplug.spotless") version "7.2.1"
    id("io.sentry.jvm.gradle") version "5.9.0"
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

    // Jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // H2
    testImplementation("com.h2database:h2")

    implementation("org.hibernate.orm:hibernate-spatial:6.2.7.Final")
    implementation("org.locationtech.jts:jts-core:1.19.0")

    // Spring Batch Test
    testImplementation("org.springframework.batch:spring-batch-test")

    // Mockito
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")

    // Netty Resolver DNS (macOS)
    val isMac = System.getProperty("os.name").startsWith("Mac OS X")
    val architecture = System.getProperty("os.arch")
    if (isMac && architecture == "aarch64") {
        developmentOnly("io.netty:netty-resolver-dns-native-macos:4.1.123.Final:osx-aarch_64")
    }
}

kotlin { compilerOptions { freeCompilerArgs.addAll("-Xjsr305=strict") } }

tasks.withType<Test> { useJUnitPlatform() }

sentry {
    includeSourceContext.set(true)

    org.set("dnd1302")
    projectName.set("eodigo-backend")
    authToken.set(System.getenv("SENTRY_AUTH_TOKEN"))
}
