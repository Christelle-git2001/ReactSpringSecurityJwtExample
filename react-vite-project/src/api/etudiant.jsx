import { fetchJson } from "./http.jsx";

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
  const token = getToken();
  return fetch("http://localhost:8080/etudiant/cv", {
    method : "DELETE",
    headers: {
      "Authorization": `Bearer ${token}`
    },
  })
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

