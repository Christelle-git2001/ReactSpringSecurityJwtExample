import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { FiClock, FiVideo } from "react-icons/fi";
import Button from "../Design/Button.jsx";

function AddEntrevue({ candidatures = [], onSave }) {
    const { t } = useTranslation();
    const [selectedCandidature, setSelectedCandidature] = useState("");
    const [date, setDate] = useState("");
    const [heure, setHeure] = useState("");
    const [duree, setDuree] = useState("30");
    const [mode, setMode] = useState("EN_LIGNE");
    const [errors, setErrors] = useState({});

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
        <form
            noValidate
            onSubmit={handleSubmit}
            className="rounded-2xl bg-white p-6  border border-gray-200 flex flex-col gap-5 w-full"
        >
            <h3 className="text-lg font-bold text-[#043462] border-b border-gray-100 pb-3">
                {t("entrevue.add_title", "Planifier une entrevue")}
            </h3>

            <div className="flex flex-col gap-4">
                <div className="flex flex-col gap-1.5">
                    <label htmlFor="candidature" className="text-sm font-semibold text-[#043462]">
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
                    <label htmlFor="mode" className="text-sm font-semibold text-[#043462]">
                        {t("entrevue.mode", "Format")}
                    </label>
                    <select
                        id="mode"
                        value={mode}
                        onChange={(e) => setMode(e.target.value)}
                        className={inputStyle(false)}
                    >
                        <option value="EN_LIGNE">{t("entrevue.mode_online", "En ligne (Visioconférence)")}</option>
                        <option value="PRESENTIEL">{t("entrevue.mode_onsite", "En présentiel")}</option>
                    </select>
                </div>
                <div className="flex flex-col gap-1.5">
                    <label htmlFor="date" className="text-sm font-semibold text-[#043462]">
                        {t("entrevue.date", "Date de l'entrevue")}
                    </label>
                    <input
                        id="date"
                        type="date"
                        value={date}
                        onChange={(e) => setDate(e.target.value)}
                        className={inputStyle(errors.date)}
                    />
                    {errors.date && (
                        <span className="text-xs font-medium text-red-500">{t(errors.date)}</span>
                    )}
                </div>
                <div className="grid grid-cols-2 gap-3">
                    <div className="flex flex-col gap-1.5">
                        <label htmlFor="heure" className="text-sm font-semibold text-[#043462] flex items-center gap-1">
                            <FiClock className="size-3.5 text-[#043462]" />
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

            <div className="pt-3 border-t border-gray-100 flex justify-end">
                <Button type="submit" className="bg-[#043462] text-white hover:bg-[#03284d] w-full sm:w-auto">
                    {t("common.save", "Soumettre")}
                </Button>
            </div>
        </form>
    );
}

export default AddEntrevue;