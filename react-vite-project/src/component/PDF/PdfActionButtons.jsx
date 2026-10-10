import { IoEyeSharp } from "react-icons/io5";
import { FaDownload } from "react-icons/fa";
import { useTranslation } from "react-i18next";

function PdfActionButtons({ onView, onDownload }) {
    const { t } = useTranslation();

    return (
        <div className="flex justify-start gap-2 mb-2">
            <button
                type="button"
                className="btn btn-square btn-ghost btn-sm"
                onClick={onView}
                title={t("actions.view", "Visualiser")}
                aria-label={t("actions.view", "Visualiser")}
            >
                <IoEyeSharp className="w-5 h-5 text-teal-500 hover:text-black" />
            </button>

            <button
                type="button"
                className="btn btn-square btn-ghost btn-sm"
                onClick={onDownload}
                title={t("actions.download", "Télécharger")}
                aria-label={t("actions.download", "Télécharger")}
            >
                <FaDownload className="w-5 h-5 text-green-700 hover:text-black" />
            </button>
        </div>
    );
}

export default PdfActionButtons;