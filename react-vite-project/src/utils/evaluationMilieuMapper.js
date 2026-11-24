

export const CRITERES = [
    { id: "tachesConformes",          dto: "tacheConformeScore" },
    { id: "mesuresAccueil",           dto: "integrationScore" },
    { id: "tempsEncadrement",         dto: "tempsSupervisionSuffisantScore" },
    { id: "hygieneSecurite",          dto: "environmentScore" },
    { id: "climatTravail",            dto: "climatScore" },
    { id: "salaireInteressant",       dto: "salaireSuffisentScore" },
    { id: "communicationSuperviseur", dto: "communicationScore" },
    { id: "equipementAdequat",        dto: "equipementScore" },
    { id: "volumeTravail",            dto: "volumeTravailScore" },
];

export const SCORES = {
    TOTALEMENT_EN_ACCORD: 4,
    PLUTOT_EN_ACCORD: 3,
    PLUTOT_EN_DESACCORD: 2,
    TOTALEMENT_EN_DESACCORD: 1,
    IMPOSSIBLE_DE_SE_PRONONCER: 0,
};
const NIVEAU_PAR_SCORE = Object.fromEntries(Object.entries(SCORES).map(([niveau, score]) => [score, niveau]));

export const CHAMPS_SUPERVISEUR = ["nom", "fonction", "telephone", "email"];

export const HEURES = ["heuresMois1", "heuresMois2", "heuresMois3"];
const QUARTS_VIDES = [{ de: "", a: "" }, { de: "", a: "" }, { de: "", a: "" }];

export function formulaireVide() {
    return {
        superviseur: Object.fromEntries(CHAMPS_SUPERVISEUR.map(c => [c, ""])),
        reponses: {},
        heuresMois1: "", heuresMois2: "", heuresMois3: "",
        commentaires: "",
        noStagePrivilegier: "",
        nbStagiaireAccueil: "",
        engagerMemeStagiaire: "",
        quartsVariables: "",
        quarts: QUARTS_VIDES,
    };
}

export function versDto(form, candidatureId) {
    const scores = Object.fromEntries(CRITERES.map(c => [c.dto, SCORES[form.reponses[c.id]]]));
    return {
        candiatureId: candidatureId,
        superviseurStageDTO: { ...form.superviseur },
        obersvationGeneralDTO: {
            noStagePrivilegier: Number(form.noStagePrivilegier),
            nbStagiaireAccueil: Number(form.nbStagiaireAccueil),
            engagerMemeStagiaire: form.engagerMemeStagiaire === "OUI",
            quartsTravail: form.quartsVariables === "OUI"
                ? form.quarts.filter(q => q.de && q.a).map(q => ({ heureDebut: q.de, heureFin: q.a }))
                : [],
        },
        millieuStageScoresDTO: {
            ...scores,
            tempsSupervision: HEURES.map(h => Number(form[h])),
            commentaire: form.commentaires.trim(),
        },
    };
}

export function depuisDto(dto) {
    if (!dto) return null;
    const scores = dto.millieuStageScoresDTO ?? {};
    const obs = dto.obersvationGeneralDTO ?? {};
    const heures = scores.tempsSupervision ?? [];
    const quarts = (obs.quartsTravail ?? []).map(q => ({ de: q.heureDebut ?? "", a: q.heureFin ?? "" }));
    return {
        ...formulaireVide(),
        superviseur: { ...formulaireVide().superviseur, ...(dto.superviseurStageDTO ?? {}) },
        reponses: Object.fromEntries(CRITERES.map(c => [c.id, NIVEAU_PAR_SCORE[scores[c.dto]] ?? ""])),
        heuresMois1: heures.at(0) ?? "", heuresMois2: heures.at(1) ?? "", heuresMois3: heures.at(2) ?? "",
        commentaires: scores.commentaire ?? "",
        noStagePrivilegier: obs.noStagePrivilegier != null ? String(obs.noStagePrivilegier) : "",
        nbStagiaireAccueil: obs.nbStagiaireAccueil != null ? String(obs.nbStagiaireAccueil) : "",
        engagerMemeStagiaire: obs.engagerMemeStagiaire == null ? "" : (obs.engagerMemeStagiaire ? "OUI" : "NON"),
        quartsVariables: quarts.length > 0 ? "OUI" : "NON",
        quarts: [...quarts, ...QUARTS_VIDES].slice(0, 3),
    };
}