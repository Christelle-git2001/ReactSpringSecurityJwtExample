import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { postulerOffre } from "../../api/etudiant.jsx";
import { useNavigate } from "react-router-dom";
import Button from "./Button.jsx";

function PostulerButton({ offerId, hasApplied = false, onApply, onApplied, user }) {
    const { t } = useTranslation();
    const [applied, setApplied] = useState(hasApplied);
    const [isApplying, setIsApplying] = useState(false);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        setApplied(hasApplied);
    }, [hasApplied]);

    const handleApply = async (e) => {
        e?.stopPropagation?.();

        const hasStage = user?.statut === "STAGE_TROUVE" || user?.candidatureStage != null;

        if (hasStage) {
            setError("etudiant.offers.internship_found_locked");
            return;
        }

        setIsApplying(true);
        setError(null);

        try {
            onApply?.(offerId);
            await postulerOffre(offerId);
            setApplied(true);
            onApplied?.(offerId);
        } catch (err) {
            console.error("Erreur lors de la candidature:", err);
            if (err?.message === "UNAUTHORIZED" || err?.status === 401) {
                navigate("/login");
                return;
            }
            if (err?.status === 409 || err?.status === 403) {
                setError("etudiant.offers.internship_found_locked");
                return;
            }
            setError("etudiant.offers.apply_error");
        } finally {
            setIsApplying(false);
        }
    };

    const hasStage = user?.statut === "STAGE_TROUVE" || user?.candidatureStage != null;

    if (applied) {
        return (
            <p className="text-center font-semibold text-green-700 py-2">
                {t("offre.application_received")}
            </p>
        );
    }

    if (hasStage) {
        return (
            <p className="text-center font-semibold text-slate-600 py-2">
                {t("etudiant.offers.internship_found_locked")}
            </p>
        );
    }

    return (
        <div>
            {error && (
                <p className={`text-xs mb-1 text-center ${error === "etudiant.offers.internship_found_locked" ? "text-slate-600" : "text-red-600"}`}>{t(error)}</p>
            )}
            <Button role="postuler" icon={true} onClick={handleApply} disabled={isApplying} className="w-full">
                {isApplying ? (
                    <span className="loading loading-spinner loading-sm"></span>
                ) : (
                    t("offre.apply")
                )}
            </Button>
        </div>
    );
}

export default PostulerButton;
