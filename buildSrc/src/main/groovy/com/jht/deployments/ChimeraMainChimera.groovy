package com.jht.deployments

import com.jht.JHTConfiguration
import com.jht.JHTFlavor
import com.jht.JHTFlavors

/**
 * This is the deployment for Chimera running on Chimera.
 */
class ChimeraMainChimera extends FlavoredDeployBase {
    // Package to Manifest map.
    private static final JHTPackages[][] packagesInManifest = [
            [
                    JHTPackages.UPDATER, JHTPackages.CONSOLE_APPS, JHTPackages.CHIMERA_OS_K2_2,
                    JHTPackages.ASSETS, JHTPackages.CHIMERA_TPA, JHTPackages.CHIMERA_IO2,
                    JHTPackages.SALUTRON, JHTPackages.WLT_BLUETOOTH, JHTPackages.CHIMERA_U_BOOT
            ],
            [
                    JHTPackages.UPDATER, JHTPackages.CONSOLE_APPS, JHTPackages.CHIMERA_OS_K2_2,
                    JHTPackages.ASSETS, JHTPackages.CHIMERA_TPA, JHTPackages.CHIMERA_IO2,
                    JHTPackages.SALUTRON, JHTPackages.WLT_BLUETOOTH, JHTPackages.CHIMERA_U_BOOT,
                    JHTPackages.CHIMERA_LCB1, JHTPackages.CHIMERA_LCB2,
                    JHTPackages.CHIMERA_LCB_Climbmill, JHTPackages.CHIMERA_LCBA,
                    JHTPackages.CHIMERA_LCB1xBike, JHTPackages.CHIMERA_LCBAthena,
                    JHTPackages.CHIMERA_TOUCH_PANEL, JHTPackages.CHIMERA_TOUCH_PANEL_ATHENA
            ],
            [JHTPackages.CHIMERA_TOUCH_PANEL],
            [JHTPackages.CHIMERA_TOUCH_PANEL_ATHENA]
    ]

    /**
     * Default constructor.
     *
     * @param configuration The configuration object with links to folders so we can find the source
     * files.
     */
    ChimeraMainChimera(JHTConfiguration configuration) {
        this(JHTFlavor.flavors[JHTFlavors.CHIMERA_MAIN_MATRIX_CHIMERA.ordinal()], configuration)
    }

    /**
     * Used by customizations that run on the Chimera platform.
     *
     * @param flavor        The flavor for this deployment.
     * @param configuration The configuration object with links to folders so we can find the source
     * files.
     */
    protected ChimeraMainChimera(JHTFlavor flavor, JHTConfiguration configuration) {
        super(flavor, configuration, ["", ".all", ".chimera_tp", ".athena_tp"], packagesInManifest)
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
                if(configuration.releaseDeploy) {
                    sources.add(new File(configuration.rootDir, "Chimera/build/outputs/apk/${flavor.flavorDir()}/release/Chimera-${flavor.flavorAPK()}-release.apk"))
                }
                else {
                    sources.add(new File(configuration.rootDir, "Chimera/build/outputs/apk/${flavor.flavorDir()}/debug/Chimera-${flavor.flavorAPK()}-debug.apk"))
                    sources.add(new File(configuration.rootDir, "Chimera/build/outputs/apk/androidTest/${flavor.flavorDir()}/debug/Chimera-${flavor.flavorAPK()}-debug-androidTest.apk"))
                }
                break
            case JHTPackages.CHIMERA_OS_K2_2:
                sources.add(new File(configuration.chimeraRootNetworkDir, "/OS/Nexcom/V${getVersion(JHTPackages.CHIMERA_OS_K2_2)}/ota_update"))
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
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/ifit_commercial/2.8.1.2864"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/MyWeather/0.3.10"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/MojiWeather/8.0506.02"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/Kindle/8.37.0.100"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/Interact/1.0"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/MyZone/0.5.0.0"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/BSJLiveTV/6.6"))
                break
            case JHTPackages.CHIMERA_IO:
                sources.add(new File(configuration.chimeraRootNetworkDir, "Firmware/IO/${getVersion(JHTPackages.CHIMERA_IO)}"))
                break
            case JHTPackages.CHIMERA_IO2:
                sources.add(new File(configuration.chimeraRootNetworkDir, "Firmware/IO/${getVersion(JHTPackages.CHIMERA_IO2)}"))
                break
            case JHTPackages.CHIMERA_LCB1:
                sources.add(new File(configuration.chimeraRootNetworkDir, "Firmware/LCB/LCB_1/${getVersion(JHTPackages.CHIMERA_LCB1)}"))
                break
            case JHTPackages.CHIMERA_LCB2:
                sources.add(new File(configuration.chimeraRootNetworkDir, "Firmware/LCB/LCB_2/${getVersion(JHTPackages.CHIMERA_LCB2)}"))
                break
            case JHTPackages.CHIMERA_LCB_Climbmill:
                sources.add(new File(configuration.chimeraRootNetworkDir, "Firmware/LCB/LCBClimbmill/${getVersion(JHTPackages.CHIMERA_LCB_Climbmill)}"))
                break
            case JHTPackages.CHIMERA_LCBA:
                sources.add(new File(configuration.chimeraRootNetworkDir, "Firmware/LCB/LCBA/${getVersion(JHTPackages.CHIMERA_LCBA)}"))
                break
            case JHTPackages.CHIMERA_LCB1xBike:
                sources.add(new File(configuration.chimeraRootNetworkDir, "Firmware/LCB/LCB_1xBike/${getVersion(JHTPackages.CHIMERA_LCB1xBike)}"))
                break
            case JHTPackages.CHIMERA_LCBAthena:
                sources.add(new File(configuration.chimeraRootNetworkDir, "Firmware/LCB/LCBAthena/${getVersion(JHTPackages.CHIMERA_LCBAthena)}"))
                break
            case JHTPackages.WLT_BLUETOOTH:
                sources.add(new File(configuration.rootDir, "Deploy/chimera_bt"))
                break
        }

        return sources
   }

    /**
     * Get a version for the given package. Deployments need to override this function.
     * @param pType The package enum
     * @return The version. Can be NO_VERSION if the package is not in the deployment.
     */
    @Override
    String getVersion(JHTPackages p) {
        switch(p) {
            case JHTPackages.UPDATER:
            case JHTPackages.CONSOLE_APPS:
                return Chimera_Main_Chimera
            case JHTPackages.ASSETS:
                return Chimera_Assets
            case JHTPackages.CHIMERA_OS_K2_2:
                return OS_VERSION_K2_2
            case JHTPackages.CHIMERA_TPA:
                return Chimera_TPA
            case JHTPackages.CHIMERA_IO:
                return IO_VERSION
            case JHTPackages.CHIMERA_IO2:
                return IO_2_VERSION
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
            case JHTPackages.CHIMERA_LCBAthena:
                return LCBAthena
            case JHTPackages.SALUTRON:
                return SALUTRON_VERSION
            case JHTPackages.CHIMERA_TOUCH_PANEL:
                return TOUCH_PANEL_VERSION
            case JHTPackages.CHIMERA_TOUCH_PANEL_ATHENA:
                return ATHENA_TOUCH_PANEL_VERSION
            case JHTPackages.WLT_BLUETOOTH:
                return BLUETOOTH_VERSION
            case JHTPackages.CHIMERA_U_BOOT:
                return U_BOOT_VERSION
        }

        return NO_VERSION
    }

    /**
     * Get the current SDC for the deployment. This allows us to store versions in a common file on
     * the server in a way that SDC's will not mess each other up.
     *
     * @return The current SDC version
     */
    @Override
    String sdc() {
        return "1.4"
    }
}
