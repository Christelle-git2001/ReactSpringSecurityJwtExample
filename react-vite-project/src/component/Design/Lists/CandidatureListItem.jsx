import React from "react";
import { useTranslation } from "react-i18next";
import { FaFileContract } from "react-icons/fa";
import { getDepartmentKey } from "../../../utils/departementConverter.js";
import { getStatusConfig } from "../../../utils/getStatusConfig.jsx";
import ListItem from "./ListItem.jsx";

const formaterDate = (dateString) => {
    if (!dateString) return null;
    return String(dateString).split("T")[0];
};

function CandidatureListItem({ candidature, onClick, variant = "card", actionLabel }) {
    const { t } = useTranslation();

    const departmentKey = getDepartmentKey(candidature?.etudiantDepartment);
    const translatedDepartment = departmentKey
        ? t(`departement.${departmentKey}`, candidature?.etudiantDepartment)
        : candidature?.etudiantDepartment;

    const contratASigner = candidature?.statut === "CONTRAT_CREE" && !candidature?.contratSigne;
    const status = contratASigner
        ? { label: t("dashboard.to_sign"), className: "bg-amber-100 text-amber-800 border-amber-200" }
        : getStatusConfig(candidature?.contratSigne ? "CONTRAT_SIGNE" : candidature?.statut, t, "candidature");
    const dateCandidature = formaterDate(candidature?.dateCandidature);

    const studentFullName = `${candidature?.etudiantFirstName || ""} ${candidature?.etudiantLastName || ""}`.trim();

    const detailRows = [
        translatedDepartment && { label: t("offre.domaine"), value: translatedDepartment },
        candidature?.employeurBusinessName && { label: t("offre.employeur"), value: candidature.employeurBusinessName },
        dateCandidature && { label: t("candidature.date"), value: dateCandidature },
    ].filter(Boolean);

    return (
        <ListItem
            icon={<FaFileContract className="size-5" />}
            title={studentFullName || t("candidature.etudiant_inconnu")}
            subtitle={candidature?.offreTitle || t("candidature.offre_sans_titre")}
            detailRows={detailRows}
            badge={status.label}
            badgeClassName={status.className}
            tone="cyan"
            variant={variant}
            actionLabel={actionLabel}
            actionRole="details"
            actionIcon
            actionClassName="bg-[#043462] text-white hover:bg-[#03284d]"
            onClick={onClick}
            ariaLabel={t("dashboard.view_contract")}
        />
    );
}

export default CandidatureListItem;