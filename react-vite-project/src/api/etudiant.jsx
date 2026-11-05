import { fetchApi, fetchJson } from "./http.jsx";

export async function inscrireEtudiant(etudiant) {
  return fetchJson("/etudiant/inscription", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(etudiant)
  });
}

export async function televerserCV(file) {
  const formData = new FormData();
  formData.append("file", file);
  const token = getToken();
  return fetchJson("/etudiant/cv", {
    method: "POST",
    headers: {
      "Authorization": `Bearer ${token}`
    },
    body: formData
  });
}

export async function postulerOffre(offreId) {
  const token = getToken();
  return fetchJson(`/etudiant/offres/${offreId}/postuler`, {
    method: "POST",
    headers: {
      "Authorization": `Bearer ${token}`,
      "Content-Type": "application/json"
    },
  });
}

export async function obtenirCandidaturesEtudiant() {
  const token = getToken();
  return fetchJson(`/etudiant/mesCandidatures`, {
    method: "GET",
    headers: { "Authorization": `Bearer ${token}`}
    })
  }

export async function declarerStageTrouve(id) {
  return fetchJson(`/etudiant/candidatures/${id}/stage-trouve`, {
    method: "PUT",
    headers: { "Authorization": `Bearer ${getToken()}` },
  });
}

  export async function obtenirCvInfo(){
  const token = getToken();
    return fetchJson("/etudiant/cv", {
      method: "GET",
      headers: {
        "Authorization": `Bearer ${token}`
      },
    })
}

export async function obtenirOffres(departement) {
  const token = getToken();
  return fetchJson("/etudiant/offres/" + departement, {
    method: "GET",
    headers: { "Authorization": `Bearer ${token}` },
  });
}

export async function obtenirCvPDF(){
  const token = getToken();
  return fetch("http://localhost:8080/etudiant/cv/download", {
    method : "GET",
    headers: {
      "Authorization": `Bearer ${token}`
    },
  })
}

export async function suppressionCv(){
  return fetchApi("/etudiant/cv", {
    method: "DELETE",
  });
}

export async function obtenirToutesConvocations() {
    const token = getToken();
    return fetchJson("/etudiant/convocations", {
      method: "GET",
      headers: {
        "Authorization": `Bearer ${token}`
      },
    });
}
export async function obtenirConvocation(userId) {
  const token = getToken();
  return fetchJson(`/etudiant/convocations/${userId}`, {
    method: "GET",
    headers: {
      "Authorization": `Bearer ${token}`
    },
  });
}

export async function accepterConvocation(id) {
  const token = getToken();
  return fetchJson(`/etudiant/convocations/${id}/accepter`, {
    method: "PUT",
    headers: { "Authorization": `Bearer ${token}` },
  });
}

export async function refuserConvocation(id, motifRefus) {
  const token = getToken();
  return fetchJson(`/etudiant/convocations/${id}/refuser`, {
    method: "PUT",
    body: JSON.stringify({ motifRefus }),
    headers: { "Authorization": `Bearer ${token}` },
  });
}

function getToken(){
  const token = localStorage.getItem("token");
  if (!token) {
    const error = new Error("UNAUTHORIZED");
    error.status = 401;
    throw error;
  }
  return token;
}

export async function signerContratEtudiant(contratId) {
  return fetchJson(`/etudiant/contrats/${contratId}/signature`, {
    method: "PUT",
  });
}