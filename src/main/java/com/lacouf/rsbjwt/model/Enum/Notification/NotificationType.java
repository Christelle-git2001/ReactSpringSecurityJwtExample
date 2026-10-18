package com.lacouf.rsbjwt.model.Enum.Notification;


public enum NotificationType {
    CV_ATTENTE_VALIDATION("Un CV est en attente de votre validation"),
    CV_STATUT_CHANGE("Le statut à changé"),
    OFFRE_ATTENTE_VALIDATION("Une offre de stage est en attente de votre validation"),
    NOUVELLE_OFFRE_STAGE("Une nouvelle offre de stage est disponible");

    private final String label;

    NotificationType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
