import {fetchApi, fetchJson} from "./http.jsx";
import {json} from "../../.vite/deps/react-router-dom.js";

export async function obtenirContratPDF(contratId, langue = "fr") {
    return fetchApi(`/pdf/contrat/${contratId}?langue=${langue}`, {
        method: "GET",
    });
}

export async function obtenirEvaluationStagiairePDF(candidatureId, langue = "fr") {
    return fetchApi(`/pdf/evaluation/etudiant/${candidatureId}?langue=${langue}`, {
        method: "GET",
    });
}
