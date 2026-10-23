import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { obtenirCvInfo } from "../../api/etudiant.jsx";
import CurriculumVitaeZone from "./CurriculumVitaeZone.jsx";
import toast from "react-hot-toast";
import { useNavigate } from "react-router-dom";

const CvOnLandingPage = () => {
    const { t } = useTranslation();
    const [ouvert, setOuvert] = useState(false);
    const [error, setError] = useState("");
    const [isSubmitted, setIsSubmitted] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchData = async () => {
            const data = await obtenirCvInfo()
                .catch((e) => {
                    if (e?.status === 404) {
                        setOuvert(true);
                    } else if (e?.message === "UNAUTHORIZED" || e?.status === 401) {
                        navigate("/login");
                    } else {
                        setOuvert(true);
                        setError("error.generic");
                    }
                });
            if (data?.statut === "REFUSEE")
                setOuvert(true);
        };
        fetchData();
    }, [navigate]);

    const fermer = () => {
        setOuvert(false);
        console.log(isSubmitted)
        if (isSubmitted) {
            toast.success(t("cv.toast.waiting_validation"), {
                position: 'bottom-center',
                duration: 3000
            });
        }else {
            toast.error(t("cv.toast.dont_forget_CV"), {
                position: 'bottom-center',
                duration: 3000
            });
        }
    };

    const handleSend = () => {
        setIsSubmitted(true);
    };

    useEffect(() => {
        console.log(isSubmitted, "THER2E");
    }, [isSubmitted]);

    if (!ouvert) return null;

    return (
        <dialog className="modal modal-open">
            <div className="modal-box w-full max-w-3xl bg-[radial-gradient(circle_at_top_left,#00CCCB33,transparent_70%)]">
                <div className="grid grid-cols-12 mb-5">
                    <div className="col-span-11 flex justify-center">
                        <h3 className="font-bold text-lg text-center w-100">{t("etudiant.popup.popup_title")}</h3>
                    </div>
                    <div className="col-span-1">
                        <button className="btn btn-error btn-sm btn-circle" onClick={fermer}>X</button>
                    </div>
                </div>
                <p className="text-center">{t("etudiant.popup.popup_description")}</p>

                <div className="modal-action">
                    {error ? (
                        <p role="alert" className="text-sm text-red-700">
                            {t(error)}
                        </p>
                    ) : (
                        <CurriculumVitaeZone onSend={handleSend} />
                    )}
                </div>
            </div>
        </dialog>
    );
};

export default CvOnLandingPage;