import "./App.css";
import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import AppRoutes from "./component/route/AppRoute.jsx";
import useAuth from "./utils/useAuth.js";
import { addEtudiant, addProfesseur, addEmployeur } from "./utils/userService.js";
import Drawer from "./component/Design/Drawer.jsx";
import { Toaster } from "react-hot-toast";

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
        </div>
    );
}

export default App;