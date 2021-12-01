package com.jht.deployments

import com.jht.JHTConfiguration
import com.jht.JHTFlavor
import com.jht.JHTFlavors

/**
 * This is the deployment for Vision 600 E running on Parker.
 */
class Vision600EParker extends ParkerMainParker {
    // Package to Manifest map.
    private static final JHTPackages[][] packagesInManifest = [
            [
                    JHTPackages.UPDATER, JHTPackages.CONSOLE_APPS, JHTPackages.ASSETS,
                    JHTPackages.CHIMERA_TPA, JHTPackages.PARKER_IO, JHTPackages.PARKER_OS
            ],
            [
                    JHTPackages.UPDATER, JHTPackages.CONSOLE_APPS, JHTPackages.ASSETS,
                    JHTPackages.CHIMERA_TPA, JHTPackages.PARKER_IO, JHTPackages.PARKER_OS,
                    JHTPackages.VISION_AD_BOARD
            ],
    ]

    /**
     * Default constructor.
     *
     * @param configuration The configuration object with links to folders so we can find the source
     * files.
     */
    Vision600EParker(JHTConfiguration configuration) {
        super(JHTFlavor.flavors[JHTFlavors.VISION600E_MATRIX_PARKER.ordinal()], configuration, ["", ".all"], packagesInManifest)
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
            case JHTPackages.VISION_AD_BOARD:
                sources.add(new File(configuration.parkerRootNetworkDir, "Firmware/ADBoard/${getVersion(JHTPackages.VISION_AD_BOARD)}"))
                break
            case JHTPackages.ASSETS:
                // vision overrides some of the base chimera assets
                sources.add(new File(configuration.parkerRootNetworkDir, "Software/assets_vision"))
                break
            case JHTPackages.CHIMERA_TPA:
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/Cards/5.11"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/ChineseTraditionalKeyboard/1.0"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/Flipboard/4.2.26"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/HangulKeyboard/1.0"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/Hulu/3.65.0.308030"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/Instagram/119.0.0.33.147"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/KindleLite/1.15"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/Netflix/6.26.1.15.31696"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/PressReader/5.6.20.0303"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/Snapchat/10.70.0.0"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/Spotify/8.5.14.460"))
                //sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/TeamViewer/14.3.178"))
                //sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/TeamViewerAddOn/14.v3"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/WeChat/7.0.12"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/Weibo/3.6.0"))
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
        switch (p) {
            case JHTPackages.UPDATER:
                return Vision_Parker_Updater
            case JHTPackages.CONSOLE_APPS:
                return Vision_Parker
            case JHTPackages.ASSETS:
                return Vision_Assets
            case JHTPackages.CHIMERA_TPA:
                return Vision_TPA
            case JHTPackages.PARKER_OS:
                return Parker_OS
            case JHTPackages.PARKER_IO:
                return Parker_IO
            case JHTPackages.VISION_AD_BOARD:
                return Vision600EAdBoard
            default:
                return NO_VERSION
        }
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