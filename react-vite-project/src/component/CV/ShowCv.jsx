import { useTranslation } from "react-i18next";
import { obtenirCvPDF, suppressionCv } from "../../api/etudiant.jsx";
import React, { useState } from "react";
import { FiFileText } from "react-icons/fi";
import { IoEyeSharp } from "react-icons/io5";
import { FaDownload, FaTrashAlt } from "react-icons/fa";
import { PDFVisioneuse } from "../PDF/PDFVisioneuse.jsx";
import {usePdfDocument} from "../../utils/filesUtils.jsx";
import { getStatusConfig } from "../../utils/getStatusConfig.jsx";
import { formaterDate, formaterTailleFichier } from "../../utils/cvFormatters.jsx";

const ShowCv = ({ cv, onCvDeleted }) => {
    const { t } = useTranslation();
    const [deleteError, setDeleteError] = useState(null);

    const status = getStatusConfig(cv?.statut, t);

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
                    setDeleteError("token.doesntExist");
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
            <div className="mt-5 bg-purple-200 rounded-4xl flex flex-col justify-center items-center p-2">
                {cv?.id ? (
                    <div className="flex justify-around w-full items-center h-full">
                        <FiFileText className="size-10 rounded-box text-red-500 border border-t-0" />
                        <div>
                            {cv?.fileName}
                            <div className="text-xs uppercase font-semibold opacity-60">
                                {formaterDate(cv?.uploadDate)}
                            </div>
                        </div>
                        <div>
                            <div>{formaterTailleFichier(cv?.fileSize)}</div>
                        </div>
                        <button
                            type="button"
                            className="btn btn-square btn-ghost"
                            onClick={viewPDF}
                            title={t("actions.view", "Visualiser")}
                        >
                            <IoEyeSharp className="w-full h-full text-teal-500 hover:text-black" />
                        </button>
                        <button
                            type="button"
                            onClick={() => gererTelechargement(cv?.fileName)}
                            className="btn btn-ghost"
                            title={t("actions.download", "Télécharger")}
                        >
                            <FaDownload className="w-full h-full text-green-700 hover:text-black" />
                        </button>
                        {cv?.statut !== "ACCEPTEE"  && (
                            <button
                                type="button"
                                className="btn btn-square btn-ghost"
                                onClick={supprimerCv}
                                title={t("actions.delete", "Supprimer")}
                            >
                                <FaTrashAlt className="w-full h-full text-red-900 hover:text-black" />
                            </button>
                        )}
                    </div>
                ) : (
                    <div className="flex justify-center w-full">
                        <p className="text-center">{t("etudiant.noCVUpload")}</p>
                    </div>
                )}
                {currentError && (
                    <div className="mt-1">
                        <p className="text-xs text-red-500">{t(curentError)}</p>
                    </div>
                )}
            </div>

            {cv?.id && (
                <div className=" rounded-lg px-3 py-2 text-left">
                    <span className={`badge badge-xs ${status.className}`}>
                        {status.label}
                    </span>
                    {cv?.rejectionComment && (
                        <div className="mt-2">
                            <p className="text-xs font-bold text-[#043462]">
                                {t("cv.previous_rejection_comment")}
                            </p>
                            <p className="mt-1 whitespace-pre-line text-sm text-gray-800">
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