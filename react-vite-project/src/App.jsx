import "./App.css";
import React, { useState, useEffect } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";
import AppRoutes from "./component/route/AppRoute.jsx";
import useAuth from "./utils/useAuth.js";
import { addEtudiant, addProfesseur, addEmployeur } from "./utils/userService.js";
import Drawer from "./component/Design/Drawer.jsx";
import { Toaster } from "react-hot-toast";
import ChatBot from "./component/Chatbot/ChatBot.jsx";
import { poserQuestionChatbot } from "./api/chatbot.jsx";

function App() {
    const { t } = useTranslation();
    const { user, setUser, error, setError } = useAuth();
    const [message, setMessage] = useState("");
    const [drawerContent, setDrawerContent] = useState(null);
    const [sessionKey, setSessionKey] = useState(
        () => localStorage.getItem("sessionCible") || "default"
    );

    const navigate = useNavigate();
    const location = useLocation();

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

            {user?.isLoggedIn && location.pathname !== "/login" && (() => {
                const roleName = user.role?.name || "GESTIONNAIRE";

                const roleKey = roleName.toLowerCase();

                const roleLabel = roleName.charAt(0) +
                    roleName.slice(1).toLowerCase();

                const suggestionsList = t(`chatbot.suggestions.${roleKey}`, {
                    returnObjects: true
                });

                return (
                    <ChatBot
                        titre={t("chatbot.titre")}
                        sousTitre={t(`chatbot.sousTitre${roleLabel}`)}
                        initialMessage={t(`chatbot.initial${roleLabel}`)}
                        suggestions={Array.isArray(suggestionsList) ? suggestionsList : []}
                        t={t}
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