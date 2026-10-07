import { fetchJson } from "./http.jsx";


export async function getDepartements() {
    return await fetchJson("/gestionnaire/departement");
}

export async function getSecteursEmployeur() {
    return fetchJson("/gestionnaire/secteurEmployeur");
}

export async function getOffresEnAttente() {
    return fetchJson("/gestionnaire/offres", {
        method: "GET",
    });
}

export async function approuverOffre(id) {
    return fetchJson(`/gestionnaire/offres/${id}/approuver`, {
        method: "PUT",
    });
}

export async function refuserOffre(id, commentText) {
    return fetchJson(`/gestionnaire/offres/${id}/refuser`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            rejectionComment: commentText
        }),
    });
}


export async function getCvsEnAttente() {
    return fetchJson("/gestionnaire/cvs/attente");
}

export async function getTousLesCvs() {
    return fetchJson("/gestionnaire/cvs");
}

export async function approuverCv(id, commentText) {
    return fetchJson(`/gestionnaire/cv/${id}/approuver`, {
        method: "PUT",
    });
}

export async function refuserCv(id, commentText) {
    return fetchJson(`/gestionnaire/cv/${id}/refuser`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            rejectionComment : commentText
        }),
    });
}

export async function obtenirCvPDFGestionnaire(id) {
    return fetchApi(`/gestionnaire/cv/${id}/download`, {
        method: "GET",
    });
}