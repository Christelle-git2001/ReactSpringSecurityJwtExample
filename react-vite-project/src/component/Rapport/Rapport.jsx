
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import {
    FiBriefcase,
    FiUsers,
    FiClipboard,
    FiCalendar,
    FiBarChart2
} from "react-icons/fi";
import toast from "react-hot-toast";

import {
    genererRapportPdf,
    obtenirDonneesRapport
} from "../../api/rapport.jsx";

import CategorieRapport from "./CategorieRapport.jsx";
import "../../css/Rapport.css";

const categories = [
    {
        id: "etudiants",
        titreKey: "rapport.categories.students",
        descriptionKey: "rapport.category_descriptions.students",
        Icon: FiUsers,
        couleur: "bg-emerald-50/80",
        iconeCouleur: "bg-emerald-100 text-emerald-600",
        rapports: [
            {
                id: "etudiants-inscrits",
                dataEndpoint: "/gestionnaire/rapports/etudiants",
                pdfEndpoint: "/pdf/gestionnaire/rapports/etudiants"
            },
            {
                id: "etudiants-sans-cv",
                dataEndpoint: "/gestionnaire/rapports/etudiants/sans-cv",
                pdfEndpoint: "/pdf/gestionnaire/rapports/etudiants/sans-cv"
            },
            {
                id: "cv-en-attente",
                dataEndpoint: "/gestionnaire/rapports/cvs/en-attente",
                pdfEndpoint: "/pdf/gestionnaire/rapports/cv/en-attente"
            },
            {
                id: "etudiants-sans-entrevue",
                dataEndpoint: "/gestionnaire/rapports/etudiants/sans-entrevue",
                pdfEndpoint: "/pdf/gestionnaire/rapports/etudiants/sans-entrevue"
            },
            {
                id: "etudiants-stage-trouve",
                dataEndpoint: "/gestionnaire/rapports/etudiants/stage-trouve",
                pdfEndpoint: "/pdf/gestionnaire/rapports/etudiants/stage-trouve"
            }
        ]
    },
    {
        id: "offres",
        titreKey: "rapport.categories.offers",
        descriptionKey: "rapport.category_descriptions.offers",
        Icon: FiBriefcase,
        couleur: "bg-blue-50/80",
        iconeCouleur: "bg-blue-100 text-blue-600",
        rapports: [
            {
                id: "offres-en-attente",
                dataEndpoint: "/gestionnaire/rapports/offres/en-attente",
                pdfEndpoint: "/pdf/gestionnaire/rapports/offres/en-attente"
            },
            {
                id: "offres-approuvees",
                dataEndpoint: "/gestionnaire/rapports/offres/validees",
                pdfEndpoint: "/pdf/gestionnaire/rapports/offres/approuvees"
            }
        ]
    },
    {
        id: "candidatures",
        titreKey: "rapport.categories.applications",
        descriptionKey: "rapport.category_descriptions.applications",
        Icon: FiClipboard,
        couleur: "bg-amber-50/80",
        iconeCouleur: "bg-amber-100 text-amber-600",
        rapports: [
            {
                id: "candidatures-en-attente-entrevue",
                dataEndpoint: "/gestionnaire/rapports/candidatures/en-attente-entrevue",
                pdfEndpoint: "/pdf/gestionnaire/rapports/candidatures/en-attente-entrevue"
            },
            {
                id: "candidatures-sans-evaluation",
                dataEndpoint: "/gestionnaire/rapports/candidatures/sans-evaluation",
                pdfEndpoint: "/pdf/gestionnaire/rapports/candidatures/sans-evaluation"
            },
            {
                id: "candidatures-sans-evaluation-milieu-stage",
                dataEndpoint: "/gestionnaire/rapports/candidatures/sans-evaluation-milieu-stage",
                pdfEndpoint: "/pdf/gestionnaire/rapports/candidatures/sans-evaluation-milieu-stage"
            }
        ]
    },
    {
        id: "entrevues",
        titreKey: "rapport.categories.interviews",
        descriptionKey: "rapport.category_descriptions.interviews",
        Icon: FiCalendar,
        couleur: "bg-rose-50/80",
        iconeCouleur: "bg-rose-100 text-rose-600",
        rapports: [
            {
                id: "entrevues-en-attente-reponse",
                dataEndpoint: "/gestionnaire/rapports/entrevues/en-attente-reponse",
                pdfEndpoint: "/pdf/gestionnaire/rapports/entrevues/en-attente-reponse"
            }
        ]
    }
];

function Rapport() {
    const navigate = useNavigate();
    const { t, i18n } = useTranslation();

    const [compteurs, setCompteurs] = useState({});

    useEffect(() => {
        let actif = true;

        const chargerCompteurs = async () => {
            const requetes = categories.flatMap((categorie) =>
                categorie.rapports.map(async (rapport) => {
                    try {
                        const donnees = await obtenirDonneesRapport(
                            rapport.dataEndpoint
                        );

                        return [rapport.id, donnees?.length ?? 0];
                    } catch (error) {
                        console.error(
                            `Erreur de chargement du compteur ${rapport.id}:`,
                            error
                        );

                        return [rapport.id, null];
                    }
                })
            );

            const resultats = await Promise.all(requetes);

            if (actif) {
                setCompteurs(Object.fromEntries(resultats));
            }
        };

        chargerCompteurs();

        return () => {
            actif = false;
        };
    }, []);

    const traduireRapport = (rapport) => ({
        ...rapport,
        titre: t(`rapport.items.${rapport.id}.title`),
        description: t(`rapport.items.${rapport.id}.description`)
    });

    const consulterRapport = (rapport) => {
        navigate(`/gestionnaire/rapports/${rapport.id}`);
    };

    const telechargerPdf = async (rapport) => {
        try {
            toast.loading(t("rapport.pdf_loading"), {
                id: "rapport-pdf"
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
            lien.download = `${rapport.id}.pdf`;

            document.body.appendChild(lien);
            lien.click();
            lien.remove();

            window.setTimeout(() => {
                window.URL.revokeObjectURL(url);
            }, 1000);

            toast.success(t("rapport.pdf_success"), {
                id: "rapport-pdf"
            });
        } catch (error) {
            console.error("Erreur de téléchargement PDF :", error);

            toast.error(t("rapport.pdf_error"), {
                id: "rapport-pdf"
            });
        }
    };

    return (
        <div className="w-full max-w-7xl mx-auto px-2 md:px-4 py-4 space-y-5">

            <div className="flex items-center gap-3 border-b border-slate-200 pb-4">
                <div className="rounded-xl bg-blue-50 p-3 text-blue-600">
                    <FiBarChart2 className="size-7" />
                </div>

                <div>
                    <h1 className="text-2xl font-bold text-slate-800">
                        {t("rapport.title")}
                    </h1>

                    <p className="text-sm text-slate-500">
                        {t("rapport.description")}
                    </p>
                </div>
            </div>

            <div className="space-y-4">
                {categories.map((categorie) => (
                    <CategorieRapport
                        key={categorie.id}
                        categorie={{
                            ...categorie,
                            titre: t(categorie.titreKey),
                            description: t(categorie.descriptionKey),
                            rapports: categorie.rapports.map(traduireRapport)
                        }}
                        compteurs={compteurs}
                        onView={consulterRapport}
                        onDownload={telechargerPdf}
                    />
                ))}
            </div>

        </div>
    );
}

export default Rapport;
