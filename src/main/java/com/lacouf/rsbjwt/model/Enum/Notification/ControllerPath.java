package com.lacouf.rsbjwt.model.Enum.Notification;

public enum ControllerPath {
    GESTIONNAIRE_CV_ID_DETAIL("/gestionnaire/cv/{id}"),
    GESTIONNAIRE_OFFRE_ID_DETAIL("/gestionnaire/offres/{id}"),
    GESTIONNAIRE_CANDIDATURE_CONTRAT("/gestionnaire/candidatures/{id}/contrat"),
    EMPLOYEUR_CANDIDATURES("/employeur"),
    ETUDIANT_OFFRES("/etudiant"),
    ETUDIANT_ENTREVUES("/etudiant/entrevues"),
    EMPLOYEUR_CONTRAT("/employeur/candidatures/details"),
    ETUDIANT_CONTRAT("/candidatures/{candidatureId}/contrat");

    private final String pattern;

    ControllerPath(String pattern) {
        this.pattern = pattern;
    }

    public String build(String... params) {
        String result = pattern;
        for (String param : params) {
            result = result.replaceFirst("\\{[^}]+}", String.valueOf(param));
        }
        return result;
    }
}
