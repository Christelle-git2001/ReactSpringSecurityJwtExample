import { useState } from "react";

import AddEtudiant from "../component/Profil/Etudiant/AddEtudiant.jsx";
import AddProfesseur from "../component/Profil/Professeur/AddProfesseur.jsx";
import AddEmployeur from "../component/Profil/Employeur/AddEmployeur.jsx";

function InscriptionHome({ addEtudiant, addProfesseur, error, message }) {

    const [profil, setProfil] = useState("ETUDIANT");

    return (
        <div className="signup-container">

            {/* Titre */}
            <div className="signup-header">
                <h1>Créer un compte</h1>
                <p>Choisissez votre type de profil</p>
            </div>

            {/* Formulaire dynamique */}
            <div className="signup-form">

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
                        onAdd={() => {}}   // Employeur n’a pas de backend pour l’instant
                    />
                )}
            </div>

            {/* Boutons de sélection */}
            <div className="signup-profile-selector">
                <button onClick={() => setProfil("ETUDIANT")}>
                    Étudiant
                </button>

                <button onClick={() => setProfil("PROFESSEUR")}>
                    Professeur
                </button>

                <button onClick={() => setProfil("EMPLOYEUR")}>
                    Employeur
                </button>
            </div>

            {/* Lien vers login */}
            <div className="signup-login-link">
                <p>Déjà inscrit ?</p>
                <a href="/login">Se connecter</a>
            </div>

        </div>
    );
}

export default InscriptionHome;
