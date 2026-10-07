import { useTranslation } from "react-i18next";
import { obtenirCvPDF, suppressionCv } from "../../api/etudiant.jsx";
import React, { useState } from "react";
import {usePdfDocument} from "../../utils/filesUtils.jsx";
import CvDocumentViewer from "./CvDocumentViewer.jsx";
import { getCvStatusConfig } from "../../utils/cvStatusConfig.jsx";

const ShowCv = ({ cv, onCvDeleted }) => {
    const { t } = useTranslation();
    const [deleteError, setDeleteError] = useState(null);

    const status = getCvStatusConfig(cv?.statut, t);

    const {
        pdfUrl,
        error,
        dialogRef,
        viewPDF,
        fermerModal,
        gererTelechargement,
    } = usePdfDocument({
        loadPdfResponse: obtenirCvPDF,
        fileNameFallback: cv?.fileName,
    });

    const supprimerCv = async () => {
        try {
            await suppressionCv();
            if (onCvDeleted) {
                onCvDeleted();
            }

            setDeleteError(null);
        } catch (error) {
            switch (error?.status) {
                case 404:
                    setDeleteError("etudiant.noCvFound");
                    break;
                case 401:
                    setDeleteError("token.doesntExist");
                    break;
                default:
                    setDeleteError("error.generic");
                    break;
            }
        }
    };

    const currentError = error || deleteError;

    return (
        <>
            <CvDocumentViewer
                cv={cv}
                pdfUrl={pdfUrl}
                error={error || deleteError}
                dialogRef={dialogRef}
                viewPDF={viewPDF}
                fermerModal={fermerModal}
                gererTelechargement={gererTelechargement}
                supprimerCv={supprimerCv}
                showDelete
            />

            {cv?.id && (
                <div className="mt-3 rounded-lg border border-gray-200 bg-gray-50/70 px-3 py-2 text-left">
                    <span className="text-sm font-bold text-[#043480]">Statut : </span><span className={`badge badge-xs ${status.className}`}>
                        {status.label}
                    </span>

                    {cv?.rejectionComment && (
                        <div className="mt-2">
                            <p className="text-sm font-bold text-[#043462]">
                                {t("cv.previous_rejection_comment")}
                            </p>
                            <p className="mt-1 whitespace-pre-line text-sm text-gray-800">
                                {cv.rejectionComment}
                            </p>
                        </div>
                    )}
                </div>
            )}
        </>
    );
};

export default ShowCv;