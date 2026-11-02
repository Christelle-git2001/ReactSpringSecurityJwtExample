import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { obtenirToutesConvocations } from "../../api/etudiant.jsx";
import { FaCalendarAlt, FaClock } from "react-icons/fa";
import Button from "../Design/Button.jsx";

function ListeConvocations() {
    const { t, i18n } = useTranslation();
    const navigate = useNavigate();
    const [convocations, setConvocations] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(false);
    const visibles = convocations.filter((c) => c.statut !== "REFUSEE");

    useEffect(() => {
        obtenirToutesConvocations()
            .then((data) => setConvocations(data ?? []))
            .catch(() => setError(true))
            .finally(() => setLoading(false));
    }, []);

    if (loading) {
        return <div className="p-8 text-center"><span className="loading loading-spinner" /></div>;
    }

    if (error) {
        return <p role="alert" className="p-8 text-center text-red-700">{t("error.generic")}</p>;
    }

    return (
        <div className="w-full max-w-6xl mx-auto p-4 md:p-8">
            <h1 className="text-2xl md:text-3xl font-bold text-[#043462] mb-6">
                {t("convocation.list_title")}
            </h1>

            {visibles.length === 0 ? (
                <p className="text-center text-gray-500 py-8">
                    {t("convocation.none")}
                </p>
            ) : (
                <ul className="flex flex-col gap-3">
                    {visibles.map((c) => (
                        <li key={c.id}>
                            <div
                                className="w-full text-left flex flex-wrap sm:flex-nowrap items-center gap-4 sm:gap-6 rounded-2xl border border-gray-200 bg-white bg-[radial-gradient(circle_at_top_left,_#00CCCB33,_transparent_70%)] p-5 shadow-sm"
                            >
                                <div className="hidden sm:flex size-16 shrink-0 items-center justify-center rounded-2xl bg-sky-100 text-[#043462]">
                                    <FaCalendarAlt className="size-8" aria-hidden="true" />
                                </div>
                                <div className="min-w-0 flex-1 text-[#043462]">
                                    <p className="text-lg sm:text-xl font-bold break-words">{c.offreTitle}</p>
                                    <p className="mt-1 font-semibold">{c.employeur?.businessName}</p>
                                    <div className="mt-2 flex flex-wrap items-center gap-x-5 gap-y-2 text-sm sm:text-base">
                                        <span className="inline-flex items-center gap-2">
                                            <FaCalendarAlt aria-hidden="true" />
                                            {c.dateHeureEntrevue
                                                ? new Date(c.dateHeureEntrevue).toLocaleDateString(i18n.language, {
                                                    dateStyle: "medium",
                                                })
                                                : "—"}
                                        </span>
                                        <span className="inline-flex items-center gap-2 sm:border-l sm:border-[#043462]/30 sm:pl-5">
                                            <FaClock aria-hidden="true" />
                                            {c.dateHeureEntrevue
                                                ? new Date(c.dateHeureEntrevue).toLocaleTimeString(i18n.language, {
                                                    hour: "2-digit", minute: "2-digit",
                                                })
                                                : "—"}
                                        </span>
                                    </div>
                                </div>
                                {c.statut === "CONFIRMEE" && (
                                    <span className="badge badge-sm badge-success shrink-0 text-sm">
                                        {t("convocation.scheduled")}
                                    </span>
                                )}
                                <Button variant="standard" className="ml-auto shrink-0"
                                        onClick={() => navigate("/etudiant/entrevues/details", {
                                            state: { id: c.id, from: "/etudiant/entrevues" },
                                        })}>
                                    {t("convocation.view_details")}
                                </Button>
                            </div>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}

export default ListeConvocations;
