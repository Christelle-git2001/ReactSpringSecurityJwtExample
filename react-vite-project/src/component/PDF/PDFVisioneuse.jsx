import React, { useState } from 'react';
import { Document, Page, pdfjs } from 'react-pdf';
import { useTranslation } from "react-i18next";
import {FiX} from "react-icons/fi";


const PDFVisioneuse = () => {
    const { t } = useTranslation();
    pdfjs.GlobalWorkerOptions.workerSrc = `//unpkg.com/pdfjs-dist@${pdfjs.version}/build/pdf.worker.min.mjs`;
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
            ) : (
                <div className={"grid grid-cols-12"}>
                    <div className={"col-span-12"}>
                        <p className="mb-5">{t("pdfVisio.filesName")} : {nameFile}</p>
                    </div>
                    <div className={"col-span-12 mb-3 lg:col-span-4 lg:mb-0"}>
                        <button type="button" onClick={() => pdfModal.showModal()}
                                className={"btn bg-[#0FFFDF] hover:bg-teal-600 hover:text-white"}>{t("pdfVisio.visualize")}</button>
                    </div>
                    <div className={"col-span-12 mb-3 lg:col-span-4 lg:mb-0"}>
                        <input
                            id="pdf-upload"
                            type="file"
                            accept="application/pdf"
                            onChange={handleFileChange}
                            className="hidden"
                        />
                        <label htmlFor="pdf-upload"
                               className="btn bg-[#0FFFDF] hover:bg-teal-600 hover:text-white cursor-pointer">
                            {t("pdfVisio.choice")}
                        </label>
                    </div>
                    <div className={"col-span-12 mb-3 lg:col-span-4 lg:mb-0"}>
                        <a
                            href={pdfFile}
                            download={nameFile || "document.pdf"}
                            className="btn bg-[#0FFFDF] hover:bg-teal-600 hover:text-white"
                        >
                            {t("pdfVisio.download")}
                        </a>
                    </div>
                </div>
            )}

            <dialog id="pdfModal" className="modal">
                <div className="modal-box w-11/12 max-w-5xl max-h-[90vh] flex flex-col bg-[radial-gradient(circle_at_top_left,#000CCB33,#00CCCFFF,#000CCB33)]">
                    <form method="dialog">
                        <button className="btn btn-sm btn-circle btn-ghost absolute right-2 top-2"><FiX></FiX></button>
                    </form>
                    <div className="flex flex-col items-center border p-4 rounded-xl shadow-xs bg-base-100 overflow-hidden w-full">
                        <div className="w-full flex justify-center overflow-auto max-h-[70vh]">
                            <Document file={pdfFile} onLoadSuccess={onDocumentLoadSuccess}>
                                <Page
                                    pageNumber={pageNumber}
                                    renderTextLayer={false}
                                    renderAnnotationLayer={false}
                                    width={Math.min(window.innerWidth * 0.7, 800)}
                                />
                            </Document>
                        </div>
                        {numPages && (
                            <div className="flex items-center gap-4 mt-4 shrink-0">
                                <button className="btn btn-sm" disabled={pageNumber <= 1} onClick={() => setPageNumber(prev => prev - 1)}>
                                    {t("button.previous")}
                                </button>
                                <span className="text-sm">{pageNumber}/{numPages}</span>
                                <button className="btn btn-sm" disabled={pageNumber >= numPages} onClick={() => setPageNumber(prev => prev + 1)}>
                                    {t("button.next")}
                                </button>
                            </div>
                        )}
                    </div>
                </div>

                <form method="dialog" className="modal-backdrop">
                    <button>close</button>
                </form>
            </dialog>
        </div>
    );
};
export default PDFVisioneuse