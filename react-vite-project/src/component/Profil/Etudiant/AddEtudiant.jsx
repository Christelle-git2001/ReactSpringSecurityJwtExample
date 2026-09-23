import { Link } from "react-router-dom";
import "../../../css/AddEtudiant.css";
import { useTranslation } from "react-i18next";

function AddEtudiant({ onAdd, error, message }) {
  const { t } = useTranslation();

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
    if (ajoutReussi) e.target.reset();
  };

  return (
      <div className="add-etudiant-page">

        <div className="add-etudiant-page-header">
          <h1 className="add-etudiant-title">{t('add_etudiant.page_title')}</h1>
          <p className="add-etudiant-subtitle">{t('add_etudiant.page_subtitle')}</p>
          <span className="add-etudiant-subtitle-line" />
        </div>

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
              <input type="email" name="email" required className="add-etudiant-input" />
            </div>

            <div className="add-etudiant-row">
              <div>
                <label className="add-etudiant-label">{t('add_etudiant.phone')}</label>
                <input type="tel" name="telephone" required className="add-etudiant-input" />
              </div>

              <div>
                <label className="add-etudiant-label">{t('add_etudiant.matricule')}</label>
                <input type="text" name="matricule" required className="add-etudiant-input" />
              </div>
            </div>

            <div>
              <label className="add-etudiant-label">{t('add_etudiant.department')}</label>
              <select name="department" required className="add-etudiant-input">
                <option value="" disabled>{t('add_etudiant.department_placeholder')}</option>
                <option value="INFORMATIQUE">{t('add_etudiant.department_informatique')}</option>
                <option value="GESTION">{t('add_etudiant.department_gestion')}</option>
                <option value="TRAVAIL_SOCIAL">{t('add_etudiant.department_travail_social')}</option>
                <option value="EDUCATION_ENFANCE">{t('add_etudiant.department_education_enfance')}</option>
                <option value="SOINS_INFIRMIERS">{t('add_etudiant.department_soins_infirmiers')}</option>
                <option value="GENIE_CIVIL">{t('add_etudiant.department_genie_civil')}</option>
                <option value="GENIE_ELECTRIQUE">{t('add_etudiant.department_genie_electrique')}</option>
                <option value="GENIE_PHYSIQUE">{t('add_etudiant.department_genie_physique')}</option>
                <option value="ARCHITECTURE">{t('add_etudiant.department_architecture')}</option>
                <option value="ESTIMATION_EVALUATION">{t('add_etudiant.department_estimation_evaluation')}</option>
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

          {error && <p className="error-message">{error}</p>}
          {message && <p className="success-message">{message}</p>}

          <input type="submit" value={t('add_etudiant.submit')} className="primary-submit" />

          <p className="add-etudiant-login-text">
            {t('add_etudiant.already_registered')}{" "}
            <Link to="/login" className="add-etudiant-login-link">
              {t('add_etudiant.login')}
            </Link>
          </p>

        </form>
      </div>
  );
}

export default AddEtudiant;
