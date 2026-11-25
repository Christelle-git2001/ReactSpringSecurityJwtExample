
import { fetchApi, fetchJson } from "./http.jsx";

export async function genererRapportPdf(endpoint) {
    return fetchApi(endpoint, {
        method: "GET"
    });
}

export async function obtenirDonneesRapport(endpoint) {
    return fetchJson(endpoint, {
        method: "GET"
    });
}
