import { Link } from "react-router-dom";
import "../../../css/AddEtudiant.css";
import { useTranslation } from "react-i18next";
import { getDepartements } from "../../../api/http.jsx";
import { useState, useEffect } from "react";

function AddEtudiant({ onAdd, error, message }) {
  const { t } = useTranslation();

  const [departements, setDepartements] = useState([]);

  useEffect(() => {
    getDepartements()
        .then(setDepartements)
        .catch(() => setDepartements([]));
  }, []);

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
          <h1 className="add-etudiant-title">{t('add_etudiant.page_title')}</h1>
          <p className="add-etudiant-subtitle">{t('add_etudiant.page_subtitle')}</p>
          <span className="add-etudiant-subtitle-line" />
        </div>

        <div className="add-etudiant-section">
          <form onSubmit={onSubmit} className="add-etudiant-form">

            <div className="add-etudiant-fields">

              <div className="add-etudiant-row">
                <div>
                  <label className="add-etudiant-label">{t('add_etudiant.first_name')}</label>
                  <input type="text" name="firstName" required className="add-etudiant-input" />
                </div>

                <div>
                  <label className="add-etudiant-label">{t('add_etudiant.last_name')}</label>
                  <input type="text" name="lastName" required className="add-etudiant-input" />
                </div>
              </div>

              <div>
                <label className="add-etudiant-label">{t('add_etudiant.email')}</label>
                <input
                    type="email"
                    name="email"
                    required
                    className="add-etudiant-input"
                    pattern="^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$"
                />
              </div>

              <div className="add-etudiant-row">
                <div>
                  <label className="add-etudiant-label">{t('add_etudiant.phone')}</label>
                  <input type="tel" name="telephone" pattern="[0-9]{3}-?[0-9]{3}-?[0-9]{4}" maxLength={"12"} required className="add-etudiant-input" />
                </div>

                <div>
                  <label className="add-etudiant-label">{t('add_etudiant.matricule')}</label>
                  <input type="text" name="matricule" required className="add-etudiant-input" minLength={"7"} maxLength={"7"} />
                </div>
              </div>

              <div>
                <label htmlFor="department" className="add-etudiant-label">{t('add_etudiant.department')}</label>
                <select id="department" name="department" required className="add-etudiant-input">
                  <option value="">{t('add_etudiant.department_placeholder')}</option>
                  {departements.map(dep => (
                      <option key={dep.name} value={dep.name}>
                        {t(`departement.${dep.name}`)}
                      </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="add-etudiant-label">{t('add_etudiant.password')}</label>
                <input type="password" name="password" required className="add-etudiant-input" />
              </div>

              <div>
                <label className="add-etudiant-label">{t('add_etudiant.confirm_password')}</label>
                <input type="password" name="confirmPassword" required className="add-etudiant-input" />
              </div>

            </div>

            {/* Traduction dynamique des clés i18n d'erreur et de succès */}
            {error && <p className="error-message">{t(error)}</p>}
            {message && <p className="success-message">{t(message)}</p>}

            <input type="submit" value={t('add_etudiant.submit')} className="primary-submit" />

            <p className="add-etudiant-login-text">
              {t('add_etudiant.already_registered')}{" "}
              <Link to="/login" className="add-etudiant-login-link">
                {t('add_etudiant.login')}
              </Link>
            </p>

          </form>
        </div>
      </div>
  );
}

export default AddEtudiant;