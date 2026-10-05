import { useTranslation } from "react-i18next";
import { obtenirCvInfo, obtenirCvPDF, suppressionCv } from "../../api/etudiant.jsx";
import React, { useEffect, useState, useRef } from "react";
import { FiFileText } from "react-icons/fi";
import { IoEyeSharp } from "react-icons/io5";
import { FaDownload, FaTrashAlt } from "react-icons/fa";
import { PDFVisioneuse } from "../PDF/PDFVisioneuse.jsx";
import { declencherTelechargement } from "../../utils/filesUtils.jsx";

const ShowCv = ({ refreshTrigger }) => {
    const { t } = useTranslation();
    const [cv, setCv] = useState({
        id: null,
        fileName: "",
        contentType: "",
        fileSize: 0,
        uploadDate: ""
    });
    const [cvPDFUrl, setCvPDFUrl] = useState(null);
    const [error, setError] = useState(null);
    const dialogRef = useRef(null);

    async function obtenirCVs() {
        try {
            const data = await obtenirCvInfo();
            setCv(data);
            setError(null);
        } catch (error) {
            switch (error?.status) {
                case 404:
                    setError("etudiant.noCvFound");
                    break;
                case 401:
                    setError("token.doesntExist");
                    break;
                default:
                    setError("error.generic");
                    break;
            }
        }
    }

    async function obtenirCVUrlPDF() {
        try {
            const response = await obtenirCvPDF(cv.id);
            if (!response.ok) {
                setError("error.generic");
                return null;
            }
            const data = await response.blob();
            return URL.createObjectURL(data);
        } catch (error) {
            switch (error?.status) {
                case 404:
                    setError("etudiant.noCvFound");
                    break;
                case 401:
                    setError("token.doesntExist");
                    break;
                default:
                    setError("error.generic");
                    break;
            }
            return null;
        }
    }

    const viewPDF = async () => {
        let url = cvPDFUrl;
        if (!url) {
            url = await obtenirCVUrlPDF();
            if (url) {
                setCvPDFUrl(url);
            }
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

    useEffect(() => {
        return () => {
            if (cvPDFUrl) {
                URL.revokeObjectURL(cvPDFUrl);
            }
        };
    }, [cvPDFUrl]);

    const gererTelechargement = async () => {
        let url = cvPDFUrl;
        if (!url) {
            url = await obtenirCVUrlPDF();
            if (url) {
                setCvPDFUrl(url);
            }
        }
        if (url) {
            declencherTelechargement(url, cv.fileName);
        }
    };

    const supprimerCv = async () => {
        try {
            await suppressionCv();
            setCv({
                id: null,
                fileName: "",
                contentType: "",
                fileSize: 0,
                uploadDate: ""
            });
            setCvPDFUrl(null);
        } catch (error) {
            switch (error?.status) {
                case 404:
                    setError("etudiant.noCvFound");
                    break;
                case 401:
                    setError("token.doesntExist");
                    break;
                default:
                    setError("error.generic");
                    break;
            }
        }
    };

    const formaterTailleFichier = (octets) => {
        if (!octets || octets === 0) return "0 KB";
        const kb = octets / 1024;

        if (kb >= 1024) {
            return `${(kb / 1024).toFixed(1)} MB`;
        }
        return `${Math.round(kb)} KB`;
    };

    const formaterDate = (isoString) => {
        if (!isoString) return "";
        const date = new Date(isoString);

        const annee = date.getFullYear();
        const mois = String(date.getMonth() + 1).padStart(2, "0");
        const jour = String(date.getDate()).padStart(2, "0");
        const heures = String(date.getHours()).padStart(2, "0");
        const minutes = String(date.getMinutes()).padStart(2, "0");

        return `${annee}-${mois}-${jour} ${heures}:${minutes}`;
    };

    useEffect(() => {
        obtenirCVs();
    }, [refreshTrigger]);

    return (
        <div className="w-full">
            <div className="mt-5 bg-purple-200 rounded-4xl flex flex-col justify-center items-center p-2">
                {cv.id !== null ? (
                    <div className="flex justify-around w-full items-center h-full">
                        <FiFileText className="size-10 rounded-box text-red-500 border border-t-0" />
                        <div>
                            {cv.fileName}
                            <div className="text-xs uppercase font-semibold opacity-60">
                                {formaterDate(cv.uploadDate)}
                            </div>
                        </div>
                        <div>
                            <div>{formaterTailleFichier(cv.fileSize)}</div>
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
                            onClick={gererTelechargement}
                            className="btn btn-ghost"
                            title={t("actions.download", "Télécharger")}
                        >
                            <FaDownload className="w-full h-full text-green-700 hover:text-black" />
                        </button>
                        <button
                            type="button"
                            className="btn btn-square btn-ghost"
                            onClick={supprimerCv}
                            title={t("actions.delete", "Supprimer")}
                        >
                            <FaTrashAlt className="w-full h-full text-red-900 hover:text-black" />
                        </button>
                    </div>
                ) : (
                    <div className="flex justify-center w-full">
                        <p className="text-center">{t("etudiant.noCVUpload")}</p>
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
                    cvUrlAAffiche={cvPDFUrl}
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