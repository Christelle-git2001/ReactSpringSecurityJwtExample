package com.lacouf.rsbjwt.service.dto.donnee;

import lombok.Builder;

@Builder
public record MetricGestionnaireDTO(
        EtudiantsStats etudiants,
        ProfesseursStats professeurs,
        EmployeursStats employeurs,
        ContratsStats contrats
) {
    @Builder
    public record EtudiantsStats(
            Long etudiantInscrits,
            Long aucunStageTrouve,
            Long stageTrouve,
            double tauxPlacement,
            Long cvVerification
    ) {}

    @Builder
    public record ProfesseursStats(
            Long professeurEleve,
            Long professeurSansEleve,
            Long moyenneEleve
    ) {}

    @Builder
    public record EmployeursStats(
            Long employeursInscrits,
            Long offresActives,
            Long offreVerification,
            Long offreSansStagiaire,
            Long tauxOffres
    ) {}

    @Builder
    public record ContratsStats(
            Long nombreContratASigner,
            Long contratSigne,
            Long nombreContratGenerer,
            Long contratBloque,
            Long tauxFinalisation
    ) {}
}