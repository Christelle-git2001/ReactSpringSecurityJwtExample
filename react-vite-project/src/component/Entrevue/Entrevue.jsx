import React from "react";
import { useTranslation } from "react-i18next";
import { FiVideo, FiMapPin, FiXCircle, FiCheckCircle } from "react-icons/fi";
import { actionsCandidatureDesactivees } from "../../utils/candidatureActions.js";

function Entrevue({ entrevue, onClick }) {
    const { t } = useTranslation();
    const isOnline = entrevue?.mode === "EN_LIGNE";
    const terminee = entrevue?.statut === "TERMINEE";
    const annulee = ["A_CONFIRMER", "CONFIRMEE"].includes(entrevue?.statut)
        && actionsCandidatureDesactivees(entrevue?.candidatureComplete);

    const calculateEndTime = (startTime, durationStr) => {
        if (!startTime) return "00:00";
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
            title={t("candidature.read")}
            className={`relative flex flex-wrap bg-white items-center gap-4 p-3 rounded-xl border transition-all cursor-pointer group shadow-xs ${
                isOnline
                    ? "hover:bg-teal-50/60 hover:border-teal-200 border-transparent"
                    : "hover:bg-indigo-50/60 hover:border-indigo-200 border-transparent"
            }`}
        >
            <div className="flex flex-col items-end w-16 shrink-0">
                <span className="text-sm font-bold text-[#043462]">
                    {entrevue?.heure}
                </span>
                <span className="text-xs text-gray-400">
                    {heureFin}
                </span>
            </div>

            <div className={`w-1 h-9 rounded-full ${isOnline ? "bg-teal-200" : "bg-indigo-200"}`}></div>

            <div className="flex flex-col flex-1 min-w-[100px]">
                <span className="text-xs font-semibold text-gray-400 uppercase tracking-wider truncate">
                    {entrevue?.posteTitre || t("candidature.offer_unknown")}
                </span>
                <span className={`text-sm font-bold text-[#043462] truncate transition-colors ${
                    isOnline ? "group-hover:text-teal-700" : "group-hover:text-indigo-700"
                }`}>
                    {entrevue?.candidatNom || t("candidature.student_unknown")}
                </span>
            </div>

            <div className="ml-auto flex max-w-full flex-wrap items-center justify-end gap-2">
                {annulee && <span className="inline-flex items-center gap-1.5 rounded-full border border-red-200 bg-red-100 px-2.5 py-1 text-xs font-semibold text-red-700">
                    <FiXCircle className="size-3.5 shrink-0" aria-hidden="true" />
                    {t("agenda.interview_cancelled")}
                </span>}
                {terminee && <span className="inline-flex items-center gap-1.5 rounded-full border border-blue-200 bg-blue-100 px-2.5 py-1 text-xs font-semibold text-blue-800">
                    <FiCheckCircle className="size-3.5 shrink-0" aria-hidden="true" />
                    {t("agenda.interview_completed")}
                </span>}
                <span className={`text-xs p-1.5 rounded-lg flex items-center justify-center ${
                    isOnline ? "bg-teal-50 text-teal-600" : "bg-indigo-50 text-indigo-600"
                }`} title={isOnline ? t("entrevue.mode_online_title") : t("entrevue.mode_onsite_title")}
                >
                    {isOnline ? <FiVideo className="size-3.5" /> : <FiMapPin className="size-3.5" />}
                </span>
            </div>
        </div>
    );
}

export default Entrevue;
