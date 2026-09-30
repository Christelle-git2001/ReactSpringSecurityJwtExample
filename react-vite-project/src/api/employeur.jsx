import { fetchJson } from "./http.jsx";

export async function inscrireEmployeur(employeur) {
    return fetchJson("/employeur/inscription", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(employeur),
    });
}

export async function creerOffreEmployeur(dto, file) {
    const formData = new FormData();
    formData.append(
        "offre",
        new Blob([JSON.stringify(dto)], { type: "application/json" })
    );
    if (file) {
        formData.append("file", file);
    }
    return fetchJson("/employeur/offres", {
        method: "POST",
        body: formData,
    });
}

export async function getOffresEmployeur() {
    return fetchJson("/employeur/offres", {
        method: "GET",
    });
}