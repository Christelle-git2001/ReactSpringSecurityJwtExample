import { Link } from "react-router-dom";
import "../../../css/AddEtudiant.css";
import { useTranslation } from "react-i18next";

function AddEmployeur({ onAdd, error, message }) {
    const { t } = useTranslation();

    const onSubmit = async (e) => {
        e.preventDefault();
        const formData = new FormData(e.target);

        const nouvelEmployeur = {
            company_name: formData.get("company_name"),
            company_type: formData.get("company_type"),
            email: formData.get("email"),
            telephone: formData.get("telephone"),
            id_number: formData.get("id_number"),
            contact_name: formData.get("contact_name"),
            password: formData.get("password"),
            confirm_password: formData.get("confirm_password"),
        };

        const ajoutReussi = await onAdd(nouvelEmployeur);
        if (ajoutReussi) e.target.reset();
    };

    return (
        <div className="add-etudiant-page">

            {/* En-tête */}
            <div className="add-etudiant-page-header">
                <h1 className="add-etudiant-title">{t('add_employeur.titre')}</h1>
                <p className="add-etudiant-subtitle">{t('add_employeur.type-utilisateur')}</p>
                <span className="add-etudiant-subtitle-line" />
            </div>

            {/* Formulaire */}
            <form onSubmit={onSubmit} className="add-etudiant-form">

                <div className="add-etudiant-fields">

                    <div className="add-etudiant-row">
                        <div>
                            <label className="add-etudiant-label">{t('add_employeur.nom')}</label>
                            <input
                                type="text"
                                name="company_name"
                                required
                                minLength="2"
                                maxLength="50"
                                className="add-etudiant-input"
                            />
                        </div>
                    </div>

                    <div>
                        <label className="add-etudiant-label">{t('add_employeur.secteur')}</label>
                        <select
                            name="company_type"
                            required
                            className="add-etudiant-input"
                        >
                            <option value="">{t('add_employeur.secteur')}</option>
                            <option value="PME">{t('add_employeur.type_pme')}</option>
                            <option value="GAFAM">{t('add_employeur.type_gafam')}</option>
                            <option value="STARTUP">{t('add_employeur.type_startup')}</option>
                            <option value="ORGANISME">{t('add_employeur.type_organisme')}</option>
                        </select>
                    </div>

                    <div>
                        <label className="add-etudiant-label">{t('add_employeur.adresse')}</label>
                        <input
                            type="email"
                            name="email"
                            required
                            className="add-etudiant-input"
                        />
                    </div>

                    <div className="add-etudiant-row">
                        <div>
                            <label className="add-etudiant-label">{t('add_employeur.numero-ID')}</label>
                            <input
                                type="text"
                                name="id_number"
                                required
                                minLength="10"
                                maxLength="10"
                                className="add-etudiant-input"
                            />
                        </div>
                    </div>

                    <div className="add-etudiant-row">
                        <div>
                            <label className="add-etudiant-label">{t('add_employeur.nom-personne-contact')}</label>
                            <input
                                type="text"
                                name="contact_name"
                                required
                                minLength="2"
                                maxLength="50"
                                className="add-etudiant-input"
                            />
                        </div>
                    </div>

                    <div>
                        <label className="add-etudiant-label">{t('add_employeur.telephone-personne-contact')}</label>
                        <input
                            type="tel"
                            name="telephone"
                            required
                            placeholder="450-111-2222"
                            minLength="12"
                            maxLength="12"
                            className="add-etudiant-input"
                        />
                    </div>

                    <div>
                        <label className="add-etudiant-label">{t('add_employeur.mdp')}</label>
                        <input
                            type="password"
                            name="password"
                            required
                            minLength="4"
                            className="add-etudiant-input"
                        />
                    </div>

                    <div>
                        <label className="add-etudiant-label">{t('add_employeur.confirmation-mdp')}</label>
                        <input
                            type="password"
                            name="confirm_password"
                            required
                            minLength="4"
                            className="add-etudiant-input"
                        />
                    </div>

                </div>

                {error && <p className="error-message">{error}</p>}
                {message && <p className="success-message">{message}</p>}

                <input
                    type="submit"
                    value={t('add_employeur.bouton-inscrire')}
                    className="primary-submit"
                />

                <p className="add-etudiant-login-text">
                    {t('add_employeur.deja-inscrit')}{" "}
                    <Link to="/login" className="add-etudiant-login-link">
                        {t('add_employeur.bouton-connecter')}
                    </Link>
                </p>

            </form>
        </div>
    );
}

export default AddEmployeur;
