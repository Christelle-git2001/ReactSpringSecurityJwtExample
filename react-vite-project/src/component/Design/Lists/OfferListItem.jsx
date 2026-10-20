import React from "react";
import { useTranslation } from "react-i18next";
import { FaBriefcase } from "react-icons/fa";
import { getDepartmentKey } from "../../../utils/departementConverter.js";
import { getStatusConfig } from "../../../utils/getStatusConfig.jsx";
import ListItem from "./ListItem.jsx";

const formaterSalaire = (salary, t) => {
    if (salary === null || salary === undefined) return null;
    return `${salary} ${t("offre.hourly_rate_suffix")}`;
};

function OfferListItem({ offer, onClick, variant = "card", actionLabel, actionClassName }) {
    const { t } = useTranslation();
    const departmentKey = getDepartmentKey(offer?.domain);
    const translatedDepartment = departmentKey
        ? t(`departement.${departmentKey}`, offer?.domain)
        : offer?.domain;
    const status = getStatusConfig(offer?.statut || offer?.status, t);
    const salary = formaterSalaire(offer?.salary, t);
    const startDate = offer?.startDate ? String(offer.startDate).split("T")[0] : null;
    const endDate = offer?.endDate ? String(offer.endDate).split("T")[0] : null;
    const detailRows = [
        translatedDepartment && { label: t("offre.domaine"), value: translatedDepartment },
        salary && { label: t("offre.salary"), value: salary },
        startDate && { label: t("offre.date_debut"), value: startDate },
        endDate && { label: t("offre.date_fin"), value: endDate },
    ].filter(Boolean);

    return (
        <ListItem
            icon={<FaBriefcase className="size-5" />}
            title={offer?.title || t("dashboard.offer_without_title")}
            subtitle={offer?.employeur?.businessName || offer?.businessName || translatedDepartment}
            detailRows={detailRows}
            badge={status.label}
            badgeClassName={status.className}
            tone="green"
            variant={variant}
            actionLabel={actionLabel}
            actionClassName={actionClassName}
            onClick={onClick}
            ariaLabel={t("dashboard.view_offer_details")}
        />
    );
}

export default OfferListItem;