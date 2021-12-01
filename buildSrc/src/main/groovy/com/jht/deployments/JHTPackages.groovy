package com.jht.deployments

import org.jetbrains.annotations.Nullable

/**
 * List all packages.
 */
enum JHTPackages {
    UPDATER("Updater", "d9a76997-7a2d-4f47-b429-6273de4bcfeb", "chimera_updater", "UpdateService", "software_updater", true),
    // reboot overridden by doesConsoleAppNeedReboot()
    CONSOLE_APPS("Apps", "63a74e6c-8380-4416-963a-2ee9d2357cc6", "chimera_mainapp", "general", true, false),
    ASSETS("Assets", "f5a6a28a-dacc-44a5-9949-697fc379873d", "chimera_assets", "general", false, false),
    CHIMERA_OS_K2_2("OS K2_2", "2ec6a147-375f-4a61-ad53-6050c17c27bb", "chimera_os", "general", true),
    CHIMERA_TPA("Third Party Apps", "fd9193ca-3ab3-43e9-829f-b41e83fc915f", "chimera_tpa", "general", false),
    CHIMERA_IO("IO", "fb50aaa6-64bc-4217-a2aa-fdeda79684b0", "chimera_firmware", "general", true),
    CHIMERA_IO2("IO2", "348ffaf2-804c-47ac-9250-8b011a7bf9fc", "chimera_firmware", "general", true),
    CHIMERA_LCB1("LCB1", "100ec386-af52-4ee5-ab62-326388ea36e1", "chimera_firmware", "general", true, true),
    CHIMERA_LCB2("LCB2", "94f764e0-b90e-4c4a-ba13-0f0be210ecbb", "chimera_firmware", "general", true, true),
    CHIMERA_LCB_Climbmill("LCB Climbmill", "41e6d75f-ec8a-4481-8e4a-91885b429fd0", "chimera_firmware", "general", true, true),
    CHIMERA_LCBA("LCBA", "c3136682-5a04-4795-88d2-61044f3a14ef", "chimera_firmware", "general", true, true),
    CHIMERA_LCB1xBike("LCB1xBike", "18bb5557-e9d2-4256-a99a-44bfa2e51168", "chimera_firmware", "general", true, true),
    CHIMERA_LCBAthena("LCBAthena", "eb7359d0-68e8-4293-ada3-2c42392b62c8", "chimera_firmware", "general", true, true),
    SALUTRON("Salutron", "acd5713e-3e57-4b15-98f9-37716a3440a6", "chimera_salutron", "general", false),
    CHIMERA_TOUCH_PANEL("Touch Panel Firmware", "41595365-8fce-438a-98ff-5e089c1a01eb", "chimera_touch_panel", "general", false),
    CHIMERA_TOUCH_PANEL_ATHENA("Touch Panel Firmware Athena", "41595365-8fce-438a-98ff-5e089c1a01eb", "chimera_touch_panel_athena", "general", false),
    WLT_BLUETOOTH("Bluetooth", "f253ef1f-960a-4400-aca8-a4c84ff18df3", "chimera_firmware", "general", false),
    CHIMERA_U_BOOT("U-Boot", "d3ff5cbb-cdba-4c7d-a8c8-2f5bcf977ce7", "chimera_uboot", "general", true),
    PARKER_OS("OS", "2ec6a147-375f-4a61-ad53-6050c17c27bb", "chimera_os", "os", true),
    PARKER_IO("IO", "fb50aaa6-64bc-4217-a2aa-fdeda79684b0", "chimera_firmware", "general", false),
    VISION_AD_BOARD("ADboard", "251db1fe-cbbc-4c9f-b396-7505c1643111", "vision_adboard", "general", false),
    GENERATIONS_OS("OS", "5d0ca704-0b3f-11e5-a6c0-1697f925ec7b", "chimera_os", "general", true),
    GENERATIONS_OS_RECOVERY("OS Recovery", "722f4ae8-8529-4c8d-b062-62e6f14b6ea8", "generations_os_recovery", "general", true),
    GENERATIONS_IO("IO", "5d0c9cdc-0b3f-11e5-a6c0-1697f925ec7b", "generations_io", "general", true),
    GENERATIONS_LCM("LCM", "5d0ca1a0-0b3f-11e5-a6c0-1697f925ec7b", "generations_lcm", "general", false),
    // TODO: GENERATIONS_LCB("LCB", "5d0ca3bc-0b3f-11e5-a6c0-1697f925ec7b", "generations_lcb", "general", false),
    // No longer used GENERATIONS_TOUCH_PANEL("Touch Panel", "b0c56d39-0c51-4a1b-849e-b12f179063d8", "generations_touch_panel", "general", false),
    GENERATIONS_BT("Bluetooth Firmware", "86580ede-ee48-4b1e-b8bf-b1ce2e26fe84", "generations_bt", "general", false),
    GENERATIONS_GYMKIT("Gymkit", "5d0c9cdc-0b3f-11e5-a6c0-1697f925abcd", "generations_gymkit", "general", false),
    GENERATIONS_UBOOT("UBoot", "e3b6905b-19e2-4c9e-936f-e762d7ddffba", "generations_uboot", "general", true),

    /**
     * The folder in the deploy project with the update script.
     */
    private String baseFolder

    /**
     * If there is a code folder that should be used for md5 hash, set this to that folder relative
     * to repo root
     */
    private String codeFolder

    /**
     * The name of the package.
     */
    private String packageName

    /**
     * The package UUID.
     */
    private String id

    /**
     * The update type
     */
    private String updateType

    /**
     * True if we need to reboot after installing the package.
     */
    private boolean reboot

    /**
     * True if we need to force a manual reboot after installing the package
     */
    private boolean manualReboot

    /**
     * Creates the package enum with the properties.
     *
     * @param packageName The package name.
     * @param id          The package UUID.
     * @param baseFolder  The folder containing the update script for the package.
     * @param updateType  The update type.
     * @param reboot      true if we need to reboot after installing the package.
     */
    JHTPackages(String packageName, String id, String baseFolder, String updateType, boolean reboot) {
        this(packageName, id, baseFolder, null, updateType, reboot)
    }

    /**
     * Creates the package enum with the properties.
     *
     * @param packageName The package name
     * @param id          The package UUID
     * @param baseFolder  The folder containing the update script for the package
     * @param codeFolder  The folder containing the source code for the package (if using source
     * code for md5 hashing)
     * @param updateType  The update type
     * @param reboot      True if we need to reboot after installing the package.
     */
    JHTPackages(String packageName, String id, String baseFolder, String codeFolder, String updateType, boolean reboot) {
        this.packageName = packageName
        this.id = id
        this.baseFolder = baseFolder
        this.codeFolder = codeFolder
        this.updateType = updateType
        this.reboot = reboot
        this.manualReboot = false
    }

    /**
     * Creates the package enum with the properties.
     *
     * @param packageName  The package name
     * @param id           The package UUID
     * @param baseFolder   The folder containing the update script for the package
     * @param updateType   The update type
     * @param reboot       True if we need to reboot after installing the package.
     * @param manualReboot True if we need to force a manual reboot after installing the package
     */
    JHTPackages(String packageName, String id, String baseFolder, String updateType, boolean reboot, boolean manualReboot) {
        this.packageName = packageName
        this.id = id
        this.baseFolder = baseFolder
        this.codeFolder = null
        this.updateType = updateType
        this.reboot = reboot
        this.manualReboot = manualReboot
    }

    /**
     * @return The folder that contains the update script.
     */
    String baseFolder() {
        return baseFolder
    }

    /**
     * @return the code folder that should be used for the MD5 hash, else null.
     */
    @Nullable
    String codeFolder() {
        return codeFolder
    }

    /**
     * @return The package name
     */
    String packageName() {
        return packageName
    }

    /**
     * @return The package UUID
     */
    String id() {
        return id
    }

    /**
     * @return The package update type.
     */
    String updateType() {
        return updateType
    }

    /**
     * @return True if we need to reboot after installing the package.
     */
    boolean reboot() {
        return reboot
    }

    /**
     * @return True if we need to reboot after installing the package.
     */
    boolean manualReboot() {
        return manualReboot
    }
}