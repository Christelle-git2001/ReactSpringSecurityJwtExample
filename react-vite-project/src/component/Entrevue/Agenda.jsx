import React, { useRef, useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import Entrevue from "./Entrevue";
import { FiCalendar, FiFilter } from "react-icons/fi";
import { FaFilter } from "react-icons/fa";
import Button from "../Design/Button.jsx";
import { getCandidaturesEmployeur } from "../../api/employeur.jsx";
import "./Agenda.css";
import "cally";

function Agenda({ entrevuesList = [], candidaturesList = [], selectedDate, onDateSelect, onDecision, compact = false, onViewCandidature }) {
    const { t, i18n } = useTranslation();
    const navigate = useNavigate();
    const calendarRef = useRef(null);
    const [showAllInterviews, setShowAllInterviews] = useState(!selectedDate);
    const [lastSelectedDate, setLastSelectedDate] = useState(selectedDate || "");
    const interviewDates = useMemo(
        () => new Set(entrevuesList.map((entrevue) => getCleanDate(entrevue?.date)).filter(Boolean)),
        [entrevuesList]
    );

    useEffect(() => {
        if (calendarRef.current) {
            calendarRef.current.getDayParts = (date) =>
                interviewDates.has(date.toISOString().slice(0, 10)) ? "interview" : "";
        }
    }, [interviewDates]);

    useEffect(() => {
        const calendarEl = calendarRef.current;
        if (!calendarEl) return;

        calendarEl.value = selectedDate || "";
        if (selectedDate) calendarEl.focusedDate = selectedDate;

        const handleDateChange = (e) => {
            const newDate = e.target.value;
            setLastSelectedDate(newDate);
            setShowAllInterviews(false);
            onDateSelect?.(newDate || "");
        };

        calendarEl.addEventListener("change", handleDateChange);
        return () => {
            calendarEl.removeEventListener("change", handleDateChange);
        };
    }, [onDateSelect, selectedDate]);

    const handleToggleInterviews = () => {
        if (showAllInterviews) {
            const date = lastSelectedDate ||
                new Date().toLocaleDateString("en-CA");

            setLastSelectedDate(date);
            setShowAllInterviews(false);
            onDateSelect?.(date);
        } else {
            setShowAllInterviews(true);
        }
    };

    const cleanSelectedDate = getCleanDate(selectedDate);
    const locale = i18n.resolvedLanguage || i18n.language || "fr";
    const formatDate = (date) => new Intl.DateTimeFormat(locale, { dateStyle: "long" })
        .format(new Date(`${date}T12:00:00`));

    const filteredEntrevues = showAllInterviews
        ? entrevuesList
        : entrevuesList.filter((entrevue) => getCleanDate(entrevue?.date) === cleanSelectedDate);

    const groupedEntrevues = filteredEntrevues.reduce((acc, entrevue) => {
        const dateKey = getCleanDate(entrevue?.date) === "" ? "sans_date" : getCleanDate(entrevue?.date);
        if (!acc[dateKey]) acc[dateKey] = [];
        acc[dateKey].push(entrevue);
        return acc;
    }, {});

    const sortedDates = Object.keys(groupedEntrevues).sort((a, b) => new Date(a) - new Date(b));

    const handleViewCandidature = async (entrevue) => {
        if (onViewCandidature) {
            onViewCandidature(entrevue);
            return;
        }
        try {
            let candidatureComplete = entrevue.candidatureComplete || candidaturesList.find(
                (c) => String(c.id) === String(entrevue.candidatureId)
            );

            if (!candidatureComplete || !candidatureComplete.cvId) {
                const toutesLesCandidatures = await getCandidaturesEmployeur();
                candidatureComplete = (toutesLesCandidatures || []).find(
                    (c) => String(c.id) === String(entrevue.candidatureId)
                );
            }

            if (candidatureComplete) {
                navigate(`/employeur/candidatures/details`, {
                    state: {
                        candidature: candidatureComplete,
                        from: "/employeur/entrevues"
                    }
                });
            } else {
                console.error("Erreur : Impossible de trouver la candidature complète pour l'ID :", entrevue.candidatureId);
            }
        } catch (error) {
            console.error("Erreur lors de la récupération de la candidature :", error);
        }
    };

    return (
        <div className={`bg-[#99F0EF] rounded-2xl shadow-sm border border-gray-200 flex flex-col w-full min-w-0 ${compact ? "p-3 gap-3" : "p-4 sm:p-6 gap-6"}`}>
            <div className="flex justify-between items-center border-b border-[#043462] pb-4">
                <div>
                    <h2 className="flex items-center gap-2 text-xl font-bold text-[#043462]">
                        <FiCalendar aria-hidden="true" />
                        {t("agenda.title")}
                    </h2>
                </div>
                <span className="text-xs font-semibold px-3 py-1 rounded-full bg-[#043462] text-white">
                   {t("agenda.interviews_list")} : {entrevuesList.length}
                </span>
            </div>

            <div className={compact ? "grid gap-4 md:grid-cols-[280px_minmax(0,1fr)]" : "contents"}>
                <div className={compact ? "min-w-0 space-y-3" : "contents"}>
                    <div className="flex justify-center w-full">
                        <div className={`w-full flex items-center justify-center ${compact ? "max-w-[300px] h-[300px]" : "max-w-[700px] h-[430px]"}`}>
                            <calendar-date
                                ref={calendarRef}
                                value={cleanSelectedDate}
                                locale={locale}
                                first-day-of-week="1"
                                aria-label={t("agenda.title")}
                                className="cally interview-calendar bg-white border border-gray-200 shadow-sm rounded-2xl p-3
                               w-full h-full overflow-hidden flex flex-col"
                            >
                                <svg aria-label={t("agenda.previous_month")} className="fill-current size-4 text-[#043462]" slot="previous"
                                     xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
                                    <path fill="currentColor" d="M15.75 19.5 8.25 12l7.5-7.5"></path>
                                </svg>

                                <svg aria-label={t("agenda.next_month")} className="fill-current size-4 text-[#043462]" slot="next"
                                     xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
                                    <path fill="currentColor" d="m8.25 4.5 7.5 7.5-7.5 7.5"></path>
                                </svg>

                                <calendar-month></calendar-month>
                            </calendar-date>
                        </div>
                    </div>

                    <div className="space-y-2 text-xs text-[#043462]">
                        <div className="flex flex-wrap items-center justify-between gap-3">
                            <div className="flex flex-wrap items-center gap-x-4 gap-y-2">
                                <span className="inline-flex items-center gap-2">
                                    <span className="size-3 rounded-full bg-[#E53935]" aria-hidden="true" />
                                    {t("agenda.interview_day")}
                                </span>
                                <span className="inline-flex items-center gap-2">
                                    <span className="size-3 aspect-square rounded-none border-2 border-[#6B91B5] shrink-0" aria-hidden="true" />
                                    {t("agenda.today")}
                                </span>
                            </div>
                            <Button variant="secondary" size="small"
                                    icon={showAllInterviews
                                        ? <FiFilter className="size-4" aria-hidden="true" />
                                        : <FaFilter className="size-4" aria-hidden="true" />}
                                    className={`${compact ? "" : "ml-auto"} shrink-0`} onClick={handleToggleInterviews}>
                                {showAllInterviews ? t("agenda.selected_day_interviews") : t("agenda.clear_filter")}
                            </Button>
                        </div>
                    </div>

                </div>
                <div className={compact ? "min-w-0" : "contents"}>
                    {compact && <h3 className="mb-3 flex items-center gap-2 text-sm font-bold text-[#043462]">
                        <FiCalendar aria-hidden="true" />{t("agenda.interviews_list")}
                    </h3>}
                    <div aria-live="polite" className="flex flex-col gap-4 overflow-y-auto max-h-[360px] pr-1 scrollbar-thin scrollbar-thumb-gray-300">
                        {sortedDates.length > 0 ? (
                            sortedDates.map((dateKey) => (
                                <div key={dateKey} className="flex flex-col gap-2">
                                    <div className="sticky top-0 bg-[#99F0EF] py-1 z-10 flex items-center justify-between">
                                        <span className="px-1 text-xs font-bold text-[#043462] uppercase tracking-wider">
                                            {dateKey === "sans_date" ? t("agenda.no_date") : formatDate(dateKey)}
                                        </span>
                                    </div>
                                    {[...groupedEntrevues[dateKey]].sort((a, b) => (a.heure || "").localeCompare(b.heure || "")).map((entrevue) => (
                                        <Entrevue
                                            key={entrevue.id}
                                            entrevue={entrevue}
                                            onClick={() => handleViewCandidature(entrevue)}
                                            onDecision={onDecision}
                                        />
                                    ))}
                                </div>
                            ))
                        ) : (
                            <div className="bg-white py-10 text-center text-gray-400 text-sm flex flex-col items-center justify-center gap-2 border border-dashed border-gray-200 rounded-xl">
                                <FiCalendar className="size-6 text-gray-300" />
                                {!showAllInterviews && cleanSelectedDate
                                    ? t("agenda.no_interviews_date")
                                    : t("agenda.no_interviews")}
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}

function getCleanDate(dateVal) {
    return dateVal ? String(dateVal).trim().split("T")[0] : "";
}

export default Agenda;
