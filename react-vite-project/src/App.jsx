import "./App.css";
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { inscrireEtudiant } from "./api/etudiant.jsx";
import { inscrireProfesseur } from "./api/professeur.jsx";
import { inscrireEmployeur } from "./api/employeur.jsx";
import AppRoutes from "./component/route/AppRoute.jsx";
import useAuth from "./utils/useAuth.js";

function App() {
  const { user, setUser, error, setError } = useAuth();
  const [message, setMessage] = useState("");
  const navigate = useNavigate();

  async function addEtudiant(etudiant) {
    try {
      await inscrireEtudiant(etudiant);
      setMessage("success.student_added");
      setError(null);
      navigate("/login");
      return true;
    } catch (error) {
      setError(error.message || "error.generic");
      setMessage("");
      return false;
    }
  }

  async function addProfesseur(professeur) {
    try {
      await inscrireProfesseur(professeur);
      setMessage("success.professor_added");
      setError(null);
      navigate("/login");
      return true;
    } catch (error) {
      setError(error.message || "error.generic");
      setMessage("");
      return false;
    }
  }

  async function addEmployeur(employeur) {
    try {
      await inscrireEmployeur(employeur);
      setMessage("success.employer_added");
      setError(null);
      navigate("/login");
      return true;
    } catch (error) {
      setError(error.message || "error.generic");
      setMessage("");
      return false;
    }
  }

  return (
      <div className="App">
        <AppRoutes
            user={user}
            error={error}
            message={message}
            setError={setError}
            setUser={setUser}
            addEtudiant={addEtudiant}
            addProfesseur={addProfesseur}
            addEmployeur={addEmployeur}
            setMessage={setMessage}
        />
      </div>
  );
}

export default App;