package com.lacouf.rsbjwt.service.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReferenceTemporelleDTO(LocalDate date, LocalDateTime dateHeure, String fuseau,
                                    LocalDate debutSemaine, LocalDate finSemaine) {}