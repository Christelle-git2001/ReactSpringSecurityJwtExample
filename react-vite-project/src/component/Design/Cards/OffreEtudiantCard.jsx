import React from 'react';
import { useTranslation } from 'react-i18next';

function OffreEtudiantCard({ offer, onView, onEdit }) {
    const { t } = useTranslation();

    const offre = [
        { label: t("offre.employeur"), value: offer?.employeur?.businessName},
        { label: t("offre.lieu"), value: offer?.employeur?.town},
        { label: t("offre.date_debut"), value: offer?.startDate },
        { label: t("offre.date_fin"), value: offer?.endDate },
        { label: t("offre.date_fin_affichage"), value: offer?.displayEndDate },
    ];

    return (
        <div className="card w-96 bg-base-100 shadow-sm bg-[radial-gradient(circle_at_top_left,_#00CCCB33,_transparent_70%)]">
            <div className="card-body">
                <div className="flex justify-between items-baseline mt-2">
                    <h2 className="text-2l font-bold text-[#043462]">{offer?.title}</h2>
                    <span className="text-2l font-bold">{offer?.salary} $ / h</span>
                </div>

                <ul className="mt-6 flex flex-col gap-2 text-xs">
                    {offre.map((item, index) => (
                        <li key={index}>
                            <span>
                                <strong className="text-[#043462]">{item.label}</strong> : {item.value}
                            </span>
                        </li>
                    ))}
                </ul>

                <label
                    htmlFor="my-drawer-5"
                    className="btn bg-[#66E5E4] btn-block text-[#043462]"
                    onClick={() => onView?.(offer)}
                >
                    {t("offre.details")}
                </label>
            </div>
        </div>
    );
}

export default React.memo(OffreEtudiantCard);
