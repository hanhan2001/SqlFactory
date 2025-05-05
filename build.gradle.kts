plugins {
    `maven-publish`
    `java-library`

    id("java")
    id("com.github.johnrengelman.shadow").version("8.1.1")
}

group = "me.xiaoying.sqlfactory"
version = "1.0.0"

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
    "sqlite" (group = "org.xerial", name = "sqlite-jdbc", version = "3.49.1.0")
    implementation("org.xerial:sqlite-jdbc:3.49.1.0")

    // Mysql
    "mysql" (group = "com.mysql", name = "mysql-connector-j", version = "9.2.0")
    implementation("com.mysql:mysql-connector-j:9.2.0")

    // PostgreSql
    "postgresql" (group = "org.postgresql", name = "postgresql", version = "42.7.5")
    implementation("org.postgresql:postgresql:42.7.5")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from (components["java"])
            groupId = "me.xiaoying"
            artifactId = "sqlfactory"
            version = project.version.toString()
        }
    }

    repositories {
        mavenLocal()
    }
}

// 根据不同的依赖分别打包
listOf("mysql", "sqlite", "postgresql").forEach {variable ->
    tasks.register<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>(variable) {
        group = "build"
        configurations = listOf(project.configurations[variable])
        archiveClassifier.set(variable)
        archiveFileName.set("sqlfactory-${variable}-${project.version}.jar")
        from(sourceSets.main.get().output)
    }
}

tasks.jar {
    archiveFileName.set("sqlfactory-${project.version}.jar")
}

tasks.shadowJar {
    archiveFileName.set("sqlfactory-all-${project.version}.jar")
}

tasks.build {
    dependsOn("sqlite", "mysql", "postgresql", tasks.jar, tasks.shadowJar)
}