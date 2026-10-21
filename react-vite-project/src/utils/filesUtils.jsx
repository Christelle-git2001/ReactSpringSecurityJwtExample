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

export function usePdfDocument({ loadPdfResponse, fileNameFallback = "document.pdf" }) {
    const [pdfUrl, setPdfUrl] = useState(null);
    const [error, setError] = useState(null);
    const dialogRef = useRef(null);
    const navigate = useNavigate();


    const obtenirCVUrlPDF = async () => {
        try {
            const response = await
                loadPdfResponse();
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
            const url = URL.createObjectURL(data);

            setPdfUrl(url);
            setError(null);

            return url;
        } catch  {
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
        }
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
        }
    };

    useEffect(() => {
        return () => {
            if (pdfUrl) {
                URL.revokeObjectURL(pdfUrl);
            }
        };
    }, [pdfUrl]);

    return {
        pdfUrl,
        error,
        dialogRef,
        obtenirCVUrlPDF,
        viewPDF,
        fermerModal,
        gererTelechargement,
    };
}