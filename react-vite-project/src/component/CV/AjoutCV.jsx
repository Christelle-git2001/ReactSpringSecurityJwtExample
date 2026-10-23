import React, {useState} from "react";
import {useTranslation} from "react-i18next";
import {televerserCV} from "../../api/etudiant.jsx";
import {useNavigate} from "react-router-dom";
import {formaterTailleFichier} from "../../utils/cvFormatters.jsx";

const AjoutCV = ({onCvAjoute}) => {
    const { t } = useTranslation();
    const [erreur,setErreur] = useState("")
    const [cv,setCv] = useState("")
    const TAILLE_MAX_OCTETS = 10 * 1024 * 1024; // 10 MB en octets
    const navigate = useNavigate();

    function onCvSelection(e){
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
        setCv(file);
    }

    async function ajoutCVCall(){
        if (cv) {
            try {
                await televerserCV(cv)
                if (onCvAjoute) {
                    onCvAjoute();
                    setErreur("")
                }
            } catch (error) {
                if (error?.message === "UNAUTHORIZED" || error?.status === 401) {
                    navigate("/login");
                }

                if (error.status === 409) {
                    setErreur("cv.cv_already_approved");
                    return;
                }
                console.log(error)
                setErreur("error.generic");
            }
        }
    }

    function annulation() {
        setCv(null);
        setErreur("")
    }

    return (
        <div className="flex flex-col items-center">
            {cv && (
                <div className="flex flex-col w-full text-center">
                    <h2 className={"font-bold text-xl"}>{t("pdfVisio.cvAttrib.titleCvSelectionner")}</h2>
                    <div className="flex justify-center mt-3">
                        <ol className="text-start">
                            <li><span className="font-bold">{t("pdfVisio.cvAttrib.name")}</span> {cv.name}</li>
                            <li><span className="font-bold">{t("pdfVisio.cvAttrib.size")}</span> {formaterTailleFichier(cv.size)}</li>
                            <li><span className="font-bold">{t("pdfVisio.cvAttrib.type")}</span> {cv.type.split('/').pop().toLocaleUpperCase()}</li>
                        </ol>
                    </div>
                </div>
            )}
            <input
                id="pdf-upload"
                type="file"
                accept="application/pdf"
                onChange={onCvSelection}
                className="hidden"
            />
            <label htmlFor="pdf-upload" className="btn btn-primary cursor-pointer w-1/2 mt-5">
                {cv ? (<p>{t("pdfVisio.modification")}</p>):(<p>{t("pdfVisio.choice")}</p>)}
            </label>
            {erreur &&
                <div>
                    <p className="text-red-500">{t(erreur)}</p>
                </div>
            }
            <div className="flex justify-around  w-full mt-5">
                <button type="button" onClick={annulation}  className="btn btn-error w-50">Annuler</button>
                <button type="button" onClick={ajoutCVCall} className="btn btn-success w-50">Envoyer</button>
            </div>
        </div>
    )
};export default AjoutCV;