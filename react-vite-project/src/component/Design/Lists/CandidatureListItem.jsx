import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { FaFileContract } from "react-icons/fa";
import { getDepartmentKey } from "../../../utils/departementConverter.js";
import { getStatusConfig } from "../../../utils/getStatusConfig.jsx";
import ListItem from "./ListItem.jsx";
import Button from "../Button.jsx";

const formaterDate = (dateString) => {
    if (!dateString) return null;
    return String(dateString).split("T")[0];
};

function CandidatureListItem({ candidature, onClick, variant = "card", actionLabel, onRappel }) {
    const { t, i18n } = useTranslation();
    const [rappelEnCours, setRappelEnCours] = useState(false);

    const departmentKey = getDepartmentKey(candidature?.etudiantDepartment);
    const translatedDepartment = departmentKey
        ? t(`departement.${departmentKey}`, candidature?.etudiantDepartment)
        : candidature?.etudiantDepartment;

    let status = getStatusConfig(candidature?.contratSigne ? "CONTRAT_SIGNE" : candidature?.statut, t, "candidature");
    if (candidature?.statut === "CONTRAT_CREE" && !candidature?.contratSigne) {
        status = candidature?.contratASigner
            ? { label: t("dashboard.to_sign"), className: "bg-amber-100 text-amber-800 border-amber-200" }
            : { label: t("dashboard.signing_started"), className: "bg-violet-100 text-violet-800 border-violet-200" };
    }
    const dateCandidature = formaterDate(candidature?.dateCandidature);
    const dateCreation = candidature?.dateCreationContrat ? new Date(candidature.dateCreationContrat) : null;
    const dateCreationValide = dateCreation && !Number.isNaN(dateCreation.getTime());
    const studentFullName = `${candidature?.etudiantFirstName || ""} ${candidature?.etudiantLastName || ""}`.trim();

    const detailRows = [
        translatedDepartment && { label: t("offre.domaine"), value: translatedDepartment },
        candidature?.employeurBusinessName && { label: t("offre.employeur"), value: candidature.employeurBusinessName },
        dateCandidature && { label: t("candidature.date"), value: dateCandidature },
    ].filter(Boolean);

    const rappeler = async () => {
        setRappelEnCours(true);
        try { await onRappel(candidature); }
        finally { setRappelEnCours(false); }
    };

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
            extraActions={onRappel && !candidature?.contratSigne && !candidature?.contratASigner && (
                <Button role="rappel" size="small" loading={rappelEnCours} onClick={rappeler}>
                    {t("dashboard.remind_signatures")}
                </Button>
            )}
            footer={dateCreationValide ? (
                <span>{t("dashboard.process_started_since")} <time dateTime={candidature.dateCreationContrat}>
                    {dateCreation.toLocaleString(i18n?.language || "fr", { dateStyle: "short", timeStyle: "short" })}
                </time></span>
            ) : t("dashboard.started_date_unknown")}
            actionRole="details"
            actionIcon
            actionClassName="bg-[#043462] text-white hover:bg-[#03284d]"
            onClick={onClick}
            ariaLabel={t("dashboard.view_contract")}
        />
    );
}

export default CandidatureListItem;