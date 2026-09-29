import React, { useState } from 'react';
import { Document, Page, pdfjs } from 'react-pdf';
import { useTranslation } from "react-i18next";


const PDFVisioneuseTest = () => {
    const { t } = useTranslation();
    pdfjs.GlobalWorkerOptions.workerSrc = `//unpkg.com/pdfjs-dist@${pdfjs.version}/build/pdf.worker.min.mjs`;
    const [veutVisualiser, setveutVisualiser] = useState(false)
    const [pdfFile, setPdfFile] = useState(null);
    const [numPages, setNumPages] = useState(null);
    const [nameFile, setNameFiles] = useState("")
    const [pageNumber, setPageNumber] = useState(1);

    const handleFileChange = (e) => {
        const file = e.target.files[0];
        if (file && file.type === "application/pdf") {
            setNameFiles(file.name)
            setPdfFile(URL.createObjectURL(file));
            setPageNumber(1);
            setveutVisualiser(false)
        }
    };

    const onDocumentLoadSuccess = ({ numPages }) => {
        setNumPages(numPages);
    };
    return (
        <div className="p-6 max-w-2xl mx-auto space-y-4">
            {!pdfFile ? (
                <div>
                    <input
                        id="pdf-upload"
                        type="file"
                        accept="application/pdf"
                        onChange={handleFileChange}
                        className="hidden"
                    />
                    <label htmlFor="pdf-upload" className="btn btn-primary cursor-pointer">
                        {t("pdfVisio.choice")}
                    </label>
                </div>
            ):(
                <div className={"grid grid-cols-12"}>
                    <div className={"col-span-12"}>
                        <p className="mb-5">{t("pdfVisio.filesName")} : {nameFile}</p>
                    </div>
                    <div className={"col-span-12 mb-3 lg:col-span-4 lg:mb-0"}>
                        <button type="button" onClick={() => setveutVisualiser(!veutVisualiser)} className={"btn btn-primary"}>{t("pdfVisio.visualize")}</button>
                    </div>
                    <div className={"col-span-12 mb-3 lg:col-span-4 lg:mb-0"}>
                            <input
                                id="pdf-upload"
                                type="file"
                                accept="application/pdf"
                                onChange={handleFileChange}
                                className="hidden"
                            />
                            <label htmlFor="pdf-upload" className="btn btn-primary cursor-pointer">
                                {t("pdfVisio.choice")}
                            </label>
                    </div>
                    <div className={"col-span-12 mb-3 lg:col-span-4 lg:mb-0"}>
                        <a
                            href={pdfFile}
                            download={nameFile || "document.pdf"}
                            className="btn btn-secondary"
                        >
                            {t("pdfVisio.download")}
                        </a>
                    </div>
                </div>
            )}

            {pdfFile && veutVisualiser && (
                <div className="flex flex-col items-center border p-4 rounded-xl shadow-xs bg-base-100">
                    <Document file={pdfFile} onLoadSuccess={onDocumentLoadSuccess}>
                        <Page
                            pageNumber={pageNumber}
                            renderTextLayer={false}
                            renderAnnotationLayer={false}
                        />
                    </Document>

                    {numPages && (
                        <div className="flex items-center gap-4 mt-4">
                            <button
                                className="btn btn-sm"
                                disabled={pageNumber <= 1}
                                onClick={() => setPageNumber(prev => prev - 1)}
                            >
                                Précédent
                            </button>

                            <span className="text-sm">
                                Page {pageNumber} sur {numPages}
                            </span>

                            <button
                                className="btn btn-sm"
                                disabled={pageNumber >= numPages}
                                onClick={() => setPageNumber(prev => prev + 1)}
                            >
                                Suivant
                            </button>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
};export default PDFVisioneuseTest