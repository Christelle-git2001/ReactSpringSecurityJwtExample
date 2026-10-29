import { useTranslation } from "react-i18next";
import React from "react";

const EtudiantItemShort = ({ etudiant }) => {
    const { t } = useTranslation();

    return (
        <div className="flex justify-between items-center w-full py-2 px-3 border-b border-gray-900 hover:bg-gray-50 transition-colors">
            <span className="text-gray-800 text-sm font-medium">
                {etudiant?.firstName || t("gestionnaire.assignation.etudiant.notFound")} {etudiant?.lastName || ""}
            </span>

            <div className="flex items-center gap-1.5 text-xs">
                <span className="text-gray-400 font-normal">{t("general.matricule")}:</span>
                <span className="font-semibold text-gray-700">
                    {etudiant?.matricule || t("gestionnaire.assignation.etudiant.notFound")}
                </span>
            </div>
        </div>
    );
};

export default EtudiantItemShort;