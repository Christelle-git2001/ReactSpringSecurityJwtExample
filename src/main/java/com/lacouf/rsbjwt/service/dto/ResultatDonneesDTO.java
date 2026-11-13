package com.lacouf.rsbjwt.service.dto;

import java.util.List;

public record ResultatDonneesDTO(Long nombre, List<?> resultats) {
    public static ResultatDonneesDTO nombre(long nombre) {
        return new ResultatDonneesDTO(nombre, List.of());
    }

    public static ResultatDonneesDTO liste(List<?> resultats) {
        return new ResultatDonneesDTO(null, resultats);
    }
}