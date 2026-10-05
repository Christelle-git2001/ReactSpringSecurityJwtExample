import React, { useState } from 'react';
import { Document, Page, pdfjs } from 'react-pdf';
import { useTranslation } from "react-i18next";
import { FiX } from "react-icons/fi";

pdfjs.GlobalWorkerOptions.workerSrc = `//unpkg.com/pdfjs-dist@${pdfjs.version}/build/pdf.worker.min.mjs`;

export const PDFContent = ({ cvUrlAAffiche, width }) => {
    const { t } = useTranslation();
    const [numPages, setNumPages] = useState(null);
    const [pageNumber, setPageNumber] = useState(1);

    const onDocumentLoadSuccess = ({ numPages }) => {
        setNumPages(numPages);
    };

    if (!cvUrlAAffiche) {
        return (
            <div className="p-4 text-center">
                <p className="text-gray-500 text-sm">{t("pdfVisio.noFileDetected")}</p>
            </div>
        );
    }

    return (
        <div className="flex flex-col items-center border p-4 rounded-xl shadow-xs bg-base-100 overflow-hidden w-full">
            <div className="w-full flex justify-center overflow-auto max-h-[70vh]">
                <Document file={cvUrlAAffiche} onLoadSuccess={onDocumentLoadSuccess}>
                    <Page
                        pageNumber={pageNumber}
                        renderTextLayer={false}
                        renderAnnotationLayer={false}
                        width={width || Math.min(window.innerWidth * 0.7, 800)}
                    />
                </Document>
            </div>
            {numPages && (
                <div className="flex items-center gap-4 mt-4 shrink-0">
                    <button
                        type="button"
                        className="btn btn-sm"
                        disabled={pageNumber <= 1}
                        onClick={() => setPageNumber((prev) => prev - 1)}
                    >
                        {t("button.previous", "Précédent")}
                    </button>
                    <span className="text-sm">{pageNumber}/{numPages}</span>
                    <button
                        type="button"
                        className="btn btn-sm"
                        disabled={pageNumber >= numPages}
                        onClick={() => setPageNumber((prev) => prev + 1)}
                    >
                        {t("button.next", "Suivant")}
                    </button>
                </div>
            )}
        </div>
    );
};

export const PDFVisioneuse = ({ cvUrlAAffiche, onClose }) => {
    return (
        <div className="modal-box w-11/12 max-w-5xl max-h-[90vh] flex flex-col bg-[radial-gradient(circle_at_top_left,#000CCB33,#00CCCFFF,#000CCB33)]">
            <button
                type="button"
                className="btn btn-sm btn-circle btn-ghost absolute right-2 top-2 z-10"
                onClick={onClose}
            >
                <FiX className="size-5" />
            </button>

            <PDFContent cvUrlAAffiche={cvUrlAAffiche} />
        </div>
    );
};