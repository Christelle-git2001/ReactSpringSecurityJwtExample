
import React, { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
    FiArrowLeft,
    FiSearch,
    FiEye,
    FiDownload,
    FiFileText
} from "react-icons/fi";
import toast from "react-hot-toast";

import {
    obtenirDonneesRapport,
    genererRapportPdf
} from "../../api/rapport.jsx";

import "../../css/ConsultationRapport.css";

const rapports = {
    "etudiants-inscrits": {
        titre: "Étudiants inscrits",
        dataEndpoint: "/gestionnaire/rapports/etudiants",
        pdfEndpoint: "/pdf/gestionnaire/rapports/etudiants",
        colonnes: [
            { label: "Prénom", champ: "firstName" },
            { label: "Nom", champ: "lastName" },
            { label: "Matricule", champ: "matricule" },
            { label: "Courriel", champ: "email" },
            { label: "Département", champ: "department" },
            { label: "Statut", champ: "statut" }
        ]
    },
    "etudiants-sans-cv": {
        titre: "Étudiants sans CV",
        dataEndpoint: "/gestionnaire/rapports/etudiants/sans-cv",
        pdfEndpoint: "/pdf/gestionnaire/rapports/etudiants/sans-cv",
        colonnes: [
            { label: "Prénom", champ: "firstName" },
            { label: "Nom", champ: "lastName" },
            { label: "Matricule", champ: "matricule" },
            { label: "Courriel", champ: "email" },
            { label: "Département", champ: "department" },
            { label: "Statut", champ: "statut" }
        ]
    },
    "cv-en-attente": {
        titre: "CV en attente de validation",
        dataEndpoint: "/gestionnaire/rapports/cvs/en-attente",
        pdfEndpoint: "/pdf/gestionnaire/rapports/cv/en-attente",
        colonnes: [
            { label: "Étudiant", champ: "etudiant", type: "etudiant" },
            { label: "Nom du fichier", champ: "fileName" },
            { label: "Date de dépôt", champ: "uploadDate", type: "date" },
            { label: "Taille", champ: "fileSize", type: "taille" },
            { label: "Statut", champ: "statut" }
        ]
    },
    "etudiants-sans-entrevue": {
        titre: "Étudiants sans entrevue",
        dataEndpoint: "/gestionnaire/rapports/etudiants/sans-entrevue",
        pdfEndpoint: "/pdf/gestionnaire/rapports/etudiants/sans-entrevue",
        colonnes: [
            { label: "Prénom", champ: "firstName" },
            { label: "Nom", champ: "lastName" },
            { label: "Matricule", champ: "matricule" },
            { label: "Courriel", champ: "email" },
            { label: "Département", champ: "department" }
        ]
    },
    "etudiants-stage-trouve": {
        titre: "Étudiants ayant trouvé un stage",
        dataEndpoint: "/gestionnaire/rapports/etudiants/stage-trouve",
        pdfEndpoint: "/pdf/gestionnaire/rapports/etudiants/stage-trouve",
        colonnes: [
            { label: "Prénom", champ: "firstName" },
            { label: "Nom", champ: "lastName" },
            { label: "Matricule", champ: "matricule" },
            { label: "Courriel", champ: "email" },
            { label: "Département", champ: "department" }
        ]
    },
    "offres-en-attente": {
        titre: "Offres en attente de validation",
        dataEndpoint: "/gestionnaire/rapports/offres/en-attente",
        pdfEndpoint: "/pdf/gestionnaire/rapports/offres/en-attente",
        colonnes: [
            { label: "Titre", champ: "title" },
            { label: "Département", champ: "domain" },
            { label: "Salaire", champ: "salary", type: "salaire" },
            { label: "Date de début", champ: "startDate", type: "date" },
            { label: "Date de fin", champ: "endDate", type: "date" },
            { label: "Statut", champ: "statut" }
        ]
    },
    "offres-approuvees": {
        titre: "Offres validées",
        dataEndpoint: "/gestionnaire/rapports/offres/validees",
        pdfEndpoint: "/pdf/gestionnaire/rapports/offres/approuvees",
        colonnes: [
            { label: "Titre", champ: "title" },
            { label: "Département", champ: "domain" },
            { label: "Salaire", champ: "salary", type: "salaire" },
            { label: "Date de début", champ: "startDate", type: "date" },
            { label: "Date de fin", champ: "endDate", type: "date" },
            { label: "Statut", champ: "statut" }
        ]
    },
    "candidatures-en-attente-entrevue": {
        titre: "Candidatures en attente d'entrevue",
        dataEndpoint: "/gestionnaire/rapports/candidatures/en-attente-entrevue",
        pdfEndpoint: "/pdf/gestionnaire/rapports/candidatures/en-attente-entrevue",
        colonnes: [
            { label: "Étudiant", champ: "etudiant", type: "nomCandidature" },
            { label: "Département", champ: "etudiantDepartment" },
            { label: "Offre", champ: "offreTitle" },
            { label: "Entreprise", champ: "employeurBusinessName" },
            { label: "Date de candidature", champ: "dateCandidature", type: "date" },
            { label: "Statut", champ: "statut" }
        ]
    },
    "candidatures-sans-evaluation": {
        titre: "Candidatures sans évaluation du superviseur",
        dataEndpoint: "/gestionnaire/rapports/candidatures/sans-evaluation",
        pdfEndpoint: "/pdf/gestionnaire/rapports/candidatures/sans-evaluation",
        colonnes: [
            { label: "Étudiant", champ: "etudiant", type: "nomCandidature" },
            { label: "Département", champ: "etudiantDepartment" },
            { label: "Offre", champ: "offreTitle" },
            { label: "Entreprise", champ: "employeurBusinessName" },
            { label: "Date de candidature", champ: "dateCandidature", type: "date" },
            { label: "Statut", champ: "statut" }
        ]
    },
    "candidatures-sans-evaluation-milieu-stage": {
        titre: "Candidatures sans évaluation du milieu de stage",
        dataEndpoint: "/gestionnaire/rapports/candidatures/sans-evaluation-milieu-stage",
        pdfEndpoint: "/pdf/gestionnaire/rapports/candidatures/sans-evaluation-milieu-stage",
        colonnes: [
            { label: "Étudiant", champ: "etudiant", type: "nomCandidature" },
            { label: "Département", champ: "etudiantDepartment" },
            { label: "Offre", champ: "offreTitle" },
            { label: "Entreprise", champ: "employeurBusinessName" },
            { label: "Date de candidature", champ: "dateCandidature", type: "date" },
            { label: "Statut", champ: "statut" }
        ]
    },
    "entrevues-en-attente-reponse": {
        titre: "Entrevues en attente de réponse",
        dataEndpoint: "/gestionnaire/rapports/entrevues/en-attente-reponse",
        pdfEndpoint: "/pdf/gestionnaire/rapports/entrevues/en-attente-reponse",
        colonnes: [
            { label: "Étudiant", champ: "etudiant", type: "nomEntrevue" },
            { label: "Offre", champ: "offreTitle" },
            { label: "Date de l'entrevue", champ: "dateHeureEntrevue", type: "date" },
            { label: "Format", champ: "format" },
            { label: "Durée", champ: "duree", type: "duree" },
            { label: "Statut", champ: "statut" }
        ]
    }
};

function afficherValeur(ligne, colonne) {
    const valeur = ligne[colonne.champ];

    if (colonne.type === "etudiant") {
        return valeur
            ? `${valeur.firstName ?? ""} ${valeur.lastName ?? ""}`.trim()
            : "—";
    }

    if (colonne.type === "nomCandidature") {
        return `${ligne.etudiantFirstName ?? ""} ${ligne.etudiantLastName ?? ""}`.trim() || "—";
    }

    if (colonne.type === "nomEntrevue") {
        return `${ligne.etudiantFirstName ?? ""} ${ligne.etudiantLastName ?? ""}`.trim() || "—";
    }

    if (colonne.type === "date") {
        return valeur
            ? new Date(valeur).toLocaleString("fr-CA")
            : "—";
    }

    if (colonne.type === "salaire") {
        return valeur != null ? `${valeur} $` : "—";
    }

    if (colonne.type === "taille") {
        if (valeur == null) return "—";
        return valeur < 1024 * 1024
            ? `${(valeur / 1024).toFixed(1)} Ko`
            : `${(valeur / (1024 * 1024)).toFixed(1)} Mo`;
    }

    if (colonne.type === "duree") {
        return valeur != null ? `${valeur} min` : "—";
    }

    if (valeur === null || valeur === undefined || valeur === "") {
        return "—";
    }

    return typeof valeur === "object" ? "—" : String(valeur);
}

function ConsultationRapport() {
    const { id } = useParams();
    const navigate = useNavigate();

    const rapport = rapports[id];

    const [donnees, setDonnees] = useState([]);
    const [recherche, setRecherche] = useState("");
    const [statutFiltre, setStatutFiltre] = useState("TOUS");
    const [chargement, setChargement] = useState(true);
    const [erreur, setErreur] = useState(false);

    useEffect(() => {
        let actif = true;

        const chargerRapport = async () => {
            if (!rapport) {
                setChargement(false);
                return;
            }

            setChargement(true);
            setErreur(false);

            try {
                const resultat = await obtenirDonneesRapport(
                    rapport.dataEndpoint
                );

                if (actif) {
                    setDonnees(Array.isArray(resultat) ? resultat : []);
                }
            } catch (error) {
                console.error("Erreur de chargement du rapport :", error);

                if (actif) {
                    setErreur(true);
                }
            } finally {
                if (actif) {
                    setChargement(false);
                }
            }
        };

        chargerRapport();

        return () => {
            actif = false;
        };
    }, [id]);

    const statuts = useMemo(() => {
        return [...new Set(
            donnees
                .map((ligne) => ligne.statut)
                .filter(Boolean)
        )];
    }, [donnees]);

    const donneesFiltrees = useMemo(() => {
        return donnees.filter((ligne) => {
            const texte = JSON.stringify(ligne).toLowerCase();

            const correspondRecherche = texte.includes(
                recherche.toLowerCase()
            );

            const correspondStatut =
                statutFiltre === "TOUS" ||
                ligne.statut === statutFiltre;

            return correspondRecherche && correspondStatut;
        });
    }, [donnees, recherche, statutFiltre]);

    const traiterPdf = async (telecharger) => {
        try {
            toast.loading(
                telecharger ? "Préparation du téléchargement..." : "Préparation de l'aperçu...",
                { id: "consultation-pdf" }
            );

            const fenetre = telecharger ? null : window.open("", "_blank");

            if (!telecharger && !fenetre) {
                toast.error("Autorise les fenêtres pop-up.", {
                    id: "consultation-pdf"
                });
                return;
            }

            const response = await genererRapportPdf(rapport.pdfEndpoint);
            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);

            if (telecharger) {
                const lien = document.createElement("a");
                lien.href = url;
                lien.download = `${id}.pdf`;
                document.body.appendChild(lien);
                lien.click();
                lien.remove();

                window.setTimeout(() => {
                    window.URL.revokeObjectURL(url);
                }, 1000);
            } else {
                fenetre.location.href = url;
            }

            toast.success(
                telecharger ? "PDF téléchargé." : "Aperçu PDF prêt.",
                { id: "consultation-pdf" }
            );
        } catch (error) {
            console.error("Erreur PDF :", error);
            toast.error("Impossible de traiter le PDF.", {
                id: "consultation-pdf"
            });
        }
    };

    if (!rapport) {
        return (
            <div className="p-8 text-center">
                <h1 className="text-xl font-bold text-slate-800">
                    Rapport introuvable
                </h1>
                <button
                    onClick={() => navigate("/gestionnaire/rapports")}
                    className="mt-4 rounded-lg bg-[#60DEDE] px-4 py-2 font-semibold text-[#043462]"
                >
                    Retour aux rapports
                </button>
            </div>
        );
    }

    return (
        <div className="consultation-rapport w-full max-w-7xl mx-auto px-3 md:px-5 py-5 space-y-5">

            <button
                onClick={() => navigate("/gestionnaire/rapports")}
                className="flex items-center gap-2 text-sm font-semibold text-[#043462] hover:text-blue-600"
            >
                <FiArrowLeft />
                Retour aux rapports
            </button>

            <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4 border-b border-slate-200 pb-5">
                <div className="flex items-center gap-3">
                    <div className="rounded-xl bg-blue-50 p-3 text-blue-600">
                        <FiFileText className="size-7" />
                    </div>

                    <div>
                        <h1 className="text-2xl font-bold text-slate-800">
                            {rapport.titre}
                        </h1>
                        <p className="text-sm text-slate-500">
                            Consultation des données du rapport
                        </p>
                    </div>
                </div>

                <div className="flex gap-2">
                    <button
                        onClick={() => traiterPdf(false)}
                        className="rapport-consultation-button"
                    >
                        <FiEye />
                        Aperçu PDF
                    </button>

                    <button
                        onClick={() => traiterPdf(true)}
                        className="rapport-consultation-button rapport-consultation-pdf"
                    >
                        <FiDownload />
                        Télécharger PDF
                    </button>
                </div>
            </div>

            <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
                <div className="rapport-total">
                    {donneesFiltrees.length} résultat(s)
                    {donneesFiltrees.length !== donnees.length &&
                        ` sur ${donnees.length}`}
                </div>

                <div className="flex flex-col sm:flex-row gap-3">
                    <div className="relative">
                        <FiSearch className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />
                        <input
                            type="text"
                            value={recherche}
                            onChange={(event) => setRecherche(event.target.value)}
                            placeholder="Rechercher..."
                            className="rapport-search"
                        />
                    </div>

                    {statuts.length > 0 && (
                        <select
                            value={statutFiltre}
                            onChange={(event) => setStatutFiltre(event.target.value)}
                            className="rapport-search"
                        >
                            <option value="TOUS">Tous les statuts</option>
                            {statuts.map((statut) => (
                                <option key={statut} value={statut}>
                                    {statut}
                                </option>
                            ))}
                        </select>
                    )}
                </div>
            </div>

            <div className="overflow-x-auto rounded-xl border border-slate-200 bg-white shadow-sm">
                {chargement ? (
                    <div className="p-10 text-center text-slate-500">
                        Chargement du rapport...
                    </div>
                ) : erreur ? (
                    <div className="p-10 text-center text-red-600">
                        Impossible de charger les données du rapport.
                    </div>
                ) : donneesFiltrees.length === 0 ? (
                    <div className="p-10 text-center text-slate-500">
                        Aucun résultat à afficher.
                    </div>
                ) : (
                    <table className="w-full text-sm text-left">
                        <thead className="bg-slate-50 text-slate-600">
                            <tr>
                                {rapport.colonnes.map((colonne) => (
                                    <th
                                        key={colonne.champ}
                                        className="px-5 py-4 font-semibold whitespace-nowrap"
                                    >
                                        {colonne.label}
                                    </th>
                                ))}
                            </tr>
                        </thead>

                        <tbody className="divide-y divide-slate-100">
                            {donneesFiltrees.map((ligne, index) => (
                                <tr
                                    key={ligne.id ?? index}
                                    className="hover:bg-slate-50 transition-colors"
                                >
                                    {rapport.colonnes.map((colonne) => (
                                        <td
                                            key={colonne.champ}
                                            className="px-5 py-4 text-slate-700 whitespace-nowrap"
                                        >
                                            {afficherValeur(ligne, colonne)}
                                        </td>
                                    ))}
                                </tr>
                            ))}
                        </tbody>
                    </table>
                )}
            </div>

        </div>
    );
}

export default ConsultationRapport;
