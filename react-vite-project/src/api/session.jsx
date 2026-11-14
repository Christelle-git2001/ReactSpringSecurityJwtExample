import {fetchApi, fetchJson} from "./http.jsx";

export async function getSessions() {
    return await fetchJson("/session/disponibles");
}

export async function getSessionActuelle() {
    return await fetchJson("/session/actuelle", { ignoreSession: true });
}