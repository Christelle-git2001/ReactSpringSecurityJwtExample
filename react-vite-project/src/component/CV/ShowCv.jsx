import { useTranslation } from "react-i18next";
import { obtenirCvPDF, suppressionCv } from "../../api/etudiant.jsx";
import React, { useState } from "react";
import {usePdfDocument} from "../../utils/filesUtils.jsx";
import CvDocumentViewer from "./CvDocumentViewer.jsx";

const ShowCv = ({ cv, onCvDeleted }) => {
    const { t } = useTranslation();
    const [deleteError, setDeleteError] = useState(null);

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
    );
};

export default ShowCv;