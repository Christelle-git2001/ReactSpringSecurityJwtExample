package com.lacouf.rsbjwt.model.Enum.Notification;

public enum NavigationPath {
    GESTIONNAIRE_CV_ID_DOWNLOAD("/gestionnaire/cv/{id}/download");

    private final String pattern;

    NavigationPath(String pattern) {
        this.pattern = pattern;
    }

    public String build(String... params) {
        String result = pattern;
        for (Object param : params) {
            result = result.replaceFirst("\\{[^}]+}", String.valueOf(param));
        }
        return result;
    }
}
