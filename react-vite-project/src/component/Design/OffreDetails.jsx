import { useTranslation } from "react-i18next";

const OffreDetails = ({ offre, onClose, onPostuler }) => {
    const { t, i18n } = useTranslation();
    if (!offre) return null;

    const formatDate = (d) => new Date(d).toLocaleDateString(i18n.language);

    return (
        <>
            <div className="fixed inset-0 bg-black/40 z-40" onClick={onClose} />

            <aside className="fixed top-0 right-0 h-full w-96 max-w-full bg-base-100 shadow-xl z-50 p-6 flex flex-col gap-4 overflow-y-auto">
                <button className="btn btn-sm btn-ghost self-end" onClick={onClose}>✕</button>

                <p className="text-sm opacity-70">{offre.employeur?.businessName}</p>
                <h2 className="text-2xl font-bold">{offre.title}</h2>

                <div className="flex flex-wrap gap-2">
                    <span className="badge badge-outline">{offre.salary} $/h</span>
                </div>

                <div>
                    <h3 className="font-semibold">{t("offre.description")}</h3>
                    <p>{offre.description}</p>
                </div>

                <div>
                    <h3 className="font-semibold">{t("offre.period")}</h3>
                    <p>{formatDate(offre.startDate)} {t("etudiant.offers.fromTo")} {formatDate(offre.endDate)}</p>
                </div>

                <div className="mt-auto">
                    <button className="btn btn-accent w-full" onClick={() => onPostuler(offre)}>
                        {t("etudiant.offers.apply_button")}
                    </button>
                </div>
            </aside>
        </>
    );
};

export default OffreDetails;