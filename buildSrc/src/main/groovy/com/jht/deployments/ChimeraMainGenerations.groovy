package com.jht.deployments

import com.jht.JHTConfiguration
import com.jht.JHTFlavor
import com.jht.JHTFlavors

/**
 * This is the deployment for Chimera running on Generations.
 *
 * FIXME(ARR) - The paths on this are in need of updating.
 */
class ChimeraMainGenerations extends FlavoredDeployBase {
    // Package to Manifest map.
    private static final JHTPackages[][] packagesInManifest = [
            [
                    JHTPackages.UPDATER, JHTPackages.CONSOLE_APPS, JHTPackages.GENERATIONS_OS,
                    JHTPackages.GENERATIONS_OS_RECOVERY, JHTPackages.CHIMERA_TPA,
                    JHTPackages.ASSETS, JHTPackages.GENERATIONS_LCM, JHTPackages.GENERATIONS_BT,
                    JHTPackages.GENERATIONS_GYMKIT, JHTPackages.GENERATIONS_UBOOT,
                    JHTPackages.GENERATIONS_IO
            ],
    ]

    /**
     * Default constructor.
     *
     * @param configuration The configuration object with links to folders so we can find the source
     * files.
     */
    ChimeraMainGenerations(JHTConfiguration configuration) {
        // FIXME(ARR) - This is a horrible method of searching. This doesn't make any sense...
        this(JHTFlavor.flavors[JHTFlavors.CHIMERA_MAIN_MATRIX_GENERATIONS.ordinal()], configuration)
    }


    /**
     * Used by customizations that run on the Generations platform.
     *
     * @param flavor        The flavor for this deployment.
     * @param configuration The configuration object with links to folders so we can find the source
     * files.
     */
    protected ChimeraMainGenerations(JHTFlavor flavor, JHTConfiguration configuration) {
        super(flavor, configuration, [""], packagesInManifest)
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
        sources.add(new File(configuration.rootDir, "Deploy/${pType.baseFolder()}"))
        switch (pType) {
            case JHTPackages.UPDATER:
                sources.add(new File(configuration.rootDir, "UpdateService/build/outputs/apk/${flavor.flavorDir()}/release/UpdateService-${flavor.flavorAPK()}-release.apk"))
                break
            case JHTPackages.CONSOLE_APPS:
                sources.add(new File(configuration.rootDir, "Chimera/build/outputs/apk/${flavor.flavorDir()}/release/Chimera-${flavor.flavorAPK()}-release.apk"))
                break
            case JHTPackages.GENERATIONS_OS:
                sources.add(new File(configuration.generationsRootNetworkDir, "/OS_Updates/V${getVersion(JHTPackages.GENERATIONS_OS)}"))
                break
            case JHTPackages.GENERATIONS_OS_RECOVERY:
                sources.add(new File(configuration.generationsRootNetworkDir, "/OS_Updates/V${getVersion(JHTPackages.GENERATIONS_OS_RECOVERY)}"))
                break
            case JHTPackages.CHIMERA_TPA:
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/eGalaxSensorTester/0.32_0651_0694"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/PMCalib/2"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/ScreenTest/1"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/APKPure/2.0.4"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/Cards/5.5"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/TeamViewer/Generations"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/Stability/2.7"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/Flipboard/4.2.26"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/Instagram/119.0.0.33.147"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/Netflix/3.16.3"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/PressReader/5.3.17.1026"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/Spotify/8.4.22.857"))
                break
            case JHTPackages.GENERATIONS_IO:
                sources.add(new File(configuration.generationsRootNetworkDir, "IO_Update/${getVersion(JHTPackages.GENERATIONS_IO)}"))
                break
        //case JHTPackages.GENERATIONS_TOUCH_PANEL:
        //    ssources.add(new File(configuration.generationsRootNetworkDir, "TouchPanel/${getVersion(JHTPackages.GENERATIONS_TOUCH_PANEL)}"))
        //    break
            case JHTPackages.GENERATIONS_LCM:
                sources.add(new File(configuration.generationsRootNetworkDir, "LCM/${getVersion(JHTPackages.GENERATIONS_LCM)}"))
                break
            case JHTPackages.GENERATIONS_BT:
                sources.add(new File(configuration.maxHeadroomRootNetworkDir, "Bluetooth/ReleasedFirmware${getVersion(JHTPackages.GENERATIONS_BT)}"))
                break
            case JHTPackages.GENERATIONS_GYMKIT:
                sources.add(new File(configuration.generationsRootNetworkDir, "GymkitSoftware/GymkitUpdateD52App.apk"))
                sources.add(new File(configuration.generationsRootNetworkDir, "GymkitSoftware/gk_ublx_update.zip"))
                break
            case JHTPackages.ASSETS:
                sources.add(new File(configuration.generationsRootNetworkDir, "Assets"))
                break
            case JHTPackages.GENERATIONS_UBOOT:
                sources.add(new File(configuration.generationsRootNetworkDir, "u-boot"))
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
        switch(p) {
            case JHTPackages.UPDATER:
            case JHTPackages.CONSOLE_APPS:
                return Chimera_Main_Generations
            case JHTPackages.ASSETS:
                return Generations_Assets
            case JHTPackages.CHIMERA_TPA:
                return Generations_TPA
            case JHTPackages.GENERATIONS_OS:
                return GenerationsOS
            case JHTPackages.GENERATIONS_OS_RECOVERY:
                return GenerationsOSRecovery
            case JHTPackages.GENERATIONS_IO:
                return GenerationsIO
            case JHTPackages.GENERATIONS_LCM:
                return GenerationsLCM
            case JHTPackages.GENERATIONS_BT:
                return GenerationsBT
            case JHTPackages.GENERATIONS_GYMKIT:
                return GenerationsGymkit
            case JHTPackages.GENERATIONS_UBOOT:
                return GenerationsUBoot
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
        // ALSO CHECK getSDC in MASTER.GRADLE
        return "1.9"
    }
}