import { IoEyeSharp } from "react-icons/io5";
import { FaDownload } from "react-icons/fa";
import { useTranslation } from "react-i18next";

function PdfActionButtons({ onView, onDownload, variant = "default", disabled = false, ghost = false }) {
    const { t } = useTranslation();
    const isCard = variant === "card";
    const cardButtonClass = `${ghost ? "btn btn-square btn-ghost btn-sm" : "p-2"} text-[#043462] cursor-pointer tooltip`;
    const previewLabel = isCard ? t("documents.preview") : t("actions.view", "Visualiser");
    const downloadLabel = isCard ? t("documents.download") : t("actions.download", "Télécharger");

    return (
        <div className={isCard ? "flex shrink-0 gap-3" : "flex justify-start gap-2 mb-2"}>
            <button
                type="button"
                className={isCard ? cardButtonClass : "btn btn-square btn-ghost btn-sm"}
                onClick={onView}
                disabled={disabled}
                title={previewLabel}
                data-tip={isCard ? previewLabel : undefined}
                aria-label={previewLabel}
            >
                <IoEyeSharp className={isCard ? "size-5" : "w-5 h-5 text-teal-500 hover:text-black"} aria-hidden="true" />
            </button>

            <button
                type="button"
                className={isCard ? cardButtonClass : "btn btn-square btn-ghost btn-sm"}
                onClick={onDownload}
                disabled={disabled}
                title={downloadLabel}
                data-tip={isCard ? downloadLabel : undefined}
                aria-label={downloadLabel}
            >
                <FaDownload className={isCard ? "size-5" : "w-5 h-5 text-green-700 hover:text-black"} aria-hidden="true" />
            </button>
        </div>
    );
}

export default PdfActionButtons;