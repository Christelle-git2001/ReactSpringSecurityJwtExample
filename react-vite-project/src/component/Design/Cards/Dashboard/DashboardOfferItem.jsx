import React from "react";
import { useTranslation } from "react-i18next";
import { FaBriefcase } from "react-icons/fa";
import { getDepartmentKey } from "../../../../utils/departementConverter.js";
import { getStatusConfig } from "../../../../utils/getStatusConfig.jsx";
import RecentActivityBoardItem from "./RecentActivityBoardItem.jsx";

function DashboardOfferItem({ offer, onClick, isNew = false, variant = "card", actionLabel }) {
    const { t } = useTranslation();
    const departmentKey = getDepartmentKey(offer?.domain);
    const translatedDepartment = departmentKey
        ? t(`departement.${departmentKey}`, offer?.domain)
        : offer?.domain;
    const status = getStatusConfig(offer?.statut || offer?.status, t);

    return (
        <RecentActivityBoardItem
            icon={<FaBriefcase className="size-5" />}
            title={offer?.title || t("dashboard.offer_without_title")}
            subtitle={offer?.employeur?.businessName || offer?.businessName || translatedDepartment}
            meta={translatedDepartment}
            badge={status.label}
            badgeClassName={status.className}
            isNew={isNew}
            tone="green"
            variant={variant}
            actionLabel={actionLabel}
            onClick={onClick}
            ariaLabel={t("dashboard.view_offer_details")}
        />
    );
}

export default DashboardOfferItem;
