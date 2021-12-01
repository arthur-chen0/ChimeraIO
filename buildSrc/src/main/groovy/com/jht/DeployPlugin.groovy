package com.jht

import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.GradleBuild
import org.gradle.api.tasks.TaskExecutionException

class DeployPlugin implements Plugin<Project> {
    private static final String GROUP_BUILD = "deploy - build"
    private static final String GROUP_PUBLISH = "deploy - publish"
    private static final String GROUP_VERSION = "deploy - version"
    private static final String GROUP_TPA_MANIFEST = "deploy - tpa manifest"
    private static final String GROUP_CLEAN_APP = "deploy - clean app"
    private static final String GROUP_BUILD_APP = "deploy - build app"
    private static final String GROUP_CLEAN_UPDATER = "deploy - clean updater"
    private static final String GROUP_BUILD_UPDATER = "deploy - build updater"
    private static final String GROUP_THIRD_PARTY_APPS = "deploy - third party apps"
    private static final String GROUP_ASSETS = "deploy - assets"
    private static final String GROUP_INSTALL = "deploy - install"
    private static final String GROUP_FORCE_INSTALL = "deploy - forceInstall"

    private List<String> getCleanCmd(String projectName) {
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            return ["cmd", "/c", "gradlew.bat", "${projectName}:clean"]
        }
        return ["./gradlew", "${projectName}:clean"]
    }

    private List<String> getAssembleCmd(String projectName, JHTFlavor flavor) {
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            return ["cmd", "/c", "gradlew.bat", "${projectName}:assemble${flavor.flavorCapitalized()}Release"]
        }
        return ["./gradlew", "${projectName}:assemble${flavor.flavorCapitalized()}Release"]
    }

    private List<String> getAssembleCmd2(String projectName, JHTFlavor flavor) {
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            return ["cmd", "/c", "gradlew.bat", "${projectName}:assemble${flavor.flavorCapitalized()}Debug"]
        }
        return ["./gradlew", "${projectName}:assemble${flavor.flavorCapitalized()}Debug"]
    }

    private List<String> getAssembleCmd3(String projectName, JHTFlavor flavor) {
        if (Os.isFamily(Os.FAMILY_WINDOWS)) {
            return ["cmd", "/c", "gradlew.bat", "${projectName}:assemble${flavor.flavorCapitalized()}DebugAndroidTest"]
        }
        return ["./gradlew", "${projectName}:assemble${flavor.flavorCapitalized()}DebugAndroidTest"]
    }

    private static final List<String> TASKS_BUILD_APP = new ArrayList<String>() {{
        add("Chimera:clean")
        add("Chimera:assembleRelease")
    }}
    private static final List<String> TASKS_BUILD_UPDATER = new ArrayList<String>() {{
        add("UpdateService:clean")
        add("UpdateService:assembleRelease")
    }}
    private static final List<String> TASKS_BUILD_ALL = new ArrayList<String>() {{
        addAll(TASKS_BUILD_APP)
        addAll(TASKS_BUILD_UPDATER)
    }}

    @Override
    void apply(Project project) {
        project.tasks.create("buildApp", GradleBuild.class) {
            description = "Builds all release flavors of the Chimera app."
            group = GROUP_BUILD
            tasks = TASKS_BUILD_APP
        }

        project.tasks.create("buildUpdater", GradleBuild.class) {
            description = "Builds all release flavors of the updater app."
            group = GROUP_BUILD
            tasks = TASKS_BUILD_UPDATER
        }

        project.tasks.create("buildAll", GradleBuild.class) {
            description = "Builds all release flavors of the Chimera and Updater App."
            group = GROUP_BUILD
            tasks = TASKS_BUILD_ALL
        }

        project.tasks.create("buildTests", GradleBuild.class) {
            group = GROUP_BUILD
            description = "Builds tests for the Chimera App."
            tasks = ["Chimera:assembleAndroidTest"]

            doLast {
                String apkTestLocation = "${project.projectDir}\\out_tests"
                project.delete(apkTestLocation)

                println("COPY Test APKS")
                FileTree testApkTree = project.fileTree(".").include("**/*androidTest*.apk")
                testApkTree.each { File file ->
                    if (file.size() > 20000) {
                        println(file)
                        project.copy {
                            from file
                            into apkTestLocation
                        }
                    }
                }
            }
        }

        Task publishRelease = project.tasks.create("publishRelease") {
            description = "Publishes all flavors"
            group = GROUP_PUBLISH
        }

        Task computeVersion = project.tasks.create("computeVersion") {
            description = "Computes the versions for all flavors."
            group = GROUP_VERSION
        }

        Task getVersion = project.tasks.create("getVersion") {
            description = "Computes the versions for all flavors."
            group = GROUP_VERSION
        }


        project.tasks.create("convertSTBSettings") {
            group = "utilities"
            doLast {
                try {
                    STBConverter.run(
                            new File("Chimera/src/main/assets/settings/tv_settings/set_top_boxes/input"),
                            new File("Chimera/src/main/assets/settings/tv_settings/set_top_boxes/output")
                    )
                } catch(Exception ex) {
                    ex.printStackTrace()
                }
            }
        }

        project.tasks.create("convertSTBSettings2") {
            group = "utilities"
            doLast {
                try {
                    STBConverter.updateJson(
                            new File("Chimera/src/main/assets/settings/tv_settings/set_top_boxes/input")
                    )
                } catch(Exception ex) {
                    ex.printStackTrace()
                }
            }
        }

        /*
         * Create tasks for each flavor to compute the version and publish the release. This will
         * add the tasks to the overall publishRelease and computeVersion tasks above.
         *
         * FIXME(ARR) - Remove this once I finally finish cleaning up this project.
         */
        JHTFlavor.flavors.each { JHTFlavor flavor ->
            project.tasks.create("printTpaManifest-${flavor.flavorCapitalized()}") {
                description = "Print the flavored TPA Manifest task in the console."
                group = GROUP_TPA_MANIFEST
                doLast {
                    try {
                        TpaList tpas = new ReleaseSoftware(flavor, project.projectDir).deployment().getTpaList()
                        println("=== printTpaManifest-${flavor.flavorCapitalized()} ===")
                        println()

                        if (tpas == null) {
                            println("!! ${flavor.flavorCapitalized()} does NOT support TPA manifests !!")
                        } else {
                            println(tpas.toJson())
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace()
                        throw new TaskExecutionException(this, ex)
                    }
                }
            }

            project.tasks.create("verifyTpaManifest-${flavor.flavorCapitalized()}") {
                description = "Verify current TPAs against the flavored TPA manifest task."
                group = GROUP_TPA_MANIFEST
                doLast {
                    try {
                        TpaList tpas = new ReleaseSoftware(flavor, project.projectDir).deployment().getTpaList()
                        if (tpas == null) {
                            println("!! ${flavor.flavorCapitalized()} does NOT support TPA manifests !!")
                        } else {
                            tpas.validateTpaSources()
                            println("<< TPA manifest verification complete >>")
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace()
                        throw new TaskExecutionException(this, ex)
                    }
                }
            }

            Task getVersionTask = project.tasks.create("getVersion${flavor.flavorCapitalized()}") {
                description = "Create the flavored compute version task."
                group = GROUP_VERSION
                doLast {
                    try {
                        new ReleaseSoftware(flavor, project.projectDir).deployment().printVersions()
                    } catch (Exception ex) {
                        ex.printStackTrace()
                    }
                }
            }

            // Add the compute version task to the overall computeVersion task.
            getVersion.dependsOn(getVersionTask)

            Task computeTask = project.tasks.create("computeVersion${flavor.flavorCapitalized()}") {
                description = "Create the flavored compute version task."
                group = GROUP_VERSION
                doLast {
                    try {
                        new ReleaseSoftware(flavor, project.projectDir).deployment().computePackageVersions()
                    } catch (Exception ex) {
                        ex.printStackTrace()
                    }
                }
            }

            // Add the compute version task to the overall computeVersion task.
            computeVersion.dependsOn(computeTask)

            Exec cleanAppTask = project.tasks.create("cleanApp${flavor.flavorCapitalized()}", Exec.class) {
                group = GROUP_CLEAN_APP
                commandLine = getCleanCmd("Chimera")
                mustRunAfter(computeTask)
            }

            Exec buildAppTask = project.tasks.create("buildApp${flavor.flavorCapitalized()}", Exec.class) {
                group = GROUP_BUILD_APP
                commandLine = getAssembleCmd("Chimera", flavor)
                mustRunAfter(cleanAppTask)
            }

            Exec buildDebugAppTask = project.tasks.create("buildDebugApp${flavor.flavorCapitalized()}", Exec.class) {
                group = GROUP_BUILD_APP
                commandLine = getAssembleCmd2("Chimera", flavor)
                mustRunAfter(buildAppTask)
            }

            Exec buildDebugAppAndroidTestTask = project.tasks.create("builDebugdAppAndroidTestApp${flavor.flavorCapitalized()}", Exec.class) {
                group = GROUP_BUILD_APP
                commandLine = getAssembleCmd3("Chimera", flavor)
                mustRunAfter(buildDebugAppTask)
            }

            Exec cleanUpdaterTask = project.tasks.create("cleanUpdater${flavor.flavorCapitalized()}", Exec.class) {
                group = GROUP_CLEAN_UPDATER
                commandLine = getCleanCmd("UpdateService")
                mustRunAfter(computeTask)
            }

            Exec buildUpdaterTask = project.tasks.create("buildUpdater${flavor.flavorCapitalized()}", Exec.class) {
                group = GROUP_BUILD_UPDATER
                commandLine = getAssembleCmd("UpdateService", flavor)
                mustRunAfter(cleanUpdaterTask)
            }

            project.tasks.create("build${flavor.flavorCapitalized()}") {
                group = GROUP_BUILD
                dependsOn([cleanAppTask, buildAppTask, buildDebugAppTask, buildDebugAppAndroidTestTask, cleanUpdaterTask, buildUpdaterTask])
            }

            Task publishTask = project.tasks.create("publishRelease${flavor.flavorCapitalized()}") {
                description = "Create the flavored publish task."
                group = GROUP_PUBLISH
                mustRunAfter(buildAppTask, buildUpdaterTask)
                doLast {
                    try {
                        new ReleaseSoftware(flavor, project.projectDir).deployment().releaseSoftware()
                    } catch (Exception ex) {
                        ex.printStackTrace()
                    }
                }
            }

            // Add the flavored publish task to the master publish task.
            publishRelease.dependsOn(publishTask)

            project.tasks.create("installThirdPartyApps${flavor.flavorCapitalized()}") {
                group = GROUP_THIRD_PARTY_APPS
                doLast {
                    try {
                        new ReleaseSoftware(flavor, project.projectDir).deployment().installThirdPartyApps()
                    } catch (Exception ex) {
                        ex.printStackTrace()
                    }
                }
            }

            project.tasks.create("installAssets${flavor.flavorCapitalized()}") {
                group = GROUP_ASSETS
                doLast {
                    try {
                        new ReleaseSoftware(flavor, project.projectDir).deployment().installAssets()
                    } catch (Exception ex) {
                        ex.printStackTrace()
                    }
                }
            }

            project.tasks.create("install${flavor.flavorCapitalized()}") {
                group = GROUP_INSTALL
                mustRunAfter(publishTask)
                doLast {
                    try {
                        new ReleaseSoftware(flavor, project.projectDir).deployment().adbInstall(false)
                    } catch (Exception ex) {
                        ex.printStackTrace()
                    }
                }
            }

            project.tasks.create("forceInstall${flavor.flavorCapitalized()}") {
                group = GROUP_FORCE_INSTALL
                mustRunAfter(publishTask)
                doLast {
                    try {
                        new ReleaseSoftware(flavor, project.projectDir).deployment().adbInstall(true)
                    } catch (Exception e) {
                        e.printStackTrace()
                    }
                }
            }

            project.tasks.create("createCIPackages${flavor.flavorCapitalized()}") {
                group = GROUP_INSTALL
                mustRunAfter(publishTask)
                doLast {
                    try {
                        new ReleaseSoftware(flavor, project.projectDir, false).deployment().generateCIPackages()
                        new ReleaseSoftware(flavor, project.projectDir, true).deployment().generateCIPackages()
                    } catch (Exception e) {
                        e.printStackTrace()
                    }
                }
            }

            project.tasks.create("installLastRelease${flavor.flavorCapitalized()}") {
                group = GROUP_INSTALL
                mustRunAfter(publishTask)
                doLast {
                    try {
                        new ReleaseSoftware(flavor, project.projectDir).deployment().installLastRelease(false)
                    } catch (Exception e) {
                        e.printStackTrace()
                    }
                }
            }

            project.tasks.create("forceInstallLastRelease${flavor.flavorCapitalized()}") {
                group = GROUP_FORCE_INSTALL
                mustRunAfter(publishTask)
                doLast {
                    try {
                        new ReleaseSoftware(flavor, project.projectDir).deployment().installLastRelease(true)
                    } catch (Exception e) {
                        e.printStackTrace()
                    }
                }
            }

            project.tasks.create("flavorInfo${flavor.flavorCapitalized()}") {
                group = "documentation"
                doLast {
                    try {
                        new ReleaseSoftware(flavor, project.projectDir).deployment().printFlavorDebugInfo()
                    } catch (Exception e) {
                        e.printStackTrace()
                    }
                }
            }

        }
    }
}