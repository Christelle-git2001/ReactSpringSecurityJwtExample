import React from 'react';
import { useTranslation } from 'react-i18next';
import { FiEdit } from 'react-icons/fi';

function Card({ offer, onView, onEdit }) {
    const { t } = useTranslation();
     const STATUS_CONFIG = {
        EN_ATTENTE: {
            label: t("status.en_attente"),
            className: "badge-warning"
        },
        ACCEPTEE: {
            label: t("status.acceptee"),
            className: "badge-success"
        },
        REFUSEE: {
            label: t("status.refusee"),
            className: "badge-error"
        }
    };

    const currentStatusKey = offer?.statut || offer?.status;
    const status = STATUS_CONFIG[offer?.statut] || STATUS_CONFIG.EN_ATTENTE;
    const canEdit = Boolean(onEdit) && currentStatusKey === "EN_ATTENTE";

    const offre = [
        { label: t("offre.domaine"), value: offer?.domain },
        { label: t("offre.date_debut"), value: offer?.startDate },
        { label: t("offre.date_fin"), value: offer?.endDate },
        { label: t("offre.document"), value: offer?.fileName || "-" },
    ];

    return (
        <div className="card w-96 bg-base-100 shadow-sm  bg-[radial-gradient(circle_at_top_left,_#00CCCB33,_transparent_70%)]">
        <div className="card-body">
                <div className="flex justify-between items-center">
                    <span className={`badge badge-xs ${status.className}`}>
                        {status.label}
                    </span>
                    {canEdit && (
                    <button
                        type="button"
                        onClick={() => onEdit?.(offer)}
                        className="btn btn-ghost btn-xs btn-circle text-base-content/70 hover:text-primary"
                        aria-label={t("offre.edit")}
                    >
                        <FiEdit className="size-4" />
                    </button>)}
                </div>
                <div className="flex justify-between items-baseline mt-2">
                    <h2 className="text-2l font-bold text-[#043462]">{offer?.title}</h2>
                    <span className="text-2l font-bold">{offer?.salary} $ / h</span>
                </div>
                <ul className="mt-6 flex flex-col gap-2 text-xs">
                    {offre.map((item, index) => (
                        <li key={index}>
                            <span><strong className="text-[#043462]">{item.label}</strong> : {item.value}</span>
                        </li>
                    ))}
                </ul>
                <label
                    htmlFor="my-drawer-5"
                    className="btn bg-[#66E5E4] btn-block text-[#043462]"
                    onClick={() => onView?.(offer)}
                >{t("offre.details")}
                </label>

            </div>
        </div>
    );
}

export default React.memo(Card);
