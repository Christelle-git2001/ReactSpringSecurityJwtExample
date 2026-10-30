import React from "react";
import { useTranslation } from "react-i18next";
import PdfActionButtons from "./PdfActionButtons.jsx";
import { PDFVisioneuse } from "./PDFVisioneuse.jsx";
import { obtenirOffrePDF } from "../../api/employeur.jsx";
import { usePdfDocument } from "../../utils/filesUtils.jsx";

const ShowDocument = ({ offerId, fileName, loadPdfResponse }) => {
    const { t } = useTranslation();

    const {
        pdfUrl,
        error,
        dialogRef,
        viewPDF,
        fermerModal,
        gererTelechargement,
    } = usePdfDocument({
        loadPdfResponse: loadPdfResponse || (() => obtenirOffrePDF(offerId)),
        fileNameFallback: fileName || `document_offre_${offerId}.pdf`,
    });

    return (
        <div className="w-full">
            <div className="document-card document-card-navy mt-2">
                {fileName || offerId  || loadPdfResponse ? (
                    <>
                        <div className="document-card-info">
                            <p className="document-card-name">
                                {fileName || t("offre.default_document_name", "Document de l'offre")}
                            </p>
                            <p className="document-card-details">PDF</p>
                        </div>
                        <PdfActionButtons
                            variant="card"
                            onView={viewPDF}
                            onDownload={() => gererTelechargement(fileName)}
                        />
                    </>
                ) : (
                    <div className="flex justify-center w-full">
                        <p className="text-center text-sm text-white/80">
                            {t("offre.no_document_attached", "Aucun document rattaché à cette offre")}
                        </p>
                    </div>
                )}

                {error && (
                    <div className="mt-1">
                        <p className="text-xs text-red-200">{t(error)}</p>
                    </div>
                )}
            </div>

            <dialog ref={dialogRef} className="modal">
                <PDFVisioneuse
                    cvUrlAAffiche={pdfUrl}
                    onClose={fermerModal}
                />
                <form method="dialog" className="modal-backdrop">
                    <button type="submit">close</button>
                </form>
            </dialog>
        </div>
    );
};

export default ShowDocument;