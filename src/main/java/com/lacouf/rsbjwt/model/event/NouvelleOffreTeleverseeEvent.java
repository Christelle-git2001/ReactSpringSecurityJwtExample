package com.lacouf.rsbjwt.model.event;

import com.lacouf.rsbjwt.model.OffreDeStage;

public record NouvelleOffreTeleverseeEvent(OffreDeStage offre) {

    @Override
    public OffreDeStage offre (){
        return offre;
    }
}
