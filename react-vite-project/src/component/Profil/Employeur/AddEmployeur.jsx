import { Link } from "react-router-dom";
import "../../../css/AddEtudiant.css";
import { useTranslation } from "react-i18next";

function AddEmployeur({ onAdd, error, message }) {
    const onSubmit = async (e) => {
        e.preventDefault();

        const formData = new FormData(e.target);

        const nouvelEmployeur = {
            firstName: formData.get("firstName"),
            lastName: formData.get("lastName"),
            phone: formData.get("phone"),
            email: formData.get("email"),
            town: formData.get("town"),
            businessName: formData.get("businessName"),
            businessSector: formData.get("businessSector"),
            businesstype: formData.get("businesstype"),
            password: formData.get("password"),
            passwordConfirmation: formData.get("passwordConfirmation"),
        };

        const ajoutReussi = await onAdd(nouvelEmployeur);

        if (ajoutReussi) {
            e.target.reset();
        }
    };
    const { t } = useTranslation();

    return (
        <div className="add-etudiant-page">
            <div className="add-etudiant-page-header">
                <h1 className="add-etudiant-title">{t('add_employeur.titre')}</h1>
                <p className="add-etudiant-subtitle">{t('add_employeur.type-utilisateur')}</p>
                <span className="add-etudiant-subtitle-line" />
            </div>

            <div className="add-etudiant-section">
                <div className="add-etudiant-slogan">
                    <div className="circleDesign circleDesign-top-right" />
                    <div className="circleDesign circleDesign-bottom-left-1" />
                    <div className="circleDesign circleDesign-bottom-left-2" />
                    <h1 className="add-etudiant-slogan-title">{t('add_employeur.message-bienvenue')}<br /><span
                        className="add-etudiant-slogan-name">-{t('add_employeur.nom-app')}-</span><span className="add-etudiant-slogan-line" /></h1>
                </div>

                <form onSubmit={onSubmit} className="add-etudiant-form">
                    <div className="add-etudiant-fields">

                        <div className="add-etudiant-row">
                            <div>
                                <label htmlFor="firstName" className="add-etudiant-label">{t('add_employeur.prenom')}</label>
                                <input type="text" placeholder={t('add_employeur.prenom')} required minLength="2" maxLength="50" id="firstName" name="firstName" className="add-etudiant-input" />
                            </div>
                        </div>

                        <div className="add-etudiant-row">
                            <div>
                                <label htmlFor="lastName" className="add-etudiant-label">{t('add_employeur.nom-famille')}</label>
                                <input type="text" placeholder={t('add_employeur.nom-famille')} required minLength="2" maxLength="50" id="lastName" name="lastName" className="add-etudiant-input" />
                            </div>
                        </div>

                        <div className="add-etudiant-row">
                            <div>
                                <label htmlFor="businessName" className="add-etudiant-label">{t('add_employeur.nom')}</label>
                                <input type="text" placeholder={t('add_employeur.nom')} required minLength="2" maxLength="50" id="businessName" name="businessName" className="add-etudiant-input" />
                            </div>
                        </div>

                        <div>
                            <label htmlFor="businessSector" className="add-etudiant-label">{t('add_employeur.secteur')}</label>
                            <select
                                required
                                name="businessSector"
                                id="businessSector"
                                defaultValue=""
                                className="w-full py-3 px-4 border border-gray-300 rounded-lg
                                    text-gray-700 bg-white
                                    focus:outline-none focus:ring-2 focus:ring-blue-500
                                    cursor-pointer"
                            >
                                <option value="" disabled>{t('add_employeur.secteur')}</option>
                                <option value="INFORMATIQUE">{t('add_employeur.INFORMATIQUE')}</option>
                                <option value="PHARMACEUTIQUE">{t('add_employeur.PHARMACEUTIQUE')}</option>
                                <option value="FINANCE">{t('add_employeur.FINANCE')}</option>
                                <option value="SANTE">{t('add_employeur.SANTE')}</option>
                                <option value="EDUCATION">{t('add_employeur.EDUCATION')}</option>
                                <option value="COMMERCE_DETAIL">{t('add_employeur.COMMERCE_DETAIL')}</option>
                                <option value="COMMERCE_GROS">{t('add_employeur.COMMERCE_GROS')}</option>
                                <option value="RESTAURATION_HOTELLERIE">{t('add_employeur.RESTAURATION_HOTELLERIE')}</option>
                                <option value="CONSTRUCTION">{t('add_employeur.CONSTRUCTION')}</option>
                                <option value="IMMOBILIER">{t('add_employeur.IMMOBILIER')}</option>
                                <option value="TRANSPORT_LOGISTIQUE">{t('add_employeur.TRANSPORT_LOGISTIQUE')}</option>
                                <option value="MANUFACTURE_INDUSTRIE">{t('add_employeur.MANUFACTURE_INDUSTRIE')}</option>
                                <option value="AGRICULTURE">{t('add_employeur.AGRICULTURE')}</option>
                                <option value="MEDIAS_COMMUNICATION">{t('add_employeur.MEDIAS_COMMUNICATION')}</option>
                                <option value="AEROSPATIAL">{t('add_employeur.AEROSPATIAL')}</option>
                                <option value="ENERGIE">{t('add_employeur.ENERGIE')}</option>
                                <option value="SERVICES_CONSEIL">{t('add_employeur.SERVICES_CONSEIL')}</option>
                                <option value="ARTS_DIVERTISSEMENT">{t('add_employeur.ARTS_DIVERTISSEMENT')}</option>
                                <option value="ADMINISTRATION_PUBLIQUE">{t('add_employeur.ADMINISTRATION_PUBLIQUE')}</option>
                            </select>
                        </div>

                        <div>
                            <label htmlFor="businesstype" className="add-etudiant-label">{t('add_employeur.type-entreprise')}</label>
                            <input type="text" placeholder={t('add_employeur.type-entreprise')} required id="businesstype" name="businesstype" className="add-etudiant-input" />
                        </div>

                        <div>
                            <label htmlFor="email" className="add-etudiant-label">{t('add_employeur.adresse')}</label>
                            <input type="email" placeholder={t('add_employeur.adresse')} required id="email" name="email" className="add-etudiant-input" />
                        </div>

                        <div>
                            <label htmlFor="town" className="add-etudiant-label">{t('add_employeur.ville')}</label>
                            <input type="text" placeholder={t('add_employeur.ville')} required id="town" name="town" className="add-etudiant-input" />
                        </div>

                        <div>
                            <label htmlFor="phone" className="add-etudiant-label">{t('add_employeur.telephone-personne-contact')}</label>
                            <input type="tel" placeholder="450-111-2222" required minLength="12" maxLength="12" id="phone" name="phone" className="add-etudiant-input" />
                        </div>

                        <div>
                            <label htmlFor="password" className="add-etudiant-label">{t('add_employeur.mdp')}</label>
                            <input type="password" placeholder={t('add_employeur.mdp')} required minLength="4" id="password" name="password" className="add-etudiant-input" />
                        </div>

                        <div>
                            <label htmlFor="passwordConfirmation" className="add-etudiant-label">{t('add_employeur.confirmation-mdp')}</label>
                            <input type="password" placeholder={t('add_employeur.confirmation-mdp')} required minLength="4" id="passwordConfirmation" name="passwordConfirmation" className="add-etudiant-input" />
                        </div>
                    </div>

                    {error && <p className="error-message">{error}</p>}
                    {message && <p className="success-message">{message}</p>}

                    <input type="submit" value={t('add_employeur.bouton-inscrire')} className="primary-submit" />

                    <p className="add-etudiant-login-text">
                        {t('add_employeur.deja-inscrit')} <Link to="/login" className="add-etudiant-login-link">{t('add_employeur.bouton-connecter')}</Link>
                    </p>
                </form>
            </div>
        </div>
    );
}

export default AddEmployeur;