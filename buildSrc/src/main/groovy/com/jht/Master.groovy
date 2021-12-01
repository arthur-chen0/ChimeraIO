package com.jht

import org.gradle.api.Project
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

/**
 * Container for all custom functions from master.gradle. These have been moved here to make
 * maintenance and usage easier.
 */
class Master {
    private Master() {
        // prevent instantiation attempts because this is a utility class right now.
    }

    /**
     * FIXME(ARR) - This needs to be updated to include the Z Drive detection logic I use for my
     *  projects. This is assuming that you don't already have some environment variable you are
     *  storing this value in.
     *
     * @param project The project that this is being accessed from.
     * @return The File reference to the Z Drive, else null if not defined.
     */
    @Nullable
    static File getZDrive(@NotNull Project project) {
        Properties properties = new Properties()

        if (project.rootProject.file("local.properties").exists()) {
            project.rootProject.file("local.properties").withDataInputStream { properties.load(it) }
            String zDriveLocation = properties.getProperty("zDrive")

            if (zDriveLocation == null || zDriveLocation.isEmpty()) {
                return null
            }

            return new File(zDriveLocation)
        }

        return null
    }

    /**
     * FIXME(ARR) - Not sure how these are being used right now. This doesn't appear to be done
     *  correctly though in this implementation.
     *
     * @param buildType
     * @return
     */
    @Nullable
    static String getSDC(@NotNull String buildType) {
        if (buildType.startsWith("chimera_mainMatrixChimera")) return "1.4"
        if (buildType.startsWith("pf_mainPfChimera")) return "2.0"
        if (buildType.startsWith("chimera_mainMatrixGenerations")) return "1.9"
        if (buildType.startsWith("pf_mainPfGenerations")) return "2.0"
        if (buildType.startsWith("parker_mainMatrixParker")) return "1.2"
        if (buildType.startsWith("vision600eMatrixParker")) return "1.0"
        if (buildType.startsWith("t600xeMatrixParker")) return "1.0"
        if (buildType.startsWith("chimera_mainMatrixLcgui")) return "0.1"
        return null
    }

    /**
     * FIXME(ARR) - Correct this useless implementation.
     * @param product
     * @param ui
     * @param platform
     * @return
     */
    @Nullable
    static String getSDC(@NotNull String product, @NotNull String ui, @NotNull String platform) {
        return getSDC(product + ui.capitalize() + platform.capitalize())
    }

    @Nullable
    static String getReleaseDir(@NotNull String buildType) {
        if (buildType.startsWith("chimera_mainMatrixChimera")) return "Chimera"
        if (buildType.startsWith("pf_mainPfChimera")) return "Chimera"
        if (buildType.startsWith("chimera_mainMatrixGenerations")) return "Generations"
        if (buildType.startsWith("pf_mainPfGenerations")) return "Generations"
        if (buildType.startsWith("parker_mainMatrixParker")) return "Parker"
        if (buildType.startsWith("vision600eMatrixParker")) return "Vision600e"
        if (buildType.startsWith("t600xeMatrixParker")) return "T600xe"
         if (buildType.startsWith("chimera_mainMatrixLcgui")) return "LowCostGui"
        return null
    }

    static String getEquipment(String buildType) {
        // FIXME - This will be annoying in the null case.
        return getReleaseDir(buildType).toLowerCase()
    }

    @Nullable
    static String stripBuildType(@Nullable String buildType) {
        if (buildType != null) {
            return buildType.replaceAll("Debug", "").replaceAll("Release", "")
        }

        return null
    }

    static String getVersion(Project project, String buildType) {
        File zDrive = getZDrive(project)
        if (zDrive == null) return "0.0.0.0"

        File propertiesFile = new File(zDrive, "Software Releases/Console/${getReleaseDir(buildType)}/local.properties.versions")
        Properties properties = new Properties()
        try {
            propertiesFile.withDataInputStream { properties.load(it) }
        } catch (Exception ignore) {}

        String proj = project.name.startsWith("Update") ? "_updater" : "_apps"
        // some deployments just use updater version for apps, so if no version found, fallback to updater.
        String version = properties.get(stripBuildType(buildType).toLowerCase() + "_" + getSDC(buildType) + proj)
        if (version == null) {
            version = properties.get(stripBuildType(buildType).toLowerCase() + "_" + getSDC(buildType) + "_updater")
        }

        return version == null ? "" : version
    }

    static String getKeystoreDir(Project project) {
        String projectPath = project.projectDir.toString()
        File path = new File(projectPath)

        while (path.parent != null) {
            String keystorePath = path.toString() + System.properties["file.separator"] + "Keystore"

            if (new File(keystorePath).exists()) {
                return keystorePath + System.properties["file.separator"]
            }

            path = new File(path.parent)
        }

        return projectPath
    }
}
