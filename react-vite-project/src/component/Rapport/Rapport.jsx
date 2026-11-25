
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
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
        titre: "Étudiants",
        description: "Rapports liés aux étudiants.",
        Icon: FiUsers,
        couleur: "bg-emerald-50/80",
        iconeCouleur: "bg-emerald-100 text-emerald-600",
        rapports: [
            {
                id: "etudiants-inscrits",
                titre: "Étudiants inscrits",
                description: "Liste de tous les étudiants inscrits.",
                dataEndpoint: "/gestionnaire/rapports/etudiants",
                pdfEndpoint: "/pdf/gestionnaire/rapports/etudiants"
            },
            {
                id: "etudiants-sans-cv",
                titre: "Étudiants sans CV",
                description: "Liste des étudiants n'ayant pas de CV.",
                dataEndpoint: "/gestionnaire/rapports/etudiants/sans-cv",
                pdfEndpoint: "/pdf/gestionnaire/rapports/etudiants/sans-cv"
            },
            {
                id: "cv-en-attente",
                titre: "CV en attente de validation",
                description: "Liste des CV à valider.",
                dataEndpoint: "/gestionnaire/rapports/cvs/en-attente",
                pdfEndpoint: "/pdf/gestionnaire/rapports/cv/en-attente"
            },
            {
                id: "etudiants-sans-entrevue",
                titre: "Étudiants sans entrevue",
                description: "Liste des étudiants n'ayant pas eu d'entrevue.",
                dataEndpoint: "/gestionnaire/rapports/etudiants/sans-entrevue",
                pdfEndpoint: "/pdf/gestionnaire/rapports/etudiants/sans-entrevue"
            },
            {
                id: "etudiants-stage-trouve",
                titre: "Étudiants ayant trouvé un stage",
                description: "Liste des étudiants avec un stage confirmé.",
                dataEndpoint: "/gestionnaire/rapports/etudiants/stage-trouve",
                pdfEndpoint: "/pdf/gestionnaire/rapports/etudiants/stage-trouve"
            }
        ]
    },
    {
        id: "offres",
        titre: "Offres",
        description: "Rapports liés aux offres de stage.",
        Icon: FiBriefcase,
        couleur: "bg-blue-50/80",
        iconeCouleur: "bg-blue-100 text-blue-600",
        rapports: [
            {
                id: "offres-en-attente",
                titre: "Offres en attente de validation",
                description: "Liste des offres à valider.",
                dataEndpoint: "/gestionnaire/rapports/offres/en-attente",
                pdfEndpoint: "/pdf/gestionnaire/rapports/offres/en-attente"
            },
            {
                id: "offres-approuvees",
                titre: "Offres validées",
                description: "Liste des offres approuvées.",
                dataEndpoint: "/gestionnaire/rapports/offres/validees",
                pdfEndpoint: "/pdf/gestionnaire/rapports/offres/approuvees"
            }
        ]
    },
    {
        id: "candidatures",
        titre: "Candidatures",
        description: "Rapports liés aux candidatures.",
        Icon: FiClipboard,
        couleur: "bg-amber-50/80",
        iconeCouleur: "bg-amber-100 text-amber-600",
        rapports: [
            {
                id: "candidatures-en-attente-entrevue",
                titre: "Candidatures en attente d'entrevue",
                description: "Liste des candidatures à planifier en entrevue.",
                dataEndpoint: "/gestionnaire/rapports/candidatures/en-attente-entrevue",
                pdfEndpoint: "/pdf/gestionnaire/rapports/candidatures/en-attente-entrevue"
            },
            {
                id: "candidatures-sans-evaluation",
                titre: "Candidatures sans évaluation du superviseur",
                description: "Liste des candidatures sans évaluation.",
                dataEndpoint: "/gestionnaire/rapports/candidatures/sans-evaluation",
                pdfEndpoint: "/pdf/gestionnaire/rapports/candidatures/sans-evaluation"
            },
            {
                id: "candidatures-sans-evaluation-milieu-stage",
                titre: "Candidatures sans évaluation du milieu de stage",
                description: "Liste des candidatures sans évaluation du milieu.",
                dataEndpoint: "/gestionnaire/rapports/candidatures/sans-evaluation-milieu-stage",
                pdfEndpoint: "/pdf/gestionnaire/rapports/candidatures/sans-evaluation-milieu-stage"
            }
        ]
    },
    {
        id: "entrevues",
        titre: "Entrevues",
        description: "Rapports liés aux entrevues.",
        Icon: FiCalendar,
        couleur: "bg-rose-50/80",
        iconeCouleur: "bg-rose-100 text-rose-600",
        rapports: [
            {
                id: "entrevues-en-attente-reponse",
                titre: "Entrevues en attente de réponse",
                description: "Liste des entrevues dont la réponse est attendue.",
                dataEndpoint: "/gestionnaire/rapports/entrevues/en-attente-reponse",
                pdfEndpoint: "/pdf/gestionnaire/rapports/entrevues/en-attente-reponse"
            }
        ]
    }
];

function Rapport() {
    const navigate = useNavigate();
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

    const consulterRapport = (rapport) => {
        navigate(`/gestionnaire/rapports/${rapport.id}`);
    };

    const telechargerPdf = async (rapport) => {
        try {
            toast.loading("Préparation du PDF...", {
                id: "rapport-pdf"
            });

            const response = await genererRapportPdf(rapport.pdfEndpoint);
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

            toast.success("PDF téléchargé avec succès.", {
                id: "rapport-pdf"
            });
        } catch (error) {
            console.error("Erreur de téléchargement PDF :", error);

            toast.error("Impossible de télécharger le PDF.", {
                id: "rapport-pdf"
            });
        }
    };

    const apercuPdf = async (rapport) => {
        const fenetre = window.open("", "_blank");

        if (!fenetre) {
            toast.error("Autorise les fenêtres pop-up pour afficher le PDF.");
            return;
        }

        try {
            fenetre.document.title = "Préparation du rapport";

            const response = await genererRapportPdf(rapport.pdfEndpoint);
            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);

            fenetre.location.href = url;

            toast.success("Aperçu PDF prêt.");
        } catch (error) {
            fenetre.close();
            console.error("Erreur d'aperçu PDF :", error);

            toast.error("Impossible de prévisualiser le PDF.");
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
                        Rapports
                    </h1>

                    <p className="text-sm text-slate-500">
                        Générez et consultez les rapports sur les offres,
                        les étudiants, les candidatures et les entrevues.
                    </p>
                </div>
            </div>

            <div className="space-y-4">
                {categories.map((categorie) => (
                    <CategorieRapport
                        key={categorie.id}
                        categorie={categorie}
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
