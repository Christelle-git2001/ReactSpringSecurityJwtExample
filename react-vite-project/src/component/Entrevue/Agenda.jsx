import React, { useRef, useEffect, useMemo } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import Entrevue from "./Entrevue";
import { FiCalendar } from "react-icons/fi";
import Button from "../Design/Button.jsx";
import "./Agenda.css";
import "cally";

function Agenda({ entrevuesList = [], candidaturesList = [], selectedDate, onDateSelect }) {
    const { t, i18n } = useTranslation();
    const navigate = useNavigate();
    const calendarRef = useRef(null);
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
            onDateSelect?.(newDate || "");
        };

        calendarEl.addEventListener("change", handleDateChange);
        return () => {
            calendarEl.removeEventListener("change", handleDateChange);
        };
    }, [onDateSelect, selectedDate]);

    const handleClearFilter = () => {
        onDateSelect?.("");
        if (calendarRef.current) {
            calendarRef.current.value = "";
        }
    };

    const cleanSelectedDate = getCleanDate(selectedDate);
    const locale = i18n.resolvedLanguage || i18n.language || "fr";
    const formatDate = (date) => new Intl.DateTimeFormat(locale, { dateStyle: "long" })
        .format(new Date(`${date}T12:00:00`));

    const filteredEntrevues = cleanSelectedDate
        ? entrevuesList.filter((entrevue) => getCleanDate(entrevue?.date) === cleanSelectedDate)
        : entrevuesList;

    const groupedEntrevues = filteredEntrevues.reduce((acc, entrevue) => {
        const dateKey = getCleanDate(entrevue?.date) || "sans_date";
        if (!acc[dateKey]) acc[dateKey] = [];
        acc[dateKey].push(entrevue);
        return acc;
    }, {});

    const sortedDates = Object.keys(groupedEntrevues).sort((a, b) => new Date(a) - new Date(b));

    const handleViewCandidature = (entrevue) => {
        const candidatureComplete = candidaturesList.find(
            (c) => String(c.id) === String(entrevue.candidatureId)
        );

        if (candidatureComplete) {
            navigate(`/employeur/candidatures/details`, {
                state: {
                    candidature: candidatureComplete,
                    from: "/employeur/entrevues"
                }
            });
        }
    };

    return (
        <div className="bg-[#99F0EF] p-4 sm:p-6 rounded-2xl shadow-sm border border-gray-200 flex flex-col gap-6 w-full min-w-0">
            <div className="flex justify-between items-center border-b border-[#043462] pb-4">
                <div>
                    <h2 className="text-xl font-bold text-[#043462]">
                        {t("agenda.title")}
                    </h2>
                </div>
                <span className="text-xs font-semibold px-3 py-1 rounded-full bg-[#043462] text-white">
                   {t("agenda.all_interviews")} {entrevuesList.length}
                </span>
            </div>

            <div className="flex justify-center w-full">
                <div className="w-full max-w-[600px] h-[360px] flex items-center justify-center">
                    <calendar-date
                        ref={calendarRef}
                        value={cleanSelectedDate}
                        locale={locale}
                        first-day-of-week="1"
                        aria-label={t("agenda.title")}
                        aria-describedby="interview-calendar-hint"
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
                            <span className="size-2 rounded-full bg-[#043462]" aria-hidden="true" />
                            {t("agenda.interview_day")}
                        </span>
                        <span className="inline-flex items-center gap-2">
                            <span className="size-3 rounded-sm border border-[#043462]" aria-hidden="true" />
                            {t("agenda.today")}
                        </span>
                    </div>
                    <Button variant="secondary" size="small" className="ml-auto shrink-0" onClick={handleClearFilter}>
                        {t("agenda.clear_filter")}
                    </Button>
                </div>
                <p id="interview-calendar-hint">{t("agenda.select_date_hint")}</p>
            </div>

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
                                />
                            ))}
                        </div>
                    ))
                ) : (
                    <div className="bg-white py-10 text-center text-gray-400 text-sm flex flex-col items-center justify-center gap-2 border border-dashed border-gray-200 rounded-xl">
                        <FiCalendar className="size-6 text-gray-300" />
                        {cleanSelectedDate
                            ? t("agenda.no_interviews_date")
                            : t("agenda.no_interviews")}
                    </div>
                )}
            </div>
        </div>
    );
}

function getCleanDate(dateVal) {
    return dateVal ? String(dateVal).trim().split("T")[0] : "";
}

export default Agenda;
