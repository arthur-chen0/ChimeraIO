package com.jht.deployments

import com.jht.*
import com.jht.filemanagement.AWSHelper
import com.jht.filemanagement.FileManagement
import com.jht.rscu.RSCU
import com.jht.rscu.RSCUAssociation
import org.apache.tools.ant.taskdefs.condition.Os

/**
 * This contains the bulk of the code for managing a deployment. Typically you should only need to
 * modify the versions at the top or the packages. Most likely you will only need to touch the
 * package enum if you need to add a package. The package properties are overridden in the various
 * deployment classes. That is where most of the changes will be if you need to modify a package for
 * a deployment. The deployments should map to build flavors.
 *
 * FIXME(ARR) - All of the paths in this directory are going to be wrong soon.
 */
abstract class FlavoredDeployBase {
    // Package base versions -----------------------------------------------------------------
    protected static final String Chimera_Main_Chimera = "1.4.4"
    protected static final String PF_Chimera = "2.0.0"
    protected static final String Chimera_Main_Generations = "1.9.0"
    protected static final String PF_Generations = "2.0.0"
    protected static final String Parker_Main_Parker = "1.2.2"
    protected static final String Vision_Parker = "1.0.2"
    protected static final String T600_Parker = "1.0.2"
    protected static final String Chimera_Main_LCGUI = "0.1.0"

    // If app version does not match updater version, be sure to override appMatchesUpdaterVersion()
    protected static final String Parker_Main_Parker_Updater = "1.2.2" // we had glitch versioning this as 1.2.1.X, bumped this to skip over the incorrect versions
    protected static final String Vision_Parker_Updater = "1.0.2"
    protected static final String T600_Parker_Updater = "1.0.2"

    protected static final String Chimera_Assets = "1.3.1"
    protected static final String PF_Chimera_Assets = "2.0.0"
    protected static final String Generations_Assets = "1.9.0"
    protected static final String PF_Generations_Assets = "2.0.0"
    protected static final String Parker_Assets = "1.2.0"
    protected static final String Vision_Assets = "1.0.2"
    protected static final String T600_Assets = "1.0.2"
    protected static final String LCGUI_Assets = "0.1.0"

    protected static final String Chimera_TPA = "1.1.3"
    protected static final String PF_Chimera_TPA = "2.0.0"
    protected static final String Generations_TPA = "1.9.0"
    protected static final String PF_Generations_TPA = "2.0.0"
    protected static final String Parker_TPA = "1.2.0"
    protected static final String Vision_TPA = "1.0.2"
    protected static final String LCGUI_TPA = "0.1.0"

    // Below are fixed version numbers that are not computed, but used as-is

    protected static final String OS_VERSION_K2_2 = "1.1.7.3"
    protected static final String Parker_OS = "1.0.2.7" // force downgrading from 1.0.3.2, TODO - when new version, remove the needsDowngrade from parker deploy
    protected static final String Parker_IO = "4.9.0.0"
	protected static final String Lcgui_OS = "1.0.3.1"
	protected static final String Lcgui_IO = "4.6.0.0"
    protected static final String IO_VERSION = "45.0.0.0"
    protected static final String IO_2_VERSION = "73.3.0.0"
    protected static final String LCB1 = "10.0.0.0"
    protected static final String LCB2 = "1.6.0.0"
    protected static final String LCBClimbmill = "1.7.0.9"
    protected static final String LCBA = "8.0.0.0"
    protected static final String LCB1xBike = "4.0.0.5"
    protected static final String LCBAthena = "1.17.0.0"
    protected static final String SALUTRON_VERSION = "4538.0.0.0"
    protected static final String TOUCH_PANEL_VERSION = "0.6.0.0"
    protected static final String ATHENA_TOUCH_PANEL_VERSION = "0.6.0.0"
    protected static final String BLUETOOTH_VERSION = "10.5.20.0"
    protected static final String U_BOOT_VERSION = "0.7.0.0"

    protected static final String GenerationsOS = "2.0.1.4"
    protected static final String GenerationsOSRecovery = "1.0.5.35"
    protected static final String GenerationsIO = "44.0.0.0"
    protected static final String GenerationsLCM = "12.0.0.0"
    protected static final String GenerationsBT = "4.96.0.0"
    protected static final String GenerationsGymkit = "1.0.17.0"
    protected static final String GenerationsUBoot = "2.1.0.0"

    protected static final String Vision600EAdBoard = "5.0.0.0"

    protected static final String NO_VERSION = "N/A"
    // Package base versions -----------------------------------------------------------------

    /**
     * Helper function to execute a command and print the output.
     *
     * @param cmd The command to run.
     */
    private static void execute(String cmd) {
        println(cmd)
        println(cmd.execute().text)
    }


    /**
     * Helper function to execute a command and print the output.
     *
     * @param cmd The command to run.
     */
    private static String execute(List<String> cmd) {
        println(cmd)
        StringBuffer errorText = new StringBuffer()
        Process p = cmd.execute()
        p.consumeProcessErrorStream(errorText)
        String output = p.text
        println(output)
        println(errorText.toString())
        return output
    }

    /**
     * Helper function to execute a command and print the output.
     *
     * @param cmd The command to run.
     */
    private static void execute(String cmd, File workingDir) {
        println(cmd)
        StringBuffer errorText = new StringBuffer()
        Process p = cmd.execute(new String[0], workingDir)
        p.consumeProcessErrorStream(errorText)
        println(p.text)
        println(errorText.toString())
    }

    /**
     * Helper function to execute a command and print the output.
     *
     * @param cmd The command to run.
     */
    private static void execute(List<String> cmd, File workingDir) {
        println(cmd)
        StringBuffer errorText = new StringBuffer()
        Process p = cmd.execute(new String[0], workingDir)
        p.consumeProcessErrorStream(errorText)
        println(p.text)
        println(errorText.toString())
    }

    private static String extractAttribute(String[] attributes, String attributeName) {
        for (String attribute : attributes) {
            String[] attributeParts = attribute.split("=")

            if (attributeParts != null && attributeParts.length >= 2) {
                if (attributeParts[0] == attributeName) {
                    return attributeParts[1].substring(1, attributeParts[1].length() - 1)
                }
            }
        }

        return null
    }

    /**
     * List of all the packages in this deployment.
     */
    protected HashMap<JHTPackages, JHTPackage> packages = new HashMap<>()

    /**
     * Current flavor.
     */
    protected JHTFlavor flavor

    /**
     * Contains a lot of configuration so we can find everything that goes into the packages.
     */
    protected JHTConfiguration configuration

    /**
     * This is where we place a release on the Z drive.
     */
    protected File stagingDir

    /**
     * List of manifest files for this package.  We may need to create several for each deployment.
     */
    protected List<JHTManifest> manifests = new ArrayList<>()

    /**
     * Flavors can override and create manager for TPAs that will validate APK's against the
     * expected name and version
     */
    protected TpaList strictTpas

    /**
     * Default constructor.
     *
     * @param flavor             The flavor for the deployment so we can find the correct source
     * files.
     * @param configuration      The configuration object with links to folders so we can find the
     * source files.
     * @param manifests          The manifest files for this deployment.
     * @param packagesInManifest Mapping of packages to the manifest file.
     */
    FlavoredDeployBase(JHTFlavor flavor, JHTConfiguration configuration, String[] manifests, JHTPackages[][] packagesInManifest) {
        // Store the deployment properties.
        this.configuration = configuration
        this.flavor = flavor

        // if flavor has implemented one, set it up once configuration is set
        createStrictTpaList()

        boolean appMatchUpdater = appMatchesUpdaterVersion()
        println("appMatchesUpdaterVersion=${appMatchUpdater}")

        // Create the packages.
        for (JHTPackages p : JHTPackages.values()) {
            String version = getVersion(p)
            if (version != NO_VERSION) {
                JHTPackage thePackage = new JHTPackage(p.ordinal(), p.packageName(), p.id(), p.reboot(), p.manualReboot(), getSources(p), p.updateType(), configuration)
                if (p.codeFolder() != null) {
                    thePackage.setCodeFolder(new File(configuration.rootDir, p.codeFolder()))
                }

                if (needsDowngrade(p)) {
                    thePackage.setForceDowngrade(true)
                }

                if (version.split("\\.").length == 4) {
                    thePackage.setVersion(version)
                } else {
                    PackageVersionHelper helper = new PackageVersionHelper(flavor, sdc(), thePackage, configuration.deploymentVersions)
                    thePackage.setVersion(helper.version())
                }

                packages.put(p, thePackage)
            }
        }

        // Update the console apps version.  This shares a version with the updater.
        JHTPackage consoleApp = packages.get(JHTPackages.CONSOLE_APPS)
        if (appMatchUpdater) {
            consoleApp.setVersion(packages.get(JHTPackages.UPDATER).getVersion())
        }

        // Some flavors need console app reboot and others do not
        consoleApp.setRequiresReboot(doesConsoleAppNeedReboot())

        // Create the manifests and link the packages.
        for (String manifest : manifests) {
            this.manifests.add(new JHTManifest(manifest))
        }

        for (int i = 0; i < packagesInManifest.length; i++) {
            for (int j = 0; j < packagesInManifest[i].length; j++) {
                this.manifests.get(i).addPackage(packages.get(packagesInManifest[i][j]))
            }
        }
    }

    /**
     * Loop through the packages and compute the version for each if necessary. Here is where we
     * look at packages that are auto versioned where this script manages the build number and
     * determine if we need to bump it up.
     */
    void computePackageVersions() {
        boolean appMatchUpdater = appMatchesUpdaterVersion()

        // TODO - not sure best place to do this? also should TPAs be downloaded only once to temp
        //  local for all this hashing/zipping/etc?
        if (strictTpas != null) {
            strictTpas.validateTpaSources()
        }

        if (appMatchUpdater) {
            for (Map.Entry<JHTPackages, JHTPackage> packageEntry : packages.entrySet()) {
                JHTPackage p = packageEntry.value
                if (p.type() != JHTPackages.CONSOLE_APPS.ordinal()) {
                    computeVersion(p, p.type() != JHTPackages.UPDATER.ordinal())
                }
            }

            packages.get(JHTPackages.CONSOLE_APPS).setVersion(packages.get(JHTPackages.UPDATER).getVersion())
        } else {
            for (Map.Entry<JHTPackages, JHTPackage> packageEntry : packages.entrySet()) {
                JHTPackage p = packageEntry.value
                // I think we always rev up console app since that is used as our release version?
                computeVersion(p, p.type() != JHTPackages.CONSOLE_APPS.ordinal())
            }
        }

        configuration.propertiesFile.withDataOutputStream { stream -> configuration.deploymentVersions.store(stream, "") }
    }

    /**
     * Update the package version if we are managing the build number.
     *
     * @param p      The package to check.
     * @param useMd5 If this is true then we update the build number if the prefix changes or if we
     * detect the source files have changed via MD5 checks.
     */
    protected void computeVersion(JHTPackage p, boolean useMd5) {
        String version = getVersion(JHTPackages.values()[p.type()])

        // Make sure we manage the version otherwise do nothing
        if (version.split("\\.").length != 3) {
            p.version = version
            configuration.deploymentVersions.setProperty("${flavor.flavor()}_${sdc()}_${p.getShortName()}", version)

            // The version is N/A or is fully set in code.
            return
        }

        // Compute the next version number.
        PackageVersionHelper versionHelper = new PackageVersionHelper(flavor, sdc(), p, configuration.deploymentVersions)
        if (!versionHelper.build().isEmpty() && version == versionHelper.prefix()) {
            // Prefix is the same. Check MD5 if necessary.
            if (useMd5) {
                String currentMd5 = p.computeSrcMD5()
                println("md5 ${p.getShortName()} ${currentMd5} ${versionHelper.md5()}")

                // Nothing to do. There have been no changes.
                if (currentMd5 == versionHelper.md5()) {
                    return
                }

                versionHelper.md5(currentMd5)
            }

            // Compute the next build number.
            if (versionHelper.build() != null && !versionHelper.build().isEmpty()) {
                try {
                    int buildNumber = Integer.parseInt(versionHelper.build())
                    buildNumber++
                    versionHelper.build("" + buildNumber)
                } catch (NumberFormatException ignored) {
                    versionHelper.build("0")
                }
            }
        } else {
            // Prefix has changed.  Start back at 0.
            versionHelper.prefix(version)
            versionHelper.build("0")
        }

        // Save the newly computed version.
        versionHelper.save(configuration.deploymentVersions)
        p.version = versionHelper.version()
    }

    /**
     * Print all versions. This is useful for debugging.
     */
    void printVersions() {
        for (Map.Entry<JHTPackages, JHTPackage> packageEntry : packages.entrySet()) {
            println("package ${packageEntry.value.shortName} ${packageEntry.value.version}")
        }
    }

    /**
     * @return The version for this deployment. That equals the version for the updater and console
     * apps.
     */
    protected String getVersion() {
        return packages.get(JHTPackages.CONSOLE_APPS).version
    }


    /**
     * Some platforms do not need to reboot for console app update
     *
     * @return true if console app needs reboot, false otherwise.
     */
    protected boolean doesConsoleAppNeedReboot() {
        return true
    }

    /**
     * Default is opt-out.
     */
    protected void createStrictTpaList() {
        strictTpas = null
    }

    /**
     * @return TpaList if using one, otherwise null
     */
    TpaList getTpaList() {
        return strictTpas
    }

    /**
     * @return true if the given package needs a forced downgrade
     */
    protected boolean needsDowngrade(JHTPackages pack) {
        return false
    }

    /**
     * This creates all of the packages and uploads them to the Z drive and AWS.
     */
    protected void createPackages() {
        for (Map.Entry<JHTPackages, JHTPackage> packageEntry : packages.entrySet()) {
            packageEntry.value.createPackage(flavor.awsSubFolder)
        }
    }

    void printFlavorDebugInfo() {
        // We will add to this as needed.  Useful for testing.
        StringBuilder info = new StringBuilder()
        info.append("Flavor ").append(flavor.flavor()).append("\n")
        info.append("\tDefault Clubs:")
        for(int club : flavor.defaultRSCUClub()) {
            info.append(" ").append(club)
        }
        info.append("\n")
        println(info.toString())
    }

    /**
     * Create all the packages locally. This is used for CI automated tests.
     */
    void generateCIPackages() {
        if(configuration.releaseDeploy) {
            stagingDir = new File(configuration.rootDir, "ciPackages/release")
        }
        else {
            stagingDir = new File(configuration.rootDir, "ciPackages/debug")
        }
        stagingDir.deleteDir()
        stagingDir.mkdirs()
        configuration.disableAWS = false
        configuration.deleteStagingDir = true
        String version = "0." + sdc() + ".0"
        if(configuration.releaseDeploy) {
            version = "0." + sdc() + ".1"
        }

        // Copy over helper files. (Basically Windows zip and wget)
        FileManagement.copy(new File(configuration.rootDir, "Deploy/deploy_apps"), stagingDir)

        // For Windows, to help find apps.
        FileManagement.binPath = "${stagingDir.absolutePath}\\"

        // Create the package.
        packages.each { packageEntry ->
            computeVersion(packageEntry.getValue(), true)
            if(packageEntry.getValue().shortName == "apps" || !packageEntry.getValue().getPackageLocation().exists()) {
                packageEntry.getValue().setVersion(version)
                packageEntry.getValue().setForce(true)
                packageEntry.getValue().setForceRecreate(true)
                packageEntry.getValue().setReleaseFolder(stagingDir)
                packageEntry.getValue().createPackage(flavor.platfrom())
            }

        }

        // Create the manifest files.
        manifests.each { manifest ->
            String otaFileName = createManifest(manifest, true)
            String localFileName = "update" + manifest.suffix() + ".xml"
            String s3Path = "${flavor.getAwsSubFolder()}/manifest/${version}/${localFileName}"
            String s3Location = "https://s3-us-west-2.amazonaws.com/jhtsoftware/" + s3Path
            AWSHelper.putObject("jhtsoftware", s3Path, new File(stagingDir, otaFileName), "", configuration.disableAWS)
            FileManagement.createScriptFile(new File(stagingDir, "rscu_version.txt"), version)
            FileManagement.createScriptFile(new File(stagingDir, localFileName + ".rscu_location.txt"), s3Location)
            FileManagement.createScriptFile(new File(stagingDir, localFileName + ".rscu_md5.txt"), FileManagement.getMD5(new File(stagingDir, otaFileName)))
        }

        // Copy over the update service APK to get all updates needed to install the package.

        if(configuration.releaseDeploy) {
            FileManagement.copy(new File(configuration.rootDir, "UpdateService/build/outputs/apk/${flavor.flavorDir()}/release/UpdateService-${flavor.flavorAPK()}-release.apk"), new File(stagingDir, "UpdateService-${flavor.flavorAPK()}-release.apk"))
        }
        else {
            FileManagement.copy(new File(configuration.rootDir, "UpdateService/build/outputs/apk/${flavor.flavorDir()}/debug/UpdateService-${flavor.flavorAPK()}-debug.apk"), new File(stagingDir, "UpdateService-${flavor.flavorAPK()}-debug.apk"))
        }
        // Remove all packages created as they should be on AWS.
        List<File> files = stagingDir.listFiles(new FilenameFilter() {
            @Override
            boolean accept(File dir, String name) {
                return name.endsWith("zip")
            }
        })
        for(File f : files) {
            f.delete()
        }
    }

    /**
     * Create all the packages, deploy them to the Z drive and AWS and create the version scripts.
     */
    void releaseSoftware() {
        String version = getVersion()
        File releaseDir = new File(configuration.releaseDir, version)

        // Make sure we haven't pushed the version. We do not want to overwrite a previous release.
        if (releaseDir.exists()) {
            println("!! Version has already been published, throwing !!")
            throw Exception("Version has already been published")
        }

        // Staging area for the scripts.
        stagingDir = new File(releaseDir, "/updateFilesStaging")
        stagingDir.mkdirs()

        // Copy over helper files. (Basically Windows zip and wget)
        FileManagement.copy(new File(configuration.rootDir, "Deploy/deploy_apps"), stagingDir)
        // for windows, to help to find apps
        FileManagement.binPath = stagingDir.absolutePath + "\\"

        // Create the package.
        createPackages()

        // Create the manifest files.
        manifests.each { manifest ->
            String localFileName = createManifest(manifest, false)
            String otaFileName = createManifest(manifest, true)
            String s3Path = "${flavor.getAwsSubFolder()}/manifest/${version}/${localFileName}"
            createJSON(manifest, "https://s3-us-west-2.amazonaws.com/jhtsoftware/${s3Path}", "")
            createBrandSiteFile(manifest)

            AWSHelper.putObjectIfNotExists("jhtsoftware", s3Path, new File(stagingDir, otaFileName), "", configuration.disableAWS)

            String apiKey = System.getenv("RSCU_API_KEY")
            if (apiKey != null && !apiKey.isEmpty() && !configuration.disableAWS && localFileName == "update.xml" && flavor.defaultRSCUClub() != -1) {
                // This is the base file.  Associate it.  Other flavors may need to think about the club.
                List<RSCUAssociation> associations = new ArrayList<>()
                for(int club : flavor.defaultRSCUClub()) {
                    associations.add(new RSCUAssociation(club, true, true, true))
                }
                RSCU.associate(flavor.softwareType(), flavor.softwareConfigurationClass(), version, "https://s3-us-west-2.amazonaws.com/jhtsoftware/" + s3Path, FileManagement.getMD5(new File(stagingDir, otaFileName)), associations)
            }

        }

        // Create the download scripts.
        StringBuilder downloadShell = new StringBuilder()
        StringBuilder downloadBatch = new StringBuilder()

        packages.each { packageEntry ->
            JHTPackage p = packageEntry.getValue()
            downloadBatch.append(p.getDownloadBatchScript(flavor.getAwsSubFolder()))
            downloadShell.append(p.getDownloadShellScript(flavor.getAwsSubFolder()))
        }

        FileManagement.createScriptFile(new File(stagingDir, "downloadUSBUpdate.bat"), downloadBatch.toString())
        FileManagement.createScriptFile(new File(stagingDir, "downloadUSBUpdate.sh"), downloadShell.toString())

        if (strictTpas != null) {
            FileManagement.createScriptFile(new File(stagingDir, "tpa_manifest.json"), strictTpas.toJson())
        }

        // Create the ADB scripts.
        createRecovery()

        // Copy over helper files.
        FileManagement.copy(new File(configuration.rootDir, "Deploy/deploy_apps"), stagingDir)
        FileManagement.copy(new File(configuration.rootDir, "Chimera/build/outputs/mapping/${flavor.flavorDir()}Release/mapping.txt"), new File(stagingDir, "chimera_mapping.txt"))
        FileManagement.copy(new File(configuration.rootDir, "UpdateService/build/outputs/mapping/${flavor.flavorDir()}Release/mapping.txt"), new File(stagingDir, "update_service_mapping.txt"))


        // Zip up the files for ease of transfer
        FileManagement.zipFiles(stagingDir, "update_files.zip", true)
        File updateFiles = new File(releaseDir, "update_files.zip")
        FileManagement.move(new File(stagingDir, "update_files.zip"), updateFiles)
        AWSHelper.putObjectIfNotExists("jhtsoftware", flavor.platfrom() + "/manifest/${version}/update_files.zip", updateFiles, "", configuration.disableAWS)
    }



    /**
     * Create the scripts to run an ADB update.

    protected void createADBScripts(boolean ota) {
        // Copy over the update manager apk.  We need to install that and then can use it to update
        // everything else.
        FileManagement.copy(
                new File(configuration.rootDir, "UpdateService/build/outputs/apk/${flavor.flavorDir()}/release/UpdateService-${flavor.flavorAPK()}-release.apk"),
                new File(stagingDir, "UpdateService-${flavor.flavorAPK()}-release.apk")
        )

        StringBuilder adbScript = new StringBuilder()
        adbScript.append("sh consoleReady.sh \$1\n")
        adbScript.append("adb install -r -d UpdateService-${flavor.flavorAPK()}-release.apk\n")
        adbScript.append("adb reboot &\n")
        adbScript.append("sleep 5\n")
        adbScript.append("sh consoleReady.sh \$1\n")
        adbScript.append("adb shell mkdir -p /sdcard/update/automated\n")
        adbScript.append("adb shell rm -rf /sdcard/update/automated/*\n")

        if(!ota) {
            packages.each { packageEntry ->
                JHTPackage p = packageEntry.getValue()
                if (new File(stagingDir, p.shortName).exists()) {
                    adbScript.append("adb push ${p.getShortName()}.zip /sdcard/update/automated/${p.getShortName()}.zip\n")
                }
            }
            adbScript.append("adb push \$2 /sdcard/update/automated/update.xml\n")
        }
        adbScript.append("sleep 2\n")


        StringBuilder adbBatch = new StringBuilder()

        adbBatch.append("adb install -r -d UpdateService-${flavor.flavorAPK()}-release.apk\r\n")
        adbBatch.append("adb reboot\r\n")
        adbBatch.append("timeout /t 60\r\n")
        adbBatch.append("adb shell mkdir -p /sdcard/update/automated\r\n")
        adbBatch.append("adb shell rm -rf /sdcard/update/automated/*\r\n")

        if(!ota) {
            packages.each { packageEntry ->
                JHTPackage p = packageEntry.getValue()
                adbBatch.append("adb push ${p.getShortName()}.zip /sdcard/update/automated/${p.getShortName()}.zip\r\n")
            }
            adbBatch.append("adb push update.xml /sdcard/update/automated\r\n")
            adbBatch.append("timeout /t 2\r\n")
        }

        if(ota) {
            String rscuVersion = "0." + sdc() + ".0"
            String rscuLocation = "https://s3-us-west-2.amazonaws.com/jhtsoftware/${flavor.getAwsSubFolder()}/manifest/${version}/${localFileName}"
            AWSHelper.putObject("jhtsoftware", s3Path, new File(stagingDir, otaFileName), configuration.disableAWS)
            updateCommand = "adb shell am broadcast -a com.jht.automatedupdate --es manifest /sdcard/update/automated/update.xml --es rscu_version $rscuVersion --es rscuLocation.\n"
            forceUpdateCommand = "adb shell am broadcast -a com.jht.automatedupdate --ez force_update true --es manifest /sdcard/update/automated/update.xml\n"

        }
        else {
            updateCommand = "adb shell am broadcast -a com.jht.automatedupdate --es manifest /sdcard/update/automated/update.xml\n"
            forceUpdateCommand = "adb shell am broadcast -a com.jht.automatedupdate --ez force_update true --es manifest /sdcard/update/automated/update.xml\n"
        }
        FileManagement.createScriptFile(new File(stagingDir, "adbUpdate.sh"), adbScript.toString() + updateCommand)
        FileManagement.createScriptFile(new File(stagingDir, "adbUpdate.bat"), adbBatch.toString() + updateCommand.replace("\n", "\r\n"))
        FileManagement.createScriptFile(new File(stagingDir, "adbForceUpdate.sh"), adbScript.toString() + forceUpdateCommand)
        FileManagement.createScriptFile(new File(stagingDir, "adbForceUpdate.bat"), adbBatch.toString() + forceUpdateCommand.replace("\n", "\r\n"))

    }
*/
    private getUpdateCommand(boolean ota) {

    }

    /**
     * Create the recovery scripts.
     */
    protected void createRecovery() {
        FileManagement.createScriptFile(
                new File(stagingDir, "createRecovery.sh"),
                "mkdir recovery\n" +
                        "cd recovery\n" +
                        "cp ../update.xml update.xml\n" +
                        "cp ../recovery.sh recovery.sh\n" +
                        "sh ../downloadUSBUpdate.sh\n")
        FileManagement.createScriptFile(
                new File(stagingDir, "createRecovery.bat"),
                "mkdir recovery\r\n" +
                        "cd recovery\r\n" +
                        "copy ..\\update.xml update.xml\r\n" +
                        "copy ..\\recovery.sh recovery.sh\r\n" +
                        "..\\downloadUSBUpdate.bat\r\n")
        StringBuilder recoveryScript = new StringBuilder()
        recoveryScript.append("mkdir -p /sdcard/update/automated\n")
        recoveryScript.append("rm -rf /sdcard/update/automated/*\n")
        recoveryScript.append("cp . /sdcard/update/automated\n")
        recoveryScript.append("am broadcast -a com.jht.automatedupdate --es manifest /sdcard/update/automated/update.xml\n")
        FileManagement.createScriptFile(new File(stagingDir, "recovery.sh"), recoveryScript.toString())
    }

    /**
     * Create a manifest file.
     *
     * @param manifest The manifest we are creating.
     * @param ota      True if we should add ota links.
     */
    protected String createManifest(JHTManifest manifest, boolean ota) {
        String[] versionNumbers = getVersion().tokenize(".")
        StringBuilder builder = new StringBuilder()
        builder.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n")
        builder.append("<updates equipment=\"${flavor.equipment()}\" major=\"${versionNumbers[0]}\" minor=\"${versionNumbers[1]}\" patch=\"${versionNumbers[2]}\" build=\"${versionNumbers[3]}\" requires_reboot=\"true\">\n")
        //builder.append("<!-- Use force=\"true\" to force a package install -->\n")

        int order = 1
        manifest.packages().each { p ->
            p.order(order++)
            builder.append(p.getManifestXML(flavor.getAwsSubFolder(), ota))
            builder.append("\n")
        }
        builder.append("</updates>")
        String fileName = ota ? "update" + manifest.suffix() + "_ota.xml" : "update" + manifest.suffix() + ".xml"
        FileManagement.createScriptFile(new File(stagingDir, fileName), builder.toString())
        return fileName
    }

    protected void createJSON(JHTManifest manifest, String manifestLocation, String releaseNotes) {
        StringBuilder stringBuilder = new StringBuilder()
        String fileNameXml = "update" + manifest.suffix() + "_ota.xml"
        String fileNameJson = "update" + manifest.suffix() + "_ota.json"
        File manifestFile = new File(stagingDir, fileNameXml)
        String md5 = FileManagement.getMD5(manifestFile)

        stringBuilder.append("{\n")
        stringBuilder.append("\t\"updates\": [ {\n")
        stringBuilder.append("\t\t\"version\": \"${this.getVersion()}\",\n")
        stringBuilder.append("\t\t\"manifestLocation\": \"${manifestLocation}\",\n")
        stringBuilder.append("\t\t\"manifestMD5\": \"${md5}\",\n")
        stringBuilder.append("\t\t\"releaseNotes\": \"${releaseNotes}\",\n")
        stringBuilder.append("\t\t\"packages\": [\n")
        boolean first = true
        for (JHTPackage p : manifest.packages()) {
            if (first) {
                stringBuilder.append("\n").append(p.getJSON())
            } else {
                stringBuilder.append(",\n").append(p.getJSON())
            }

            first = false
        }
        stringBuilder.append("\n\t\t]\n")
        stringBuilder.append("\t}]\n}")
        FileManagement.createScriptFile(new File(stagingDir, fileNameJson), stringBuilder.toString())
    }

    void createBrandSiteFile(JHTManifest manifest) {

        String brandSiteData = "{\"consoleType\":\"${flavor.equipment()}\",";
        brandSiteData += "\"version\":\"${this.getVersion()}\",\"files\":["
        boolean comma = false;
        manifest.packages().each() { p ->
            if(comma) {
                brandSiteData += ","
            }
            comma = true;
            brandSiteData += "\"${p.getAWSURL(flavor.getAwsSubFolder())}\""
        }
        brandSiteData += "]}"
        FileManagement.createScriptFile(new File(stagingDir, "brandSiteUpdate${manifest.suffix()}.json"), brandSiteData)
    }


    /**
     * Install the deployment to the console over ADB.
     *
     * @param force True if we should force the update (downgrade).
     */
    void adbInstall(boolean force) {
        File packageDir = new File(configuration.rootDir, "ciPackages")

        // FIXME(ARR) - This should be done using File(packageDir, "adbForceUpdate.bat").absolutePath
        if (new File(packageDir, "apps.zip").exists()) {
            if (Os.isFamily(Os.FAMILY_WINDOWS)) {
                if (force) {
                    execute(["${packageDir.absolutePath}\\adbForceUpdate.bat"])
                } else {
                    execute(["${packageDir.absolutePath}\\adbUpdate.bat"])
                }
            } else {
                if (force) {
                    execute(["sh", "${packageDir.absolutePath}/adbForceUpdate.sh"], packageDir)
                } else {
                    execute(["sh", "${packageDir.absolutePath}/adbUpdate.sh"], packageDir)
                }
            }
        } else {
            throw new Exception("Create the local CI packages before running this command")
        }
    }

    /**
     * Download and installs the last release
     *
     * @param force True if we should force the update (downgrade).
     */
    void installLastRelease(boolean force) {
        String versionDescription = execute(["git", "describe", "--tags"]).replace("\n", "")
        println("Chimera version is $versionDescription")

        String lastVersion = versionDescription.contains("-") ? versionDescription.split("-")[0] : versionDescription

        if (lastVersion.startsWith("Chimera_")) {
            String version = lastVersion.substring("Chimera_".length())

            println("Chimera version is $version")
            println(new File(configuration.releaseDir, version + "/update_files.zip").getAbsolutePath())

            File downloadStaging = new File(configuration.rootDir, "adbUpdateStaging")
            downloadStaging.mkdirs()

            File updateFiles = new File(downloadStaging, "update_files.zip")
            println("Attempting to download https://s3-us-west-2.amazonaws.com/jhtsoftware/${flavor.platfrom()}/manifest/${version}/update_files.zip" )

            FileManagement.downloadFile("https://s3-us-west-2.amazonaws.com/jhtsoftware/${flavor.platfrom()}/manifest/${version}/update_files.zip", updateFiles)
            if (!updateFiles.exists()) {
                throw new Exception("Unable to download last version https://s3-us-west-2.amazonaws.com/jhtsoftware/" + flavor.platfrom() + "/manifest/${version}/update_files.zip")
            }

            FileManagement.unzipFile(updateFiles, downloadStaging, true)


            // FIXME(ARR) - This needs some reworking to get a more accurate path.
            if (Os.isFamily(Os.FAMILY_WINDOWS)) {
                execute(["${downloadStaging.absolutePath}\\downloadUSBUpdate.bat"])

                if (force) {
                    execute(["${downloadStaging.absolutePath}\\adbForceUpdate.bat"])
                } else {
                    execute(["${downloadStaging.absolutePath}\\adbUpdate.bat"])
                }
            } else {
                execute("${downloadStaging.absolutePath}/downloadUSBUpdate.sh", downloadStaging)

                if (force) {
                    execute("${downloadStaging.absolutePath}/adbForceUpdate.sh", downloadStaging)
                } else {
                    execute("${downloadStaging.absolutePath}/adbUpdate.sh", downloadStaging)
                }
            }

            downloadStaging.deleteDir()
        } else {
            throw new Exception("Unable to get last version")
        }
    }

    /**
     * Install the third party apps for this deployment.
     */
    void installThirdPartyApps() {
        JHTPackage tpa = packages.get(JHTPackages.CHIMERA_TPA)

        for (File file : tpa.sourceFiles) {
            if (file.isDirectory()) {
                File[] children = file.listFiles()
                if (children != null) {
                    for (File f : children) {
                        if (f.isFile() && f.name.endsWith(".apk")) {
                            execute("adb install -r -d \"${f}\"")
                        }
                    }
                }
            } else if (file.name.endsWith(".apk")) {
                execute("adb install -r -d \"${file}\"")
            }
        }
    }

    /**
     * Install the assets for this deployment.
     */
    void installAssets() {
        JHTPackage assets = packages.get(JHTPackages.ASSETS)
        for (File file : assets.sourceFiles) {
            if (file.isDirectory()) {
                File[] children = file.listFiles()
                for (File f : children) {
                    execute("adb push ${f} /sdcard/jht")
                }
            }
        }
    }

    /**
     * Get a version for the given package. Deployments need to override this function.
     *
     * @param pType The package enum
     * @return The version.  Can be NO_VERSION if the package is not in the deployment.
     */
    protected abstract String getVersion(JHTPackages pType)

    /**
     * Get whether the console app and updater share version number.
     *
     * @return True if console app should use updater version, False otherwise
     */
    protected boolean appMatchesUpdaterVersion() {
        return true
    }

    /**
     * Get the current SDC for the deployment. This allows us to store versions in a common file on
     * the server in a way that SDC's will not mess each other up.
     *
     * @return The current SDC version
     */
    protected abstract String sdc()

    /**
     * This allows deployments to add source files and folders to a package.
     *
     * @param pType The package.
     * @return      The additional sources for the package.
     */
    protected abstract ArrayList<File> getSources(JHTPackages pType)

    void fixVersion(String version) {
        File versionFolder = new File(configuration.releaseDir, version)
        File stagingDir = new File(versionFolder, "updateFilesStaging")
        //File updateDir = new File(".", "updateStagingTest")
        //updateDir.mkdirs()
        File updateDir = stagingDir
        if (!versionFolder.exists()) {
            return
        }

        fixManifest(new File(stagingDir, "update.xml"), updateDir)
        fixManifest(new File(stagingDir, "update.all.xml"), updateDir)
        fixManifest(new File(stagingDir, "update_ota.xml"), updateDir)
        fixManifest(new File(stagingDir, "update.all_ota.xml"), updateDir)

        fixManifestJSON(new File(stagingDir, "update_ota.json"), new File(stagingDir, "update_ota.xml"), updateDir)
        fixManifestJSON(new File(stagingDir, "update.all_ota.json"), new File(stagingDir, "update.all_ota.xml"), updateDir)


        String s3Path = flavor.getAwsSubFolder() + "/manifest/${version}/update.xml"
        AWSHelper.putObject("jhtsoftware", s3Path, new File(updateDir, "update_ota.xml"), "", configuration.disableAWS)

        // Zip up the files for ease of transfer
        File updateFiles = new File(versionFolder, "update_files.zip")
        updateFiles.delete()
        FileManagement.zipFiles(stagingDir, "update_files.zip", true)
        FileManagement.move(new File(stagingDir, "update_files.zip"), updateFiles)
        AWSHelper.putObject("jhtsoftware", flavor.platfrom() + "/manifest/${version}/update_files.zip", updateFiles, "", configuration.disableAWS)

    }

    private static void fixManifestJSON(File jsonFile, File manifestFile, File updateDir) {
        if(jsonFile.exists()) {
            String result = jsonFile.readBytes().toString().replace("manifestMD5\": \"(\\S*)\",\n", FileManagement.getMD5(manifestFile))

            new File(updateDir, jsonFile.getName()).write(result)
        }
    }

    private void fixManifest(File file, File updateDir) {
        StringBuilder newFile = new StringBuilder()

        for (String line : file.readLines()) {
            if (line.contains("name=")) {
                String[] attributes = line.split(" ")
                String name = extractAttribute(attributes, "name")
                String major = extractAttribute(attributes, "major")
                String minor = extractAttribute(attributes, "minor")
                String patch = extractAttribute(attributes, "patch")
                String build = extractAttribute(attributes, "build")
                String md5 = extractAttribute(attributes, "md5")

                if (name != null && major != null && minor != null && patch != null && build != null && md5 != null) {
                    File packageFile = new File(configuration.releaseDir, "${name}/${major}.${minor}.${patch}.${build}/${name}.zip")

                    if (packageFile.exists()) {
                        String realMd5 = FileManagement.getMD5(packageFile)
                        if (md5 != realMd5) {
                            println(md5 + " " + realMd5)
                            line = line.replace(md5, realMd5)
                        }
                    }
                }
            }

            newFile.append(line).append("\n")
        }

        new File(updateDir, file.getName()).write(newFile.toString())

    }

}
