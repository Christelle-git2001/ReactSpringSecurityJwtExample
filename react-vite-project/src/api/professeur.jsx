import { fetchJson } from "./http.jsx";

export function inscrireProfesseur(professeur) {
    return fetchJson("/professeur/inscription", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(professeur)
    });
}

export function getEtudiants(){
    return fetchJson("/professeur/etudiants", {
        method : "GET"
    })
}

export function getCandidatureParEtudidant(etudiantId){
    return fetchJson("/professeur/etudiants/" + etudiantId + "/candidatures", {
        method : "GET"
    }
    )
}

export function getResumeOffre(offreId){
    return fetchJson("/professeur/offre/" + offreId,
        {
            method:"GET"
        })
}

export async function soumettreEvaluationMilieuStage(candidatureId, evaluation) {
    return fetchJson("/professeur/evaluation/stage", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ ...evaluation, candidatureId }),
    });
}
