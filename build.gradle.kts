plugins {
    id("java")
}

group = "me.xiaoying.sqlfactory"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    // 从本地仓库中寻找依赖文件
    // 其实就是从中央仓库或别的仓库中下载以来后会存储到本地
    mavenLocal()
    // 从 maven 中央仓库中寻找依赖文件
    mavenCentral()

    // 事实上 maven 允许搭建个人仓库，通常的格式是 maven("仓库地址")
    // 不过在 SqlFactory 的开发过程中理论上用不上这个功能
}

// 依赖文件
dependencies {
    // lombok
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")

    // Sqlite
    implementation("org.xerial:sqlite-jdbc:3.49.1.0")
    // Mysql
    implementation("com.mysql:mysql-connector-j:9.2.0")
}