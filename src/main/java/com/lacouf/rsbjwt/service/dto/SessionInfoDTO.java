package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Enum.Session;

    public record SessionInfoDTO(
            Session session,
            int annee
    ) {}