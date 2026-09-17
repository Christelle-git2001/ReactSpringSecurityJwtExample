package com.lacouf.rsbjwt.model;

public enum Departement {
    INFORMATIQUE("Techniques de l'informatique"),
    GESTION("Techniques de la gestion"),
    TRAVAIL_SOCIAL("Techniques de travail social"),
    EDUCATION_ENFANCE("Techniques d'éducation à l'enfance"),
    SOINS_INFIRMIERS("Techniques de soins infirmiers"),
    GENIE_CIVIL("Technologie du génie civil"),
    GENIE_ELECTRIQUE("Technologie du génie électrique"),
    GENIE_PHYSIQUE("Technologie du génie physique"),
    ARCHITECTURE("Technologie de l'architecture"),
    ESTIMATION_EVALUATION("Technologie de l'estimation et de l'évaluation en bâtiment");

    private final String label;

    Departement(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }
}
