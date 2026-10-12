package com.lacouf.rsbjwt.model.Enum;


public enum NotificationType {
    CVATTENTEVALIDATION("Un CV est en attente de votre validation");

    private final String label;

    NotificationType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
