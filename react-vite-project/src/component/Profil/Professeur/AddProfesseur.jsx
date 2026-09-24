import { Link } from "react-router-dom";
import "../../../css/AddEtudiant.css";
import { useTranslation } from "react-i18next";
import { useState, useEffect } from "react";
import { getDepartements } from "../../../api/http.jsx";

function AddProfesseur({ onAdd, error, message, isValid, validateForm }) {
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

        const nouveauProfesseur = {
            firstName: formData.get("firstName"),
            lastName: formData.get("lastName"),
            email: formData.get("email"),
            phoneNumber: formData.get("telephone"),
            matricule: formData.get("matricule"),
            password: formData.get("password"),
            passwordConfirmation: formData.get("confirmPassword"),
            department: formData.get("department")
        };

        const ajoutReussi = await onAdd(nouveauProfesseur);
        if (ajoutReussi) e.target.reset();
    };

    return (
        <div className="add-etudiant-page">

            <div className="add-etudiant-page-header">
                <h1 className="add-etudiant-title">{t('add_professeur.page_title')}</h1>
                <p className="add-etudiant-subtitle">{t('add_professeur.page_subtitle')}</p>
                <span className="add-etudiant-subtitle-line" />
            </div>

            <form onSubmit={onSubmit} className="add-etudiant-form" onChange={validateForm}>

            <div className="add-etudiant-fields">

                    <div className="add-etudiant-row">
                        <div>
                            <label className="add-etudiant-label">{t('add_professeur.first_name')}</label>
                            <input type="text" name="firstName" required className="add-etudiant-input" />
                        </div>

                        <div>
                            <label className="add-etudiant-label">{t('add_professeur.last_name')}</label>
                            <input type="text" name="lastName" required className="add-etudiant-input" />
                        </div>
                    </div>

                    <div>
                        <label className="add-etudiant-label">{t('add_professeur.email')}</label>
                        <input type="email" name="email" required className="add-etudiant-input" />
                    </div>

                    <div className="add-etudiant-row">
                        <div>
                            <label className="add-etudiant-label">{t('add_professeur.phone')}</label>
                            <input type="tel" name="telephone" pattern="[0-9]{3}-?[0-9]{3}-?[0-9]{4}" required className="add-etudiant-input" />
                        </div>

                        <div>
                            <label className="add-etudiant-label">{t('add_professeur.matricule')}</label>
                            <input type="text" name="matricule" required minLength="7" maxLength="7" className="add-etudiant-input" />
                        </div>
                    </div>

                    <div>
                        <label className="add-etudiant-label">{t('add_professeur.department')}</label>
                        <select name="department" required className="add-etudiant-input">
                            <option value="">{t('add_professeur.department_placeholder')}</option>
                            {departements.map(dep => (
                                <option key={dep.name} value={dep.name}>
                                    {t(`departement.${dep.name}`)}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div>
                        <label className="add-etudiant-label">{t('add_professeur.password')}</label>
                        <input type="password" name="password" required className="add-etudiant-input" />
                    </div>

                    <div>
                        <label className="add-etudiant-label">{t('add_professeur.confirm_password')}</label>
                        <input type="password" name="confirmPassword" required className="add-etudiant-input" />
                    </div>

                </div>

                {error && <p className="error-message">{error}</p>}
                {message && <p className="success-message">{message}</p>}

                <input
                    type="submit"
                    value={t('add_professeur.submit')}
                    className="primary-submit"
                    disabled={!isValid}
                />

                <p className="add-etudiant-login-text">
                    {t('add_professeur.already_registered')}{" "}
                    <Link to="/login" className="add-etudiant-login-link">
                        {t('add_professeur.login')}
                    </Link>
                </p>

            </form>
        </div>
    );
}

export default AddProfesseur;
