buildscript {
    repositories {
        mavenLocal()
        maven { url=uri("https://maven.aliyun.com/repository/public/") }
        mavenCentral()
        maven { url=uri("https://plugins.gradle.org/m2/") }
        maven { url=uri("https://oss.sonatype.org/content/repositories/releases/") }
        maven { url=uri("https://dl.bintray.com/jetbrains/intellij-plugin-service") }
        maven { url=uri("https://dl.bintray.com/jetbrains/intellij-third-party-dependencies/") }
    }
}

plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.5.2"
}

group = "cn.mercury"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
intellij {
    version.set("2021.3.3")
    type.set("IU")
    plugins.set(listOf("DatabaseTools","java"))
}


dependencies {
    implementation("uk.com.robust-it:cloning:1.9.2")
    implementation("org.mybatis.generator:mybatis-generator-core:1.4.0")
    implementation("org.freemarker:freemarker:2.3.29")
    implementation("com.itranswarp:compiler:1.0")
    implementation("cn.hutool:hutool-core:5.8.0")
    implementation("org.dom4j:dom4j:2.1.3")
    implementation("jaxen:jaxen:1.1.1")

    testImplementation("junit:junit:4.12")
    testImplementation("commons-io:commons-io:2.8.0")

    compileOnly("org.projectlombok:lombok:1.18.4")
    annotationProcessor("org.projectlombok:lombok:1.18.4")
    testImplementation("org.projectlombok:lombok:1.18.4")
    testCompileOnly("org.projectlombok:lombok:1.18.4")
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
        options.encoding = "UTF-8"
    }

    patchPluginXml {
        sinceBuild.set("212")
        untilBuild.set("222.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
