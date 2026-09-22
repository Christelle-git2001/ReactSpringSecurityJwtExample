import { Link } from "react-router-dom";
import "../../../css/AddEtudiant.css";
import {useTranslation} from "react-i18next";

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
  const { t } = useTranslation();

  return (
      <div className="add-etudiant-page">
        <div className="add-etudiant-page-header">
          <h1 className="add-etudiant-title">{t('add_etudiant.page_title')}</h1>
          <p className="add-etudiant-subtitle">{t('add_etudiant.page_subtitle')}</p>
          <span className="add-etudiant-subtitle-line" />
        </div>

        <div className="add-etudiant-section">
          <div className="add-etudiant-slogan">
            <div className="circleDesign circleDesign-top-right" />
            <div className="circleDesign circleDesign-bottom-left-1" />
            <div className="circleDesign circleDesign-bottom-left-2" />
            <h1 className="add-etudiant-slogan-title">{t('add_etudiant.slogan_message_1')}<br />{t('add_etudiant.slogan_message_2')}<br /><span
                className="add-etudiant-slogan-name">{t('add_etudiant.slogan_message_3')}</span><span className="add-etudiant-slogan-line" /></h1>
          </div>

          <form onSubmit={onSubmit} className="add-etudiant-form">
            <div className="add-etudiant-fields">
              <div className="add-etudiant-row">
                <div>
                  <label htmlFor="firstName" className="add-etudiant-label">{t('add_etudiant.first_name')}</label>
                  <input type="text" placeholder={t('add_etudiant.first_name_placeholder')} required minLength="2" maxLength="50" id="firstName" name="firstName" className="add-etudiant-input" />
                </div>

                <div>
                  <label htmlFor="lastName" className="add-etudiant-label">{t('add_etudiant.last_name')}</label>
                  <input type="text" placeholder={t('add_etudiant.last_name_placeholder')} required minLength="2" maxLength="50" id="lastName" name="lastName" className="add-etudiant-input" />
                </div>
              </div>

              <div>
                <label htmlFor="email" className="add-etudiant-label">{t('add_etudiant.email')}</label>
                <input type="email" placeholder={t('add_etudiant.email_placeholder')} required id="email" name="email" className="add-etudiant-input" />
              </div>

              <div className="add-etudiant-row">
                <div>
                  <label htmlFor="telephone" className="add-etudiant-label">{t('add_etudiant.phone')}</label>
                  <input type="tel" placeholder={t('add_etudiant.phone_placeholder')} required minLength="10"
                         maxLength="12" id="telephone" name="telephone"
                         pattern="[0-9]{3}-?[0-9]{3}-?[0-9]{4}" className="add-etudiant-input" />
                </div>

                <div>
                  <label htmlFor="matricule" className="add-etudiant-label">{t('add_etudiant.matricule')}</label>
                  <input type="text" placeholder={t('add_etudiant.matricule_placeholder')} required minLength="7" maxLength="7" id="matricule" name="matricule" className="add-etudiant-input" />
                </div>
              </div>

              <div>
                <label htmlFor="department" className="add-etudiant-label">{t('add_etudiant.department')}</label>
                <select required id="department" name="department" className="add-etudiant-input" defaultValue="">
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
                <label htmlFor="password" className="add-etudiant-label">{t('add_etudiant.password')}</label>
                <input type="password" placeholder={t('add_etudiant.password_placeholder')} required minLength="4" id="password" name="password" className="add-etudiant-input" />
              </div>

              <div>
                <label htmlFor="confirmPassword" className="add-etudiant-label">{t('add_etudiant.confirm_password')}</label>
                <input type="password" placeholder={t('add_etudiant.confirm_password_placeholder')} required minLength="4" id="confirmPassword" name="confirmPassword" className="add-etudiant-input" />
              </div>
            </div>

            {error && <p className="error-message">{error}</p>}
            {message && <p className="success-message">{message}</p>}

            <input type="submit" value={t('add_etudiant.submit')} className="primary-submit" />

            <p className="add-etudiant-login-text">
              {t('add_etudiant.already_registered')} <Link to="/login" className="add-etudiant-login-link">{t('add_etudiant.login')}</Link>
            </p>
          </form>
        </div>
      </div>
  );
}

export default AddEtudiant;
