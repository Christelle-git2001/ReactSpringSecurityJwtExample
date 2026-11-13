package com.lacouf.rsbjwt.Exception;

public class ParametreDonneesInvalideException extends Exception {
    public enum Code {
        STATUT_CV_OBLIGATOIRE("cv_status_required", "Le statut du CV est obligatoire."),
        DEPARTEMENT_OBLIGATOIRE("department_required", "Le département est obligatoire."),
        PROFESSEUR_INVALIDE("invalid_professor", "L'identifiant du professeur doit être positif."),
        STATUT_OFFRE_OBLIGATOIRE("offer_status_required", "Le statut de l'offre est obligatoire."),
        STATUT_ENTREVUE_OBLIGATOIRE("interview_status_required", "Le statut d'entrevue est obligatoire."),
        STATUT_CANDIDATURE_OBLIGATOIRE("application_status_required", "Le statut de candidature est obligatoire."),
        DATES_INVALIDES("invalid_dates", "Les dates doivent être présentes, entre les années 1 et 9998, avec une fin égale ou postérieure au début."),
        RECHERCHE_OBLIGATOIRE("search_required", "Le poste et l'entreprise doivent contenir du texte et ne pas dépasser 200 caractères chacun."),
        ENTREVUE_INVALIDE("invalid_interview", "L'identifiant de l'entrevue doit être positif."),
        ENTREVUE_INTROUVABLE("interview_missing", "Aucune entrevue ne correspond à cet identifiant."),
        SIGNATAIRE_OBLIGATOIRE("signer_required", "Le signataire doit être ETUDIANT, EMPLOYEUR ou GESTIONNAIRE."),
        MODE_OBLIGATOIRE("mode_required", "Le mode de résultat doit être LISTE ou NOMBRE.");

        private final String cle;
        private final String explication;

        Code(String cle, String explication) {
            this.cle = "chatbot.errors." + cle;
            this.explication = explication;
        }

        public String cleTraduction() {
            return cle;
        }
    }

    private final Code code;

    public ParametreDonneesInvalideException(Code code) {
        super(code.cleTraduction());
        this.code = code;
    }

    public String explicationPourOutil() {
        return code.explication;
    }
}