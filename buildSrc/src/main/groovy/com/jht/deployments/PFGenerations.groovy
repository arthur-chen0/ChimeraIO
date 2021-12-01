package com.jht.deployments

import com.jht.JHTConfiguration
import com.jht.JHTFlavor
import com.jht.JHTFlavors

/**
 * This is the deployment for Planet Fitness running on Generations.
 *
 * FIXME(ARR) - Correct the paths.
 */
class PFGenerations extends ChimeraMainGenerations {
    /**
     * Default constructor.
     *
     * @param configuration The configuration object with links to folders so we can find the source files.
     */
    PFGenerations(JHTConfiguration configuration) {
        super(JHTFlavor.flavors[JHTFlavors.PF_MAIN_PF_GENERATIONS.ordinal()], configuration)
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
            case JHTPackages.CHIMERA_TPA:
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/eGalaxSensorTester/0.32_0651_0694"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/PMCalib/2"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/ScreenTest/1"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/APKPure/2.0.4"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/Cards/5.5"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/TeamViewer/Generations"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/Stability/2.7"))
                // sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/Flipboard/4.2.26"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/Instagram/119.0.0.33.147"))
                // sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/Netflix/3.16.3"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/PressReader/5.3.17.1026"))
                // sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/Spotify/8.4.22.857"))
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
            case JHTPackages.CONSOLE_APPS:
                return PF_Generations
            case JHTPackages.ASSETS:
                return PF_Generations_Assets
            case JHTPackages.CHIMERA_TPA:
                return PF_Generations_TPA
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
        return "2.0"
    }
}