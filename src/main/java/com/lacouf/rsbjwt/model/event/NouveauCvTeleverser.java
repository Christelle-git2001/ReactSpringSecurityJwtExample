package com.lacouf.rsbjwt.model.event;

import com.lacouf.rsbjwt.model.CvEtudiant;

public record NouveauCvTeleverser(CvEtudiant cv) {

    @Override
    public CvEtudiant cv() {
        return cv;
    }
}
