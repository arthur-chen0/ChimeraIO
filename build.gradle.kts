buildscript {
    repositories {
        mavenLocal()
        google()
        jcenter()
        maven(url = "https://maven.google.com")
    }

    dependencies {
        classpath("com.android.tools.build:gradle:4.1.3")
    }
}

apply<com.jht.DeployPlugin>()
apply(from = "device-control.gradle")
//apply(from = "tests.gradle")
//apply(from = "logParsers.gradle")

val VERSION_MANAGEMENT = com.jht.VersionManagement(project)

allprojects {
    val publishUser: String = VERSION_MANAGEMENT.user()
    val publishPass: String = VERSION_MANAGEMENT.password()

    repositories {
        mavenLocal()
        google()
        jcenter()
        maven(url = "https://gitlab.com/api/v4/projects/20204467/packages/maven")
        maven(url = "https://maven.google.com")
        maven(url = "https://download.01.org/crosswalk/releases/crosswalk/android/maven2")
        maven {
            setUrl("https://jht-artifacts.com:8081/artifactory/jht-dev-local")
            credentials {
                username = publishUser
                password = publishPass
            }
        }
        maven {
            setUrl("https://jht-artifacts.com:8081/artifactory/jht-release-local")
            credentials {
                username = publishUser
                password = publishPass
            }
        }
        flatDir {
            dirs("libs")
        }
    }

    gradle.projectsEvaluated {
        tasks.withType(JavaCompile::class.java) {
            options.compilerArgs.addAll(arrayOf("-Xlint:unchecked", "-Xlint:-deprecation"))
        }
    }
}

task("clean", Delete::class) {
    delete(rootProject.buildDir)
}
