import React from 'react';
import { useTranslation } from 'react-i18next';
import { FiEdit, FiStar, FiUser } from 'react-icons/fi';
import { getDepartmentKey } from "../../../utils/departementConverter.js";
import { obtenirOffrePDF } from "../../../api/employeur.jsx";
import { usePdfDocument } from "../../../utils/filesUtils.jsx";
import PdfActionButtons from "../../PDF/PdfActionButtons.jsx";
import PdfPreviewDialog from "../../PDF/PdfPreviewDialog.jsx";
import { getStatusConfig } from "../../../utils/getStatusConfig.jsx";

function OffreStageCard({ offer, candidatures = [], onViewCandidature, onEdit = false, showPdfActions = true }) {
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

    const candidaturesOffre = candidatures.filter(c => c.offreId === offer?.id);

    const offre = [
        { label: t("offre.domaine"), value: translatedDepartment },
        { label: t("offre.date_debut"), value: offer?.startDate },
        { label: t("offre.date_fin"), value: offer?.endDate },
        { label: t("offre.date_fin_affichage"), value: offer?.displayEndDate },
    ];

    return (
        <>
            <div className="card relative w-full max-w-sm bg-base-100 gap-6 shadow-sm bg-[radial-gradient(circle_at_top_left,_#00CCCB33,_transparent_70%)]">

                {offer?.isTarget && (
                    <span className="absolute top-3 right-3 badge badge-xs bg-amber-200 text-amber-800 border border-amber-300 flex items-center gap-1">
                        <FiStar className="size-3 text-amber-600" />
                                        {t("offre.targeted")}
                    </span>
                )}

                <div className="card-body">
                    <div className="flex justify-between items-center">
                        <div className="flex flex-col gap-2">
                            <span className={`badge badge-xs ${status.className}`}>
                                {status.label}
                            </span>
                            {showPdfActions && (
                                <PdfActionButtons variant="card" ghost
                                    onView={viewPDF}
                                    onDownload={() => gererTelechargement(offer?.fileName)}
                                />
                            )}
                        </div>

                        {canEdit && (
                            <button
                                type="button"
                                onClick={() => onEdit?.(offer)}
                                className="btn btn-ghost btn-xs btn-circle text-base-content/70 hover:text-primary"
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

                    {offer?.description && (
                        <details open className="collapse collapse-arrow mt-2 rounded-none text-xs">
                            <summary className="collapse-title min-h-0 py-1 pl-0 text-xs font-bold text-[#043462]">
                                {t("offre.description")}
                            </summary>
                            <div className="collapse-content p-0">
                                <div className="mt-1 rounded-r-md border-l-[3px] border-[#00CCCB] bg-[#00CCCB]/5 px-3 py-2">
                                    <p className="leading-relaxed whitespace-pre-line break-words text-gray-700">
                                        {offer.description}
                                    </p>
                                </div>
                            </div>
                        </details>
                    )}

                    {currentStatusKey === "REFUSEE" && offer?.rejectionComment && (
                        <div className="mt-2 text-xs">
                            <p className="py-1 text-xs font-bold text-red-700">
                                {t("offre.rejection_comment")}
                            </p>
                            <div className="p-0">
                                <div className="mt-1 bg-red-50 border border-red-200 p-3 rounded-lg">
                                    <p className="text-red-600 break-words">
                                        {offer.rejectionComment}
                                    </p>
                                </div>
                            </div>
                        </div>
                    )}

                    <details className="collapse collapse-arrow mt-4 rounded-none border-t border-gray-200">
                        <summary className="collapse-title min-h-0 py-3 pl-0 text-xs font-bold text-[#043462]">
                            {t("candidature.list_title")} ({candidaturesOffre.length})
                        </summary>
                        <div className="collapse-content px-0">
                            {candidaturesOffre.length === 0 ? (
                                <p className="text-xs text-gray-500 italic">{t("candidature.no_candidatures")}</p>
                            ) : (
                                <ul className="max-h-48 space-y-1 overflow-y-auto">
                                    {candidaturesOffre.map(candidature => (
                                        <li key={candidature.id}>
                                            <button
                                                type="button"
                                                onClick={() => onViewCandidature?.(candidature)}
                                                className="flex w-full items-center gap-2 rounded p-2 text-left text-sm text-[#043462] hover:bg-blue-50 hover:underline"
                                            >
                                                <FiUser className="size-4 shrink-0" aria-hidden="true" />
                                                <span>{candidature.etudiantFirstName} {candidature.etudiantLastName}</span>
                                            </button>
                                        </li>
                                    ))}
                                </ul>
                            )}
                        </div>
                    </details>
                </div>

                {showPdfActions && (
                    <PdfPreviewDialog
                        dialogRef={dialogRef}
                        pdfUrl={pdfUrl}
                        onClose={fermerModal}
                    />
                )}
            </div>
        </>
    );
}

export default React.memo(OffreStageCard);
