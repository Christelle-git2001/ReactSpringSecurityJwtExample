package com.lacouf.rsbjwt.model.Enum.Notification;

public enum ControllerPath {
    ETUDIANT_CV("/etudiant/cv"), //TODO a modifier sans la US-68
    GESTIONNAIRE_CV_ID_DETAIL("/cv/{id}/download");

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
