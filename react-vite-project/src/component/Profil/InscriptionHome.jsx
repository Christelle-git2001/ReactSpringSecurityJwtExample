import {Route, Routes, useNavigate} from "react-router-dom";
import "../../css/InscriptionHome.css";
import {useTranslation} from "react-i18next";

function InscriptionHome(){
    const navigate = useNavigate();
    const { t } = useTranslation();

    return (
        <div className="inscription-page">
            <div className="inscription-card">
                <h1 className="inscription-title">
                    {t('inscription.titre')}
                </h1>
            </div>
            <div className="inscription-buttons">
                <button
                    className="inscription-btn inscription-btn-etudiant"
                    onClick={() => navigate('/inscription/addetudiant')}>
                    {t('inscription.etudiant')}
                </button>
            </div>
            <div>
                <button
                    className="inscription-btn inscription-btn-employeur"
                    onClick={() => navigate('/inscription/addemployeur')}>
                    {t('inscription.employeur')}
                </button>
            </div>
        </div>
    )
}

export default InscriptionHome;