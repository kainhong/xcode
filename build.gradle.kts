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
    id("org.jetbrains.kotlin.jvm") version "1.8.21"
    id("org.jetbrains.intellij") version "1.15.0"
}

group = "cn.mercury"
version = "2023.1.4"

repositories {
    mavenLocal()
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
intellij {
    version.set("2023.1.2")
    type.set("IU")
    plugins.set(listOf("DatabaseTools","java","Spring" ))
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation("uk.com.robust-it:cloning:1.9.2")
    implementation("org.mybatis.generator:mybatis-generator-core:1.4.0")
    implementation("org.freemarker:freemarker:2.3.29")
    implementation("com.itranswarp:compiler:1.0")
    implementation("cn.hutool:hutool-core:5.8.0")
    implementation("org.dom4j:dom4j:2.1.3")
    implementation("jaxen:jaxen:1.1.1")

    implementation("cn.wonhigh.mercury:mercury-mybatis-parser:3.2.1-SNAPSHOT")

    testImplementation("junit:junit:4.12")
    testImplementation("commons-io:commons-io:2.8.0")

    compileOnly("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.28")
    testImplementation("org.projectlombok:lombok:1.18.28")
    testCompileOnly("org.projectlombok:lombok:1.18.28")
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
        options.encoding = "UTF-8"
    }

    patchPluginXml {
        sinceBuild.set("231")
        untilBuild.set("232.*")
    }

}
