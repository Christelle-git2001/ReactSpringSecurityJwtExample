package com.lacouf.rsbjwt.model.Enum;

import lombok.Getter;

@Getter
public enum SecteurActivite {
    INFORMATIQUE("Informatique"),
    PHARMACEUTIQUE("Pharmaceutique"),
    FINANCE("Finance"),
    SANTE("Santé"),
    EDUCATION("Education"),
    COMMERCE_DETAIL("Commerce de détail"),
    COMMERCE_GROS("Commerce en Gros"),
    RESTAURATION_HOTELLERIE("Restauration et Hôtellerie"),
    CONSTRUCTION("Construction"),
    IMMOBILIER("Immobilier"),
    TRANSPORT_LOGISTIQUE("Transport Logistique"),
    MANUFACTURE_INDUSTRIE("Industrie de Manufacture"),
    AGRICULTURE("Agriculture"),
    MEDIAS_COMMUNICATION("Media et Communication"),
    AEROSPATIAL("Aerospatial"),
    ENERGIE("Énergie"),
    SERVICES_CONSEIL("Service et Conseil"),
    ARTS_DIVERTISSEMENT("Arts et Divertissements"),
    ADMINISTRATION_PUBLIQUE("Administration publique");

    private final String label;

    SecteurActivite(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
