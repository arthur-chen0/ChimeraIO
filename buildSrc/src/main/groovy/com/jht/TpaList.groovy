package com.jht

import com.jht.filemanagement.FileManagement
import groovy.json.JsonSlurperClassic
import net.dongliu.apk.parser.ApkFile
import net.dongliu.apk.parser.bean.ApkMeta

class TpaList {
    static class ApkInfo {
        public String packageName
        public String versionName
        public String versionCode

        String toString() {
            return toJson()
        }

        String toJson() {
            return "{ \"packageName\": \"$packageName\", \"versionName\":\"$versionName\", \"versionCode\":\"$versionCode\" }"
        }
    }

    static class TpaInfo extends ApkInfo {
        public File srcFolder
        public String md5


        boolean matches(ApkInfo info) {
            return packageName == info.packageName && versionName == info.versionName && versionCode == info.versionCode
        }

        String toString() {
            return toJson()
        }

        String toJson() {
            return "{ \"packageName\": \"$packageName\", \"versionName\":\"$versionName\", \"versionCode\":\"$versionCode\", \"md5\":\"$md5\", \"srcFolder\":\"${srcFolder.getPath().replace('\\','/')}\" }"
        }
    }

    private static JsonSlurperClassic jsonSlurper = new JsonSlurperClassic()

    static ApkInfo parseXApkInfo(File xapkFolder) {
        if (!xapkFolder.isDirectory()) {
            // TODO - maybe later?
            println("!! Don't do unzipped xapk files !!")
            throw new Exception("Cannot read zipped xapk file ${xapkFolder.path}")
        }

        File[] folderItems = xapkFolder.listFiles()
        if (folderItems == null) {
            return null
        }

        for (File item : folderItems) {
            if (item.name.equalsIgnoreCase("manifest.json")) {
                HashMap map = jsonSlurper.parseText(item.text)
                ApkInfo info = new ApkInfo()
                info.packageName = map.get("package_name", "")
                info.versionName = map.get("version_name", "")
                info.versionCode = map.get("version_code", "")
                return info
            }
        }
        return null
    }

    static ApkInfo getApkInfo(File srcFolder) {
        if (!srcFolder.exists()) {
            throw new Exception("Folder does not exist " + srcFolder.getPath())
        }

        if (!srcFolder.isDirectory()) {
            throw new Exception("Not a folder> " + srcFolder.getPath())
        }

        // if we expect a folder, we should check for one
        File[] folderItems = srcFolder.listFiles()
        if (folderItems == null) {
            return null
        }

        ApkInfo info = null
        for (File item : folderItems) {
            if (item.path.endsWith(".apk")) {
                if (item.name.contains(" ")) {
                    throw new Exception("Please avoid spaces in apk name ${item.path}")
                }

                // those could cause issues if scripting doesn't cover spaces!
                if (info != null) {
                    throw new Exception("Multiple apks found ${srcFolder.path}")
                }

                info = parseApkInfo(item)
            } else if (item.path.endsWith(".xapk")) {
                if (info != null) {
                    throw new Exception("Multiple apks found ${srcFolder.path}")
                }

                info = parseXApkInfo(item)
            } else {
                // should we throw if there are any other items? install should ignore anything not
                // apk/xapk ?
                println("Has other items? ${item.path}")
            }
        }

        return info
    }

    static ApkInfo parseApkInfo(File apkFile) {
        ApkFile apkFileReader = null
        try {
            apkFileReader = new ApkFile(apkFile)
            ApkInfo info = new ApkInfo()
            ApkMeta apkMeta = apkFileReader.getApkMeta()
            info.packageName = apkMeta.packageName
            info.versionName = apkMeta.versionName
            info.versionCode = "${apkMeta.versionCode}"
            return info
        } catch (Exception ex) {
            println("getApkInfo exception ${ex}")
            throw new Exception("getApkInfo ${apkFile.path}", ex)
        } finally {
            try { if (apkFileReader != null) apkFileReader.close() } catch (Exception ignored) {}
        }
    }

    protected List<TpaInfo> items = new ArrayList<>()
    protected File baseFolder

    TpaList(File srcBaseFolder) {
        baseFolder = srcBaseFolder
    }

    void add(String subFolder, String packageName, String versionName, String versionCode, String optionalMd5) {
        TpaInfo info = new TpaInfo()
        info.srcFolder = getFilePath(subFolder)
        info.md5 = (optionalMd5 == null || optionalMd5.isEmpty()) ? null : optionalMd5
        info.packageName = packageName
        info.versionName = versionName
        info.versionCode = versionCode
        items.add(info)
    }

    void populateSources(List<File> sources) {
        for (TpaInfo item : items) {
            sources.add(item.srcFolder)
        }
    }

    String toJson() {
        StringBuilder stringBuilder = new StringBuilder()
        stringBuilder.append("{ \"tpas\": [")

        // FIXME(ARR) - Do we really want to be incrementing beyond the first element?
        for (int i = 0; i < items.size(); ++i) {
            TpaInfo item = items[i]

            if (i > 0) {
                stringBuilder.append(",\r\n")
            } else {
                stringBuilder.append("\r\n")
            }

            stringBuilder.append(item.toJson())
        }

        stringBuilder.append("]}")
        return stringBuilder.toString()
    }

    void validateTpaSources() {
        String duplicates = findDuplicates()
        if (duplicates != null) {
            throw new Exception(duplicates)
        }

        // TODO - should we stop at first issue, or collect all issues before bailing?
        for (TpaInfo item : items) {
            ApkInfo info = getApkInfo(item.srcFolder)
            if (info == null) {
                throw new Exception("Failed to get app info from ${item.srcFolder.path}")
            }

            if (!item.matches(info)) {
                throw new Exception("App Info read ${info.toString()} expected ${item.toJson()}")
            }

            if (item.md5 != null && !item.md5.isEmpty()) {
                String md5 = FileManagement.getMD5(item.srcFolder)
                if (md5 != item.md5) {
                    throw new Exception("HASH FAILED: Calc ${md5}, Expect ${item.md5} for ${item.srcFolder}")
                }
            }
        }
    }

    String findDuplicates() {
        List<String> duplicates = []
        Set<String> nonDuplicates = new HashSet<>()

        for (TpaInfo info : items) {
            if (!nonDuplicates.add(info.packageName)) {
                duplicates.add(info.packageName)
            }
        }

        if (duplicates.size() == 0) {
            return null
        }

        return "Duplicate packages> ${duplicates.join(", ")}"
    }

    protected File getFilePath(String subFolder) {
        return new File(baseFolder, subFolder)
    }
}