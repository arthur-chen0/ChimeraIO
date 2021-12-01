package com.jht.deployments

import com.jht.JHTConfiguration
import com.jht.JHTFlavor
import com.jht.JHTFlavors
import com.jht.TpaList

/**
 * This is the deployment for Parker running on Parker.
 */
class ParkerMainParker extends FlavoredDeployBase {
    // Package to Manifest map.
    private static final JHTPackages[][] packagesInManifest = [
            [
                    JHTPackages.UPDATER, JHTPackages.CONSOLE_APPS, JHTPackages.ASSETS,
                    JHTPackages.CHIMERA_TPA, JHTPackages.PARKER_IO, JHTPackages.PARKER_OS
            ],
            [
                    JHTPackages.UPDATER, JHTPackages.CONSOLE_APPS, JHTPackages.ASSETS,
                    JHTPackages.CHIMERA_TPA, JHTPackages.PARKER_IO
            ],
            [JHTPackages.UPDATER, JHTPackages.CONSOLE_APPS, JHTPackages.ASSETS],
    ]

    /**
     * Default constructor.
     *
     * @param configuration The configuration object with links to folders so we can find the source
     * files.
     */
    ParkerMainParker(JHTConfiguration configuration) {
        this(JHTFlavor.flavors[JHTFlavors.PARKER_MAIN_MATRIX_PARKER.ordinal()], configuration, ["", ".no_os", ".emu"], packagesInManifest)
    }

    /**
     * Used by customizations that run on the Parker platform.
     *
     * @param flavor             The flavor for this deployment.
     * @param configuration      The configuration object with links to folders so we can find the
     * source files.
     * @param manifests          The manifest files for this deployment.
     * @param packagesInManifest Mapping of packages to the manifest file.
     */
    protected ParkerMainParker(JHTFlavor flavor, JHTConfiguration configuration, String[] manifests, JHTPackages[][] packagesInManifests) {
        super(flavor, configuration, manifests, packagesInManifests)
    }

    @Override
    protected boolean needsDowngrade(JHTPackages pack) {
        return (pack == JHTPackages.PARKER_OS) // we are downgrading from 1.0.3.2 to 1.0.2.7
    }

    @Override
    protected void createStrictTpaList() {
        strictTpas = new TpaList(configuration.chimeraRootNetworkDir)
        // MD5 hashes are an optional extra check, good if xapk is unzipped and you want to makes sure nothing happened to its files
        strictTpas.add("Apps/Cards/5.11", "com.rikkigames.solsuite", "5.11", "92", "e5d4e6a1a4ae63a99ef8172461d925ce")
        strictTpas.add("Apps/ChineseTraditionalKeyboard/1.0", "com.googlecode.tcime.unofficial", "1.0", "1", "1bec14d89c2509521e325bc725ac615a")
        strictTpas.add("Apps/Flipboard/4.2.53", "flipboard.app", "4.2.53", "4956", "c83038affb794464dbb98f9f664dd0be")
        strictTpas.add("Apps/HangulKeyboard/1.0", "org.kandroid.app.hangulkeyboard", "1.0", "1", "ca3cf94348c211e28104100846a041e3")
        strictTpas.add("Apps/Hulu/4.12.0.409250", "com.hulu.plus", "4.12.0.409250", "409250", "2c41c843bf7ea6a91a1520f4413325ce")
        strictTpas.add("Apps/Instagram/164.0.0.46.123", "com.instagram.android", "164.0.0.46.123", "252055948", "ce022b21c7b2365a3d5fb9959a27b0fd")
        strictTpas.add("Apps/Kindle/8.37.0.100", "com.amazon.kindle", "8.37.0.100(1.3.234695.0)", "1217658981", "9a21871979c889ee0c07597fcc352d43")
        strictTpas.add("Apps/Netflix/7.86.1", "com.netflix.mediaclient", "7.86.1 build 16 35285", "35285", "c12dca0170fced31bb46c01eb4b8b5b7")
        strictTpas.add("Apps/PressReader/6.1.201015", "com.newspaperdirect.pressreader.android", "6.1.201015", "437", "110611375a2941a0d2f4b44f9406e09c")
        strictTpas.add("Apps/Snapchat/10.89.0.66", "com.snapchat.android", "10.89.0.66", "2078", "d41009f195783c3f9ae3bfb198606f15")
        strictTpas.add("Apps/Spotify/8.5.78.909", "com.spotify.music", "8.5.78.909", "64755932", "3330f17143107a7658217ec50f47b1e5")
        strictTpas.add("Apps/TeamViewer/15.9.128", "com.teamviewer.quicksupport.market", "15.9.128", "159128", "77d707c76039c258d0e4b994366fa926")
        strictTpas.add("Apps/TeamViewerAddOn/14.v3", "com.teamviewer.quicksupport.addon.aosp14", "11.0.5583", "5583", "165210b53e5e4a92cf50152efc87a3f6")
        strictTpas.add("Apps/cloudmusic/7.2.10.1594782837", "com.netease.cloudmusic", "7.2.10", "7002010", "7ee05c2234b79c4bb7ad4ecb039a5801")
        strictTpas.add("Apps/ifit/2.8.0.2583", "com.ifit.standalone", "2.8.0", "2583", "6d86bd280af5c03bc7ea4baa1bd8ad90")
        strictTpas.add("Apps/WeChat/7.0.17", "com.tencent.mm", "7.0.17", "1701", "235cf89c12e3bb642e56b60ee0f7d1e9")
        strictTpas.add("Apps/Weibo/3.6.2", "com.weico.international", "3.6.2", "3620", "5157add58ca211d3455125d0b9a57995")
        strictTpas.add("Apps/Webview/90.0.4430.66", "com.google.android.webview", "90.0.4430.66", "443006603", "67470ba5a3525c89b843cf713b590ae9")
//        TODO - FOR IFIT DEV, DELETE LATER
//        strictTpas.add("Apps/MatrixCommTestkApk/10.0.5.1", "com.jht.matrixcomm.testapp", "1.0", "1", "f82752314ffafa69a8fcec167dc8b4a1")
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
                strictTpas.populateSources(sources)
                break
            case JHTPackages.PARKER_IO:
                sources.add(new File(configuration.parkerRootNetworkDir, "Firmware/IO/${getVersion(JHTPackages.PARKER_IO)}"))
                break
            case JHTPackages.ASSETS:
                sources.add(new File(configuration.parkerRootNetworkDir, "Software/assets"))
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
                return Parker_Main_Parker_Updater
            case JHTPackages.CONSOLE_APPS:
                return Parker_Main_Parker
            case JHTPackages.ASSETS:
                return Parker_Assets
            case JHTPackages.CHIMERA_TPA:
                return Parker_TPA
            case JHTPackages.PARKER_OS:
                return Parker_OS
            case JHTPackages.PARKER_IO:
                return Parker_IO
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
        return "1.2"
    }
}