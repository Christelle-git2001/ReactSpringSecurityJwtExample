package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Enum.Saison;

    public record SessionDTO(
            Saison saison,
            int annee
    ) {}