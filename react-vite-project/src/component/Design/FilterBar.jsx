import React from "react";
import { useTranslation } from "react-i18next";
import { FiFilter } from "react-icons/fi";
import { getDepartmentKey } from "../../utils/departementConverter.js";

export default function FilterBar({selectedStatus, onStatusChange, selectedDomain, onDomainChange, availableDomains = [], className = ""}) {
    const { t } = useTranslation();

    const statuses = [
        { key: "TOUS", label: t("status.tous") },
        { key: "EN_ATTENTE", label: t("status.en_attente") },
        { key: "ACCEPTEE", label: t("status.acceptee") },
        { key: "REFUSEE", label: t("status.refusee") }
    ];

    const isFilterActive =
        selectedStatus !== "TOUS" || selectedDomain !== "TOUS";

    return (
        <div
            className={`w-full collapse collapse-arrow bg-base-100 border border-base-300 rounded-xl shadow-sm ${className}`}
        >
            <input type="checkbox" />

            <div className="collapse-title flex items-center gap-2 font-medium text-sm text-gray-600">
                <FiFilter className="text-primary" />
                {t("actions.filter_by")}
                {isFilterActive && (
                    <span className="badge badge-primary badge-xs ml-2">
            {t("actions.active")}
          </span>
                )}
            </div>

            <div className="collapse-content space-y-4 text-sm">
                {/* Statut */}
                <div className="space-y-2">
          <span className="text-xs font-bold text-gray-500 uppercase tracking-wider block">
            {t("offre.statut")}
          </span>

                    <form className="filter flex flex-wrap gap-2">
                        {statuses.map((s) => (
                            <label key={s.key}>
                                <input
                                    type="checkbox"
                                    className="btn btn-sm"
                                    checked={selectedStatus === s.key}
                                    onChange={() => onStatusChange(s.key)}
                                    aria-label={s.label}
                                />
                            </label>
                        ))}

                        <input
                            className="btn btn-square btn-sm"
                            type="reset"
                            value="×"
                            title={t("actions.reset_filters")}
                            onClick={() => onStatusChange("TOUS")}
                        />
                    </form>
                </div>

                {/* Domaine */}
                {availableDomains.length > 0 && (
                    <div className="space-y-2 pt-2 border-t border-base-200">
            <span className="text-xs font-bold text-gray-500 uppercase tracking-wider block">
              {t("offre.domaine")}
            </span>

                        <form className="filter flex flex-wrap gap-2">
                            {/* Bouton TOUS */}
                            <label>
                                <input
                                    type="checkbox"
                                    className="btn btn-xs"
                                    checked={selectedDomain === "TOUS"}
                                    onChange={() => onDomainChange("TOUS")}
                                    aria-label={t("status.tous")}
                                />
                            </label>

                            {/* Boutons domaines convertis + traduits */}
                            {availableDomains.map((domain) => {
                                const key = getDepartmentKey(domain);
                                const translated = key
                                    ? t(`departement.${key}`, domain)
                                    : domain;

                                return (
                                    <label key={domain}>
                                        <input
                                            type="checkbox"
                                            className="btn btn-xs"
                                            checked={selectedDomain === domain}
                                            onChange={() => onDomainChange(domain)}
                                            aria-label={translated}
                                        />
                                    </label>
                                );
                            })}

                            <input
                                className="btn btn-square btn-xs"
                                type="reset"
                                value="×"
                                title={t("actions.reset_filters")}
                                onClick={() => onDomainChange("TOUS")}
                            />
                        </form>
                    </div>
                )}
            </div>
        </div>
    );
}
