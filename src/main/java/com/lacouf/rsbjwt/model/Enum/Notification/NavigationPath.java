package com.lacouf.rsbjwt.model.Enum.Notification;

public enum NavigationPath {
    GESTIONNAIRE_CV_details("/gestionnaire/cvs/details"),
    ETUDIANT_CV("/etudiant/cv");

    private final String pattern;

    NavigationPath(String pattern) {
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
