import React, { useState, useEffect, useRef } from "react";
import { useTranslation } from "react-i18next";
import { FiClock, FiVideo } from "react-icons/fi";
import Button from "../Design/Button.jsx";
import "cally";

function AddEntrevue({ candidatures = [], onSave }) {
    const { t } = useTranslation();
    const [selectedCandidature, setSelectedCandidature] = useState("");
    const [date, setDate] = useState("");
    const [heure, setHeure] = useState("");
    const [duree, setDuree] = useState("30");
    const [mode, setMode] = useState("EN_LIGNE"); // TODO "EN_LIGNE" ou "PRESENTIEL"
    const [errors, setErrors] = useState({});

    const calendarRef = useRef(null);

    useEffect(() => {
        const calendarEl = calendarRef.current;
        if (!calendarEl) return;

        const handleDateChange = (e) => {
            setDate(e.target.value);
        };

        calendarEl.addEventListener("change", handleDateChange);
        return () => {
            calendarEl.removeEventListener("change", handleDateChange);
        };
    }, []);

    const inputStyle = (hasError, isDisabled = false) =>
        `w-full rounded-lg border px-3.5 py-2.5 text-sm transition-colors outline-none focus:ring-2 ${
            isDisabled
                ? "bg-gray-100 text-gray-500 cursor-not-allowed border-gray-200"
                : hasError
                    ? "border-red-500 focus:ring-red-200"
                    : "border-gray-300 focus:border-[#0ee1cc] focus:ring-[#0ee1cc]/20"
        }`;

    const resetForm = () => {
        setSelectedCandidature("");
        setDate("");
        setHeure("");
        setDuree("30");
        setMode("EN_LIGNE");
        setErrors({});

        if (calendarRef.current) {
            calendarRef.current.value = "";
        }
    };

    const handleSubmit = (e) => {
        e.preventDefault();

        const newErrors = {};
        if (!selectedCandidature) newErrors.candidature = "entrevue.errors.select_candidature";
        if (!date) newErrors.date = "entrevue.errors.select_date";
        if (!heure) newErrors.heure = "entrevue.errors.select_time";

        if (Object.keys(newErrors).length > 0) {
            setErrors(newErrors);
            return;
        }

        setErrors({});

        const candidatureInfo = candidatures.find((c) => String(c.id) === String(selectedCandidature));

        const nouvelleEntrevue = {
            id: Date.now(),
            candidatureId: selectedCandidature,
            candidatNom: candidatureInfo
                ? `${candidatureInfo.etudiantFirstName || ""} ${candidatureInfo.etudiantLastName || ""}`.trim()
                : t("candidature.student_unknown"),
            posteTitre: candidatureInfo?.offreTitle || t("candidature.offer_unknown"),
            date,
            heure,
            duree: `${duree} min`,
            mode,
        };

        onSave?.(nouvelleEntrevue);
        resetForm();
    };

    return (
        <div className="min-h-screen flex items-center justify-center">
            <form
                noValidate
                onSubmit={handleSubmit}
                className="mx-auto max-w-2xl rounded-xl bg-white p-6 shadow-md md:p-8 w-full"
            >
                <h3 className="mb-6 text-xl font-bold text-[#043462] flex items-center gap-2 border-b border-gray-100 pb-4">
                    {t("entrevue.add_title", "Planifier une entrevue")}
                </h3>

                <div className="flex flex-col gap-5">
                    <div className="flex flex-col gap-1.5">
                        <label htmlFor="candidature" className="text-sm font-semibold text-[#043462] flex items-center gap-1.5">
                            {t("entrevue.select_candidature", "Candidat")}
                        </label>
                        <select
                            id="candidature"
                            value={selectedCandidature}
                            onChange={(e) => setSelectedCandidature(e.target.value)}
                            className={inputStyle(errors.candidature)}
                        >
                            <option value="" disabled>
                                {t("entrevue.placeholder_candidature", "-- Sélectionner un candidat --")}
                            </option>
                            {candidatures.map((c) => (
                                <option key={c.id} value={c.id}>
                                    {c.etudiantFirstName} {c.etudiantLastName} - {c.offreTitle}
                                </option>
                            ))}
                        </select>
                        {errors.candidature && (
                            <span className="text-xs font-medium text-red-500">{t(errors.candidature)}</span>
                        )}
                    </div>
                    <div className="flex flex-col gap-1.5">
                        <label htmlFor="mode" className="text-sm font-semibold text-[#043462] flex items-center gap-1.5">
                            {t("entrevue.mode", "Format")}
                        </label>
                        <select
                            id="mode"
                            value={mode}
                            onChange={(e) => setMode(e.target.value)}
                            className={inputStyle(false)}
                        >
                            <option value="EN_LIGNE">{t("entrevue.mode_online", "En ligne (Visioconférence)")}</option>
                            <option value="PRESENTIEL">{t("entrevue.mode_onsite", "En présentiel (locaux de l'entreprise)")}</option>
                        </select>
                    </div>

                    <div className="flex flex-col gap-1.5 items-center">
                        <label className="text-sm font-semibold text-[#043462] self-start flex items-center gap-1.5">
                            {t("entrevue.date", "Date de l'entrevue")}
                            {date && <span className="text-[#0ee1cc] font-bold">({date})</span>}
                        </label>

                        <calendar-date
                            ref={calendarRef}
                            class="cally bg-white border border-gray-300 shadow-sm rounded-xl p-3 w-full max-w-md"
                        >
                            <svg aria-label="Previous" className="fill-current size-4 text-[#043462]" slot="previous" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
                                <path fill="currentColor" d="M15.75 19.5 8.25 12l7.5-7.5"></path>
                            </svg>
                            <svg aria-label="Next" className="fill-current size-4 text-[#043462]" slot="next" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
                                <path fill="currentColor" d="m8.25 4.5 7.5 7.5-7.5 7.5"></path>
                            </svg>
                            <calendar-month></calendar-month>
                        </calendar-date>
                        {errors.date && (
                            <span className="text-xs font-medium text-red-500 self-start">{t(errors.date)}</span>
                        )}
                    </div>

                    <div className="grid grid-cols-1 gap-5 md:grid-cols-2">
                        <div className="flex flex-col gap-1.5">
                            <label htmlFor="heure" className="text-sm font-semibold text-[#043462] flex items-center gap-1.5">
                                {t("entrevue.time", "Heure")}
                            </label>
                            <input
                                id="heure"
                                type="time"
                                value={heure}
                                onChange={(e) => setHeure(e.target.value)}
                                className={inputStyle(errors.heure)}
                            />
                            {errors.heure && (
                                <span className="text-xs font-medium text-red-500">{t(errors.heure)}</span>
                            )}
                        </div>

                        <div className="flex flex-col gap-1.5">
                            <label htmlFor="duree" className="text-sm font-semibold text-[#043462]">
                                {t("entrevue.duration", "Durée")}
                            </label>
                            <select
                                id="duree"
                                value={duree}
                                onChange={(e) => setDuree(e.target.value)}
                                className={inputStyle(false)}
                            >
                                <option value="15">15 min</option>
                                <option value="30">30 min</option>
                                <option value="45">45 min</option>
                                <option value="60">60 min</option>
                            </select>
                        </div>
                    </div>
                </div>

                <div className="mt-8 flex justify-end gap-3 border-t border-gray-100 pt-5">
                    <Button type="submit" className="bg-[#043462] text-white hover:bg-[#03284d] flex items-center gap-2">
                        {t("common.save", "Soumettre")}
                    </Button>
                </div>
            </form>
        </div>
    );
}

export default AddEntrevue;