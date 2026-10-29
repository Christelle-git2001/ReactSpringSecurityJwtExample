import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { FiClock } from "react-icons/fi";
import Button from "../Design/Button.jsx";
import { validateEntrevueForm } from "../../utils/entrevueValidation.jsx";

function AddEntrevue({ candidatures = [],initialCandidatureId = "", onSave }) {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const [selectedCandidature, setSelectedCandidature] = useState(initialCandidatureId);
    const [date, setDate] = useState("");
    const [heure, setHeure] = useState("");
    const [duree, setDuree] = useState("30");
    const [mode, setMode] = useState("EN_LIGNE");
    const [errors, setErrors] = useState({});
    const [isSubmitting, setIsSubmitting] = useState(false);

    useEffect(() => {
        if (initialCandidatureId) {
            setSelectedCandidature(String(initialCandidatureId));
        }
    }, [initialCandidatureId]);

    const now = new Date();
    const currentHour = now.getHours();
    const currentMinute = now.getMinutes();

    const isDayPassed = currentHour > 18 || (currentHour === 18 && currentMinute > 0);

    const formatDate = (d) => [
        d.getFullYear(),
        String(d.getMonth() + 1).padStart(2, "0"),
        String(d.getDate()).padStart(2, "0"),
    ].join("-");

    const todayStr = formatDate(now);

    const minDateStr = isDayPassed ? formatDate(new Date(now.setDate(now.getDate() + 1))) : todayStr;

    const getTimeSlots = () => {
        const slots = [];
        const currentTotalMinutes = date === todayStr ? currentHour * 60 + currentMinute : -1;

        for (let hour = 8; hour <= 18; hour++) {
            for (let minute = 0; minute < 60; minute += 15) {
                if (hour === 18 && minute > 0) break;

                const slotTotalMinutes = hour * 60 + minute;

                if (date === todayStr && slotTotalMinutes <= currentTotalMinutes) {
                    continue;
                }

                const formattedHour = String(hour).padStart(2, "0");
                const formattedMinute = String(minute).padStart(2, "0");
                slots.push(`${formattedHour}:${formattedMinute}`);
            }
        }
        return slots;
    };

    const timeSlots = getTimeSlots();

    const inputStyle = (hasError) =>
        `w-full rounded-lg border px-3.5 py-2.5 text-sm transition-colors outline-none focus:ring-2 ${
            hasError
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
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        const newErrors = validateEntrevueForm({
            selectedCandidature,
            date,
            heure,
            format: mode,
        });

        if (Object.keys(newErrors).length > 0) {
            setErrors(newErrors);
            return;
        }

        setErrors({});
        setIsSubmitting(true);

        const dateHeureFormatted = `${date}T${heure}:00`;
        const entrevueDTO = {
            candidatureId: Number(selectedCandidature),
            format: mode,
            dateHeureEntrevue: dateHeureFormatted,
        };

        try {
            await onSave?.(entrevueDTO);
            resetForm();
        } catch (error) {
            console.error(error);
            setErrors({ submit: error?.message || "entrevue.errors.submit_failed" });
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <form
            noValidate
            onSubmit={handleSubmit}
            className="rounded-2xl bg-white p-6 border border-gray-200 flex flex-col gap-5 w-full"
        >
            <h3 className="text-lg font-bold text-[#043462] border-b border-gray-100 pb-3">
                {t("entrevue.add_title")}
            </h3>

            {errors.submit && (
                <div className="text-xs font-medium text-red-600 bg-red-50 p-2.5 rounded-lg border border-red-200">
                    {t(errors.submit)}
                </div>
            )}

            <div className="flex flex-col gap-4">
                <div className="flex flex-col gap-1.5">
                    <label htmlFor="candidature" className="text-sm font-semibold text-[#043462]">
                        {t("entrevue.select_candidature")}
                    </label>
                    <select
                        id="candidature"
                        value={selectedCandidature}
                        onChange={(e) => setSelectedCandidature(e.target.value)}
                        className={inputStyle(errors.candidature)}
                    >
                        <option value="" disabled>
                            {t("entrevue.placeholder_candidature")}
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
                    <label htmlFor="mode" className="text-sm font-semibold text-[#043462]">
                        {t("entrevue.mode")}
                    </label>
                    <select
                        id="mode"
                        value={mode}
                        onChange={(e) => setMode(e.target.value)}
                        className={inputStyle(errors.format)}
                    >
                        <option value="EN_LIGNE">{t("entrevue.mode_online")}</option>
                        <option value="PRESENTIEL">{t("entrevue.mode_onsite")}</option>
                    </select>
                    {errors.format && (
                        <span className="text-xs font-medium text-red-500">{t(errors.format)}</span>
                    )}
                </div>

                <div className="flex flex-col gap-1.5">
                    <label htmlFor="date" className="text-sm font-semibold text-[#043462]">
                        {t("entrevue.date")}
                    </label>
                    <input
                        id="date"
                        type="date"
                        min={minDateStr}
                        value={date}
                        onChange={(e) => {
                            setDate(e.target.value);
                            setHeure("");
                        }}
                        className={inputStyle(errors.date)}
                    />
                    {errors.date && (
                        <span className="text-xs font-medium text-red-500">{t(errors.date)}</span>
                    )}
                </div>

                <div className="grid grid-cols-2 gap-3">
                    <div className="flex flex-col gap-1.5">
                        <label htmlFor="heure" className="text-sm font-semibold text-[#043462] flex items-center gap-1">
                            {t("entrevue.time")}
                        </label>
                        <select
                            id="heure"
                            value={heure}
                            onChange={(e) => setHeure(e.target.value)}
                            className={inputStyle(errors.heure)}
                        >
                            <option value="" disabled>
                                --:--
                            </option>
                            {timeSlots.map((slot) => (
                                <option key={slot} value={slot}>
                                    {slot}
                                </option>
                            ))}
                        </select>
                        {errors.heure && (
                            <span className="text-xs font-medium text-red-500">{t(errors.heure)}</span>
                        )}
                    </div>

                    <div className="flex flex-col gap-1.5">
                        <label htmlFor="duree" className="text-sm font-semibold text-[#043462]">
                            {t("entrevue.duration")}
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

            <div className="pt-3 border-t border-gray-100 flex justify-end">
                <Button
                    type="button"
                    onClick={() => navigate("/employeur")}
                    className="bg-steal-500 mx-3 text-[#043462]  flex items-center justify-center gap-2 border border-steal-200"
                >
                    {t("actions.backhome")}
                </Button>
                <Button
                    type="submit"
                    disabled={isSubmitting}
                    className="bg-[#043462] text-white hover:bg-[#03284d] w-full sm:w-auto disabled:opacity-50"
                >
                    {isSubmitting ? t("common.loading") : t("common.save")}
                </Button>
            </div>
        </form>
    );
}

export default AddEntrevue;