import "./App.css";
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { inscrireEtudiant } from "./api/etudiant.jsx";
import { inscrireProfesseur } from "./api/professeur.jsx";
import { inscrireEmployeur } from "./api/employeur.jsx";
import AppRoutes from "./component/route/AppRoute.jsx";
import useAuth from "./utils/useAuth.js";
import {addEtudiant, addProfesseur, addEmployeur} from "./utils/userService.js";

function App() {
  const { user, setUser, error, setError } = useAuth();
  const [message, setMessage] = useState("");
  const navigate = useNavigate();
  const handleEtudiant = (etudiant) => addEtudiant(etudiant, setMessage, setError, navigate); ;
  const handleProfesseur = (professeur) => addProfesseur(professeur, setMessage, setError, navigate); ;
  const handleEmployeur = (employeur) => addEmployeur(employeur, setMessage, setError, navigate); ;


  return (
      <div className="App">
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
        />
      </div>
  );
}

export default App;