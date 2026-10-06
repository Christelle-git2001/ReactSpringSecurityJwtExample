import BASE_URL from "../config/Config.jsx";

export async function fetchJson(path, options = {}) {
    const res = await fetchApi(path, options);
    return res.json();
}

export async function fetchApi(path, options = {}) {
    try {
        const cleanPath = path.startsWith('/') ? path : `/${path}`;
        const cleanBaseUrl = BASE_URL.endsWith('/') ? BASE_URL.slice(0, -1) : BASE_URL;
        const token = localStorage.getItem("token");
        const headers = {
            ...(options.headers || {}),
        };

        if (token) {
            headers["Authorization"] = `Bearer ${token}`;
        }
        if (!(options.body instanceof FormData) && !headers["Content-Type"]) {
            headers["Content-Type"] = "application/json";
        }

        const res = await fetch(`${cleanBaseUrl}${cleanPath}`, {
            ...options,
            headers,
        });

        if (!res.ok) {
            await manageError(res);
        }

        return res;
    } catch (error) {
        if (error instanceof TypeError && error.message.includes("fetch")) {
            throw new Error("Impossible de se connecter au serveur.");
        }
        throw error;
    }
}

async function manageError(res) {
    const errorData = await res.json().catch(() => ({ message: res.statusText }));

    const message = errorData?.message || `Erreur HTTP. Statut : ${res.status}`;
    const error = new Error(message);
    error.status = res.status;
    error.data = errorData;

    throw error;
}

export async function getDepartements() {
    return await fetchJson("/gestionnaire/departement");
}

export async function getSecteursEmployeur() {
    return fetchJson("/gestionnaire/secteurEmployeur");
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
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            rejectionComment: commentText
        }),
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