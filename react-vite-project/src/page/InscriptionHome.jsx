import { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import LanguageSwitche from "../locales/LanguageSwitch.jsx";

import AddEtudiant from "../component/registration/AddEtudiant.jsx";
import AddProfesseur from "../component/registration/AddProfesseur.jsx";
import AddEmployeur from "../component/registration/AddEmployeur.jsx";

import "../css/InscriptionHome.css";

function InscriptionHome({ addEtudiant, addProfesseur, addEmployeur, error, message, setError, setMessage }) {
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

            <div className="fixed top-4 right-4 z-50">
                <LanguageSwitche />
            </div>

            <div className="inscription-background">
                <div className="circleDesign circleDesign-top-right" />
                <div className="circleDesign circleDesign-bottom-left-1" />
                <div className="circleDesign circleDesign-bottom-left-2" />
            </div>

            <div className="inscription-page">

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
                            onAdd={addEmployeur}
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
