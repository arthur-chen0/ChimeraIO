package com.jht.deployments

import com.jht.JHTPackage

class JHTManifest {
    private String suffix
    private List<JHTPackage> packages = new ArrayList<>()

    JHTManifest(String suffix) {
        this.suffix = suffix
    }

    void addPackage(JHTPackage p) {
        packages.add(p)
    }

    List<JHTPackage> packages() {
        return packages
    }

    String suffix() {
        return suffix
    }
}