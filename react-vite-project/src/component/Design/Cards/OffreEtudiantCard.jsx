import React from 'react';
import { useTranslation } from 'react-i18next';
import PdfActionButtons from "../../PDF/PdfActionButtons.jsx";
import PdfPreviewDialog from "../../PDF/PdfPreviewDialog.jsx";
import { usePdfDocument } from "../../../utils/filesUtils.jsx";
import { obtenirOffrePDF } from "../../../api/employeur.jsx";
import PostulerButton from "../PostulerButton.jsx";
import { FaCheckCircle } from "react-icons/fa";
import { FiStar } from "react-icons/fi";

function OffreEtudiantCard({ offer, onView, hasApplied, onApplied }) {
    const { t } = useTranslation();

    const isTargeted = Boolean(offer?.isTarget);

    const offre = [
        { label: t("offre.employeur"), value: offer?.employeur?.businessName },
        { label: t("offre.lieu"), value: offer?.employeur?.town },
        { label: t("offre.date_debut"), value: offer?.startDate },
        { label: t("offre.date_fin"), value: offer?.endDate },
        { label: t("offre.date_fin_affichage"), value: offer?.displayEndDate },
    ];

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

    const targetedLabelText = t("offre.targeted", "Ciblée");
    const applicationSentText = t("offre.application_sent", "Candidature envoyée");

    return (
        <>
            <div className="card relative w-full max-w-sm bg-base-100 gap-6 shadow-sm bg-[radial-gradient(circle_at_top_left,_#00CCCB33,_transparent_70%)]">

                {/* Conteneur pour les badges en haut à droite */}
                <div className="absolute top-3 right-3 z-10 flex flex-wrap items-center justify-end gap-1.5 max-w-[80%]">
                    {/* Badge Offre Ciblée */}
                    {isTargeted && (
                        <span
                            title={targetedLabelText}
                            className="max-w-[140px] truncate badge badge-xs bg-amber-200 text-amber-800 border border-amber-300 flex items-center gap-1 px-2.5 py-2 font-semibold shadow-sm"
                        >
                            <FiStar className="size-3 shrink-0 text-amber-600 fill-amber-500" />
                            <span className="truncate">{targetedLabelText}</span>
                        </span>
                    )}

                    {/* Badge Candidature Envoyée */}
                    {hasApplied && (
                        <div
                            title={applicationSentText}
                            className="max-w-[160px] truncate flex items-center gap-1.5 rounded-full bg-green-100 px-3 py-1 text-xs font-semibold text-green-700 shadow-sm border border-green-200"
                        >
                            <FaCheckCircle className="size-3.5 shrink-0" />
                            <span className="truncate">{applicationSentText}</span>
                        </div>
                    )}
                </div>

                <div className="card-body">
                    <PdfActionButtons
                        onView={viewPDF}
                        onDownload={() => gererTelechargement(offer?.fileName)}
                    />

                    <div className="flex justify-between items-center gap-2 mt-2">
                        <div className="flex items-center gap-2 flex-wrap flex-1">
                            <h2 className="text-2xl font-bold text-[#043462] break-words">
                                {offer?.title}
                            </h2>
                        </div>

                        <span className="text-2xl font-bold shrink-0">
                            {offer?.salary} $ / h
                        </span>
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

                    <PostulerButton
                        offerId={offer?.id}
                        hasApplied={hasApplied}
                        onApplied={onApplied}
                    />
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

export default React.memo(OffreEtudiantCard);