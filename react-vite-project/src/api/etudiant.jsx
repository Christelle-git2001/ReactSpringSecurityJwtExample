import { fetchJson } from "./http.jsx";

export async function inscrireEtudiant(etudiant) {
  return fetchJson("/etudiant/inscription", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(etudiant)
  });
}

