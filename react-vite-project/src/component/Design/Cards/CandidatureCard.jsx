import React from "react";
import { useTranslation } from "react-i18next";
import { formaterDate } from "../../../utils/cvFormatters.jsx";
import { getDepartmentKey } from "../../../utils/departementConverter.js";

function CandidatureCard({ candidature, onView }) {
    const { t } = useTranslation();

    const getStudentName = () => {
        if (candidature.etudiantFirstName || candidature.etudiantLastName) {
            return `${candidature.etudiantFirstName || ""} ${candidature.etudiantLastName || ""}`.trim();
        }
        return t("candidature.student_unknown");
    };

    const departmentRaw = candidature.etudiantDepartment;
    const departmentKey = getDepartmentKey(departmentRaw);
    const translatedDepartment = departmentRaw
        ? (departmentKey ? t(`departement.${departmentKey}`, departmentRaw) : departmentRaw)
        : t("candidature.department_unknown");

    return (
        <div className="relative overflow-hidden rounded-xl border border-gray-200 bg-white px-5 py-2 shadow-xs transition-all hover:shadow-sm">
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">

                {/* Bloc gauche */}
                <div className="flex flex-col min-w-0">
                    <div className="flex items-center gap-2.5">
                        <h3 className="text-base font-bold text-[#043462] truncate">
                            {getStudentName()}
                        </h3>
                    </div>

                    <p className="mt-1 text-xs text-gray-500 font-medium">
                        {translatedDepartment}
                    </p>

                    <p className="mt-1 text-xs text-gray-500 font-medium">
                        {t("candidature.offer_title")} : {candidature.offreTitle}
                    </p>

                    <p className="mt-1 text-xs text-gray-500 font-medium">
                        {t("candidature.employer")} : {candidature.employeurBusinessName}
                    </p>
                </div>

                {/* Bloc droit */}
                <div className="flex items-center justify-between sm:justify-end gap-6 shrink-0">
                    <div className="text-xs text-gray-500">
                        <span className="font-semibold text-[#043462]">
                            {t("candidature.date")} :
                        </span>{" "}
                        {formaterDate(candidature.dateCandidature)}
                    </div>

                    {onView && (
                        <button
                            type="button"
                            onClick={() => onView(candidature)}
                            className="btn btn-sm bg-[#66E5E4] text-[#043462] hover:bg-[#52d2d1] border-none font-semibold px-4"
                        >
                            {t("candidature.read")}
                        </button>
                    )}
                </div>
            </div>
        </div>
    );
}

export default CandidatureCard;
