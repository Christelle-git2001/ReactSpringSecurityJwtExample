import "./App.css";
import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import AppRoutes from "./component/route/AppRoute.jsx";
import useAuth from "./utils/useAuth.js";
import { addEtudiant, addProfesseur, addEmployeur } from "./utils/userService.js";
import Drawer from "./component/Design/Drawer.jsx";
import { Toaster } from "react-hot-toast";
import ChatBot from "./component/Chatbot/ChatBot.jsx";
import { poserQuestionChatbot } from "./api/chatbot.jsx";

function App() {
    const { user, setUser, error, setError } = useAuth();
    const [message, setMessage] = useState("");
    const [drawerContent, setDrawerContent] = useState(null);
    const [sessionKey, setSessionKey] = useState(
        () => localStorage.getItem("sessionCible") || "default"
    );

    const navigate = useNavigate();

    useEffect(() => {
        const handleSessionChange = (e) => {
            setSessionKey(e.detail);
        };

        window.addEventListener("sessionChanged", handleSessionChange);
        return () => {
            window.removeEventListener("sessionChanged", handleSessionChange);
        };
    }, []);

    const handleEtudiant = (etudiant) =>
        addEtudiant(etudiant, setMessage, setError, navigate);

    const handleProfesseur = (professeur) =>
        addProfesseur(professeur, setMessage, setError, navigate);

    const handleEmployeur = (employeur) =>
        addEmployeur(employeur, setMessage, setError, navigate);

    const CONFIGS_CHATBOT = {
        ETUDIANT: {
            sousTitre: "Assistant étudiant",
            initialMessage: "Bonjour ! Je suis votre assistant étudiant. 👋\nPosez-moi une question sur votre CV, vos offres de stage ou vos candidatures.",
            suggestions: [
                "Est-ce que mon CV a été approuvé ?",
                "Quelles offres de stage sont disponibles pour moi ?",
                "Ai-je reçu de nouvelles offres ?",
                "Qui est mon professeur assigné ?"
            ]
        },
        EMPLOYEUR: {
            sousTitre: "Assistant employeur",
            initialMessage: "Bonjour ! Je suis votre assistant employeur. 👋\nPosez-moi une question sur vos offres de stage, vos candidats ou vos contrats.",
            suggestions: [
                "Quelles offres de stage ai-je publiées ?",
                "Quelles candidatures ai-je reçues ?",
                "Quelles sont mes prochaines entrevues ?",
                "Quels contrats attendent ma signature ?"
            ]
        },
        GESTIONNAIRE: {
            sousTitre: "Gestionnaire de stages",
            initialMessage: "Bonjour ! Je suis votre assistant de gestion de stages.\nPosez-moi une question ou choisissez une suggestion ci-dessous.",
            suggestions: [
                "Combien d'étudiants sont inscrits ?",
                "Quels CV attendent une validation ?",
                "Quels contrats attendent ma signature ?",
                "Combien ont trouvé un stage ?"
            ]
        }
    };

    return (
        <div key={sessionKey} className="max-h-screen">
            <AppRoutes
                user={user}
                error={error}
                message={message}
                setError={setError}
                setUser={setUser}
                addEtudiant={handleEtudiant}
                addProfesseur={handleProfesseur}
                addEmployeur={handleEmployeur}
                setMessage={setMessage}
                openDrawer={setDrawerContent}
            />
            <Drawer id="my-drawer-5" content={drawerContent} />
                        <Toaster />
                        {user?.isLoggedIn && (() => {
                            const roleName = user.role?.name || "GESTIONNAIRE";
                            const config = CONFIGS_CHATBOT[roleName] || CONFIGS_CHATBOT.GESTIONNAIRE;

                            return (
                                <ChatBot
                                    titre="Assistant IKey"
                                    sousTitre={config.sousTitre}
                                    initialMessage={config.initialMessage}
                                    suggestions={config.suggestions}
                                    onSend={async (question) => {
                                        const resultat = await poserQuestionChatbot(question);
                                        return resultat.reponse;
                                    }}
                                />
                            );
                        })()}
            </div>
    );
}

export default App;