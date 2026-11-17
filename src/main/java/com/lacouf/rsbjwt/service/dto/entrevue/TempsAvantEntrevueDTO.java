package com.lacouf.rsbjwt.service.dto.entrevue;

public record TempsAvantEntrevueDTO(EntrevueDTO entrevue, String etat,
                                    long secondesAvant, long secondesRestantes, String fuseau) {}