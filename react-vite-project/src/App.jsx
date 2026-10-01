import "./App.css";
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import AppRoutes from "./component/route/AppRoute.jsx";
import useAuth from "./utils/useAuth.js";
import { addEtudiant, addProfesseur, addEmployeur } from "./utils/userService.js";
import Drawer from "./component/Design/Drawer.jsx";

function App() {
    const { user, setUser, error, setError } = useAuth();
    const [message, setMessage] = useState("");
    const [drawerContent, setDrawerContent] = useState(null);

    const navigate = useNavigate();

    const handleEtudiant = (etudiant) =>
        addEtudiant(etudiant, setMessage, setError, navigate);

    const handleProfesseur = (professeur) =>
        addProfesseur(professeur, setMessage, setError, navigate);

    const handleEmployeur = (employeur) =>
        addEmployeur(employeur, setMessage, setError, navigate);

    return (
        <>
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
        </>
    );
}

export default App;
