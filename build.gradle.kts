plugins {
    java

    id("com.diffplug.spotless") version "7.0.0.BETA2"

    id("org.springframework.boot") version "3.4.1"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "fr.redstom"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    maven("https://maven.scijava.org/content/repositories/public/")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("club.minnced:java-discord-rpc:1.3.6")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
}

spotless {
    java {
        googleJavaFormat()
            .reorderImports(true)
            .formatJavadoc(true)
            .reflowLongStrings()
            .aosp()
    }
}
