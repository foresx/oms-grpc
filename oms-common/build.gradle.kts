plugins {
    id("com.commercehub.gradle.plugin.avro") version "0.16.0"
}

dependencies {
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.mapstruct:mapstruct-processor")

    compileOnly("org.projectlombok:lombok")
    implementation("org.mapstruct:mapstruct")

    runtimeOnly("org.flywaydb:flyway-core")
    runtimeOnly("org.postgresql:postgresql")
    compile("com.google.guava:guava")
    compile("org.zalando:problem")
    compile("com.google.protobuf:protobuf-java")
    compile("io.grpc:grpc-netty-shaded")  // shaded 可以和其他netty版本共存，避免冲突
    compile("io.grpc:grpc-stub")
    compile("io.grpc:grpc-protobuf")
    compile("net.devh:grpc-spring-boot-starter")
    compile("net.devh:grpc-spring-boot-starter")

    compile(project(":oms-db"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    //swagger
    compile("io.springfox:springfox-swagger2")
    compile("io.springfox:springfox-swagger-ui")

    testImplementation("com.castlery.commons:commons-test-support")
    testImplementation("com.github.javafaker:javafaker")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}