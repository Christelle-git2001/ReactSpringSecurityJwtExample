import {useTranslation} from "react-i18next";
import {obtenirCvInfo, obtenirCvPDF, suppressionCv} from "../../api/etudiant.jsx";
import React, {useEffect, useState} from "react";
import {FiFileText} from "react-icons/fi";
import {IoEyeSharp} from "react-icons/io5";
import {FaDownload, FaTrashAlt} from "react-icons/fa";
import {PDFVisioneuse} from "../PDF/PDFVisioneuse.jsx"
import {declencherTelechargement} from "../../utils/filesUtils.jsx";

const ShowCv = ({refreshTrigger}) => {
    const { t } = useTranslation();
    const [cv, setCv] = useState({
        id : null,
        fileName : "",
        contentType : "",
        fileSize : 0,
        uploadDate : ""

    });
    const [cvPDFUrl, setCvPDFUrl] = useState("");
    const [error, setError] = useState("fdnjhfdj")

    async function obtenirCVs(){
        try{
            const data = await obtenirCvInfo();
            setCv(data)
            setError(null)
        }catch (error){
            switch (error.status){
                case 404:
                    setError("etudiant.noCvFound")
                break
                case 401:
                    setError("token.doesntExist")
                    break
                default :
                    setError("error.generic")
                    break
            }
        }
    }

    const viewPDF = async () => {
        if (!cvPDFUrl)
            try {
                setCvPDFUrl(await obtenirCVUrlPDF());
            }catch(error){
                switch (error.status){
                    case 404:
                        setError("etudiant.noCvFound")
                        break
                    case 401:
                        setError("token.doesntExist")
                        break
                    default :
                        setError("error.generic")
                        break
                }
            }
        document.getElementById('pdfModal').showModal();

    }

    const fermerModal = () => {
        document.getElementById('pdfModal').close();
        if (cvPDFUrl) {
            URL.revokeObjectURL(cvPDFUrl);
            setCvPDFUrl(null);
        }
    };

    const gererTelechargement = async () => {
        let url = "";
        try {
             url = await obtenirCVUrlPDF();
        }catch (error){
            switch (error.status){
                case 404:
                    setError("etudiant.noCvFound")
                    break
                case 401:
                    setError("token.doesntExist")
                    break
                default :
                    setError("error.generic")
                    break
            }
            return
        }
        if (url) {
            declencherTelechargement(url, cv.fileName);
        }
    };

    const supprimerCv = async () => {
        try {
            await suppressionCv();
            setCv({
                id : null,
                fileName : "",
                contentType : "",
                fileSize : 0,
                uploadDate : ""
            })
        }catch (error){
            switch (error.status){
                case 404:
                    setError("etudiant.noCvFound")
                    break
                case 401:
                    setError("token.doesntExist")
                    break
                default :
                    setError("error.generic")
                    break
            }
        }
    }

    async function obtenirCVUrlPDF(){
        try {
            const response = await obtenirCvPDF(cv.id);
            if (!response.ok) {
                setError("error.generic")
                return
            }
            const data = await response.blob()
            return URL.createObjectURL(data);
        }catch (error){
            switch (error.status){
                case 404:
                    setError("etudiant.noCvFound")
                    break
                case 401:
                    setError("token.doesntExist")
                    break
                default :
                    setError("error.generic")
                    break
            }
        }
    }



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
        <div className="">
            <div className={"mt-5 bg-purple-200 rounded-4xl flex flex-col justify-center items-center p-2"}>
            {cv.id !== null ? (
                <div className="flex justify-around w-full items-center  h-full">
                    <FiFileText className="size-10 rounded-box text-red-500 border-0 border border-t-0" />
                        <div>{cv.fileName}</div>
                            <div className="text-xs uppercase font-semibold opacity-60">{formaterDate(cv.uploadDate)}
                        </div>
                        <div>
                            <div>{formaterTailleFichier(cv.fileSize)}</div>
                        </div>
                        <button className="btn btn-square btn-ghost" onClick={viewPDF} >
                            <IoEyeSharp className="w-full h-full text-teal-500 hover:text-black" />
                        </button>
                        <button
                            onClick={gererTelechargement}
                            className="btn btn-ghost"
                        >
                            <FaDownload className="w-full h-full text-green-700 hover:text-black"/>
                        </button>
                        <button className="btn btn-square btn-ghost" onClick={supprimerCv}>
                            <FaTrashAlt className="w-full h-full text-red-900 hover:text-black"/>
                        </button>
                </div>
                ) : (
                    <div className=" flex justify-center w-full">
                        <p className="text-center">{t("etudiant.noCVUpload")}</p>
                    </div>
                )}
            {error && (
                <div>
                    <p className="text-red-500">{t(error)}</p>
                </div>
            )}
        </div>
            <PDFVisioneuse
                cvUrlAAffiche={cvPDFUrl}
                onClose={() => fermerModal()}
            />
        </div>
    )
};
export default ShowCv;