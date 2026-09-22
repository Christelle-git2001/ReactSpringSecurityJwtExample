import { Link } from "react-router-dom";
import "../../../css/AddEtudiant.css";
import {useTranslation} from "react-i18next";

function AddEmployeur({ onAdd }) {
    const onSubmit = async (e) => {
        e.preventDefault();

        const formData = new FormData(e.target);

        const nouvelEtudiant = {
            firstName: formData.get("firstName"),
            lastName: formData.get("lastName"),
            email: formData.get("email"),
            telephone: formData.get("telephone"),
            matricule: formData.get("matricule"),
            password: formData.get("password")
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
                <h1 className="add-etudiant-title">{t('addemployeur.titre')}</h1>
                <p className="add-etudiant-subtitle">{t('addemployeur.type-utilisateur')}</p>
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
                                <label htmlFor="companyName" className="add-etudiant-label">{t('addemployeur.nom')}</label>
                                <input type="text" placeholder={t('addemployeur.nom')} required minLength="2" maxLength="50" id="companyName" name="companyName" className="add-etudiant-input" />
                            </div>
                        </div>

                        <select required className="w-full py-3 px-4 border border-gray-300 rounded-lg
                            text-gray-700 bg-white
                            focus:outline-none focus:ring-2 focus:ring-blue-500
                            cursor-pointer">
                            <option value="">{t('addemployeur.secteur')}</option>
                            <option value="etudiant">Étudiant</option>
                            <option value="employeur">Employeur</option>
                        </select>

                        <div>
                            <label htmlFor="email" className="add-etudiant-label">{t('addemployeur.adresse')}</label>
                            <input type="email" placeholder={t('addemployeur.adresse')} required id="email" name="email" className="add-etudiant-input" />
                        </div>

                        <div className="add-etudiant-row">
                            <div>
                                <label htmlFor="idNumber" className="add-etudiant-label">{t('addemployeur.numero-ID')}</label>
                                <input type="text" placeholder={t('addemployeur.numero-ID')} required minLength="10" maxLength="10" id="idNumber" name="idNumber" className="add-etudiant-input" />
                            </div>
                        </div>

                        <div className="add-etudiant-row">
                            <div>
                                <label htmlFor="contactName" className="add-etudiant-label">{t('addemployeur.nom-personne-contact')}</label>
                                <input type="text" placeholder={t('addemployeur.nom-personne-contact')} required minLength="2" maxLength="50" id="contactName" name="contactName" className="add-etudiant-input" />
                            </div>
                        </div>

                        <div>
                            <label htmlFor="telephone" className="add-etudiant-label">{t('addemployeur.telephone-personne-contact')}</label>
                            <input type="tel" placeholder="450-111-2222" required minLength="12" maxLength="12" id="telephone" name="telephone" className="add-etudiant-input" />
                        </div>

                        <div>
                            <label htmlFor="password" className="add-etudiant-label">{t('addemployeur.mdp')}</label>
                            <input type="password" placeholder={t('addemployeur.mdp')} required minLength="4" id="password" name="password" className="add-etudiant-input" />
                        </div>

                        <div>
                            <label htmlFor="confirmPassword" className="add-etudiant-label">{t('addemployeur.confirmation-mdp')}</label>
                            <input type="password" placeholder={t('addemployeur.confirmation-mdp')} required minLength="4" id="confirmPassword" name="confirmPassword" className="add-etudiant-input" />
                        </div>
                    </div>

                    <input type="submit" value={t('addemployeur.bouton-inscrire')} className="primary-submit" />

                    <p className="add-etudiant-login-text">
                        {t('addemployeur.deja-inscrit')} <Link to="/login" className="add-etudiant-login-link">{t('addemployeur.bouton-connecter')}</Link>
                    </p>
                </form>
            </div>
        </div>
    );
}

export default AddEmployeur;
