import BASE_URL from "../config/Config.jsx";

export async function fetchJson(path, options = {}) {
    const res = await fetchApi(path, options);
    return res.json();
}

export async function fetchApi(path, options = {}) {
    try {

        const cleanPath = path.startsWith('/') ? path : `/${path}`;
        const cleanBaseUrl = BASE_URL.endsWith('/') ? BASE_URL.slice(0, -1) : BASE_URL;

        const res = await fetch(`${cleanBaseUrl}${cleanPath}`, options);

        if (!res.ok) {
            await manageError(res);
        }

        return res;
    } catch (error) {
        if (error instanceof TypeError) {
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
