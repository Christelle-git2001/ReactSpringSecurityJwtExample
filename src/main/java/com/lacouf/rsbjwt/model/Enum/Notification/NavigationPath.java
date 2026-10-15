package com.lacouf.rsbjwt.model.Enum.Notification;

public enum NavigationPath {
    ETUDIANT_CV("/etudiant/cv"),
    GESTIONNAIRE_CV_DETAILS("/gestionnaire/cvs/details?cvId={id}");

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
