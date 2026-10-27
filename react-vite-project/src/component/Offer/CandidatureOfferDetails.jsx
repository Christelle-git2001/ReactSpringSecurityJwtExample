import { useTranslation } from "react-i18next";
import { FiStar, FiX } from "react-icons/fi";
import { getDepartmentKey } from "../../utils/departementConverter.js";
import { obtenirOffrePDF } from "../../api/employeur.jsx";
import { usePdfDocument } from "../../utils/filesUtils.jsx";
import PdfActionButtons from "../PDF/PdfActionButtons.jsx";
import PdfPreviewDialog from "../PDF/PdfPreviewDialog.jsx";

function CandidatureOfferDetails({ offer, id, onClose }) {
    const { t } = useTranslation();
    const departmentKey = getDepartmentKey(offer.domain);
    const targetedStudent = offer.etudiant
        ? `${offer.etudiant.firstName || ""} ${offer.etudiant.lastName || ""}`.trim()
        : offer.etudiantName;
    const fields = [
        { label: t("offre.domaine"), value: departmentKey ? t(`departement.${departmentKey}`, offer.domain) : offer.domain },
        { label: t("add_offer.targeted_company"), value: offer.isTarget ? offer.employeur?.businessName : null },
        { label: t("add_offer.targeted_student"), value: offer.isTarget ? targetedStudent : null },
        { label: t("offre.date_debut"), value: offer.startDate },
        { label: t("offre.date_fin"), value: offer.endDate },
        { label: t("offre.date_fin_affichage"), value: offer.displayEndDate },
    ];
    const { pdfUrl, error, dialogRef, viewPDF, fermerModal, gererTelechargement } = usePdfDocument({
        loadPdfResponse: () => obtenirOffrePDF(offer.id),
        fileNameFallback: offer.fileName || `offre_${offer.id}.pdf`,
        documentId: offer.id,
    });

    return (
        <section id={id} aria-label={t("dashboard.view_offer_details")} className="mt-4 border-t border-[#00CCCB]/30 pt-3">
            <div className="flex items-center justify-between gap-3">
                <div className="min-w-0 flex-1">
                    {offer.isTarget && (
                        <span className="badge badge-xs bg-amber-200 text-amber-800 border border-amber-300 flex items-center gap-1 mb-2">
                            <FiStar className="size-3 text-amber-600" aria-hidden="true" />
                            {t("offre.targeted", "Ciblée")}
                        </span>
                    )}
                    <h4 className="font-bold text-[#043462] break-words">{offer.title}</h4>
                </div>
                {offer.salary != null && (
                    <span className="shrink-0 font-bold">{offer.salary} $ / h</span>
                )}
                <button type="button" onClick={onClose} className="close-button cursor-pointer tooltip tooltip-left" aria-label={t("drawer.close")} data-tip={t("drawer.close")}>
                    <FiX className="size-5" aria-hidden="true" />
                </button>
            </div>
            <PdfActionButtons variant="card" ghost onView={viewPDF} onDownload={() => gererTelechargement(offer.fileName)} />
            {error && <p className="text-sm text-red-600">{t(error)}</p>}
            <ul className="mt-2 grid grid-cols-1 gap-x-6 gap-y-2 text-xs sm:grid-cols-2">
                {fields.filter(field => field.value != null && field.value !== "").map(field => (
                    <li key={field.label}>
                        <strong className="text-[#043462]">{field.label} :</strong> {field.value}
                    </li>
                ))}
            </ul>
            {offer.description && (
                <details open className="collapse collapse-arrow mt-2 rounded-none text-xs">
                    <summary className="collapse-title min-h-0 py-1 pl-0 text-xs font-bold text-[#043462]">
                        {t("offre.description")}
                    </summary>
                    <div className="collapse-content p-0">
                        <div className="mt-1 rounded-r-md border-l-[3px] border-[#00CCCB] bg-[#00CCCB]/5 px-3 py-2">
                            <p className="leading-relaxed whitespace-pre-line break-words text-gray-700">
                                {offer.description}
                            </p>
                        </div>
                    </div>
                </details>
            )}
            <PdfPreviewDialog dialogRef={dialogRef} pdfUrl={pdfUrl} onClose={fermerModal} />
        </section>
    );
}

export default CandidatureOfferDetails;