import {fetchApi, fetchJson} from "./http.jsx";
import {json} from "../../.vite/deps/react-router-dom.js";


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

export async function getToutesLesOffres() {
    return fetchJson("/gestionnaire/offres/toutes", {
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

export function obtenirCvGestionnaire(apiPath) {
    return fetchJson(apiPath);
}

export function obtenirOffreGestionnaire(apiPath) {
    return fetchJson(apiPath,
        {method: "GET"});
}

export async function approuverCv(id) {
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

export async function creerOffreCibleeGestionnaire(dto, file) {
    const formData = new FormData();

    formData.append(
        "offre",
        new Blob([JSON.stringify(dto)], { type: "application/json" })
    );

    if (file) {
        formData.append("file", file);
    }

    return fetchJson("/gestionnaire/offres", {
        method: "POST",
        body: formData,
    });
}

export async function getEmployeurs() {
    return fetchJson("/gestionnaire/employeurs", {
        method: "GET",
    });
}

export async function getEtudiants() {
    return fetchJson("/gestionnaire/etudiants", {
        method: "GET",
    });
}

export async function getProfesseurs(){
    return fetchJson("/gestionnaire/professeurs",
        {method : "GET"
        });
}

export async function assignStudentToTeacher(etudiantId,teacherId){
    if (!teacherId || !etudiantId || etudiantId.length === 0) {
        return;
    }
    return fetchJson("/gestionnaire/etudiant/" + teacherId,
        {
            method : "PUT",
            body : JSON.stringify(etudiantId)
        })
}
