import { usePdfDocument } from "../../utils/filesUtils.jsx";
import CvDocumentViewer from "./CvDocumentViewer.jsx";
import { obtenirCvPDFGestionnaire } from "../../api/http.jsx";

const ShowCvGestionnaire = ({ cv }) => {
    const {
        pdfUrl,
        error,
        dialogRef,
        viewPDF,
        fermerModal,
        gererTelechargement,
    } = usePdfDocument({
        loadPdfResponse: () => obtenirCvPDFGestionnaire(cv?.id),
        fileNameFallback: cv?.fileName,
    });

    return (
        <CvDocumentViewer
            cv={cv}
            pdfUrl={pdfUrl}
            error={error}
            dialogRef={dialogRef}
            viewPDF={viewPDF}
            fermerModal={fermerModal}
            gererTelechargement={gererTelechargement}
        />
   );
};

export default ShowCvGestionnaire;