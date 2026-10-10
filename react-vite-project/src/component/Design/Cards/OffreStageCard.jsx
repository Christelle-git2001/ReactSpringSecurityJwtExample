import React from 'react';
import { useTranslation } from 'react-i18next';
import { FiEdit } from 'react-icons/fi';
import { getDepartmentKey } from "../../../utils/departementConverter.js";
import { obtenirOffrePDF } from "../../../api/employeur.jsx";
import { usePdfDocument } from "../../../utils/filesUtils.jsx";
import PdfActionButtons from "../../PDF/PdfActionButtons.jsx";
import PdfPreviewDialog from "../../PDF/PdfPreviewDialog.jsx";
import {getStatusConfig} from "../../../utils/getStatusConfig.jsx";

function OffreStageCard({ offer, onView, onEdit }) {
    const { t } = useTranslation();

    const {
        pdfUrl,
        dialogRef,
        viewPDF,
        fermerModal,
        gererTelechargement,
    } = usePdfDocument({
        loadPdfResponse: () => obtenirOffrePDF(offer?.id),
        fileNameFallback: offer?.fileName || `offre_${offer?.id}.pdf`,
    });

    const currentStatusKey = offer?.statut || offer?.status;
    const status = getStatusConfig(currentStatusKey, t);
    const canEdit = Boolean(onEdit) && currentStatusKey === "EN_ATTENTE";

    const departmentKey = getDepartmentKey(offer?.domain);
    const translatedDepartment = departmentKey
        ? t(`departement.${departmentKey}`, offer?.domain)
        : offer?.domain;

    const offre = [
        { label: t("offre.domaine"), value: translatedDepartment },
        { label: t("offre.date_debut"), value: offer?.startDate },
        { label: t("offre.date_fin"), value: offer?.endDate },
        { label: t("offre.date_fin_affichage"), value: offer?.displayEndDate },
        { label: t("offre.document"), value: offer?.fileName || "-" },
    ];

    return (
        <>
            <div className="card w-96 bg-base-100 shadow-sm bg-[radial-gradient(circle_at_top_left,_#00CCCB33,_transparent_70%)]">
                <div className="card-body">
                    <div className="flex justify-between items-center">
                        <div className="flex flex-col gap-2">
                            <span className={`badge badge-xs ${status.className}`}>
                                {status.label}
                            </span>

                            <PdfActionButtons
                                onView={viewPDF}
                                onDownload={() => gererTelechargement(offer?.fileName)}
                            />
                        </div>

                        {canEdit && (
                            <button
                                type="button"
                                onClick={() => onEdit?.(offer)}
                                className="btn btn-ghost btn-xs btn-circle text-base-content/70 hover:text-primary"
                                aria-label={t("offre.edit")}
                            >
                                <FiEdit className="size-4" />
                            </button>
                        )}
                    </div>

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

                    {onView && (
                        <label
                            htmlFor="my-drawer-5"
                            className="btn bg-[#66E5E4] btn-block text-[#043462]"
                            onClick={() => onView?.(offer)}
                        >
                            {t("offre.details")}
                        </label>
                    )}
                </div>

                <PdfPreviewDialog
                    dialogRef={dialogRef}
                    pdfUrl={pdfUrl}
                    onClose={fermerModal}
                />
            </div>
        </>
    );
}

export default React.memo(OffreStageCard);
