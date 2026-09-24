import { Link } from "react-router-dom";
import "../../../css/AddEtudiant.css";
import { useTranslation } from "react-i18next";
import { useState, useEffect } from "react";
import {getSecteursEmployeur} from "../../../api/http.jsx";

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
                                disabled={loadingSecteurs}
                                className="w-full py-3 px-4 border border-gray-300 rounded-lg
                                    text-gray-700 bg-white
                                    focus:outline-none focus:ring-2 focus:ring-blue-500
                                    cursor-pointer"
                            >
                                <option value="" disabled>
                                    {loadingSecteurs ? "..." : t('add_employeur.secteur')}
                                </option>
                                {secteurs.map((secteur) => (
                                    <option key={secteur.name} value={secteur.name}>
                                        {t(`add_employeur.${secteur.name}`)}
                                    </option>
                                ))}
                            </select>
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