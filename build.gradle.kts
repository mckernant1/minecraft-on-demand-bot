import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application

    kotlin("jvm") version "1.6.20"

    id("com.github.johnrengelman.shadow") version "7.1.0"
}

group = "com.github.mckernant1.minecraft"
version = "0.0.1"

application {
    mainClass.set("com.github.mckernant1.minecraft.jocky.RunnerKt")
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://mvn.mckernant1.com/release")
    }
    maven {
        name = "m2-dv8tion"
        url = uri("https://m2.dv8tion.net/releases")
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")

    implementation("com.github.mckernant1:kotlin-utils:0.0.27")
    implementation("com.google.code.gson:gson:2.9.0")

    implementation("net.dv8tion:JDA:4.4.0_352")

    implementation("org.slf4j:slf4j-simple:1.7.36")


    implementation(platform("software.amazon.awssdk:bom:2.15.69"))
    implementation("software.amazon.awssdk:dynamodb-enhanced")
    implementation("software.amazon.awssdk:ecs")
    implementation("software.amazon.awssdk:ec2")
    implementation("software.amazon.awssdk:cloudformation")


}


tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "11"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "11"
    }
}

tasks.withType<ShadowJar>() {
    manifest {
        attributes["Main-Class"] = "RunnerKt"
    }
}
