import { useState } from "react";

import AddEtudiant from "../component/Profil/Etudiant/AddEtudiant.jsx";
import AddProfesseur from "../component/Profil/Professeur/AddProfesseur.jsx";
import AddEmployeur from "../component/Profil/Employeur/AddEmployeur.jsx";

function InscriptionHome({ addEtudiant, addProfesseur, error, message }) {
    const [profil, setProfil] = useState("ETUDIANT");

    const profiles = [
        { id: "ETUDIANT", label: "Étudiant" },
        { id: "PROFESSEUR", label: "Professeur" },
        { id: "EMPLOYEUR", label: "Employeur" }
    ];

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4 py-8">
            <div className="w-full max-w-2xl mx-auto bg-white rounded-xl shadow-lg border border-gray-100 p-6 md:p-8">

                {/* En-tête */}
                <div className="text-center mb-6">
                    <h1 className="text-3xl font-bold text-[#043462]">Créer un compte</h1>
                    <p className="text-gray-500 mt-1 text-sm">
                        Choisissez votre profil pour commencer l'inscription
                    </p>
                </div>

                {/* Boutons de sélection du profil (Onglets) */}
                <div className="flex justify-center border-b border-gray-200 mb-6">
                    <div className="grid grid-cols-3 gap-2 w-full max-w-md p-1 bg-gray-100 rounded-lg">
                        {profiles.map((p) => {
                            const isActive = profil === p.id;
                            return (
                                <button
                                    key={p.id}
                                    type="button"
                                    onClick={() => setProfil(p.id)}
                                    className={`py-2 px-3 text-sm font-semibold rounded-md transition-all duration-200 ${
                                        isActive
                                            ? "bg-[#043462] text-white shadow-sm"
                                            : "text-gray-600 hover:text-[#043462] hover:bg-gray-200/60"
                                    }`}
                                >
                                    {p.label}
                                </button>
                            );
                        })}
                    </div>
                </div>

                {/* Zone du Formulaire */}
                <div className="signup-form-content">
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