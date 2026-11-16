import "./App.css";
import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next"; // <-- Votre hook de traduction
import AppRoutes from "./component/route/AppRoute.jsx";
import useAuth from "./utils/useAuth.js";
import { addEtudiant, addProfesseur, addEmployeur } from "./utils/userService.js";
import Drawer from "./component/Design/Drawer.jsx";
import { Toaster } from "react-hot-toast";
import ChatBot from "./component/Chatbot/ChatBot.jsx";
import { poserQuestionChatbot } from "./api/chatbot.jsx";

function App() {
    const { t } = useTranslation(); // <-- Initialisation de t
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
                const roleKey = roleName.toLowerCase(); // 'etudiant', 'employeur', 'gestionnaire'

                // Récupération des suggestions sous forme de tableau d'objets [{label, query}]
                const suggestionsList = t(`chatbot.suggestions.${roleKey}`, { returnObjects: true });

                return (
                    <ChatBot
                        titre={t("chatbot.titre")}
                        sousTitre={t(`chatbot.sousTitre${roleName.charAt(0) + roleName.slice(1).toLowerCase()}`)}
                        initialMessage={t(`chatbot.initial${roleName.charAt(0) + roleName.slice(1).toLowerCase()}`)}
                        suggestions={Array.isArray(suggestionsList) ? suggestionsList : []}
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