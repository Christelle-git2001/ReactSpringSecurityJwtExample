import { useTranslation } from "react-i18next";
import { FiFileText } from "react-icons/fi";
import { IoEyeSharp } from "react-icons/io5";
import { FaDownload, FaTrashAlt } from "react-icons/fa";
import { PDFVisioneuse } from "../PDF/PDFVisioneuse.jsx";
import { formaterDate, formaterTailleFichier } from "../../utils/CvFormatters.jsx";

const CvDocumentViewer = ({
  cv,
  pdfUrl,
  error,
  dialogRef,
  viewPDF,
  fermerModal,
  gererTelechargement,
  supprimerCv,
  showDelete = false,
}) => {
    const { t } = useTranslation();
    return (
    <div className="w-full">
        <div className="mt-5 bg-purple-200 rounded-4xl flex flex-col justify-center items-center p-2">
            {cv?.id ? (
                <div className="flex justify-around w-full items-center h-full">
                    <FiFileText className="size-10 rounded-box text-red-500 border border-t-0" />

                    <div>
                        {cv?.fileName}
                        <div className="text-xs uppercase font-semibold opacity-60">
                            {formaterDate(cv?.uploadDate)}
                        </div>
                    </div>

                    <div>
                        <div>{formaterTailleFichier(cv?.fileSize)}</div>
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
                        onClick={() => gererTelechargement(cv?.fileName)}
                        className="btn btn-ghost"
                        title={t("actions.download", "Télécharger")}
                    >
                        <FaDownload className="w-full h-full text-green-700 hover:text-black" />
                    </button>

                    {showDelete && (
                        <button
                            type="button"
                            className="btn btn-square btn-ghost"
                            onClick={supprimerCv}
                            title={t("actions.delete", "Supprimer")}
                        >
                            <FaTrashAlt className="w-full h-full text-red-900 hover:text-black" />
                        </button>
                    )}
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
                cvUrlAAffiche={pdfUrl}
                onClose={fermerModal}
            />
            <form method="dialog" className="modal-backdrop">
                <button type="submit">close</button>
            </form>
        </dialog>
    </div>
);
};

export default CvDocumentViewer;