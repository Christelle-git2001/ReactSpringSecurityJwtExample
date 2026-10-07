import React, {useState} from "react";
import {useTranslation} from "react-i18next";
import {televerserCV} from "../../api/etudiant.jsx";

const AjoutCV = ({onCvAjoute}) => {
    const { t } = useTranslation();
    const [erreur,setErreur] = useState("")
    const TAILLE_MAX_OCTETS = 10 * 1024 * 1024; // 10 MB en octets

    async function ajoutCVCall(e){
        const file = e.target.files[0];
        if (!file || file.type !== "application/pdf") {
            setErreur("pdfVisio.fichierInvalide")
            return
        }
        if (file.size > TAILLE_MAX_OCTETS){
            setErreur("pdfVisio.bigFiles")
            return
        }
        if (file.size <= 0){
            setErreur("pdfVisio.lowFiles")
            return
        }
        try{
            await televerserCV(file)
            if (onCvAjoute) {
                onCvAjoute();
                setErreur("")
            }
        }catch (error){
            if (error.status === 401){
                setErreur("token.doesntExist")
            }

            if (error.status === 409) {
                setErreur("cv.cv_already_approved");
                return;
            }

            setErreur("error.generic");
        }
    }
    return (
        <div className="flex flex-col items-center">
            <input
                id="pdf-upload"
                type="file"
                accept="application/pdf"
                onChange={ajoutCVCall}
                className="hidden"
            />
            <label htmlFor="pdf-upload" className="btn btn-primary cursor-pointer w-1/2">
                {t("pdfVisio.choice")}
            </label>
            {erreur &&
                <div>
                    <p className="text-red-500">{t(erreur)}</p>
                </div>
            }
        </div>
    )
};export default AjoutCV;