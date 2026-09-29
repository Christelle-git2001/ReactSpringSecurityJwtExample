package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Enum.StatutOffre;

import java.time.LocalDate;

public class OffreDeStageDTO {
    Long id;
    String titre;
    String poste;
    StatutOffre statut;
    Double salaire;
    String description;
    LocalDate dateDebut;
    LocalDate dateFin;
    String cheminFichier;
    String messageRefus;
}
