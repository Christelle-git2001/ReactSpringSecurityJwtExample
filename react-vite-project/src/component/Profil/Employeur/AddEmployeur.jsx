import { Link } from "react-router-dom";
import "../../../css/AddEtudiant.css";
import { useTranslation } from "react-i18next";
import { useState, useEffect } from "react";
import { getSecteursEmployeur } from "../../../api/http.jsx";

function AddEmployeur({ onAdd, error, message }) {
    const { t } = useTranslation();

    const [secteurs, setSecteurs] = useState([]);
    const [loadingSecteurs, setLoadingSecteurs] = useState(true);

    useEffect(() => {
        async function fetchSecteurs() {
            try {
                const data = await getSecteursEmployeur();
                setSecteurs(data);
            } catch (err) {
                console.error("Erreur lors du chargement des secteurs :", err);
            } finally {
                setLoadingSecteurs(false);
            }
        }
        fetchSecteurs();
    }, []);

    const onSubmit = async (e) => {
        e.preventDefault();
        const formData = new FormData(e.target);

        const nouvelEmployeur = {
            firstName: formData.get("firstName"),
            lastName: formData.get("lastName"),
            email: formData.get("email"),
            phone: formData.get("telephone"),
            town: formData.get("town"),
            businessName: formData.get("businessName"),
            businessSector: formData.get("businessSector"),
            password: formData.get("password"),
            passwordConfirmation: formData.get("confirmPassword")
        };

        const ajoutReussi = await onAdd(nouvelEmployeur);
        if (ajoutReussi) e.target.reset();
    };

    return (
        <div className="add-etudiant-page">

            <div className="add-etudiant-page-header">
                <h1 className="add-etudiant-title">{t('add_employeur.page_title')}</h1>
                <p className="add-etudiant-subtitle">{t('add_employeur.page_subtitle')}</p>
                <span className="add-etudiant-subtitle-line" />
            </div>

            <form onSubmit={onSubmit} className="add-etudiant-form">

                <div className="add-etudiant-fields">
                    <div className="add-etudiant-row">
                        <div>
                            <label className="add-etudiant-label">{t('add_employeur.first_name')}</label>
                            <input type="text" name="firstName" required className="add-etudiant-input" />
                        </div>

                        <div>
                            <label className="add-etudiant-label">{t('add_employeur.last_name')}</label>
                            <input type="text" name="lastName" required className="add-etudiant-input" />
                        </div>
                    </div>

                    <div>
                        <label className="add-etudiant-label">{t('add_employeur.email')}</label>
                        <input
                            type="email"
                            name="email"
                            required
                            pattern="^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$"
                            className="add-etudiant-input"
                        />
                    </div>

                    <div className="add-etudiant-row">
                        <div>
                            <label className="add-etudiant-label">{t('add_employeur.phone')}</label>
                            <input type="tel" name="telephone" pattern="[0-9]{3}-?[0-9]{3}-?[0-9]{4}" maxlength={"12"} required className="add-etudiant-input" />
                        </div>

                        <div>
                            <label className="add-etudiant-label">{t('add_employeur.town')}</label>
                            <input type="text" name="town" required className="add-etudiant-input" />
                        </div>
                    </div>
                    <div>
                        <label className="add-etudiant-label">{t('add_employeur.business_name')}</label>
                        <input type="text" name="businessName" required className="add-etudiant-input" />
                    </div>
                    <div>
                        <label className="add-etudiant-label">{t('add_employeur.business_sector')}</label>

                        {loadingSecteurs ? (
                            <p>{t('add_employeur.loading_sectors')}</p>
                        ) : (
                            <select name="businessSector" required className="add-etudiant-input">
                                <option value="">{t('add_employeur.business_sector_placeholder')}</option>

                                {secteurs.map(secteur => (
                                    <option key={secteur.name} value={secteur.name}>
                                        {t(`secteur.${secteur.name}`)}
                                    </option>
                                ))}
                            </select>
                        )}
                    </div>
                    <div>
                        <label className="add-etudiant-label">{t('add_employeur.password')}</label>
                        <input type="password" name="password" required className="add-etudiant-input" />
                    </div>

                    <div>
                        <label className="add-etudiant-label">{t('add_employeur.confirm_password')}</label>
                        <input type="password" name="confirmPassword" required className="add-etudiant-input" />
                    </div>

                </div>

                {error && <p className="error-message">{error}</p>}
                {message && <p className="success-message">{message}</p>}

                <input
                    type="submit"
                    value={t('add_employeur.submit')}
                    className="primary-submit"
                />

                <p className="add-etudiant-login-text">
                    {t('add_employeur.already_registered')}{" "}
                    <Link to="/login" className="add-etudiant-login-link">
                        {t('add_employeur.login')}
                    </Link>
                </p>

            </form>
        </div>
    );
}

export default AddEmployeur;
