import React from "react";
import { useTranslation } from "react-i18next";
import { FiFilter } from "react-icons/fi";
import { getDepartmentKey } from "../../utils/departementConverter.js";

export default function FilterBar({selectedStatus, onStatusChange, selectedDomain, onDomainChange, availableDomains = [], selectedTown, onTownChange, availableTowns = [], className = "", statusLabels = {}}) {
    const { t } = useTranslation();

    const showStatusFilter = selectedStatus !== undefined && onStatusChange;
    const showDomainFilter = availableDomains.length > 0 && onDomainChange;
    const showTownFilter = availableTowns.length > 0 && onTownChange;

    const isFilterActive =
        (showStatusFilter && selectedStatus !== "TOUS") ||
        (showDomainFilter && selectedDomain !== "TOUS") ||
        (showTownFilter && selectedTown !== "TOUS");

    return (
        <div className={`w-full collapse collapse-arrow bg-base-100 border border-base-300 rounded-xl shadow-sm ${className}`}>
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
                {showStatusFilter && (
                    <div className="space-y-2">
                        <span className="text-xs font-bold text-gray-500 uppercase tracking-wider block">
                            {t("offre.statut")}
                        </span>

                        <form className="filter flex flex-wrap gap-2">
                            {["TOUS", "EN_ATTENTE", "ACCEPTEE", "REFUSEE"].map((key) => (
                                <label key={key}>
                                    <input
                                        type="checkbox"
                                        className="btn btn-sm"
                                        checked={selectedStatus === key}
                                        onChange={() => onStatusChange(key)}
                                        aria-label={statusLabels[key] ?? t(`status.${key.toLowerCase()}`)}
                                    />
                                </label>
                            ))}

                            <input
                                className="btn btn-square btn-sm"
                                type="reset"
                                value="×"
                                onClick={() => onStatusChange("TOUS")}
                            />
                        </form>
                    </div>
                )}
                {showDomainFilter && (
                    <div className="space-y-2 pt-2 border-t border-base-200">
                        <span className="text-xs font-bold text-gray-500 uppercase tracking-wider block">
                            {t("offre.domaine")}
                        </span>

                        <form className="filter flex flex-wrap gap-2">
                            <label>
                                <input
                                    type="checkbox"
                                    className="btn btn-xs"
                                    checked={selectedDomain === "TOUS"}
                                    onChange={() => onDomainChange("TOUS")}
                                    aria-label={t("status.tous") ?? "Tous"}
                                />
                            </label>

                            {availableDomains.map((domain) => {
                                const key = getDepartmentKey(domain);
                                const translated = key ? t(`departement.${key}`, domain) : domain;

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
                                onClick={() => onDomainChange("TOUS")}
                            />
                        </form>
                    </div>
                )}
                {showTownFilter && (
                    <div className="space-y-2 pt-2 border-t border-base-200">
                        <span className="text-xs font-bold text-gray-500 uppercase tracking-wider block">
                            {t("offre.lieu")}
                        </span>

                        <form className="filter flex flex-wrap gap-2">
                            <label>
                                <input
                                    type="checkbox"
                                    className="btn btn-xs"
                                    checked={selectedTown === "TOUS"}
                                    onChange={() => onTownChange("TOUS")}
                                    aria-label={t("status.tous") ?? "Tous"}
                                />
                            </label>

                            {availableTowns.map((town) => (
                                <label key={town}>
                                    <input
                                        type="checkbox"
                                        className="btn btn-xs"
                                        checked={selectedTown === town}
                                        onChange={() => onTownChange(town)}
                                        aria-label={town}
                                    />
                                </label>
                            ))}

                            <input
                                className="btn btn-square btn-xs"
                                type="reset"
                                value="×"
                                onClick={() => onTownChange("TOUS")}
                            />
                        </form>
                    </div>
                )}

            </div>
        </div>
    );
}