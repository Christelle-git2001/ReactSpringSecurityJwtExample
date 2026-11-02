import { useEffect, useRef, useState } from "react";
import {useNavigate} from "react-router-dom";


export const declencherTelechargement = (fileUrl, filename = "document.pdf") => {
    if (!fileUrl) return;

    const a = document.createElement("a");
    a.style.display = "none";
    a.href = fileUrl;
    a.download = filename;

    document.body.appendChild(a);
    a.click();
    a.remove();
};

export function usePdfDocument({ loadPdfResponse, fileNameFallback = "document.pdf", file = null, documentId, preload = false }) {
    const [pdfUrl, setPdfUrl] = useState(null);
    const [error, setError] = useState(null);
    const [pdfFile, setPdfFile] = useState(null);
    const pdfUrlRef = useRef(null);
    const requestId = useRef(0);
    const loadPdfResponseRef = useRef(loadPdfResponse);
    const dialogRef = useRef(null);
    const navigate = useNavigate();


    const remplacerDocument = (document) => {
        if (pdfUrlRef.current) URL.revokeObjectURL(pdfUrlRef.current);
        const url = document ? URL.createObjectURL(document) : null;
        pdfUrlRef.current = url;
        setPdfFile(document);
        setPdfUrl(url);
        return url;
    };

    const obtenirCVUrlPDF = async () => {
        if (pdfUrlRef.current) return pdfUrlRef.current;
        if (!loadPdfResponseRef.current) return null;
        const currentRequest = requestId.current;
        try {
            const response = await loadPdfResponseRef.current();
            if (currentRequest !== requestId.current) return null;
            if (!response.ok) {
                if (response?.message === "UNAUTHORIZED" || response?.status === 401) {
                    navigate("/login");
                }
                switch (response.status){
                    case 404:
                        setError("pdfVisio.noFileDetected");
                        break;
                    default:
                        setError("error.generic");
                        break;
                }
                return null;
            }
            const data = await response.blob();
            if (currentRequest !== requestId.current) return null;
            const url = remplacerDocument(data);
            setError(null);

            return url;
        } catch  {
            if (currentRequest !== requestId.current) return null;
            setError("error.generic");
            return null;
        }
    };

    const viewPDF = async () => {
        let url = pdfUrl;
        if (!url) {
            url = await obtenirCVUrlPDF();
        }
        if (url && dialogRef.current) {
            dialogRef.current.showModal();
            return true;
        }
        return false;
    };

    const fermerModal = () => {
        if (dialogRef.current) {
            dialogRef.current.close();
        }
    };

    const gererTelechargement = async (filename) => {
        let url = pdfUrl;
        if (!url) {
            url = await obtenirCVUrlPDF();
        }
        if (url) {
            declencherTelechargement(url, filename || fileNameFallback);
            return true;
        }
        return false;
    };

    useEffect(() => {
        loadPdfResponseRef.current = loadPdfResponse;
    }, [loadPdfResponse]);

    useEffect(() => {
        remplacerDocument(file);
        setError(null);
        if (!file && preload) obtenirCVUrlPDF();

        return () => {
            requestId.current += 1;
            if (pdfUrlRef.current) URL.revokeObjectURL(pdfUrlRef.current);
            pdfUrlRef.current = null;
        };
    }, [file, documentId, preload]);

    return {
        pdfUrl,
        pdfFile,
        error,
        dialogRef,
        obtenirCVUrlPDF,
        viewPDF,
        fermerModal,
        gererTelechargement,
    };
}