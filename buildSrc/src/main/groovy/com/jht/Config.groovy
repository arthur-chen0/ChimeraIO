package com.jht
import java.text.SimpleDateFormat


class Config {
    private Config() {}

    class Android {
        private Android() {}

        static final String buildToolsVersion = "29.0.3"
        static final int compileSdkVersion = 29
        static final int minSdkVersion = 18
        static final boolean multiDexEnabled = true
        static final String ndkVersion = "21.0.6113669"
        static final int targetSdkVersion = 29
        static final String testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        static final boolean useSupportLibrary = true
        static final int versionCode = 1
//        static final String versionName = "1.0" // "0.0.0.0" this is used by the main project

        static String major_number = 1
        static String minor_number = 0
        static Date date = new Date()
        static SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd.HHmm")

        static String versionName = major_number + "." + minor_number + "." + sdf.format(date)



    }
}