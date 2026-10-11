import React from "react";
import { useTranslation } from "react-i18next";
import { FaFileAlt } from "react-icons/fa";
import { formaterDate } from "../../../../utils/cvFormatters.jsx";
import { getStatusConfig } from "../../../../utils/getStatusConfig.jsx";
import RecentActivityBoardItem from "./RecentActivityBoardItem.jsx";

function DashboardCvItem({ cv, onClick, isNew = false, variant = "card", actionLabel }) {
    const { t } = useTranslation();
    const status = getStatusConfig(cv?.statut, t, cv);

    const studentName = `${cv?.etudiant?.firstName || ""} ${cv?.etudiant?.lastName || ""}`.trim();

    return (
        <RecentActivityBoardItem
            icon={<FaFileAlt className="size-5" />}
            title={studentName || t("cv.student_unknown")}
            subtitle={cv?.fileName || t("cv.file_without_name")}
            meta={cv?.uploadDate ? formaterDate(cv.uploadDate) : null}
            badge={status.label}
            badgeClassName={status.className}
            isNew={variant === "card" && isNew}
            tone="blue"
            variant={variant}
            actionLabel={actionLabel}
            onClick={onClick}
            ariaLabel={t("dashboard.view_cv_details")}
        />
    );
}

export default DashboardCvItem;
