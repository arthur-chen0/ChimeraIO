package com.jht

/**
 * Helper class to manage the package version in the local properties file.
 */
class PackageVersionHelper {
    private String version = ""
    private String build = ""
    private String prefix = ""
    private String md5 = ""

    private String versionField
    private String buildField
    private String prefixField
    private String md5Field

    PackageVersionHelper(JHTFlavor f, String SDC, JHTPackage thePackage, Properties properties) {
        versionField = "${f.flavor()}_${SDC}_${thePackage.getShortName()}"
        prefixField = "${f.flavor()}_${SDC}_${thePackage.getShortName()}_prefix"
        buildField = "${f.flavor()}_${SDC}_${thePackage.getShortName()}_build"
        md5Field = "${f.flavor()}_${SDC}_${thePackage.getShortName()}_md5"
        load(properties)
    }

    void load(Properties properties) {
        version = properties.getProperty(versionField)
        prefix = properties.getProperty(prefixField)
        build = properties.getProperty(buildField)
        md5 = properties.getProperty(md5Field)

        if (version == null) version = "0.0.0.0"
        if (prefix == null) prefix = "1.0.0"
        if (build == null) build = ""
        if (md5 == null) md5 = ""
    }

    void save(Properties properties) {
        version = "${prefix}.${build}"
        properties.setProperty(versionField, version)
        properties.setProperty(prefixField, prefix)
        properties.setProperty(buildField, build)
        properties.setProperty(md5Field, md5)
    }

    String md5() {
        return md5
    }

    String version() {
        return version == null ? "0.0.0.0" : version
    }

    String prefix() {
        return prefix
    }

    String build() {
        return build
    }

    void md5(String md5) {
        this.md5 = md5
    }

    void version(String version) {
        this.version = version
    }

    void prefix(String prefix) {
        this.prefix = prefix
    }

    void build(String build) {
        this.build = build
    }
}