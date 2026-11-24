import React, { useState, useEffect, useRef } from 'react';
import { Document, Page, pdfjs } from 'react-pdf';
import { useTranslation } from "react-i18next";
import { FiX } from "react-icons/fi";

pdfjs.GlobalWorkerOptions.workerSrc = `//unpkg.com/pdfjs-dist@${pdfjs.version}/build/pdf.worker.min.mjs`;

export const PDFContent = ({ cvUrlAAffiche, enablePageScroll = false }) => {
    const { t } = useTranslation();
    const [numPages, setNumPages] = useState(null);
    const [pageNumber, setPageNumber] = useState(1);

    const containerRef = useRef(null);
    const [containerWidth, setContainerWidth] = useState(null);

    // Mesure la largeur dès que le conteneur existe, et à chaque redimensionnement
    useEffect(() => {
        const el = containerRef.current;
        if (!el) return;
        const observer = new ResizeObserver(([entree]) => {
            setContainerWidth(Math.floor(entree.contentRect.width));
        });
        observer.observe(el);
        return () => observer.disconnect();
    }, [cvUrlAAffiche]);

    const onDocumentLoadSuccess = ({ numPages }) => {
        setNumPages(numPages);
        setPageNumber(1);
    };

    if (!cvUrlAAffiche) {
        return (
            <div className="p-4 text-center">
                <p className="text-gray-500 text-sm">{t("pdfVisio.noFileDetected")}</p>
            </div>
        );
    }

    const largeurPage = containerWidth ? Math.min(containerWidth, 900) : 800;

    return (
        <div ref={containerRef} className="flex flex-col items-center border p-4 rounded-xl shadow-xs bg-base-100 w-full overflow-hidden">
            <div className={`w-full flex justify-center max-w-full ${enablePageScroll ? '' : 'overflow-auto max-h-[70vh]'}`}>
                <Document
                    key={cvUrlAAffiche}
                    file={cvUrlAAffiche}
                    onLoadSuccess={onDocumentLoadSuccess}
                    onLoadError={(err) => console.error("Erreur de chargement du PDF :", err)}
                    loading={<span className="loading loading-spinner loading-md text-[#043462]" />}
                    error={<p className="text-xs text-gray-500 italic p-6">{t("offre.document_error")}</p>}
                    className={enablePageScroll ? "flex flex-col items-center gap-4" : ""}
                >
                    {enablePageScroll && numPages ? (
                        Array.from({ length: numPages }, (_, i) => (
                            <Page
                                key={i + 1}
                                pageNumber={i + 1}
                                renderTextLayer={false}
                                renderAnnotationLayer={false}
                                width={largeurPage}
                                className="border border-gray-200 shadow-sm"
                            />
                        ))
                    ) : (
                        <Page
                            pageNumber={pageNumber}
                            renderTextLayer={false}
                            renderAnnotationLayer={false}
                            width={largeurPage}
                        />
                    )}
                </Document>
            </div>

            {!enablePageScroll && numPages > 1 && (
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
    const { t } = useTranslation();
    return (
        <div className="modal-box w-11/12 max-w-5xl max-h-[90vh] flex flex-col bg-[radial-gradient(circle_at_top_left,#000CCB33,#00CCCFFF,#000CCB33)]">
            <button
                className="close-button absolute right-2 top-2 z-10"
                type="button"
                onClick={onClose}
                aria-label={t("drawer.close")}
            >
                <FiX className="size-5" aria-hidden="true" />
            </button>

            <PDFContent cvUrlAAffiche={cvUrlAAffiche} />
        </div>
    );
};