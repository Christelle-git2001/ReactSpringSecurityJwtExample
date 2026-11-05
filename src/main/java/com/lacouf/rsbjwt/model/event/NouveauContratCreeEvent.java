package com.lacouf.rsbjwt.model.event;

import com.lacouf.rsbjwt.model.Contrat;

public record NouveauContratCreeEvent(Contrat contrat) {
    @Override
    public Contrat contrat() {
            return contrat;
        }
}

