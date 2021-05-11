plugins {
    kotlin("jvm")
    application
}

group = "copatych"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(project(":EngineeringCalculatorLib"))
}

val fatJar = task<Jar>("fatJar") {
    group = "application"
    manifest {
        attributes["Implementation-Title"] = project.name
        attributes["Implementation-Version"] = project.version
        attributes["Main-Class"] = "MainKt"
    }
//    archiveBaseName.set("${project.name}-fat")
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get() as CopySpec)
}



tasks {
    "build" {
        dependsOn(fatJar)
    }
}

