import { useTranslation } from "react-i18next";
import { obtenirCvPDF, suppressionCv } from "../../api/etudiant.jsx";
import React, { useState } from "react";
import { FaTrashAlt } from "react-icons/fa";
import PdfActionButtons from "../PDF/PdfActionButtons.jsx";
import { PDFVisioneuse } from "../PDF/PDFVisioneuse.jsx";
import {usePdfDocument} from "../../utils/filesUtils.jsx";
import { getStatusConfig } from "../../utils/getStatusConfig.jsx";
import { formaterDate, formaterTailleFichier } from "../../utils/cvFormatters.jsx";

const ShowCv = ({ cv, onCvDeleted }) => {
    const { t } = useTranslation();
    const [deleteError, setDeleteError] = useState(null);

    const status = getStatusConfig(cv?.statut, t, "cv");

    const {
        pdfUrl,
        error,
        dialogRef,
        viewPDF,
        fermerModal,
        gererTelechargement,
    } = usePdfDocument({
        loadPdfResponse: obtenirCvPDF,
        fileNameFallback: cv?.fileName,
    });

    const supprimerCv = async () => {
        try {
            await suppressionCv();
            if (onCvDeleted) {
                onCvDeleted();
            }
            setDeleteError(null);
        } catch (error) {
            switch (error?.status) {
                case 404:
                    setDeleteError("etudiant.noCvFound");
                    break;
                case 401:
                    navigate("/login")
                    break;
                default:
                    setDeleteError("error.generic");
                    break;
            }
        }
    };

    const currentError = error || deleteError;

    return (
        <div className="w-full">
            <div className="document-card mt-5">
                {cv?.id ? (
                    <>
                         <span className={`badge badge-xs ${status.className}`}>
                        {status.label}
                        </span>
                        <div className="document-card-info">
                            <p className="document-card-name">{cv?.fileName}</p>
                            <p className="document-card-details">
                                PDF · {formaterTailleFichier(cv?.fileSize)} · {formaterDate(cv?.uploadDate)}
                            </p>
                        </div>
                        <div className="flex shrink-0 items-center gap-3">
                            <PdfActionButtons
                                variant="card"
                                onView={viewPDF}
                                onDownload={() => gererTelechargement(cv?.fileName)}
                            />
                            {cv?.statut !== "ACCEPTEE" && (
                                <button
                                    type="button"
                                    className="p-2 text-red-900 cursor-pointer hover:text-red-700"
                                    onClick={supprimerCv}
                                    title={t("actions.delete", "Supprimer")}
                                >
                                    <FaTrashAlt className="size-5" aria-hidden="true" />
                                </button>
                            )}
                        </div>
                    </>
                ) : (
                    <div className="flex justify-center w-full">
                        <p className="text-center">{t("etudiant.noCVUpload")}</p>
                    </div>
                )}
                {currentError && (
                    <div className="mt-1">
                        <p className="text-xs text-red-500">{t(currentError)}</p>
                    </div>
                )}
            </div>

            {cv?.id && (
                <div className=" rounded-lg px-3 py-2 text-left">
                    {cv?.rejectionComment && (
                        <div className="mt-2">
                            <p className=" font-bold text-[#043462]">
                                {t("cv.previous_rejection_comment")}
                            </p>
                            <p className="mt-1 whitespace-pre-line text-gray-800">
                                {cv.rejectionComment}
                            </p>
                        </div>
                    )}
                </div>
            )}

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

export default ShowCv;