import React from "react";
import { useTranslation } from "react-i18next";

const EtudiantItem = ({ etudiant }) => {
    const { t } = useTranslation();

    return (
        <div className="flex items-center justify-between w-full py-3 px-4 border rounded-lg bg-white my-1">

            <div className="flex items-center flex-1 min-w-[150px]">
                <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-xl bg-[#043462]/10 text-[#043462] font-bold text-lg">
                    {etudiant?.firstName?.charAt(0) || '?'}
                </div>
                <div className="ms-4 font-medium text-gray-800">
                    <p>{etudiant?.firstName || t("gestionnaire.assignation.etudiant.notFound")} {etudiant?.lastName || "gestionnaire.assignation.etudiant.notFound"}</p>
                </div>
            </div>

            <div className="divider divider-horizontal"></div>

            <div className="text-center flex-1 text-sm text-gray-800">
                <p className="text-gray-400 text-xs">{t("general.matricule")}</p>
                <p className="font-medium">{etudiant?.matricule || t("gestionnaire.assignation.etudiant.notFound")}</p>
            </div>

            <div className="divider divider-horizontal"></div>

            <div className="text-center flex-1 text-sm text-gray-800 px-2">
                <p className="text-gray-400 text-xs">{t("general.email")}</p>
                <p className="font-medium truncate">{etudiant?.professeurAssigne || t("gestionnaire.assignation.etudiant.noProfesseur")}</p>
            </div>
        </div>
    );
};

export default EtudiantItem;