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

export async function getCandidaturesRetenuesPourEntrevue() {
    return fetchJson("/employeur/candidatures/convoquees", {
        method: "GET",
    });
}

export async function PlanifierEntrevue(dto) {
    return fetchJson("/employeur/entrevues", {
        method: "POST",
        body: JSON.stringify(dto),
    });
}

export async function getEntrevuesEmployeur() {
    return fetchJson("/employeur/entrevues", {
        method: "GET",
    });
}

export async function retenirCandidaturePourEntrevue(candidatureId) {
    return fetchJson(`/employeur/candidatures/${candidatureId}`, {
        method: "PUT",
    });
}

export async function getCandidaturesEmployeur() {
    return fetchJson("/employeur/candidatures", {
        method: "GET",
    });
}

export async function getCandidaturesOffre(id) {
    return fetchJson(`/employeur/offres/${id}/candidatures`, {
        method: "GET",
        headers: headers,
    });
}

export async function modifierOffreEmployeur(id, dto, file) {
    const formData = new FormData();

    formData.append(
        "offre",
        new Blob([JSON.stringify(dto)], { type: "application/json" })
    );

    if (file) {
        formData.append("file", file);
    }

    return fetchJson(`/employeur/offres/${id}`, {
        method: "PUT",
        body: formData,
    });
}

export async function obtenirOffrePDF(id) {
    const token = localStorage.getItem("token");
    const headers = {};

    if (token) {
        headers["Authorization"] = `Bearer ${token}`;
    }

    return fetch(`http://localhost:8080/employeur/offres/${id}/download`, {
        method: "GET",
        headers: headers,
    });
}