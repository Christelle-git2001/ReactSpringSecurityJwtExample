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

    const status = candidature?.statut === "CONTRAT_CREE" && candidature?.contratSigne
        ? getStatusConfig("CONTRAT_SIGNE", t, "candidature")
        : getStatusConfig(candidature?.statut, t);
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
            actionClassName="bg-[#043462] text-white hover:bg-[#03284d]"
            onClick={onClick}
            ariaLabel={t("dashboard.view_candidature_details")}
        />
    );
}

export default CandidatureListItem;