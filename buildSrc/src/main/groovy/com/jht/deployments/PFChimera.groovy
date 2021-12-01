package com.jht.deployments

import com.jht.JHTConfiguration
import com.jht.JHTFlavor
import com.jht.JHTFlavors

/**
 * This is the deployment for Planet Fitness running on Chimera.
 */
class PFChimera extends ChimeraMainChimera {
    /**
     * Default constructor.
     * @param configuration The configuration object with links to folders so we can find the source
     * files.
     */
    PFChimera(JHTConfiguration configuration) {
        super(JHTFlavor.flavors[JHTFlavors.PF_MAIN_PF_CHIMERA.ordinal()], configuration)
    }

    /**
     * Used by customizations that run on the Chimera platform.
     *
     * @param flavor        The flavor for this deployment.
     * @param configuration The configuration object with links to folders so we can find the source
     * files.
     */
    @Override
    ArrayList<File> getSources(JHTPackages pType) {
        ArrayList<File> sources = new ArrayList<>()
        sources.add(new File(configuration.rootDir, "Deploy/${pType.baseFolder()}"))

        switch (pType) {
            case JHTPackages.CHIMERA_TPA:
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/Cards/5.11"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/ChineseTraditionalKeyboard/1.0"))
                //sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/Flipboard/4.2.26"))
                //sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/Hulu/3.65.0.308030"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/Instagram/119.0.0.33.147"))
                //sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/KindleLite/1.10.1"))
                //sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/Netflix/6.26.1.15.31696"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/PressReader/5.6.20.0303"))
                //sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/Spotify/8.5.14.460"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/TeamViewer/14.3.178"))
                sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/TeamViewerAddOn/14.v2"))
                //sources.add(new File(configuration.chimeraRootNetworkDir, "Apps/WeChat/7.0.12"))
                break
            default:
                return super.getSources(pType)
        }

        return sources
    }

    /**
     * This allows deployments to add source files and folders to a package.
     *
     * @param pType The package.
     * @return The additional sources for the package.
     */
    @Override
    String getVersion(JHTPackages p) {
        switch(p) {
            case JHTPackages.UPDATER:
            case JHTPackages.CONSOLE_APPS:
                return PF_Chimera
            case JHTPackages.ASSETS:
                return PF_Chimera_Assets
            case JHTPackages.CHIMERA_OS_K2_2:
                return OS_VERSION_K2_2
            case JHTPackages.CHIMERA_TPA:
                return PF_Chimera_TPA
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
            case JHTPackages.SALUTRON:
                return SALUTRON_VERSION
            case JHTPackages.CHIMERA_TOUCH_PANEL:
                return TOUCH_PANEL_VERSION
            case JHTPackages.WLT_BLUETOOTH:
                return BLUETOOTH_VERSION
            case JHTPackages.CHIMERA_U_BOOT:
                return U_BOOT_VERSION
            case JHTPackages.CHIMERA_LCBAthena:
                return LCBAthena
        }

        return NO_VERSION
    }

    /**
     * Get a version for the given package. Deployments need to override this function.
     *
     * @param pType The package enum
     * @return The version. Can be NO_VERSION if the package is not in the deployment.
     */
    @Override
    String sdc() {
        // ALSO CHECK getSDC in MASTER.GRADLE
        return "2.0"
    }
}