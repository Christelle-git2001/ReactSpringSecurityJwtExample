import React from "react";
import { useTranslation } from "react-i18next";

const EtudiantItem = ({ etudiant }) => {
    const { t } = useTranslation();

    return (
        <div className="flex items-center w-full py-3 px-4 border rounded-xl bg-white shadow-sm hover:shadow-md transition-shadow my-2 gap-4">

            <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-xl bg-[#043462]/10 text-[#043462] font-bold text-lg">
                {etudiant?.firstName?.charAt(0) || '?'}
            </div>

            <div className="grid grid-cols-2 gap-x-8 gap-y-1 w-full">

                <div>
                    <p className="font-semibold text-gray-800 text-sm">
                        {etudiant?.firstName || t("gestionnaire.assignation.etudiant.notFound")} {etudiant?.lastName || ""}
                    </p>
                </div>

                <div>
                    <p className="text-xs text-gray-500 ">
                        {etudiant?.email || t("gestionnaire.assignation.etudiant.notFound")}
                    </p>
                </div>

                <div className="flex items-center gap-2">
                    <span className="text-gray-400 text-xs uppercase tracking-wider">{t("general.matricule")} :</span>
                    <span className="font-medium text-gray-700 text-xs">
                        {etudiant?.matricule || t("gestionnaire.assignation.etudiant.notFound")}
                    </span>
                </div>

                <div className="flex items-center gap-2">
                    <span className="text-gray-400 text-xs uppercase tracking-wider">{t("gestionnaire.assignation.etudiant.teacher")} :</span>
                    <span className={`font-medium text-xs  ${etudiant?.professeurAssigne ? "text-[#043462]" : "text-amber-600 font-normal italic"}`}>
                        {etudiant?.professeurAssigne || t("gestionnaire.assignation.etudiant.noProfesseur")}
                    </span>
                </div>

            </div>

        </div>
    );
};

export default EtudiantItem;