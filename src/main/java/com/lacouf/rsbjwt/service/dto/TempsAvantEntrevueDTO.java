package com.lacouf.rsbjwt.service.dto;

public record TempsAvantEntrevueDTO(EntrevueDTO entrevue, String etat,
                                    long secondesAvant, long secondesRestantes, String fuseau) {}