import React from 'react';
import { useTranslation } from 'react-i18next';
import PdfActionButtons from "../../PDF/PdfActionButtons.jsx";
import PdfPreviewDialog from "../../PDF/PdfPreviewDialog.jsx";
import { usePdfDocument } from "../../../utils/filesUtils.jsx";
import { obtenirOffrePDF } from "../../../api/employeur.jsx";
import PostulerButton from "../PostulerButton.jsx";
import { FiStar } from "react-icons/fi";

function OffreEtudiantCard({ offer, onRead, hasApplied, onApplied }) {
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

    return (
        <>
            <div id={`offre-${offer?.id}`} onClick={(event) => {
                if (!event.target.closest("button, summary")) onRead?.(offer?.id);
            }} className="card relative w-full max-w-sm bg-base-100 gap-6 shadow-sm bg-[radial-gradient(circle_at_top_left,_#00CCCB33,_transparent_70%)]">

                {isTargeted && (
                    <div className="absolute top-3 right-3 z-10">
                        <span
                            title={targetedLabelText}
                            className="badge badge-xs bg-amber-200 text-amber-800 border border-amber-300 flex items-center gap-1"
                        >
                            <FiStar className="size-3 text-amber-600 fill-amber-500" />
                            <span>{targetedLabelText}</span>
                        </span>
                    </div>
                )}

                <div className="card-body">
                    <div className="flex justify-between items-center">
                        <div className="flex flex-col gap-2">
                            <PdfActionButtons
                                onView={viewPDF}
                                onDownload={() => gererTelechargement(offer?.fileName)}
                            />
                        </div>
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

                    <PostulerButton
                        offerId={offer?.id}
                        hasApplied={hasApplied}
                        onApplied={(id) => {
                            onApplied?.(id);
                            onRead?.(id);
                        }}
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