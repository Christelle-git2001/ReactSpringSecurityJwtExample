import { FiX } from "react-icons/fi";
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

    const fermer = (isSubmitted = false) => {
        setOuvert(false);
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
        fermer(true);
    };

    if (!ouvert) return null;

    return (
        <dialog className="modal modal-open">
            <div className="modal-box w-full max-w-3xl bg-[radial-gradient(circle_at_top_left,#00CCCB33,transparent_70%)]">
                <div className="relative mb-5 flex min-h-10 items-center justify-center px-12">
                    <h3 className="font-bold text-lg text-center">{t("etudiant.popup.popup_title")}</h3>
                    <button
                        type="button"
                        className="close-button absolute right-0 top-0"
                        onClick={() => fermer(false)}
                        aria-label={t("drawer.close")}
                    >
                        <FiX className="size-5" aria-hidden="true" />
                    </button>
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