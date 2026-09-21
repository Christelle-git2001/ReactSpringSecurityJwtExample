import { Link } from "react-router-dom";
import "../../../css/AddEtudiant.css";

function AddEtudiant({ onAdd, error, message }) {
  const onSubmit = async (e) => {
    e.preventDefault();

    const formData = new FormData(e.target);

    const nouvelEtudiant = {
      firstName: formData.get("firstName"),
      lastName: formData.get("lastName"),
      email: formData.get("email"),
      phone: formData.get("telephone"),
      matricule: formData.get("matricule"),
      department: formData.get("department"),
      password: formData.get("password"),
      passwordConfirmation: formData.get("confirmPassword")
    };

    const ajoutReussi = await onAdd(nouvelEtudiant);

    if (ajoutReussi) {
      e.target.reset();
    }
  };

  return (
      <div className="add-etudiant-page">
        <div className="add-etudiant-page-header">
          <h1 className="add-etudiant-title">S'inscrire sur OSE</h1>
          <p className="add-etudiant-subtitle">Compte étudiant</p>
          <span className="add-etudiant-subtitle-line" />
        </div>

        <div className="add-etudiant-section">
          <div className="add-etudiant-slogan">
            <div className="circleDesign circleDesign-top-right" />
            <div className="circleDesign circleDesign-bottom-left-1" />
            <div className="circleDesign circleDesign-bottom-left-2" />
            <h1 className="add-etudiant-slogan-title">BIENVENUE<br />SUR<br /><span
                className="add-etudiant-slogan-name">-OSE-</span><span className="add-etudiant-slogan-line" /></h1>
          </div>

          <form onSubmit={onSubmit} className="add-etudiant-form">
            <div className="add-etudiant-fields">
              <div className="add-etudiant-row">
                <div>
                  <label htmlFor="firstName" className="add-etudiant-label">Prénom</label>
                  <input type="text" placeholder="Prénom" required minLength="2" maxLength="50" id="firstName" name="firstName" className="add-etudiant-input" />
                </div>

                <div>
                  <label htmlFor="lastName" className="add-etudiant-label">Nom</label>
                  <input type="text" placeholder="Nom" required minLength="2" maxLength="50" id="lastName" name="lastName" className="add-etudiant-input" />
                </div>
              </div>

              <div>
                <label htmlFor="email" className="add-etudiant-label">Courriel</label>
                <input type="email" placeholder="nom@email.com" required id="email" name="email" className="add-etudiant-input" />
              </div>

              <div className="add-etudiant-row">
                <div>
                  <label htmlFor="telephone" className="add-etudiant-label">Téléphone</label>
                  <input type="tel" placeholder="450-111-2222" required minLength="10"
                         maxLength="12" id="telephone" name="telephone"
                         pattern="[0-9]{3}-?[0-9]{3}-?[0-9]{4}" className="add-etudiant-input" />
                </div>

                <div>
                  <label htmlFor="matricule" className="add-etudiant-label">Matricule</label>
                  <input type="text" placeholder="Matricule" required minLength="7" maxLength="7" id="matricule" name="matricule" className="add-etudiant-input" />
                </div>
              </div>

              <div>
                <label htmlFor="department" className="add-etudiant-label">Département</label>
                <select required id="department" name="department" className="add-etudiant-input" defaultValue="">
                  <option value="" disabled>Choisir un département</option>
                  <option value="INFORMATIQUE">Techniques de l'informatique</option>
                  <option value="GESTION">Techniques de la gestion</option>
                  <option value="TRAVAIL_SOCIAL">Techniques de travail social</option>
                  <option value="EDUCATION_ENFANCE">Techniques d'éducation à l'enfance</option>
                  <option value="SOINS_INFIRMIERS">Techniques de soins infirmiers</option>
                  <option value="GENIE_CIVIL">Technologie du génie civil</option>
                  <option value="GENIE_ELECTRIQUE">Technologie du génie électrique</option>
                  <option value="GENIE_PHYSIQUE">Technologie du génie physique</option>
                  <option value="ARCHITECTURE">Technologie de l'architecture</option>
                  <option value="ESTIMATION_EVALUATION">Technologie de l'estimation et de l'évaluation en bâtiment</option>
                </select>
              </div>

              <div>
                <label htmlFor="password" className="add-etudiant-label">Mot de passe</label>
                <input type="password" placeholder="Mot de passe" required minLength="4" id="password" name="password" className="add-etudiant-input" />
              </div>

              <div>
                <label htmlFor="confirmPassword" className="add-etudiant-label">Confirmer le mot de passe</label>
                <input type="password" placeholder="Confirmer le mot de passe" required minLength="4" id="confirmPassword" name="confirmPassword" className="add-etudiant-input" />
              </div>
            </div>

            {error && <p className="error-message">{error}</p>}
            {message && <p className="success-message">{message}</p>}

            <input type="submit" value="S'inscrire" className="primary-submit" />

            <p className="add-etudiant-login-text">
              Déjà inscrit? <Link to="/login" className="add-etudiant-login-link">Se connecter</Link>
            </p>
          </form>
        </div>
      </div>
  );
}

export default AddEtudiant;
