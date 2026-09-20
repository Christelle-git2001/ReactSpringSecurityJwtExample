const BASE_URL = "http://localhost:8080";

export async function inscrireEtudiant(etudiant) {
    return fetchJson("/etudiant/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(etudiant)
    });
}

async function fetchJson(path, options = {}) {
    const res = await fetchApi(path, options);
    return res.json();
}

async function fetchApi(path, options = {}) {
    try {
        const res = await fetch(`${BASE_URL}${path}`, options);

        if (!res.ok) {
            await manageError(res);
        }

        return res;
    } catch (error) {
        if (error instanceof TypeError) {
            console.error("Erreur reseau lors de l'inscription de l'etudiant :", error);
            throw new Error("Impossible de se connecter au serveur.");
        }

        throw error;
    }
}

async function manageError(res) {
    let errorData;

    try {
        errorData = await res.json();
    } catch {
        errorData = { message: res.statusText };
    }

    const message = errorData?.message || `Erreur HTTP. Statut : ${res.status}`;
    const error = new Error(message);
    error.status = res.status;
    error.data = errorData;

    throw error;
}