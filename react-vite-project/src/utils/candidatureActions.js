export function actionsCandidatureDesactivees(candidature) {
    if (typeof candidature?.actionsDesactivees === "boolean") {
        return candidature.actionsDesactivees;
    }
    return Boolean(
        candidature?.statutEtudiant === "STAGE_TROUVE"
        && !["STAGE_TROUVE", "CONTRAT_CREE"].includes(candidature?.statut)
    );
}