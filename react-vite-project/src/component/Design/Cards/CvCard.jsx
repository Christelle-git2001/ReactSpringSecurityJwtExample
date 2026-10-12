import React from "react";
import { useTranslation } from "react-i18next";
import { formaterDate } from "../../../utils/cvFormatters.jsx";
import { getDepartmentKey } from "../../../utils/departementConverter.js";

function CvCard({ cv, onView, isNew = false }) {
    const { t } = useTranslation();

    const getStudentName = () => {
        if (cv.etudiant?.firstName || cv.etudiant?.lastName) {
            return `${cv.etudiant?.firstName || ""} ${cv.etudiant?.lastName || ""}`.trim();
        }
        return t("cv.student_unknown");
    };

    const departmentRaw = cv.etudiant?.department;
    const departmentKey = getDepartmentKey(departmentRaw);
    const translatedDepartment = departmentRaw
        ? (departmentKey ? t(`departement.${departmentKey}`, departmentRaw) : departmentRaw)
        : t("cv.department_unknown", "Département inconnu");

    return (
        <div className="relative overflow-hidden rounded-xl border border-gray-200 bg-white px-5 py-2 shadow-xs transition-all hover:shadow-sm">
            <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                <div className="flex flex-col min-w-0">
                    <div className="flex items-center gap-2.5">
                        <h3 className="text-base font-bold text-[#043462] truncate">
                            {getStudentName()}
                        </h3>
                    </div>

                    <p className="mt-1 text-xs text-gray-500 font-medium">
                        {translatedDepartment}
                    </p>
                </div>
                <div className="flex items-center justify-between sm:justify-end gap-6 shrink-0">
                    <div className="text-xs text-gray-500">
                        <span className="font-semibold text-[#043462]">
                            {t("cv.upload_date")} :
                        </span>{"  "}
                        {formaterDate(cv.uploadDate)}
                    </div>

                    {onView && (
                        <button
                            type="button"
                            onClick={() => onView(cv)}
                            className="btn btn-sm bg-[#66E5E4] text-[#043462] hover:bg-[#52d2d1] border-none font-semibold px-4"
                        >
                            {t("cv.read")}
                        </button>
                    )}
                </div>
            </div>
        </div>
    );
}

export default CvCard;