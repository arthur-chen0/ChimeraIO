package com.jht

import org.gradle.api.Project

import java.text.SimpleDateFormat

/**
 * Manages the automated version number.
 */
class VersionManagement {
    private final Project project
    private String user, password, repo

    /**
     * Store the properties and cache the artifactory username, password, and repo.
     * @param project the current project.
     */
    VersionManagement(final Project project) {
        this.project = project
        user = getArtifactoryUser()
        password = getArtifactoryPassword()
        repo = getArtifactoryRepo()
    }

    String user() {
        return user
    }

    String password() {
        return password
    }

    String repo() {
        return repo
    }

    /**
     * @return the current version; compute if it doesn't exist.
     */
    String getVersion() {
        Properties properties = new Properties()
        File propertiesFile = new File(rootDir, "local.properties.2")

        if (propertiesFile.exists()) {
            properties.load(propertiesFile.newDataInputStream())

            if (properties.containsKey("artifact_version")) {
                if (isRelease()) {
                    return properties["artifact_version"]
                }

                return properties["artifact_version"] + "-" + System.properties.getProperty("user.name") + "-SNAPSHOT"
            }
        }

        return computeVersion()
    }

    /**
     * @return the new computed version based on the date and time.
     */
    String computeVersion() {
        Date date = new Date()
        String version = new SimpleDateFormat("yyyy.MM.dd.HHmmss").format(date)
        Properties properties = new Properties()
        properties.setProperty("artifact_version", version)

        File localProperties2 = new File(project.rootDir, "local.properties.2")
        properties.store(localProperties2.newOutputStream(), "")

        if (isRelease()) {
            return version
        }

        return "$version-${System.getProperties()["user.name"]}-SNAPSHOT"
    }

    /**
     * @return the artifactory username.
     */
    private String getArtifactoryUser() {
        if (project.properties.containsKey("jht_artifactory_user")) return project.properties["jht_artifactory_user"]
        if (System.getenv().containsKey("JHT_ARTIFACTORY_USER")) return System.getenv("JHT_ARTIFACTORY_USER")
        if (project.properties.containsKey("artifactory_user")) return project.properties["artifactory_user"]
        if (System.getenv().containsKey("ARTIFACTORY_USER")) return System.getenv("ARTIFACTORY_USER")
        return "No User"
    }

    /**
     * @return the artifactory password.
     */
    private String getArtifactoryPassword() {
        if (project.properties.containsKey("jht_artifactory_password")) return project.properties["jht_artifactory_password"]
        if (System.getenv().containsKey("JHT_ARTIFACTORY_PASSWORD")) return System.getenv("JHT_ARTIFACTORY_PASSWORD")
        if (project.properties.containsKey("artifactory_password")) return project.properties["artifactory_password"]
        if (System.getenv().containsKey("ARTIFACTORY_PASSWORD")) return System.getenv("ARTIFACTORY_PASSWORD")
        return "No Pass"
    }

    /**
     * @return the artifactory repository.
     */
    private String getArtifactoryRepo() {
        if (isRelease()) {
            return "jht-release-local"
        }

        return "jht-dev-local"
    }

    /**
     * @return true if this is being built on BitBucket or a build server.
     */
    private boolean isRelease() {
        return user == "builduser"
    }
}