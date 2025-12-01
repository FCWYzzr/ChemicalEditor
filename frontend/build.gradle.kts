import org.gradle.api.tasks.Exec

plugins {
    id("base")
}

val npm = "D:\\Kits\\Node\\22.18\\npm.cmd"

tasks {
    register<Exec>("install") {
        description = "Install npm dependencies"
        commandLine(npm, "install")
    }

    register<Exec>("dev") {
        description = "preview with hot reload"
        commandLine(npm, "run", "dev")
    }

    register<Exec>("dist") {
        description = "build dist"
        commandLine(npm, "run", "build")
        copy {
            from("dist")
            into("../backend/src/main/resources")
        }
    }
}
