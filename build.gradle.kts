buildscript {
    repositories {
        mavenLocal()
        //maven { url=uri("https://maven.aliyun.com/repository/public/") }
        maven {
            url = uri("http://m2repo.wonhigh.cn:8081/nexus/content/groups/public/")
            isAllowInsecureProtocol = true
        }
        mavenCentral()
        maven { url = uri("https://plugins.gradle.org/m2/") }
        maven { url = uri("https://oss.sonatype.org/content/repositories/releases/") }
        maven { url = uri("https://dl.bintray.com/jetbrains/intellij-plugin-service") }
        maven { url = uri("https://dl.bintray.com/jetbrains/intellij-third-party-dependencies/") }
    }
}

configurations.all{
    // check for updates every build
    resolutionStrategy.cacheChangingModulesFor(0, "seconds")
}

plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.5.2"
}

group = "cn.mercury"
version = "1.3.2"

repositories {
    mavenLocal()
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
intellij {
    version.set("2022.1")
    type.set("IU")
    plugins.set(
        listOf(
            "DatabaseTools", "java", "Spring",
            "SpringBoot",
        )
    )
    updateSinceUntilBuild.set(true)
    downloadSources.set(true)
}


dependencies {
    implementation("uk.com.robust-it:cloning:1.9.2")
    implementation("org.mybatis.generator:mybatis-generator-core:1.4.0")
    implementation("org.freemarker:freemarker:2.3.29")
    implementation("com.itranswarp:compiler:1.0")
    implementation("cn.hutool:hutool-core:5.8.0")
    implementation("org.dom4j:dom4j:2.1.3")
    implementation("org.slf4j:slf4j-nop:1.7.25")
    implementation("jaxen:jaxen:1.1.1")
    implementation("commons-lang:commons-lang:2.6")

    testImplementation("junit:junit:4.12")
    testImplementation("commons-io:commons-io:2.8.0")

    //implementation("cn.wonhigh.mercury:mybatis-x:3.4.6.6-SNAPSHOT")
    implementation("cn.wonhigh.mercury:mercury-mybatis-parser:3.2.1-SNAPSHOT")


    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")
    testImplementation("org.projectlombok:lombok:1.18.24")
    testCompileOnly("org.projectlombok:lombok:1.18.24")
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
