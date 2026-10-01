import { fetchJson } from "./http.jsx";
import {useTranslation} from "react-i18next";

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

export async function obtenirCvPDF(id){
  const token = getToken();
  return fetchJson("/etudiant/cv/" + id, {
    method : "GET",
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

