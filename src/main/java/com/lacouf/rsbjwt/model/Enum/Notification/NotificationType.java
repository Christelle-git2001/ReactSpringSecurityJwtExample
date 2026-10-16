package com.lacouf.rsbjwt.model.Enum.Notification;


public enum NotificationType {
    CV_ATTENTE_VALIDATION("Un CV est en attente de votre validation"),
    CV_STATUT_CHANGE("Le statut à changé"),
    NOUVELLE_CANDIDATURE("Une nouvelle candidature a été reçue");

    private final String label;

    NotificationType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
