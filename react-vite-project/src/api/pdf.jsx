import {fetchApi, fetchJson} from "./http.jsx";
import {json} from "../../.vite/deps/react-router-dom.js";

export async function obtenirContratPDF(contratId, langue = "fr") {
    return fetchApi(`/contrat/${contratId}?langue=${langue}`, {
        method: "GET",
    });
}

