import "./App.css";
import React, {useState} from "react";
import { useNavigate} from "react-router-dom";
import { inscrireEtudiant } from "./api/etudiant.jsx";
import { inscrireProfesseur } from "./api/professeur.jsx";
import AppRoutes from "./component/route/AppRoute.jsx";
import useAuth from "./utils/useAuth.js";
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

  async function addProfesseur(professeur) {
    try {
      await inscrireProfesseur(professeur);
      setMessage("Professeur ajouté avec succès.");
      setError(null);
      navigate("/login");
      return true;
    } catch (error) {
      setError(error.message || "Une erreur inattendue s'est produite.");
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
            setMessage={setMessage}
        />
      </div>
  );

}

export default App;
