import React, { useState, useEffect, useRef } from "react";
import { useTranslation } from "react-i18next";
import { FiFileText } from "react-icons/fi";
import { IoEyeSharp } from "react-icons/io5";
import { FaDownload } from "react-icons/fa";
import { PDFVisioneuse } from "./PDFVisioneuse.jsx";
import { declencherTelechargement } from "../../utils/filesUtils.jsx";
import { obtenirOffrePDF } from "../../api/employeur.jsx";

const ShowDocument = ({ offerId, fileName }) => {
    const { t } = useTranslation();
    const [documentPDFUrl, setDocumentPDFUrl] = useState(null);
    const [error, setError] = useState(null);
    const dialogRef = useRef(null);

    const chargerDocumentURL = async () => {
        try {
            const response = await obtenirOffrePDF(offerId);
            if (!response.ok) {
                setError("error.generic");
                return null;
            }
            const blob = await response.blob();
            return URL.createObjectURL(blob);
        } catch (err) {
            setError("error.generic");
            return null;
        }
    };

    const handleViewPDF = async () => {
        let url = documentPDFUrl;
        if (!url) {
            url = await chargerDocumentURL();
            if (url) {
                setDocumentPDFUrl(url);
            }
        }
        if (url && dialogRef.current) {
            dialogRef.current.showModal();
        }
    };

    const handleCloseModal = () => {
        if (dialogRef.current) {
            dialogRef.current.close();
        }
    };

    useEffect(() => {
        return () => {
            if (documentPDFUrl) {
                URL.revokeObjectURL(documentPDFUrl);
            }
        };
    }, [documentPDFUrl]);

    const handleDownload = async () => {
        let url = documentPDFUrl;
        if (!url) {
            url = await chargerDocumentURL();
            if (url) {
                setDocumentPDFUrl(url);
            }
        }
        if (url) {
            declencherTelechargement(url, fileName || `document_offre_${offerId}.pdf`);
        }
    };

    return (
        <div className="w-full">
            <div className="mt-2 bg-purple-200 rounded-4xl flex flex-col justify-center items-center p-3">
                {fileName || offerId ? (
                    <div className="flex justify-around w-full items-center h-full">
                        <FiFileText className="size-10 rounded-box text-red-500 border-0" />

                        <div className="flex-1 ml-4 truncate">
                            <p className="font-medium text-sm text-gray-800 truncate">
                                {fileName || t("offre.default_document_name", "Document de l'offre")}
                            </p>
                        </div>

                        <div className="flex items-center gap-1">
                            <button
                                type="button"
                                className="btn btn-square btn-ghost"
                                onClick={handleViewPDF}
                                title={t("actions.view", "Visualiser")}
                            >
                                <IoEyeSharp className="w-6 h-6 text-teal-500 hover:text-black" />
                            </button>

                            <button
                                type="button"
                                className="btn btn-square btn-ghost"
                                onClick={handleDownload}
                                title={t("actions.download", "Télécharger")}
                            >
                                <FaDownload className="w-5 h-5 text-green-700 hover:text-black" />
                            </button>
                        </div>
                    </div>
                ) : (
                    <div className="flex justify-center w-full">
                        <p className="text-center text-sm text-gray-600">
                            {t("offre.no_document_attached", "Aucun document rattaché à cette offre")}
                        </p>
                    </div>
                )}

                {error && (
                    <div className="mt-1">
                        <p className="text-xs text-red-500">{t(error)}</p>
                    </div>
                )}
            </div>

            <dialog ref={dialogRef} className="modal">
                <PDFVisioneuse
                    cvUrlAAffiche={documentPDFUrl}
                    onClose={handleCloseModal}
                />
                <form method="dialog" className="modal-backdrop">
                    <button type="submit">close</button>
                </form>
            </dialog>
        </div>
    );
};

export default ShowDocument;