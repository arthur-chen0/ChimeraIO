package com.jht

import com.jht.filemanagement.AWSHelper
import com.jht.filemanagement.FileManagement

class JHTPackage {
    private String name
    private String id
    private String version
    private boolean requiresReboot
    private boolean manualReboot
    private String packageShortName
    private ArrayList<File> srcFolder
    private File codeFolder
    private File releaseFolder
    private String updateType
    private int order
    private long installSize
    private boolean force
    private boolean forceDowngrade
    private boolean forceRecreate
    public String targetConsole = null
    private JHTConfiguration configuration
    private int type
    private String sourceMD5
    private String packageMD5

    JHTPackage(int type, String name, String id, boolean requiresReboot, boolean manualReboot, ArrayList<File> srcFolder, String updateType, JHTConfiguration configuration) {
        this.name = name
        this.packageShortName = name.replace(" ", "_").toLowerCase()
        this.id = id
        this.requiresReboot = requiresReboot
        this.manualReboot = manualReboot
        this.srcFolder = srcFolder
        this.codeFolder = null
        this.configuration = configuration
        this.updateType = updateType
        this.type = type
    }

    String getName() {
        return name
    }

    String getShortName() {
        return packageShortName
    }

    String getId() {
        return id
    }

    String getVersion() {
        return version
    }

    boolean getRequiresReboot() {
        return requiresReboot
    }
    void setRequiresReboot(boolean needsReboot) {
        requiresReboot = needsReboot
    }

    void setCodeFolder(File codePath) {
        this.codeFolder = codePath
    }

    int type() {
        return type
    }

    void setForceRecreate(boolean forceRecreate) {
        this.forceRecreate = forceRecreate
    }

    void setForce(boolean force) {
        this.force = force
    }

    void setForceDowngrade(boolean force) {
        this.forceDowngrade = force
    }

    String computeSrcMD5() {
        sourceMD5 = codeFolder != null ? FileManagement.getMD5(codeFolder) : FileManagement.getMD5(srcFolder)
        return sourceMD5
    }

    void setVersion(String version) {
        this.version = version
        this.releaseFolder = new File(configuration.releaseDir, "${packageShortName}/${version}")
    }

    void setReleaseFolder(File folder) {
        releaseFolder = folder
    }

    void order(int order) {
        this.order = order
    }

    void createPackage(String platform) {
        AWSPackageObject awsObject = new AWSPackageObject("jhtsoftware", getAWSKey(platform))
        if (forceRecreate && awsObject.sourceMd5Match(sourceMD5)) {
            // No need to do anything.  Package has already been created and exists.
            println("bdc_check " + shortName + " not uploading as source MD5 matches")
            packageMD5 = awsObject.packageMD5()
            return
        }

        File zipFile = new File(releaseFolder, "${this.packageShortName}.zip")
        for (File src : srcFolder) {
            installSize += FileManagement.getFolderSize(src)
        }
        installSize *= 3

        if (forceRecreate || !zipFile.exists()) {
            File stagingFolder = new File(releaseFolder, "staging")
            stagingFolder.mkdirs()

            for (File src : srcFolder) {
                if (src.isFile()) {
                    FileManagement.copy(src, new File(stagingFolder, src.name))
                } else {
                    FileManagement.copy(src, stagingFolder)
                }
            }

            FileManagement.zipFiles(stagingFolder, zipFile.absolutePath, true)
            if (configuration.deleteStagingDir) {
                stagingFolder.deleteDir()
            }
        }

        if (forceRecreate && !awsObject.md5Match(FileManagement.getMD5(zipFile))) {
            AWSHelper.putObject("jhtsoftware", getAWSKey(platform), zipFile, sourceMD5, configuration.disableAWS)
        } else {
            AWSHelper.putObjectIfNotExists("jhtsoftware", getAWSKey(platform), zipFile, sourceMD5, configuration.disableAWS)
        }
    }

    String getManifestXML(String awsSubFolder, boolean ota) {
        File zipFile = new File(releaseFolder, "${this.packageShortName}.zip")
        // FIXME(ARR) - Convert to using split(".")
        String[] versionNumbers = this.version.tokenize(".")
        String md5 = zipFile.exists() ? FileManagement.getMD5(zipFile) : packageMD5
        String reboot = this.requiresReboot ? "true" : "false"
        String manualReboot = this.manualReboot ? "true" : "false"

        StringBuilder strTags = new StringBuilder()
        strTags.append("order=\"${this.order}\" ")
        strTags.append("name=\"${this.name}\" ")
        strTags.append("file=\"${packageShortName}.zip\" ")

        if (ota) {
            strTags.append("file-url=\"${getAWSURL(awsSubFolder)}\" ")
        }

        if (force) {
            strTags.append("force=\"true\" ") // always installs the package
        } else if (forceDowngrade) {
            strTags.append("force_downgrade=\"true\" ") // only installs if versions are different
        }

        strTags.append("install_size=\"${this.installSize}\" ")
        strTags.append("major=\"${versionNumbers[0]}\" ")
        strTags.append("minor=\"${versionNumbers[1]}\" ")
        strTags.append("patch=\"${versionNumbers[2]}\" ")
        strTags.append("build=\"${versionNumbers[3]}\" ")
        strTags.append("update_id=\"${this.id}\" ")
        strTags.append("md5=\"$md5\" ")
        strTags.append("type=\"$updateType\" ")
        strTags.append("requires_reboot=\"$reboot\" ")
        strTags.append("manual_reboot=\"$manualReboot\" ")

        if (targetConsole != null) {
            strTags.append("target_console=\"$targetConsole\" ")
        }

        return "<update ${strTags.toString()}/>"
    }

    String getJSON() {
        StringBuilder json = new StringBuilder()
        json.append("\t\t\t\t{\n")
        json.append("\t\t\t\t\t\"packageName\": \"").append(name).append("\",\n")
        json.append("\t\t\t\t\t\"packageUUID\": \"").append(id).append("\",\n")
        json.append("\t\t\t\t\t\"packageVersion\": \"").append(version).append("\"\n")
        json.append("\t\t\t\t}")
        return json.toString()
    }

    String getAWSURL(String awsSubFolder) {
        return "https://s3-us-west-2.amazonaws.com/jhtsoftware/${getAWSKey(awsSubFolder)}"
    }

    String getAWSKey(String equipment) {
        return "${equipment}/${packageShortName}/${version}/${packageShortName}.zip"
    }

    File getPackageLocation() {
        return new File(releaseFolder, "${this.packageShortName}.zip")
    }

    String getDownloadShellScript(String awsSubFolder) {
        StringBuilder script = new StringBuilder()
        script.append("\tif [ -z \"\$Z_DRIVE\" ]; then\n")
        script.append("\t\techo \"Downloading ${this.getAWSURL(awsSubFolder)}\"\n")
        script.append("\t\twget -P . ${this.getAWSURL(awsSubFolder)}\n")
        script.append("\telse\n")
        script.append("\t\tcp -v \"\${Z_DRIVE}/${configuration.flavor.releaseDir()}/${this.packageShortName}/${this.version}/${this.packageShortName}.zip\" .\n")
        script.append("\tfi\n")
        return script.toString()
    }


    String getDownloadBatchScript(String awsSubFolder) {
        StringBuilder script = new StringBuilder()
        script.append("if defined Z_DRIVE (\r\n")
        script.append("\tcopy \"%Z_DRIVE%\\${configuration.flavor.releaseDir().replace('/', '\\')}\\${this.packageShortName}\\${this.version}\\${this.packageShortName}.zip\" .\r\n")
        script.append(") else (\r\n")
        script.append("\techo Downloading ${this.getAWSURL(awsSubFolder)}\r\n")
        script.append("\twget -P . ${this.getAWSURL(awsSubFolder)}\r\n")
        script.append(")\r\n")
        return script.toString()
    }

    List<File> getSourceFiles() {
        return srcFolder
    }
}
