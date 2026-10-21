import { useTranslation } from "react-i18next";
import { FiStar } from "react-icons/fi";
import { getDepartmentKey } from "../../utils/departementConverter.js";
import { getStatusConfig } from "../../utils/getStatusConfig.jsx";
import "../../css/EtudiantHome.css";
import PostulerButton from "../Design/PostulerButton.jsx";

function OffreDetailsDrawerContent({ offer, showStatus = true, hasApplied = false, onApplied }) {
    const { t } = useTranslation();

    const isTargeted = Boolean(offer?.isTarget);

    const departmentKey = getDepartmentKey(offer?.domain);
    const translatedDepartment = departmentKey
        ? t(`departement.${departmentKey}`, offer?.domain)
        : offer?.domain;

    const status = getStatusConfig(offer?.statut, t);

    const employeurDisplay = offer?.employeur
        ? `${offer.employeur.businessName ? ` ${offer.employeur.businessName}` : ""
        }`
        : null;

    const etudiantDisplay = offer?.etudiant
        ? `${offer.etudiant.firstName} ${offer.etudiant.lastName}`
        : offer?.etudiantName;

    const targetedLabelText = t("offre.targeted", "Offre ciblée");

    return (
        <div className="space-y-4 p-2">
            <div className="relative bg-base-100 p-4 rounded-lg shadow-md space-y-4">

                {isTargeted && (
                    <div className="flex justify-end mb-2">
                        <span
                            title={targetedLabelText}
                            className="max-w-[160px] truncate badge bg-amber-100 text-amber-800 border border-amber-300 flex items-center gap-1.5 px-3 py-1 text-xs font-semibold shadow-sm"
                        >
                            <FiStar className="size-3.5 shrink-0 text-amber-600 fill-amber-500" />
                            <span className="truncate">{targetedLabelText}</span>
                        </span>
                    </div>
                )}

                <div className="flex justify-between items-start gap-3">
                    <h2 className="text-xl font-bold text-[#043462] break-words flex-1">
                        {offer?.title}
                    </h2>
                    
                    {showStatus && (
                        <span
                            title={status.label}
                            className={`badge ${status.className} text-white shrink-0 max-w-[150px] truncate whitespace-nowrap`}
                        >
                            {status.label}
                        </span>
                    )}
                </div>

                {showStatus && offer?.statut === "REFUSEE" && offer?.rejectionComment && (
                    <div className="bg-red-50 border border-red-200 p-3 rounded-lg">
                        <p className="text-sm font-semibold text-red-700">
                            {t("offre.rejection_comment")}
                        </p>
                        <p className="text-xs text-red-600 mt-1 break-words">
                            {offer.rejectionComment}
                        </p>
                    </div>
                )}

                <ul className="text-sm space-y-1.5">
                    {isTargeted && (
                        <>
                            {employeurDisplay && (
                                <li>
                                    <strong className="text-[#043462]">{t("add_offer.targeted_company", "Employeur")} :</strong> {employeurDisplay}
                                </li>
                            )}
                            {etudiantDisplay && (
                                <li>
                                    <strong className="text-[#043462]">{t("add_offer.targeted_student", "Étudiant ciblé")} :</strong> {etudiantDisplay}
                                </li>
                            )}
                        </>
                    )}

                    <li><strong className="text-[#043462]">{t("offre.domaine")} :</strong> {translatedDepartment}</li>
                    <li><strong className="text-[#043462]">{t("offre.date_debut")} :</strong> {offer?.startDate}</li>
                    <li><strong className="text-[#043462]">{t("offre.date_fin")} :</strong> {offer?.endDate}</li>
                    <li><strong className="text-[#043462]">{t("offre.date_fin_affichage")} :</strong> {offer?.displayEndDate}</li>
                    {offer?.salary && (
                        <li><strong className="text-[#043462]">{t("offre.salary")} :</strong> {offer.salary} $ / h</li>
                    )}
                </ul>

                <p className="text-sm pt-2 border-t border-gray-100">
                    <strong className="text-[#043462]">{t("offre.description")} :</strong><br />
                    <span className="whitespace-pre-line text-gray-700 break-words">{offer?.description}</span>
                </p>

                {!showStatus && (
                    <div className="pt-4 border-t border-gray-100">
                        <PostulerButton
                            offerId={offer?.id}
                            hasApplied={hasApplied}
                            onApplied={onApplied}
                        />
                    </div>
                )}
            </div>
        </div>
    );
}

export default OffreDetailsDrawerContent;