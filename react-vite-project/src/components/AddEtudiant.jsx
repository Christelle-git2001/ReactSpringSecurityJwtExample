import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import fetcher from "../utils/fetcher";

const initialFormData = {
  firstName: "",
  lastName: "",
  email: "",
  password: "",
  confirmPassword: "",
  matricule: "",
};

function AddEtudiant() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState(initialFormData);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setError("");
    setMessage("");
    setFormData((current) => ({
      ...current,
      [name]: value,
    }));
  };

  const validateForm = () => {
    if (formData.password !== formData.confirmPassword) {
      setError("Les mots de passe ne correspondent pas.");
      return false;
    }

    if (!formData.email.includes("@")) {
      setError("Le courriel est invalide.");
      return false;
    }

    return true;
  };

  const onSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    setIsSubmitting(true);
    setError("");
    setMessage("");

    try {
      const response = await fetcher("/etudiant/inscription", {
        method: "POST",
        headers: {
          Accept: "application/json",
          "Content-Type": "application/json;charset=UTF-8",
        },
        body: JSON.stringify({
          firstName: formData.firstName.trim(),
          lastName: formData.lastName.trim(),
          email: formData.email.trim().toLowerCase(),
          password: formData.password,
          confirmPassword: formData.confirmPassword,
          matricule: formData.matricule.trim(),
        }),
      });

      if (!response.ok) {
        let errorMessage = "Impossible d'inscrire l'etudiant.";

        try {
          const data = await response.json();
          errorMessage = data.message || data.erreur || data.error || errorMessage;
        } catch {
          if (response.status === 409) {
            errorMessage = "Ce courriel ou ce matricule est deja utilise.";
          }
        }

        throw new Error(errorMessage);
      }

      setFormData(initialFormData);
      setMessage("Inscription reussie. Redirection vers la connexion...");
      setTimeout(() => navigate("/login"), 1200);
    } catch (err) {
      setError(err.message || "Une erreur est survenue.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="max-w-xl mx-auto px-4 py-8">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-gray-900">S'inscrire sur OSE</h1>
        <p className="mt-1 text-gray-600">Compte etudiant</p>
      </div>

      {error && (
        <div className="mb-4 rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
          {error}
        </div>
      )}

      {message && (
        <div className="mb-4 rounded-md border border-green-200 bg-green-50 px-4 py-3 text-sm text-green-700">
          {message}
        </div>
      )}

      <form onSubmit={onSubmit} className="bg-white shadow-md rounded-lg p-6 space-y-5">
        <div>
          <label htmlFor="firstName" className="block text-sm font-medium text-gray-700 mb-1">Prenom</label>
          <input type="text" placeholder="Prenom" required minLength="2" maxLength="50"
                 id="firstName" name="firstName" value={formData.firstName} onChange={handleChange}
                 className="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" />
        </div>

        <div>
          <label htmlFor="lastName" className="block text-sm font-medium text-gray-700 mb-1">Nom</label>
          <input type="text" placeholder="Nom" required minLength="2" maxLength="50"
                 id="lastName" name="lastName" value={formData.lastName} onChange={handleChange}
                 className="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" />
        </div>

        <div>
          <label htmlFor="email" className="block text-sm font-medium text-gray-700 mb-1">Courriel</label>
          <input type="email" placeholder="exemple@email.com" required
                 id="email" name="email" value={formData.email} onChange={handleChange}
                 className="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" />
        </div>

        <div>
          <label htmlFor="password" className="block text-sm font-medium text-gray-700 mb-1">Mot de passe</label>
          <input type="password" placeholder="Mot de passe" required minLength="4"
                 id="password" name="password" value={formData.password} onChange={handleChange}
                 className="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" />
        </div>

        <div>
          <label htmlFor="confirmPassword" className="block text-sm font-medium text-gray-700 mb-1">Confirmer le mot de passe</label>
          <input type="password" placeholder="Confirmer le mot de passe" required minLength="4"
                 id="confirmPassword" name="confirmPassword" value={formData.confirmPassword} onChange={handleChange}
                 className="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" />
        </div>

        <div>
          <label htmlFor="matricule" className="block text-sm font-medium text-gray-700 mb-1">Matricule</label>
          <input type="text" placeholder="Matricule" required minLength="1"
                 id="matricule" name="matricule" value={formData.matricule} onChange={handleChange}
                 className="w-full border border-gray-300 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500" />
        </div>

        <button type="submit"
                disabled={isSubmitting}
                className="w-full bg-blue-600 text-white font-medium py-2 px-4 rounded-md hover:bg-blue-700 disabled:cursor-not-allowed disabled:bg-blue-300 transition">
          {isSubmitting ? "Inscription..." : "S'inscrire"}
        </button>

        <p className="text-center text-sm text-gray-600">
          Deja inscrit? <Link to="/login" className="font-medium text-blue-600 hover:text-blue-700">Se connecter</Link>
        </p>
      </form>
    </div>
  );
}

export default AddEtudiant;
