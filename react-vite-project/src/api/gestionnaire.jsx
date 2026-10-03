import { fetchJson } from "./http.jsx";

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