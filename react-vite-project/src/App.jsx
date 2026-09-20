import "./App.css";
import React, {useEffect, useState} from "react";
import { useNavigate} from "react-router-dom";;
import fetcher from "./utils/fetcher.js";
import { inscrireEtudiant } from "./api/api.jsx";
import AppRoutes from "./Component/route/AppRoute.jsx";
function App() {

  const { user, setUser, error, setError } = useAuth();
  const [message, setMessage] = useState("")
  const navigate = useNavigate();

  async function addEtudiant(etudiant) {
    try {
      await inscrireEtudiant(etudiant);
      setMessage("Étudiant ajouté avec succès.");
      setError(null);
      setMessage("");
      navigate('/login');
      return true;
    } catch (error) {
      setError(error.message || "Une erreur inattendue s'est produite.");
      setMessage("");
      return false;
    }
  }

    return (
        <AppRoutes
            user={user}
            error={error}
            message={message}
            setError={setError}
            setUser={setUser}
        />
    );
}

export default App;
