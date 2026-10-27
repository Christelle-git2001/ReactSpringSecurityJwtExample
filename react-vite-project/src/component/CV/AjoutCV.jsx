import Button from "../Design/Button.jsx";
import React, {useState} from "react";
import {useTranslation} from "react-i18next";
import {televerserCV} from "../../api/etudiant.jsx";
import {useNavigate} from "react-router-dom";
import {formaterTailleFichier} from "../../utils/cvFormatters.jsx";
import { FiUpload } from "react-icons/fi";
import PdfActionButtons from "../PDF/PdfActionButtons.jsx";
import PdfPreviewDialog from "../PDF/PdfPreviewDialog.jsx";
import { usePdfDocument } from "../../utils/filesUtils.jsx";

const AjoutCV = ({onCvAjoute}) => {
    const { t } = useTranslation();
    const [erreur,setErreur] = useState("")
    const [cv,setCv] = useState(null)
    const { pdfUrl, dialogRef, viewPDF, fermerModal, gererTelechargement } = usePdfDocument({
        file: cv,
        fileNameFallback: cv?.name,
    });
    const TAILLE_MAX_OCTETS = 10 * 1024 * 1024; // 10 MB en octets
    const navigate = useNavigate();

    function onCvSelection(e){
        const file = e.target.files[0];
        if (!file) return;
        if (file.type !== "application/pdf") {
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
        setErreur("");
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

                if (error?.status === 409) {
                    setErreur("cv.cv_already_approved");
                    return;
                }
                setErreur("error.generic");
            }
        }
    }

    function annulation() {
        setCv(null);
        setErreur("")
    }

    return (
        <>
            <div className="w-full">
                {cv && (
                    <div className="mb-5 space-y-4">
                        <h2 className="text-lg font-bold text-slate-900">{t("documents.verify_selected")}</h2>
                        <div className="document-card">
                            <div className="document-card-info">
                                <p className="document-card-name">{cv.name}</p>
                                <p className="document-card-details">PDF · {formaterTailleFichier(cv.size)}</p>
                            </div>
                            <PdfActionButtons
                                variant="card"
                                disabled={!pdfUrl}
                                onView={viewPDF}
                                onDownload={() => gererTelechargement(cv.name)}
                            />
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
                <label htmlFor="pdf-upload" className="flex min-h-12 w-full cursor-pointer items-center justify-center gap-3 rounded-md border border-[#043462] bg-[#043462] px-4 py-3 font-semibold text-white hover:bg-[#03284d]">
                    <FiUpload className="size-5 shrink-0" aria-hidden="true" />
                    {cv ? t("documents.choose_another") : t("pdfVisio.choice")}
                </label>
                <p className="mt-3 text-center text-sm text-slate-500">{t("documents.file_hint", { maxSize: TAILLE_MAX_OCTETS / 1024 / 1024 })}</p>
                {erreur &&
                    <p className="mt-3 text-sm text-red-500">{t(erreur)}</p>
                }
                <div className="flex justify-around  w-full mt-5">
                    <Button variant="custom" type="button" onClick={annulation} className="w-50 bg-gray-500 text-white hover:bg-gray-600">{t("actions.cancel")}</Button>
                    <Button type="button" onClick={ajoutCVCall} className="w-50">{t("actions.upload")}</Button>
                </div>
            </div>
            <PdfPreviewDialog dialogRef={dialogRef} pdfUrl={pdfUrl} onClose={fermerModal} />
        </>
    )
};export default AjoutCV;