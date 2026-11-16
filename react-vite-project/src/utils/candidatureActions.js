export function actionsCandidatureDesactivees(candidature) {
    return Boolean(
        candidature?.statutEtudiant === "STAGE_TROUVE"
        && !["STAGE_TROUVE", "CONTRAT_CREE"].includes(candidature?.statut)
    );
}