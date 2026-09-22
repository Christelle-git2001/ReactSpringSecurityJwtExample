import { fetchJson } from "./http.jsx";

export async function inscrireEmployeur(employeur) {
    return fetchJson("/employeur/inscription", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(employeur),
    });
}