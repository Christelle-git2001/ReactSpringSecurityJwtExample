
import React from "react";
import { useTranslation } from "react-i18next";
import { FiFileText, FiEye, FiDownload } from "react-icons/fi";

function CarteRapport({
    rapport,
    nombreResultats,
    onView,
    onDownload
}) {
    const { t } = useTranslation();

    return (
        <div className="rapport-card flex items-center justify-between gap-3 rounded-xl border border-slate-200 bg-white px-4 py-3 shadow-sm hover:shadow-md transition-all">

            <div
                onClick={() => onView(rapport)}
                className="flex items-center gap-3 min-w-0 flex-1 cursor-pointer"
            >
                <div className="rounded-lg bg-blue-50 text-blue-600 p-2.5 shrink-0">
                    <FiFileText className="size-5" />
                </div>

                <div className="min-w-0">
                    <h3 className="font-semibold text-sm text-slate-800">
                        {rapport.titre}
                    </h3>

                    <p className="text-xs text-slate-500 mt-1">
                        {rapport.description}
                    </p>

                    <span className="rapport-count">
                        {nombreResultats === undefined
                            ? t("rapport.loading")
                            : nombreResultats === null
                                ? t("rapport.unavailable")
                                : t("rapport.results", {
                                    count: nombreResultats
                                })}
                    </span>
                </div>
            </div>

            <div className="rapport-actions flex items-center gap-2 shrink-0">
                <button
                    type="button"
                    onClick={() => onView(rapport)}
                    className="rapport-button"
                >
                    <FiEye />
                    <span>{t("rapport.actions.view")}</span>
                </button>

                <button
                    type="button"
                    onClick={() => onDownload(rapport)}
                    className="rapport-button rapport-button-pdf"
                >
                    <FiDownload />
                    <span>{t("rapport.actions.download_short")}</span>
                </button>
            </div>
        </div>
    );
}

export default CarteRapport;
