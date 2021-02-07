import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application

    kotlin("jvm") version "1.4.21"

    id("com.github.johnrengelman.shadow") version "5.1.0"
}

group = "com.github.mckernant1.minecraft"
version = "0.0.1"

application {
    mainClassName = "com.github.mckernant1.minecraft.jocky.RunnerKt"
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")


    implementation("net.dv8tion:JDA:4.2.0_204")


    implementation("org.slf4j:slf4j-simple:1.7.30")


    implementation(platform("software.amazon.awssdk:bom:2.15.69"))

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
