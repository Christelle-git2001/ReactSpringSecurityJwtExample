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
