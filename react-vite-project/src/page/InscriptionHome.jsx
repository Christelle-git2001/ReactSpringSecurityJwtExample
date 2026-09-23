import { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";

import AddEtudiant from "../component/Profil/Etudiant/AddEtudiant.jsx";
import AddProfesseur from "../component/Profil/Professeur/AddProfesseur.jsx";
import AddEmployeur from "../component/Profil/Employeur/AddEmployeur.jsx";

import "../css/InscriptionHome.css";

function InscriptionHome({ addEtudiant, addProfesseur, error, message, setError, setMessage }) {
    const { t } = useTranslation();
    const [profil, setProfil] = useState("ETUDIANT");
    const profiles = [
        { id: "ETUDIANT", label: t("inscription.etudiant") },
        { id: "PROFESSEUR", label: t("inscription.professeur") },
        { id: "EMPLOYEUR", label: t("inscription.employeur") }
    ];

    useEffect(() => {
        setError(null);
        setMessage("");
    }, [profil]);


    return (
        <div className="inscription-wrapper">

            {/* Fond bleu pleine page */}
            <div className="inscription-background">
                <div className="circleDesign circleDesign-top-right" />
                <div className="circleDesign circleDesign-bottom-left-1" />
                <div className="circleDesign circleDesign-bottom-left-2" />
            </div>

            {/* Contenu centré */}
            <div className="inscription-page">

                {/* Onglets */}
                <div className="inscription-buttons">
                    {profiles.map((p) => {
                        const active = profil === p.id;
                        return (
                            <button
                                key={p.id}
                                type="button"
                                onClick={() => setProfil(p.id)}
                                className={`inscription-btn ${
                                    active ? "inscription-btn-active" : "inscription-btn-inactive"
                                }`}
                            >
                                {p.label}
                            </button>
                        );
                    })}
                </div>

                {/* Formulaire */}
                <div className="inscription-form-container">
                    {profil === "ETUDIANT" && (
                        <AddEtudiant
                            onAdd={addEtudiant}
                            error={error}
                            message={message}
                        />
                    )}

                    {profil === "PROFESSEUR" && (
                        <AddProfesseur
                            onAdd={addProfesseur}
                            error={error}
                            message={message}
                        />
                    )}

                    {profil === "EMPLOYEUR" && (
                        <AddEmployeur
                            onAdd={() => {}}
                            error={error}
                            message={message}
                        />
                    )}
                </div>

            </div>
        </div>
    );
}

export default InscriptionHome;
