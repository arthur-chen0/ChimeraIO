package com.jht.deployments

import com.jht.JHTConfiguration
import com.jht.JHTFlavor
import com.jht.JHTFlavors

/**
 * This is the deployment for T 600 XE running on Parker. For the most part just a re-branding over
 * Vision 600.
 */
class T600XEParker extends Vision600EParker {
    // Package to Manifest map.
    private static final JHTPackages[][] packagesInManifest = [
            [
                    JHTPackages.UPDATER, JHTPackages.CONSOLE_APPS, JHTPackages.ASSETS,
                    JHTPackages.CHIMERA_TPA, JHTPackages.PARKER_IO, JHTPackages.PARKER_OS
            ],
    ]

    /**
     * Default constructor.
     *
     * @param configuration The configuration object with links to folders so we can find the source
     * files.
     */
    T600XEParker(JHTConfiguration configuration) {
        super(JHTFlavor.flavors[JHTFlavors.T600XE_MATRIX_PARKER.ordinal()], configuration, [""], packagesInManifest)
    }

    /**
     * This allows deployments to add source files and folders to a package.
     *
     * @param pType The package.
     * @return The additional sources for the package.
     */
    @Override
    ArrayList<File> getSources(JHTPackages pType) {
        ArrayList<File> sources = new ArrayList<>()
        sources.add(new File(configuration.rootDir, "Deploy/" + pType.baseFolder()))

        switch (pType) {
        // Any package source overrides from vision?
            case JHTPackages.VISION_AD_BOARD:
                // T600xe doesn't appear to include this vision package, so block that just in case
                // Since we removed it from JHTPackages above, probably won't call this with that package anyway
                break
            case JHTPackages.ASSETS:   // T600 uses base chimera assets without any additions, but could change later?
                break
            default:
                return super.getSources(pType)
        }

        return sources
    }

    /**
     * Get a version for the given package. Deployments need to override this function.
     *
     * @param pType The package enum
     * @return The version. Can be NO_VERSION if the package is not in the deployment.
     */
    @Override
    String getVersion(JHTPackages p) {
        switch(p) {
            case JHTPackages.UPDATER:
                return T600_Parker_Updater
            case JHTPackages.CONSOLE_APPS:
                return T600_Parker
            case JHTPackages.ASSETS:
                // T600 uses base chimera assets, but could become different later?
                return T600_Assets
            case JHTPackages.CHIMERA_TPA:
                // just use vision TPA package
                return Vision_TPA
            case JHTPackages.PARKER_OS:
                return Parker_OS
            case JHTPackages.PARKER_IO:
                return Parker_IO
        //case JHTPackages.VISION_AD_BOARD:
        //return Vision600EAdBoard
        }
        return NO_VERSION
    }

    @Override
    boolean appMatchesUpdaterVersion() {
        return false
    }

    @Override
    boolean doesConsoleAppNeedReboot() {
        return false
    }

    /**
     * Get the current SDC for the deployment. This allows us to store versions in a common file on
     * the server in a way that SDC's will not mess each other up.
     *
     * @return The current SDC version
     */
    @Override
    String sdc() {
        // ALSO CHECK getSDC in MASTER.GRADLE
        return "1.0"
    }
}