import { inscrireEmployeur } from "../api/employeur.jsx";
import { inscrireEtudiant } from "../api/etudiant.jsx";
import { inscrireProfesseur } from "../api/professeur.jsx";

export async function addEtudiant(etudiant, setMessage, setError, navigate) {
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

export async function addProfesseur(professeur, setMessage, setError, navigate) {
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

export async function addEmployeur(employeur, setMessage, setError, navigate) {
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