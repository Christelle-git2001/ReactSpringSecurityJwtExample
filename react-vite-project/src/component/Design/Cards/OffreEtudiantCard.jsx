import React from 'react';
import { useTranslation } from 'react-i18next';
import PdfActionButtons from "../../PDF/PdfActionButtons.jsx";
import PdfPreviewDialog from "../../PDF/PdfPreviewDialog.jsx";
import { usePdfDocument } from "../../../utils/filesUtils.jsx";
import { obtenirOffrePDF } from "../../../api/employeur.jsx";
import PostulerButton from "../PostulerButton.jsx";
import {FaCheckCircle} from "react-icons/fa";

function OffreEtudiantCard({ offer, onView, hasApplied , onApplied }) {
    const { t } = useTranslation();

    const offre = [
        { label: t("offre.employeur"), value: offer?.employeur?.businessName},
        { label: t("offre.lieu"), value: offer?.employeur?.town},
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

    return (
        <>
            <div className="card w-full max-w-sm bg-base-100 gap-6 shadow-sm bg-[radial-gradient(circle_at_top_left,_#00CCCB33,_transparent_70%)]">

                {hasApplied && (
                    <div className="absolute top-3 right-3 z-10 flex items-center gap-1.5 rounded-full bg-green-100 px-3 py-1 text-xs font-semibold text-green-700 shadow-sm border border-green-200">
                        <FaCheckCircle className="size-3.5" />
                        <span>{t("offre.application_sent", "Candidature envoyée")}</span>
                    </div>
                )}

                <div className="card-body">
                    <PdfActionButtons
                        onView={viewPDF}
                        onDownload={() => gererTelechargement(offer?.fileName)}
                    />

                    <div className="flex justify-between items-center gap-2 mt-2">
                        <div className="flex items-center gap-2 flex-wrap">
                            <h2 className="text-2l font-bold text-[#043462]">
                                {offer?.title}
                            </h2>
                        </div>

                        <span className="text-2l font-bold shrink-0">
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
                        offerId={offer.id}
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