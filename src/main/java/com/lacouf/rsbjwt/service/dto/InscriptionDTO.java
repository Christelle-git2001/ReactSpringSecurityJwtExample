package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Enum.Saison;
import com.lacouf.rsbjwt.model.Inscription;

public record InscriptionDTO(
        Long id,
        Saison saison,
        int annee
) {
    public static InscriptionDTO of(Inscription inscription) {
        return new InscriptionDTO(
                inscription.getId(),
                inscription.getSaison(),
                inscription.getAnnee()
        );
    }
}