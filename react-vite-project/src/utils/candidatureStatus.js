export const OPTIONS_STATUT_CANDIDATURE = ["SOUMISE", "ENTREVUE_PLANIFIEE", "ENTREVUE_REFUSEE", "ACCEPTEE", "REFUSEE", "STAGE_TROUVE", "CONTRAT_CREE", "A_CONFIRMER", "EN_ATTENTE_REPONSE"];

export function getCandidatureStatus(candidature, convocation) {
    if (candidature?.statut === "ENTREVUE_PLANIFIEE") {
        if (convocation?.statut === "A_CONFIRMER") return "A_CONFIRMER";
        if (convocation?.statut === "TERMINEE") return "EN_ATTENTE_REPONSE";
    }
    return candidature?.statut === "RETENUE_POUR_ENTREVUE" ? "SOUMISE" : candidature?.statut;
}