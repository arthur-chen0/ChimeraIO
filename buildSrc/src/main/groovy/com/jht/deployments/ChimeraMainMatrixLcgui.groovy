package com.jht.deployments

import com.jht.JHTConfiguration
import com.jht.JHTFlavor
import com.jht.JHTFlavors

/**
 * This is the deployment for Chimera running on Chimera.
 */
class ChimeraMainMatrixLcgui extends FlavoredDeployBase {
    // Package to Manifest map.
    private static final JHTPackages[][] packagesInManifest = [
            [
                    JHTPackages.UPDATER, JHTPackages.CONSOLE_APPS, JHTPackages.ASSETS,
                    JHTPackages.CHIMERA_TPA, JHTPackages.SALUTRON, JHTPackages.WLT_BLUETOOTH,
                    JHTPackages.PARKER_IO, JHTPackages.PARKER_OS
            ],
            [
                    JHTPackages.UPDATER, JHTPackages.CONSOLE_APPS, JHTPackages.ASSETS,
                    JHTPackages.CHIMERA_TPA, JHTPackages.SALUTRON, JHTPackages.WLT_BLUETOOTH,
                    JHTPackages.PARKER_IO
            ],
            [JHTPackages.UPDATER, JHTPackages.CONSOLE_APPS, JHTPackages.ASSETS],
            [
                    JHTPackages.UPDATER, JHTPackages.CONSOLE_APPS, JHTPackages.ASSETS,
                    JHTPackages.CHIMERA_TPA, JHTPackages.SALUTRON, JHTPackages.WLT_BLUETOOTH,
                    JHTPackages.PARKER_IO, JHTPackages.PARKER_OS,
                    JHTPackages.CHIMERA_LCB1, JHTPackages.CHIMERA_LCB2,
                    JHTPackages.CHIMERA_LCB_Climbmill, JHTPackages.CHIMERA_LCBA,
                    JHTPackages.CHIMERA_LCB1xBike
            ]
    ]

    /**
     * Default constructor.
     *
     * @param configuration The configuration object with links to folders so we can find the source
     * files.
     */
    ChimeraMainMatrixLcgui(JHTConfiguration configuration) {
        this(JHTFlavor.flavors[JHTFlavors.CHIMERA_MAIN_MATRIX_LCGUI.ordinal()], configuration)
    }

    /**
     * Used by customizations that run on the Chimera platform.
     *
     * @param flavor        The flavor for this deployment.
     * @param configuration The configuration object with links to folders so we can find the source
     * files.
     */
    protected ChimeraMainMatrixLcgui(JHTFlavor flavor, JHTConfiguration configuration) {
        super(flavor, configuration, ["", ".no_os", ".emu", ".all"], packagesInManifest)
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
            case JHTPackages.UPDATER:
                sources.add(new File(configuration.rootDir, "UpdateService/build/outputs/apk/${flavor.flavorDir()}/release/UpdateService-${flavor.flavorAPK()}-release.apk"))
                break
            case JHTPackages.CONSOLE_APPS:
                sources.add(new File(configuration.rootDir, "Chimera/build/outputs/apk/${flavor.flavorDir()}/release/Chimera-${flavor.flavorAPK()}-release.apk"))
                break
            case JHTPackages.PARKER_OS:
                sources.add(new File(configuration.parkerRootNetworkDir, "/Software/os/ota/${getVersion(JHTPackages.PARKER_OS)}"))
                break
            case JHTPackages.CHIMERA_TPA:
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/Cards/5.15"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/ChineseTraditionalKeyboard/1.0"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/Flipboard/4.2.53"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/Hulu/4.26.0.5673"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/Instagram/164.0.0.46.123"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/Netflix/7.106.0"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/PressReader/6.1.201015"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/Spotify/8.5.78.909"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/TeamViewer/15.9.128"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/TeamViewerAddOn/14.v2"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/WeChat/7.0.17"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/Kindle/8.37.0.100"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/ifit_commercial/2.8.1.2864"))
                break
            case JHTPackages.PARKER_IO:
                sources.add(new File(configuration.parkerRootNetworkDir, "Firmware/IO/${getVersion(JHTPackages.PARKER_IO)}"))
                break
            case JHTPackages.ASSETS:
                sources.add(new File(configuration.parkerRootNetworkDir, "Software/assets"))
                break
            case PACKAGES.CHIMERA_LCB1:
                sources.add(new File(configuration.chimeraRootNetworkDir, "Firmware/LCB/LCB_1/${getVersion(PACKAGES.CHIMERA_LCB1)}"))
                break
            case PACKAGES.CHIMERA_LCB2:
                sources.add(new File(configuration.chimeraRootNetworkDir, "Firmware/LCB/LCB_2/${getVersion(PACKAGES.CHIMERA_LCB2)}"))
                break
            case PACKAGES.CHIMERA_LCB_Climbmill:
                sources.add(new File(configuration.chimeraRootNetworkDir, "Firmware/LCB/LCBClimbmill/${getVersion(PACKAGES.CHIMERA_LCB_Climbmill)}"))
                break
            case PACKAGES.CHIMERA_LCBA:
                sources.add(new File(configuration.chimeraRootNetworkDir, "Firmware/LCB/LCBA/${getVersion(PACKAGES.CHIMERA_LCBA)}"))
                break
            case PACKAGES.CHIMERA_LCB1xBike:
                sources.add(new File(configuration.chimeraRootNetworkDir, "Firmware/LCB/LCB_1xBike/${getVersion(PACKAGES.CHIMERA_LCB1xBike)}"))
                break
            case PACKAGES.WLT_BLUETOOTH:
                sources.add(new File(configuration.rootDir, "Deploy/chimera_bt"))
                break
            default:
                break
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
            case JHTPackages.CONSOLE_APPS:
                return Chimera_Main_LCGUI
            case JHTPackages.ASSETS:
                return LCGUI_Assets
            case JHTPackages.CHIMERA_TPA:
                return LCGUI_TPA
            case JHTPackages.PARKER_OS:
                return Lcgui_OS
            case JHTPackages.PARKER_IO:
                return Lcgui_IO
            case JHTPackages.CHIMERA_LCB1:
                return LCB1
            case JHTPackages.CHIMERA_LCB2:
                return LCB2
            case JHTPackages.CHIMERA_LCB_Climbmill:
                return LCBClimbmill
            case JHTPackages.CHIMERA_LCBA:
                return LCBA
            case JHTPackages.CHIMERA_LCB1xBike:
                return LCB1xBike
            case JHTPackages.SALUTRON:
                return SALUTRON_VERSION
            case JHTPackages.WLT_BLUETOOTH:
                return BLUETOOTH_VERSION
            default:
                return NO_VERSION
        }
    }

    /**
     * Get the current SDC for the deployment. This allows us to store versions in a common file on
     * the server in a way that SDC's will not mess each other up.
     *
     * @return The current SDC version
     */
    @Override
    String sdc() {
        return "0.1"
    }
}