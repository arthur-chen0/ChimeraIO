package com.jht

/**
 * Helper class with links to a bunch of folders that contain source files for the deployment
 * packages. Also it contains the properties of the deployment.
 */
class JHTConfiguration {
    private static boolean getBooleanProperty(Properties properties, String key, boolean defaultValue) {
        String value = properties.getProperty(key)

        if (value != null && !value.isEmpty()) {
            return value.equalsIgnoreCase("true")
        }

        return defaultValue
    }

    private static final String chimeraLocationOnZDrive = "Active Projects/DPD/Projects/Chimera"
    private static final String parkerLocationOnZDrive = "Active Projects/DPD/Projects/Project Parker"
    private static final String generationsLocationOnZDrive = "Active Projects/KTKC/KC (EE)/KC - Console/Projects/4 - Complete/2015.05 - Matrix Generations/"
    private static final String maxHeadroomLocationOnZDrive = "Active Projects/KTKC/KC (EE)/KC - Console/Projects/4 - Complete/2014.11 - Max Headroom/"
    private static final String propertiesFileName = "local.properties.versions"

    final File chimeraRootNetworkDir
    final File parkerRootNetworkDir
    final File generationsRootNetworkDir
    final File maxHeadroomRootNetworkDir

    final File propertiesFile
    final File releaseDir
    final File rootDir
    final File zDrive
    final JHTFlavor flavor

    final Properties deploymentVersions = new Properties()
    final Properties localProperties = new Properties()
    boolean disableAWS = false
    boolean deleteStagingDir = true

    final boolean releaseDeploy

    JHTConfiguration(JHTFlavor flavor, File rootDir, boolean releaseDeploy) {
        this.flavor = flavor
        this.rootDir = rootDir
        this.releaseDeploy = releaseDeploy

        try {
            new File(rootDir, "local.properties").withDataInputStream {stream -> localProperties.load(stream) }
        } catch (Exception ex) {
            ex.printStackTrace()
        }

        zDrive = new File(localProperties.getProperty("zDrive"))
        String releasePath = localProperties.getProperty("releasePath")

        if (releasePath != null && !releasePath.isEmpty()) {
            this.releaseDir = new File(new File(releasePath), flavor.releaseDir())
        } else {
            this.releaseDir = new File(zDrive, flavor.releaseDir())
        }

        if (!releaseDir.exists()) {
            releaseDir.mkdirs()
        }

        disableAWS = getBooleanProperty(localProperties, "disableAWS", disableAWS)
        deleteStagingDir = getBooleanProperty(localProperties, "deleteStagingDir", deleteStagingDir)

        chimeraRootNetworkDir = new File(zDrive, chimeraLocationOnZDrive)
        parkerRootNetworkDir = new File(zDrive, parkerLocationOnZDrive)
        generationsRootNetworkDir = new File(zDrive, generationsLocationOnZDrive)
        maxHeadroomRootNetworkDir = new File(zDrive, maxHeadroomLocationOnZDrive)
        propertiesFile = new File(releaseDir, propertiesFileName)

        if (propertiesFile.exists()) {
            try {
                propertiesFile.withDataInputStream {stream -> deploymentVersions.load(stream)}
            } catch (Exception ignored) {}
        } else {
            try {
                propertiesFile.createNewFile()
            } catch (Exception ignored) {}
        }
    }
}
