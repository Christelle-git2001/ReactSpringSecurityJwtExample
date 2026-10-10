import { useTranslation } from "react-i18next";
import { getDepartmentKey } from "../../utils/departementConverter.js";
import { getStatusConfig } from "../../utils/getStatusConfig.jsx";

function OffreDetailsDrawerContent({ offer }) {
    const { t } = useTranslation();

    const departmentKey = getDepartmentKey(offer?.domain);
    const translatedDepartment = departmentKey
        ? t(`departement.${departmentKey}`, offer?.domain)
        : offer?.domain;

    const status = getStatusConfig(offer?.statut, t);

    return (
        <div className="space-y-4 p-2">
            <div className="bg-base-100 p-4 rounded-lg shadow-md space-y-4">
                <div className="flex justify-between items-start">
                    <h2 className="text-xl font-bold">{offer.title}</h2>

                    <span className={`badge ${status.className} text-white`}>
                        {status.label}
                    </span>
                </div>

                {offer.statut === "REFUSEE" && offer.rejectionComment && (
                    <div className="bg-red-50 border-red-500 p-3 rounded">
                        <p className="text-sm font-semibold text-red-700">
                            {t("offre.rejection_comment")}
                        </p>
                        <p className="text-xs text-red-600 mt-1">
                            {offer.rejectionComment}
                        </p>
                    </div>
                )}

                <ul className="text-sm space-y-1">
                    <li><strong>{t("offre.domaine")} :</strong> {translatedDepartment}</li>
                    <li><strong>{t("offre.date_debut")} :</strong> {offer.startDate}</li>
                    <li><strong>{t("offre.date_fin")} :</strong> {offer.endDate}</li>
                    <li><strong>{t("offre.date_fin_affichage")} :</strong> {offer.displayEndDate}</li>
                    {offer.salary && (
                        <li><strong>{t("offre.salary")} :</strong> {offer.salary} $ / h</li>
                    )}
                </ul>

                <p className="text-sm">
                    <strong>{t("offre.description")} :</strong><br />
                    {offer.description}
                </p>
            </div>
        </div>
    );
}

export default OffreDetailsDrawerContent;
