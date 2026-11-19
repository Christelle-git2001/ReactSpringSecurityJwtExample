export function actionsCandidatureDesactivees(candidature) {
    if (candidature?.statut === "OFFRE_DECLINEE") return true;
    if (typeof candidature?.actionsDesactivees === "boolean") {
        return candidature.actionsDesactivees;
    }
    return Boolean(
        candidature?.statutEtudiant === "STAGE_TROUVE"
        && !["STAGE_TROUVE", "CONTRAT_CREE"].includes(candidature?.statut)
    );
}
export function statutCandidatureEmployeur(candidature) {
    if (!candidature) return undefined;
    if (candidature.statut === "OFFRE_DECLINEE"
        || candidature.motifRefus === "stage.refused_by_student"
        || (actionsCandidatureDesactivees(candidature)
            && !["REFUSEE", "ENTREVUE_REFUSEE"].includes(candidature.statut))) return "OFFRE_DECLINEE";
    if (candidature.statut === "STAGE_TROUVE") return "ACCEPTEE";
    return candidature.statut === "CONTRAT_CREE" && candidature.contratSigne ? "CONTRAT_SIGNE" : candidature.statut;
}