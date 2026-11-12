package com.lacouf.rsbjwt.model.Enum.Notification;


public enum NotificationType {
    CV_ATTENTE_VALIDATION("Un CV est en attente de votre validation"),
    CV_STATUT_CHANGE("Le statut à changé"),
    OFFRE_STATUT_CHANGE("Le statut de l'offre a changé"),
    OFFRE_ATTENTE_VALIDATION("Une offre de stage est en attente de votre validation"),
    NOUVELLE_CANDIDATURE("Une nouvelle candidature a été reçue"),
    NOUVELLE_OFFRE_STAGE("Une nouvelle offre de stage est disponible"),
    CONVOCATION_ENTREVUE("Vous êtes convoqué à une entrevue"),
    CONVOCATION_ACCEPTEE("Une convocation a été acceptée"),
    CONVOCATION_REFUSEE("Une convocation a été refusée"),
    CANDIDATURE_ACCEPTEE("Votre candidature a été acceptée"),
    CANDIDATURE_REFUSEE("Votre candidature a ?t? refus?e"),
    STAGE_TROUVE("Un étudiant a choisi votre offre de stage"),
    CONTRAT_A_CREER("Un stage a été trouvé : le processus de contrat peut démarrer"),
    PROFESSEUR_ASSIGNE_ETUDIANT("Un étudiant vous a été assigné"),
    ETUDIANT_ASSIGNE_PROFESSEUR("Un professeur vous a été assigné"),
    NOUVEAU_CONTRAT("Vous avez un nouveau contrat à signer");

    private final String label;

    NotificationType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}