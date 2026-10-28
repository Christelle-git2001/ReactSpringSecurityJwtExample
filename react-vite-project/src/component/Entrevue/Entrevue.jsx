import React from "react";
import { useTranslation } from "react-i18next";
import { FiVideo, FiMapPin } from "react-icons/fi";

function Entrevue({ entrevue, onClick }) {
    const { t } = useTranslation();
    const isOnline = entrevue?.mode === "EN_LIGNE";

    const calculateEndTime = (startTime, durationStr) => {
        const [hours, minutes] = startTime.split(":").map(Number);
        const durationMin = parseInt(durationStr) || 30;
        const totalMinutes = hours * 60 + minutes + durationMin;
        const endHours = Math.floor(totalMinutes / 60) % 24;
        const endMinutes = totalMinutes % 60;

        return `${String(endHours).padStart(2, "0")}:${String(endMinutes).padStart(2, "0")}`;
    };

    const heureFin = entrevue?.heureFin || calculateEndTime(entrevue?.heure, entrevue?.duree);

    return (
        <div
            onClick={() => onClick?.(entrevue)}
            className="flex bg-white items-center gap-4 p-3 rounded-xl hover:bg-gray-50 transition-all cursor-pointer group"
        >
            <div className="flex flex-col items-end w-16 shrink-0">
                <span className="text-sm font-bold text-[#043462]">
                    {entrevue?.heure}
                </span>
                <span className="text-xs text-gray-400">
                    {heureFin}
                </span>
            </div>

            <div className={`w-1 h-9 rounded-full ${isOnline ? "bg-teal-200" : "bg-amber-200"}`}></div>

            <div className="flex flex-col flex-1 min-w-0">
                <span className="text-xs font-semibold text-gray-400 uppercase tracking-wider truncate">
                    {entrevue?.posteTitre || t("candidature.offer_unknown")}
                </span>
                <span className="text-sm font-bold text-[#043462] truncate">
                    {entrevue?.candidatNom || t("candidature.student_unknown")}
                </span>
            </div>

            <div className="shrink-0">
                <span className={`text-xs p-1.5 rounded-lg flex items-center justify-center ${
                    isOnline ? "bg-teal-50 text-teal-600" : "bg-amber-50 text-amber-600"
                }`} title={isOnline ? t("entrevue.mode_online_title") : t("entrevue.mode_onsite_title")}
                >
                    {isOnline ? <FiVideo className="size-3.5" /> : <FiMapPin className="size-3.5" />}
                </span>
            </div>
        </div>
    );
}

export default Entrevue;