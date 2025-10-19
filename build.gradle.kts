import java.util.*

plugins {
    `maven-publish`
    `java-library`

    id("java")
    id("com.github.johnrengelman.shadow").version("8.1.1")
}

group = "me.xiaoying.sqlfactory"
version = "1.2.0"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

configurations {
    create("sqlite")
    create("mysql")
    create("postgresql")
}

repositories {
    mavenLocal()
    mavenCentral()
}

// 依赖文件
dependencies {
    // lombok
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")

    // Sqlite
    "sqlite"(group = "org.xerial", name = "sqlite-jdbc", version = "3.49.1.0")
    implementation("org.xerial:sqlite-jdbc:3.49.1.0")

    // Mysql
    "mysql"(group = "com.mysql", name = "mysql-connector-j", version = "9.2.0")
    implementation("com.mysql:mysql-connector-j:9.2.0")

    // PostgreSql
    "postgresql"(group = "org.postgresql", name = "postgresql", version = "42.7.5")
    implementation("org.postgresql:postgresql:42.7.5")
}

// 根据不同依赖注册不同的编译任务
listOf("mysql", "sqlite", "postgresql").forEach { variable ->
    tasks.register<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>(variable) {
        group = "build"
        configurations = listOf(project.configurations[variable])
        archiveClassifier.set(variable)
        archiveFileName.set("sqlfactory-${variable}-${project.version}.jar")
        from(sourceSets.main.get().output)
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            groupId = "me.xiaoying"
            artifactId = "sqlfactory"
            version = project.version.toString()
        }

        // 为每个数据库变体创建发布
        listOf("mysql", "sqlite", "postgresql").forEach { dbType ->
            create<MavenPublication>("maven${dbType.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }}") {
                val shadowTask = tasks.named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>(dbType).get()
                artifact(shadowTask) {
                    classifier = dbType
                }
                groupId = "me.xiaoying"
                artifactId = "sqlfactory"
                version = project.version.toString()
            }
        }
    }

    repositories {
        mavenLocal()
    }
}

tasks {
    jar {
        archiveFileName.set("sqlfactory-${project.version}.jar")
    }

    shadowJar {
        archiveFileName.set("sqlfactory-all-${project.version}.jar")
    }

    named("publishToMavenLocal") {
        dependsOn("sqlite", "mysql", "postgresql", jar, shadowJar)
    }

    build {
        dependsOn("sqlite", "mysql", "postgresql", jar, shadowJar)
    }
}