import React, { useRef, useEffect } from "react";
import { useTranslation } from "react-i18next";
import Entrevue from "./Entrevue";
import { FiCalendar } from "react-icons/fi";
import "cally";

function Agenda({ entrevuesList = [], selectedDate, onDateSelect }) {
    const { t } = useTranslation();
    const calendarRef = useRef(null);

    useEffect(() => {
        const calendarEl = calendarRef.current;
        if (!calendarEl) return;
        if (selectedDate) {
            calendarEl.value = selectedDate;
        }

        const handleDateChange = (e) => {
            const newDate = e.target.value;
            onDateSelect?.(newDate || "");
        };

        calendarEl.addEventListener("change", handleDateChange);
        return () => {
            calendarEl.removeEventListener("change", handleDateChange);
        };
    }, [onDateSelect, selectedDate]);

    const filteredEntrevues = selectedDate
        ? entrevuesList.filter((entrevue) => {
            if (!entrevue?.date) return false;
            const formattedEntrevueDate = String(entrevue.date).trim().split("T")[0];
            return formattedEntrevueDate === String(selectedDate).trim();
        })
        : entrevuesList;

    return (
        <div className="bg-[#99F0EF] p-6 rounded-2xl shadow-sm border border-gray-200 flex flex-col gap-6 w-full">

            <div className="flex justify-between items-center border-b border-[#043462] pb-4">
                <div>
                    <h2 className="text-xl font-bold text-[#043462]">
                        {t("agenda.title", "Agenda")}
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
                        value={selectedDate || ""}
                        className="cally bg-white border border-gray-200 shadow-sm rounded-2xl p-3
                       w-full h-full overflow-hidden flex flex-col"
                    >
                        <svg aria-label="Previous" className="fill-current size-4 text-[#043462]" slot="previous"
                             xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
                            <path fill="currentColor" d="M15.75 19.5 8.25 12l7.5-7.5"></path>
                        </svg>

                        <svg aria-label="Next" className="fill-current size-4 text-[#043462]" slot="next"
                             xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
                            <path fill="currentColor" d="m8.25 4.5 7.5 7.5-7.5 7.5"></path>
                        </svg>

                        <calendar-month></calendar-month>
                    </calendar-date>
                </div>
            </div>

            <div className="flex flex-col gap-3">
                <div className="flex items-center justify-between">
                    <span className="px-3 text-xs font-bold text-[#043462] uppercase tracking-wider">
                        {selectedDate ? `${selectedDate}` : t("agenda.select_date_prompt", "Sélectionnez une date")}
                    </span>
                </div>

                <div className="flex flex-col gap-2 overflow-y-auto max-h-[320px] pr-1 scrollbar-thin scrollbar-thumb-gray-300">
                    {filteredEntrevues.length > 0 ? (
                        filteredEntrevues.map((entrevue) => (
                            <Entrevue key={entrevue.id} entrevue={entrevue} />
                        ))
                    ) : (
                        <div className="bg-white py-10 text-center text-gray-400 text-sm flex flex-col items-center justify-center gap-2 border border-dashed border-gray-200 rounded-xl">
                            <FiCalendar className="size-6 text-gray-300" />
                            {selectedDate
                                ? t("agenda.no_interviews_date", "Aucune entrevue pour cette date.")
                                : t("agenda.select_date_hint", "Cliquez sur une date pour voir les entrevues.")}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}

export default Agenda;