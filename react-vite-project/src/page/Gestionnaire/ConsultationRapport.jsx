
import React, { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import {
    FiArrowLeft,
    FiSearch,
    FiDownload,
    FiFileText
} from "react-icons/fi";
import toast from "react-hot-toast";

import {
    obtenirDonneesRapport,
    genererRapportPdf
} from "../../api/rapport.jsx";

import { getDepartmentKey } from "../../utils/departementConverter.js";

import "../../css/ConsultationRapport.css";

const rapports = {
    "etudiants-inscrits": {
        dataEndpoint: "/gestionnaire/rapports/etudiants",
        pdfEndpoint: "/pdf/gestionnaire/rapports/etudiants",
        colonnes: [
            { labelKey: "firstName", champ: "firstName" },
            { labelKey: "lastName", champ: "lastName" },
            { labelKey: "matricule", champ: "matricule" },
            { labelKey: "email", champ: "email" },
            { labelKey: "department", champ: "department" },
            { labelKey: "status", champ: "statut" }
        ]
    },

    "etudiants-sans-cv": {
        dataEndpoint: "/gestionnaire/rapports/etudiants/sans-cv",
        pdfEndpoint: "/pdf/gestionnaire/rapports/etudiants/sans-cv",
        colonnes: [
            { labelKey: "firstName", champ: "firstName" },
            { labelKey: "lastName", champ: "lastName" },
            { labelKey: "matricule", champ: "matricule" },
            { labelKey: "email", champ: "email" },
            { labelKey: "department", champ: "department" },
            { labelKey: "status", champ: "statut" }
        ]
    },

    "cv-en-attente": {
        dataEndpoint: "/gestionnaire/rapports/cvs/en-attente",
        pdfEndpoint: "/pdf/gestionnaire/rapports/cv/en-attente",
        colonnes: [
            { labelKey: "student", champ: "etudiant", type: "etudiant" },
            { labelKey: "fileName", champ: "fileName" },
            { labelKey: "uploadDate", champ: "uploadDate", type: "date" },
            { labelKey: "size", champ: "fileSize", type: "taille" },
            { labelKey: "status", champ: "statut" }
        ]
    },

    "etudiants-sans-entrevue": {
        dataEndpoint: "/gestionnaire/rapports/etudiants/sans-entrevue",
        pdfEndpoint: "/pdf/gestionnaire/rapports/etudiants/sans-entrevue",
        colonnes: [
            { labelKey: "firstName", champ: "firstName" },
            { labelKey: "lastName", champ: "lastName" },
            { labelKey: "matricule", champ: "matricule" },
            { labelKey: "email", champ: "email" },
            { labelKey: "department", champ: "department" }
        ]
    },

    "etudiants-stage-trouve": {
        dataEndpoint: "/gestionnaire/rapports/etudiants/stage-trouve",
        pdfEndpoint: "/pdf/gestionnaire/rapports/etudiants/stage-trouve",
        colonnes: [
            { labelKey: "firstName", champ: "firstName" },
            { labelKey: "lastName", champ: "lastName" },
            { labelKey: "matricule", champ: "matricule" },
            { labelKey: "email", champ: "email" },
            { labelKey: "department", champ: "department" }
        ]
    },

    "offres-en-attente": {
        dataEndpoint: "/gestionnaire/rapports/offres/en-attente",
        pdfEndpoint: "/pdf/gestionnaire/rapports/offres/en-attente",
        colonnes: [
            { labelKey: "title", champ: "title" },
            { labelKey: "department", champ: "domain" },
            { labelKey: "salary", champ: "salary", type: "salaire" },
            { labelKey: "startDate", champ: "startDate", type: "date" },
            { labelKey: "endDate", champ: "endDate", type: "date" },
            { labelKey: "status", champ: "statut" }
        ]
    },

    "offres-approuvees": {
        dataEndpoint: "/gestionnaire/rapports/offres/validees",
        pdfEndpoint: "/pdf/gestionnaire/rapports/offres/approuvees",
        colonnes: [
            { labelKey: "title", champ: "title" },
            { labelKey: "department", champ: "domain" },
            { labelKey: "salary", champ: "salary", type: "salaire" },
            { labelKey: "startDate", champ: "startDate", type: "date" },
            { labelKey: "endDate", champ: "endDate", type: "date" },
            { labelKey: "status", champ: "statut" }
        ]
    },

    "candidatures-en-attente-entrevue": {
        dataEndpoint: "/gestionnaire/rapports/candidatures/en-attente-entrevue",
        pdfEndpoint: "/pdf/gestionnaire/rapports/candidatures/en-attente-entrevue",
        colonnes: [
            { labelKey: "student", champ: "etudiant", type: "nomCandidature" },
            { labelKey: "department", champ: "etudiantDepartment" },
            { labelKey: "offer", champ: "offreTitle" },
            { labelKey: "company", champ: "employeurBusinessName" },
            { labelKey: "applicationDate", champ: "dateCandidature", type: "date" },
            { labelKey: "status", champ: "statut" }
        ]
    },

    "candidatures-sans-evaluation": {
        dataEndpoint: "/gestionnaire/rapports/candidatures/sans-evaluation",
        pdfEndpoint: "/pdf/gestionnaire/rapports/candidatures/sans-evaluation",
        colonnes: [
            { labelKey: "student", champ: "etudiant", type: "nomCandidature" },
            { labelKey: "department", champ: "etudiantDepartment" },
            { labelKey: "offer", champ: "offreTitle" },
            { labelKey: "company", champ: "employeurBusinessName" },
            { labelKey: "applicationDate", champ: "dateCandidature", type: "date" },
            { labelKey: "status", champ: "statut" }
        ]
    },

    "candidatures-sans-evaluation-milieu-stage": {
        dataEndpoint: "/gestionnaire/rapports/candidatures/sans-evaluation-milieu-stage",
        pdfEndpoint: "/pdf/gestionnaire/rapports/candidatures/sans-evaluation-milieu-stage",
        colonnes: [
            { labelKey: "student", champ: "etudiant", type: "nomCandidature" },
            { labelKey: "department", champ: "etudiantDepartment" },
            { labelKey: "offer", champ: "offreTitle" },
            { labelKey: "company", champ: "employeurBusinessName" },
            { labelKey: "applicationDate", champ: "dateCandidature", type: "date" },
            { labelKey: "status", champ: "statut" }
        ]
    },

    "entrevues-en-attente-reponse": {
        dataEndpoint: "/gestionnaire/rapports/entrevues/en-attente-reponse",
        pdfEndpoint: "/pdf/gestionnaire/rapports/entrevues/en-attente-reponse",
        colonnes: [
            { labelKey: "student", champ: "etudiant", type: "nomEntrevue" },
            { labelKey: "offer", champ: "offreTitle" },
            { labelKey: "interviewDate", champ: "dateHeureEntrevue", type: "date" },
            { labelKey: "format", champ: "format" },
            { labelKey: "duration", champ: "duree", type: "duree" },
            { labelKey: "status", champ: "statut" }
        ]
    }
};

function traduireStatut(valeur, t, i18n) {
    if (!valeur) return "—";

    const cleEtudiant = `student_status.${valeur}.label`;

    if (i18n.exists(cleEtudiant)) {
        return t(cleEtudiant, {
            defaultValue: valeur
        });
    }

    const cleStatut = `status.${valeur.toLowerCase()}`;

    if (i18n.exists(cleStatut)) {
        return t(cleStatut, {
            defaultValue: valeur
        });
    }

    return valeur;
}

function afficherValeur(ligne, colonne, t, i18n, locale) {
    const valeur = ligne[colonne.champ];

    if (colonne.type === "etudiant") {
        return valeur
            ? `${valeur.firstName ?? ""} ${valeur.lastName ?? ""}`.trim()
            : "—";
    }

    if (
        colonne.type === "nomCandidature" ||
        colonne.type === "nomEntrevue"
    ) {
        return `${ligne.etudiantFirstName ?? ""} ${ligne.etudiantLastName ?? ""}`.trim() || "—";
    }

    if (colonne.type === "date") {
        return valeur
            ? new Date(valeur).toLocaleString(locale)
            : "—";
    }

    if (colonne.type === "salaire") {
        return valeur != null
            ? `${valeur} $`
            : "—";
    }

    if (colonne.type === "taille") {
        if (valeur == null) return "—";

        return valeur < 1024 * 1024
            ? `${(valeur / 1024).toFixed(1)} ${t("rapport.consultation.units.kb")}`
            : `${(valeur / (1024 * 1024)).toFixed(1)} ${t("rapport.consultation.units.mb")}`;
    }

    if (colonne.type === "duree") {
        return valeur != null
            ? `${valeur} ${t("rapport.consultation.units.minutes")}`
            : "—";
    }

    if (valeur === null || valeur === undefined || valeur === "") {
        return "—";
    }

    if (
        colonne.champ === "department" ||
        colonne.champ === "etudiantDepartment"
    ) {
        const departmentKey = getDepartmentKey(valeur);

        return departmentKey
            ? t(`departement.${departmentKey}`, {
                defaultValue: valeur
            })
            : valeur;
    }

    if (colonne.champ === "domain") {
        return t(`secteur.${valeur}`, {
            defaultValue: valeur
        });
    }

    if (colonne.champ === "statut") {
        return traduireStatut(valeur, t, i18n);
    }

    return typeof valeur === "object" ? "—" : String(valeur);
}

function ConsultationRapport() {
    const { id } = useParams();
    const navigate = useNavigate();
    const { t, i18n } = useTranslation();

    const rapport = rapports[id];

    const locale = i18n.language?.startsWith("en") ? "en-CA" : "fr-CA";

    const [donnees, setDonnees] = useState([]);
    const [recherche, setRecherche] = useState("");
    const [statutFiltre, setStatutFiltre] = useState("TOUS");
    const [chargement, setChargement] = useState(true);
    const [erreur, setErreur] = useState(false);

    const titreRapport = t(`rapport.items.${id}.title`);

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

    const traiterPdf = async () => {
        try {
            toast.loading(t("rapport.consultation.pdf_loading"), {
                id: "consultation-pdf"
            });

            const langue = i18n.language.startsWith("en") ? "en" : "fr";

            const response = await genererRapportPdf(
                `${rapport.pdfEndpoint}?langue=${langue}`
            );

            if (!response.ok) {
                throw new Error("Impossible de générer le PDF.");
            }

            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);

            const lien = document.createElement("a");
            lien.href = url;
            lien.download = `${id}.pdf`;

            document.body.appendChild(lien);
            lien.click();
            lien.remove();

            window.setTimeout(() => {
                window.URL.revokeObjectURL(url);
            }, 1000);

            toast.success(t("rapport.consultation.pdf_success"), {
                id: "consultation-pdf"
            });
        } catch (error) {
            console.error("Erreur PDF :", error);

            toast.error(t("rapport.consultation.pdf_error"), {
                id: "consultation-pdf"
            });
        }
    };

    if (!rapport) {
        return (
            <div className="p-8 text-center">
                <h1 className="text-xl font-bold text-slate-800">
                    {t("rapport.consultation.not_found")}
                </h1>

                <button
                    onClick={() => navigate("/gestionnaire/rapports")}
                    className="mt-4 rounded-lg bg-[#60DEDE] px-4 py-2 font-semibold text-[#043462]"
                >
                    {t("rapport.consultation.back")}
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
                {t("rapport.consultation.back")}
            </button>

            <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4 border-b border-slate-200 pb-5">
                <div className="flex items-center gap-3">
                    <div className="rounded-xl bg-blue-50 p-3 text-blue-600">
                        <FiFileText className="size-7" />
                    </div>

                    <div>
                        <h1 className="text-2xl font-bold text-slate-800">
                            {titreRapport}
                        </h1>

                        <p className="text-sm text-slate-500">
                            {t("rapport.consultation.subtitle")}
                        </p>
                    </div>
                </div>

                <div className="flex gap-2">
                    <button
                        onClick={traiterPdf}
                        className="rapport-consultation-button rapport-consultation-pdf"
                    >
                        <FiDownload />
                        {t("rapport.actions.download")}
                    </button>
                </div>
            </div>

            <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-3">
                <div className="rapport-total">
                    {t("rapport.consultation.result_count", {
                        count: donneesFiltrees.length
                    })}

                    {donneesFiltrees.length !== donnees.length &&
                        ` ${t("rapport.consultation.out_of", {
                            count: donnees.length
                        })}`}
                </div>

                <div className="flex flex-col sm:flex-row gap-3">
                    <div className="relative">
                        <FiSearch className="absolute left-3 top-1/2 -translate-y-1/2 text-slate-400" />

                        <input
                            type="text"
                            value={recherche}
                            onChange={(event) => setRecherche(event.target.value)}
                            placeholder={t("rapport.consultation.search")}
                            className="rapport-search"
                        />
                    </div>

                    {statuts.length > 0 && (
                        <select
                            value={statutFiltre}
                            onChange={(event) => setStatutFiltre(event.target.value)}
                            className="rapport-search"
                        >
                            <option value="TOUS">
                                {t("rapport.consultation.all_statuses")}
                            </option>

                            {statuts.map((statut) => (
                                <option key={statut} value={statut}>
                                    {traduireStatut(statut, t, i18n)}
                                </option>
                            ))}
                        </select>
                    )}
                </div>
            </div>

            <div className="overflow-x-auto rounded-xl border border-slate-200 bg-white shadow-sm">
                {chargement ? (
                    <div className="p-10 text-center text-slate-500">
                        {t("rapport.consultation.loading")}
                    </div>
                ) : erreur ? (
                    <div className="p-10 text-center text-red-600">
                        {t("rapport.consultation.error")}
                    </div>
                ) : donneesFiltrees.length === 0 ? (
                    <div className="p-10 text-center text-slate-500">
                        {t("rapport.consultation.empty")}
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
                                        {t(`rapport.consultation.columns.${colonne.labelKey}`)}
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
                                            {afficherValeur(
                                                ligne,
                                                colonne,
                                                t,
                                                i18n,
                                                locale
                                            )}
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
