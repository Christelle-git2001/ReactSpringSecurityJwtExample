import {PDFContent} from "./PDFVisioneuse.jsx";
import {useTranslation} from "react-i18next";

const PDFOnPage = ({isPdfLoading,pdfBlobUrl}) => {
    const { t } = useTranslation();
    return (
       <>
        {isPdfLoading ? (
            <div className="flex items-center justify-center p-12 border rounded-xl bg-gray-50">
                <span className="loading loading-spinner loading-md text-[#043462]" />
            </div>
        ) : pdfBlobUrl ? (
            <PDFContent cvUrlAAffiche={pdfBlobUrl} enablePageScroll={true} />
        ) : (
            <p className="text-xs text-gray-500 italic p-6 border rounded-xl bg-gray-50 text-center">
                {t("offre.document_error", "Impossible de charger le document.")}
            </p>
        )}
    </>
   )
}; export default PDFOnPage;