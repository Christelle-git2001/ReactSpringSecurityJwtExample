import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { FiFileText } from "react-icons/fi";
import { IoEyeSharp } from "react-icons/io5";
import { FaDownload } from "react-icons/fa";
import { PDFVisioneuse } from "../PDF/PDFVisioneuse.jsx";
import { declencherTelechargement } from "../../utils/filesUtils.jsx";

const ShowOffrePdf = ({ offre }) => {
    const { t } = useTranslation();
    const [pdfUrl, setPdfUrl] = useState("");

    // Faux PDF de démonstration (URL temporaire ou fichier de test public)
    const mockPdfUrl = "https://www.w3.org/W3C/DesignIssues/diagrams/sw-horiz-banner.pdf";

    const viewPDF = () => {
        setPdfUrl(mockPdfUrl);
        document.getElementById('pdfModalOffre').showModal();
    };

    const fermerModal = () => {
        const modal = document.getElementById('pdfModalOffre');
        if (modal) modal.close();
        setPdfUrl("");
    };

    const gererTelechargement = () => {
        declencherTelechargement(mockPdfUrl, offre?.fileName || "offre_demo.pdf");
    };

    return (
        <div>
            <div className="mt-2 bg-blue-50 rounded-2xl flex flex-col justify-center items-center p-3 border border-blue-100">
                {offre?.fileName ? (
                    <div className="flex justify-around w-full items-center h-full gap-2">
                        <FiFileText className="size-8 text-blue-600 shrink-0" />
                        <div className="text-left flex-1 min-w-0">
                            <p className="font-semibold text-sm truncate">{offre.fileName}</p>
                            <span className="text-xs text-blue-500 italic">
                                (Mode Simulation)
                            </span>
                        </div>
                        <div className="flex items-center gap-1">
                            <button
                                type="button"
                                className="btn btn-square btn-ghost btn-sm"
                                onClick={viewPDF}
                            >
                                <IoEyeSharp className="w-5 h-5 text-teal-600 hover:text-black" />
                            </button>
                            <button
                                type="button"
                                onClick={gererTelechargement}
                                className="btn btn-square btn-ghost btn-sm"
                            >
                                <FaDownload className="w-4 h-4 text-green-700 hover:text-black" />
                            </button>
                        </div>
                    </div>
                ) : (
                    <div className="flex justify-center w-full p-2">
                        <p className="text-center text-sm text-gray-500">
                            {t("offre.noDocumentAttached") || "Aucun document attaché"}
                        </p>
                    </div>
                )}
            </div>

            <PDFVisioneuse
                modalId="pdfModalOffre"
                cvUrlAAffiche={pdfUrl}
                onClose={fermerModal}
            />
        </div>
    );
};

export default ShowOffrePdf;