import React from "react";
import { useTranslation } from "react-i18next";
import { FaFileAlt } from "react-icons/fa";
import { formaterDate } from "../../../utils/cvFormatters.jsx";
import { getDepartmentKey } from "../../../utils/departementConverter.js";
import { getStatusConfig } from "../../../utils/getStatusConfig.jsx";
import ListItem from "./ListItem.jsx";

function CvListItem({ cv, onClick, variant = "card", actionLabel, actionClassName }) {
    const { t } = useTranslation();
    const status = getStatusConfig(cv?.statut, t, "cv");

    const studentName = `${cv?.etudiant?.firstName || ""} ${cv?.etudiant?.lastName || ""}`.trim();
    const departmentKey = getDepartmentKey(cv?.etudiant?.department);
    const department = departmentKey
        ? t(`departement.${departmentKey}`, cv?.etudiant?.department)
        : cv?.etudiant?.department;

    return (
        <ListItem
            icon={<FaFileAlt className="size-5" />}
            title={studentName || t("cv.student_unknown")}
            subtitle={department || cv?.fileName || t("cv.file_without_name")}
            meta={cv?.uploadDate ? formaterDate(cv.uploadDate) : null}
            badge={status.label}
            badgeClassName={status.className}
            tone="blue"
            variant={variant}
            actionLabel={actionLabel}
            actionClassName={actionClassName}
            onClick={onClick}
            ariaLabel={t("dashboard.view_cv_details")}
        />
    );
}

export default CvListItem;
