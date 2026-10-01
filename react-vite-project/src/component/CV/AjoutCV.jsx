import React, {useState} from "react";
import {useTranslation} from "react-i18next";
import {televerserCV} from "../../api/etudiant.jsx";

const AjoutCV = ({onCvAjoute}) => {
    const { t } = useTranslation();
    const [erreur,setErreur] = useState("")

    async function ajoutCVCall(e){
        const file = e.target.files[0];
        if (!file || file.type !== "application/pdf") {
            setErreur(t("pdfVisio.fichierInvalide"))
        }

        try{
            await televerserCV(file)
            if (onCvAjoute) {
                onCvAjoute();
            }
        }catch (error){
            if (error.status === 401){
                setErreur(t("token.doesntExist"))//TODO demander pour la translation
            }
            //TODO do other exception
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
                    <p className="text-red-500">{erreur}</p>
                </div>
            }
        </div>
    )
};export default AjoutCV;