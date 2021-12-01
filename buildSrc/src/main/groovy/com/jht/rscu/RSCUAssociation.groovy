package com.jht.rscu

class RSCUAssociation {
    private final int clubId
    private final boolean dev
    private final boolean qa
    private final boolean staging
    RSCUAssociation(int clubId, boolean dev, boolean qa, boolean staging) {
        this.clubId = clubId
        this.dev = dev
        this.qa = qa
        this.staging = staging
    }

    int clubId() { return clubId }
    boolean dev() { return dev }
    boolean qa() { return qa }
    boolean staging() { return staging }
}