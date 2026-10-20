import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { postulerOffre } from "../../api/etudiant.jsx";

function PostulerButton({ offerId, hasApplied = false, onApplied }) {
    const { t } = useTranslation();
    const [applied, setApplied] = useState(hasApplied);
    const [isApplying, setIsApplying] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        setApplied(hasApplied);
    }, [hasApplied]);

    const handleApply = async (e) => {
        e?.stopPropagation?.();
        setIsApplying(true);
        setError(null);
        try {
            await postulerOffre(offerId);
            setApplied(true);
            onApplied?.(offerId);
        } catch (err) {
            console.error("Erreur lors de la candidature:", err);
            if (err?.status === 409) {
                setApplied(true);
                onApplied?.(offerId);
            } else {
                setError("etudiant.offers.apply_error");
            }
        } finally {
            setIsApplying(false);
        }
    };

    if (applied) {
        return (
            <p className="text-center font-semibold text-green-700 py-2">
                {t("offre.already_applied")}
            </p>
        );
    }

    return (
        <div>
            {error && (
                <p className="text-xs text-red-600 mb-1 text-center">{t(error)}</p>
            )}
            <button
                type="button"
                className="btn btn-block bg-[#66E5E4] text-[#043462] hover:bg-[#00CCCB]"
                onClick={handleApply}
                disabled={isApplying}
            >
                {isApplying ? (
                    <span className="loading loading-spinner loading-sm"></span>
                ) : (
                    t("offre.apply")
                )}
            </button>
        </div>
    );
}

export default PostulerButton;