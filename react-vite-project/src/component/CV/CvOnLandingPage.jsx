import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { obtenirCvInfo } from "../../api/etudiant.jsx";
import CurriculumVitaeZone from "./CurriculumVitaeZone.jsx";

const CLE_SESSION = "cvReminderVu";

const CvOnLandingPage = () => {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const [ouvert, setOuvert] = useState(false);

    useEffect(() => {
        if (sessionStorage.getItem(CLE_SESSION)) return;

        obtenirCvInfo()
            .catch((e) => {
                // 404 = l'étudiant n'a pas encore de CV
                if (e.status === 404) setOuvert(true);
            });
    }, []);

    const fermer = () => {
        sessionStorage.setItem(CLE_SESSION, "1");
        setOuvert(false);
    };

    const allerAuCv = () => {
        fermer();
        navigate("/etudiant/offres");
    };

    if (!ouvert) return null;

    return (
        <dialog className="modal modal-open">
            <div className="modal-box bg-[radial-gradient(circle_at_top_left,#00CCCB33,transparent_70%)]">
                <button className="btn btn-sm btn-circle btn-ghost absolute right-2 top-2" onClick={fermer}>✕</button>
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