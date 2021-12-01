package com.jht

import com.android.build.gradle.internal.dsl.BaseFlavor
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

class CustomBuildConfig {
    static CustomBuildConfig instance(BaseFlavor flavor) {
        return new CustomBuildConfig(flavor)
    }

    private final BaseFlavor flavor

    private CustomBuildConfig(BaseFlavor flavor) {
        this.flavor = flavor
    }

    /**
     * Set the value of the String build config field using the given name and value.
     *
     * @param name  The name of the build config field.
     * @param value The value to use.
     */
    private void setString(@NotNull String name, @Nullable String value) {
        flavor.buildConfigField("String", name, value == null ? "null" : "\"$value\"")
    }

    private void setBoolean(@NotNull String name, @Nullable Boolean value) {
        flavor.buildConfigField("Boolean", name, value == null ? "null" : value ? "true" : "false")
    }

    private void setInteger(@NotNull String name, @Nullable Integer value) {
        flavor.buildConfigField("Integer", name, value == null ? "null" : "$value")
    }

    CustomBuildConfig ttyIO(@Nullable String value) {
        setString("ttyIO", value)
        return this
    }

    CustomBuildConfig ttyLCB(@Nullable String value) {
        setString("ttyLCB", value)
        return this
    }

    CustomBuildConfig ttySalutron(@Nullable String value) {
        setString("ttySalutron", value)
        return this
    }

    CustomBuildConfig ttySwift(@Nullable String value) {
        setString("ttySwift", value)
        return this
    }

    CustomBuildConfig ttyBluetoothSinkModule(@Nullable String value) {
        setString("ttyBluetoothSinkModule", value)
        return this
    }

    CustomBuildConfig ttyCAB(@Nullable String value) {
        setString("ttyCAB", value)
        return this
    }

    CustomBuildConfig supportsSSO(@Nullable Boolean value) {
        setBoolean("SUPPORTS_SSO", value)
        return this
    }

    CustomBuildConfig hasEthernet(@Nullable Boolean value) {
        setBoolean("HAS_ETHERNET", value)
        return this
    }

    CustomBuildConfig maxVolume(@Nullable Integer value) {
        setInteger("MAX_VOLUME", value)
        return this
    }

    CustomBuildConfig hdmiInterface(@Nullable String value) {
        setString("HDMI_INTERFACE", value)
        return this
    }

    CustomBuildConfig hasBTSink(@Nullable Boolean value) {
        setBoolean("HAS_BT_SINK", value)
        return this
    }

    CustomBuildConfig gymkitEnabledDefault(@Nullable Boolean value) {
        setBoolean("GYMKIT_ENABLED_DEFAULT", value)
        return this
    }

    CustomBuildConfig ftmsDataAlwaysStream(@Nullable Boolean value) {
        setBoolean("FTMS_DATA_ALWAYS_STREAM", value)
        return this
    }

    CustomBuildConfig ftmsEnableControlPoint(@Nullable Boolean value) {
        setBoolean("FTMS_ENABLE_CONTROL_POINT", value)
        return this
    }

    CustomBuildConfig audioSampleRateDefault(@Nullable Integer value) {
        setInteger("AUDIO_SAMPLE_RATE_DEFAULT", value)
        return this
    }

    CustomBuildConfig audioSampleRateHDMI(@Nullable Integer value) {
        setInteger("AUDIO_SAMPLE_RATE_HDMI", value)
        return this
    }

    CustomBuildConfig audioSampleRateRemoteTV(@Nullable Integer value) {
        setInteger("AUDIO_SAMPLE_RATE_REMOTE_TV", value)
        return this
    }

    CustomBuildConfig newOCProtection(@Nullable Boolean value) {
        setBoolean("NEW_OC_PROTECTION", value)
        return this
    }

    CustomBuildConfig hasGymkit(@Nullable Boolean value) {
        setBoolean("HAS_GYMKIT", value)
        return this
    }

    CustomBuildConfig brandID(@Nullable Integer value) {
        setInteger("BRAND_ID", value)
        return this
    }

    CustomBuildConfig vividMount(@Nullable String value) {
        setString("vividMount", value)
        return this
    }

    CustomBuildConfig ttyCSAFE(@Nullable String value) {
        setString("ttyCSAFE", value)
        return this
    }

    CustomBuildConfig appTheme(@Nullable String value) {
        setString("APP_THEME", value)
        return this
    }

    CustomBuildConfig hasIFit(@Nullable Boolean value) {
        setBoolean("HAS_IFIT", value)
        return this
    }

    CustomBuildConfig checkTMOverspeed(@Nullable Boolean value) {
        setBoolean("CHECK_TM_OVERSPEED", value)
        return this
    }

    CustomBuildConfig showClimbmillIRCZSafteyScreen(@Nullable Boolean value) {
        setBoolean("SHOW_CLIMBMILL_IR_CZ_SAFETY_SCREEN", value)
        return this
    }

    CustomBuildConfig supportsSaveConsoleSerial(@Nullable Boolean value) {
        setBoolean("SUPPORTS_SAVE_CONSOLE_SERIAL", value)
        return this
    }

    CustomBuildConfig hasCAB(@Nullable Boolean value) {
        setBoolean("HAS_CAB", value)
        return this
    }

    CustomBuildConfig hasSTBTuner(@Nullable Boolean value) {
        setBoolean("HAS_STB_TUNER", value)
        return this
    }

    CustomBuildConfig hasUIPTVTuner(@Nullable Boolean value) {
        setBoolean("HAS_UIPTV_TUNER", value)
        return this
    }

    CustomBuildConfig hasJP0Tuner(@Nullable Boolean value) {
        setBoolean("HAS_JP0_TUNER", value)
        return this
    }

    CustomBuildConfig hasBR2Tuner(@Nullable Boolean value) {
        setBoolean("HAS_BR2_TUNER", value)
        return this
    }

    CustomBuildConfig hasKM4Tuner(@Nullable Boolean value) {
        setBoolean("HAS_KM4_TUNER", value)
        return this
    }

    CustomBuildConfig hasUS7Tuner(@Nullable Boolean value) {
        setBoolean("HAS_US7_TUNER", value)
        return this
    }

    CustomBuildConfig hasUS6Tuner(@Nullable Boolean value) {
        setBoolean("HAS_US6_TUNER", value)
        return this
    }

    CustomBuildConfig hasUS5Tuner(@Nullable Boolean value) {
        setBoolean("HAS_US5_TUNER", value)
        return this
    }

    CustomBuildConfig hasUS4Tuner(@Nullable Boolean value) {
        setBoolean("HAS_US4_TUNER", value)
        return this
    }

    CustomBuildConfig hasTV(@Nullable Boolean value) {
        setBoolean("HAS_TV", value)
        return this
    }

    CustomBuildConfig samsungWatchCompatible(@Nullable Boolean value) {
        setBoolean("SAMSUNG_WATCH_COMPATIBLE", value)
        return this
    }

    CustomBuildConfig mediaPlayerScanner(@Nullable Boolean value) {
        setBoolean("MEDIA_PLAYER_SCANNER", value)
        return this
    }

    CustomBuildConfig heartRateScanner(@Nullable Boolean value) {
        setBoolean("HEART_RATE_SCANNER", value)
        return this
    }

    CustomBuildConfig headphonesScanner(@Nullable Boolean value) {
        setBoolean("HEADPHONES_SCANNER", value)
        return this
    }

    CustomBuildConfig dapiMachineIDReady(@Nullable Boolean value) {
        setBoolean("DAPIMachineIdReady", value)
        return this
    }

    /**
     * Initializes the BUILD_TIME BuildConfig value using the current system time at the time of
     * this build.
     *
     * @return this custom build config instance for chaining.
     */
    CustomBuildConfig initBuildTime() {
        flavor.buildConfigField("java.util.Date", "BUILD_TIME", "new java.util.Date(${System.currentTimeMillis()}L)")
        return this
    }
}