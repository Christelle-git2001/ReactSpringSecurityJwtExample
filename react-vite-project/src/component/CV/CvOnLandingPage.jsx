import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { obtenirCvInfo } from "../../api/etudiant.jsx";
import CurriculumVitaeZone from "./CurriculumVitaeZone.jsx";

const CvOnLandingPage = () => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const [ouvert, setOuvert] = useState(false);

    useEffect(() => {

        obtenirCvInfo()
            .catch((e) => {
                if (e.status === 404) setOuvert(true);
            });
    }, []);

    const fermer = () => {
        setOuvert(false);
    };

    const allerAuOffre = () => {
        fermer();
        navigate("/etudiant/offers");
    };

    if (!ouvert) return null;

    return (
        <dialog className="modal modal-open">
            <div className="modal-box bg-[radial-gradient(circle_at_top_left,#00CCCB33,transparent_70%)]">
                <button className="btn btn-sm btn-circle btn-ghost absolute right-2 top-2" onClick={allerAuOffre}>✕</button>
                <h3 className="font-bold text-lg text-center">{t("etudiant.popup.popup_title")}</h3>
                <p className="py-4 text-center">{t("etudiant.popup.popup_description")}</p>
                <div className="modal-action">
                <CurriculumVitaeZone/>
                </div>
            </div>
        </dialog>
    );
};

export default CvOnLandingPage;