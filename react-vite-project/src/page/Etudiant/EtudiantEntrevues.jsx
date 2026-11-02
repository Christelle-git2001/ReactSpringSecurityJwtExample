import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import { obtenirToutesConvocations } from "../../api/etudiant.jsx";
import { FaCalendarAlt, FaCheckCircle, FaClock } from "react-icons/fa";
import Button from "../../component/Design/Button.jsx";
import SearchBar from "../../component/Design/Bars/SearchBar.jsx";
import FilterBar from "../../component/Design/Bars/FilterBar.jsx";
import EmptyContentMessage from "../../component/Design/EmptyContentMessage.jsx";
import { useFilters } from "../../utils/useFilters.jsx";

function EtudiantEntrevues() {
    const { t, i18n } = useTranslation();
    const navigate = useNavigate();
    const [convocations, setConvocations] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(false);
    const visibles = convocations.filter((c) => c.statut !== "REFUSEE");
    const filtres = useFilters(visibles, {
        getTown: (c) => c.employeur?.town,
        getSearchableText: (c) => `${c.offreTitle || ""} ${c.employeur?.businessName || ""} ${c.dateHeureEntrevue || ""}`,
    });

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
            <div className="mb-6 space-y-4">
                <SearchBar value={filtres.searchTerm} onChange={filtres.setSearchTerm}
                           onClear={() => filtres.setSearchTerm("")} />
                <FilterBar selectedStatus={filtres.selectedStatus} onStatusChange={filtres.setSelectedStatus}
                           statusOptions={["TOUS", "A_CONFIRMER", "CONFIRMEE", "EN_ATTENTE_REPONSE"]}
                           statusLabels={{ TOUS: t("status.tous"), A_CONFIRMER: t("convocation.to_confirm"),
                               CONFIRMEE: t("convocation.scheduled"), EN_ATTENTE_REPONSE: t("convocation.awaiting_decision") }}
                           selectedTown={filtres.selectedTown} onTownChange={filtres.setSelectedTown}
                           availableTowns={filtres.availableTowns} />
            </div>
            <details open className="collapse collapse-arrow bg-transparent">
                <summary className="collapse-title px-0 text-[#043462]">
                    <h1 className="text-2xl md:text-3xl font-bold">
                        {t("convocation.list_title")} ({filtres.itemsFiltres.length})
                    </h1>
                </summary>
                <div className="collapse-content px-0">
                    <div className="my-4 h-px w-full bg-[#043462]" aria-hidden="true" />

            {filtres.itemsFiltres.length === 0 ? (
                <EmptyContentMessage message={t(visibles.length === 0 ? "convocation.none" : "convocation.no_matching")} />
            ) : (
                <ul className="flex flex-col gap-3">
                    {filtres.itemsFiltres.map((c) => (
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
                                                ? c.dateHeureEntrevue.split("T")[0]
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
                                    <span className="inline-flex shrink-0 items-center gap-2 rounded-full bg-green-100 px-4 py-2 text-sm font-semibold text-green-700">
                                        <FaCheckCircle aria-hidden="true" />
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
            </details>
        </div>
    );
}

export default EtudiantEntrevues;
