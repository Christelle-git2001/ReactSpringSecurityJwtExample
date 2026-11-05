import React, { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { useLocation } from "react-router-dom";
import { obtenirToutesConvocations, obtenirCandidaturesEtudiant, accepterConvocation, refuserConvocation } from "../../api/etudiant.jsx";
import { FaBriefcase, FaCalendarAlt, FaCheckCircle, FaClock, FaHourglassHalf, FaMapMarkerAlt } from "react-icons/fa";
import Button from "../../component/Design/Button.jsx";
import Modal from "../../component/Modal/Modal.jsx";
import SearchBar from "../../component/Design/Bars/SearchBar.jsx";
import FilterBar from "../../component/Design/Bars/FilterBar.jsx";
import EmptyContentMessage from "../../component/Design/EmptyContentMessage.jsx";
import CandidatureTimeline from "../../component/Candidature/CandidatureTimeline.jsx";
import Sort from "../../component/Design/Sort.jsx";
import MotifRefus from "../../component/Design/MotifRefus.jsx";
import { useFilters } from "../../utils/useFilters.jsx";
import { getStatusConfig } from "../../utils/getStatusConfig.jsx";

const STATUTS_CANDIDATURE = ["SOUMISE", "RETENUE_POUR_ENTREVUE", "ENTREVUE_PLANIFIEE", "ENTREVUE_CONFIRMEE", "ENTREVUE_REFUSEE", "ACCEPTEE", "REFUSEE", "STAGE_TROUVE", "CONTRAT_CREE"];

const FORMAT_KEYS = {
    EN_LIGNE: "entrevue.mode_online_title",
    PRESENTIEL: "entrevue.mode_onsite_title",
};

function EtudiantCandidatures() {
    const { t, i18n } = useTranslation();
    const location = useLocation();
    const focusId = location.state?.id;

    const [convocations, setConvocations] = useState([]);
    const [candidatures, setCandidatures] = useState([]);
    const [sortOrder, setSortOrder] = useState("desc");
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(false);
    const [focusedCardId, setFocusedCardId] = useState(null);

    const [submittingId, setSubmittingId] = useState(null);
    const [actionError, setActionError] = useState(null);
    const [refusingId, setRefusingId] = useState(null);
    const [motifRefus, setMotifRefus] = useState("");
    const [motifError, setMotifError] = useState(false);
    const [confirmRefusId, setConfirmRefusId] = useState(null);

    const visibles = candidatures.map((candidature) => {
        const convocation = convocations.find((c) => c.candidatureId === candidature.id);
        return {
            ...convocation,
            candidature,
            candidatureId: candidature.id,
            offreTitle: candidature.offreTitle,
            employeur: convocation?.employeur ?? { businessName: candidature.employeurBusinessName },
            statutFiltre: convocation?.statut === "A_CONFIRMER" ? "A_CONFIRMER" : candidature.statut,
        };
    });
    const filtres = useFilters(visibles, {
        getStatus: (c) => c.statutFiltre,
        getTown: (c) => c.employeur?.town,
        getSearchableText: (c) => `${c.offreTitle || ""} ${c.employeur?.businessName || ""} ${c.dateHeureEntrevue || ""}`,
    });

    const candidaturesTriees = [...filtres.itemsFiltres].sort((a, b) => {
        const comparaison = (a.candidature.dateCandidature ?? "").localeCompare(b.candidature.dateCandidature ?? "");
        return sortOrder === "asc" ? comparaison : -comparaison;
    });

    const charger = () =>
        Promise.all([obtenirToutesConvocations(), obtenirCandidaturesEtudiant()])
            .then(([data, liste]) => {
                setConvocations(data ?? []);
                setCandidatures(liste ?? []);
            })
            .catch(() => setError(true));

    useEffect(() => {
        charger().finally(() => setLoading(false));
    }, []);

    useEffect(() => {
        if (focusId && !loading) {
            setFocusedCardId(Number(focusId));

            setTimeout(() => {
                const element = document.getElementById(`entrevue-${focusId}`);
                if (element) {
                    element.scrollIntoView({ behavior: 'smooth', block: 'center' });
                }
            }, 200);
        }
    }, [focusId, loading]);

    const repondre = async (id, action) => {
        setSubmittingId(id);
        setActionError(null);
        try {
            await action();
            await charger();
        } catch (err) {
            console.error("Erreur réponse convocation:", err);
            setActionError("error.generic");
        } finally {
            setSubmittingId(null);
        }
    };

    const ouvrirRefus = (id) => {
        setRefusingId(id);
        setMotifRefus("");
        setMotifError(false);
        setActionError(null);
    };

    const annulerRefus = () => {
        setRefusingId(null);
        setMotifRefus("");
        setMotifError(false);
    };

    const demanderConfirmation = () => {
        if (!motifRefus.trim()) {
            setMotifError(true);
            return;
        }
        setConfirmRefusId(refusingId);
    };

    const confirmerRefus = () => {
        const id = confirmRefusId;
        const motif = motifRefus.trim();
        setConfirmRefusId(null);
        annulerRefus();
        repondre(id, () => refuserConvocation(id, motif));
    };

    if (loading) {
        return <div className="p-8 text-center"><span className="loading loading-spinner" /></div>;
    }

    if (error) {
        return <p role="alert" className="p-8 text-center text-red-700">{t("error.generic")}</p>;
    }

    return (
        <div className="w-full max-w-6xl mx-auto p-4 md:p-8">
            <h1 className="mb-2 text-2xl md:text-3xl font-bold text-[#043462]">
                {t("candidature.my_applications")} ({filtres.itemsFiltres.length})
            </h1>
            <div className="my-4 h-px w-full bg-[#043462]" aria-hidden="true" />
            <div className="mb-6 space-y-4">
                <SearchBar value={filtres.searchTerm} onChange={filtres.setSearchTerm}
                           placeholder={t("candidature.search_placeholder")} onClear={() => filtres.setSearchTerm("")} />
                <FilterBar selectedStatus={filtres.selectedStatus} onStatusChange={filtres.setSelectedStatus}
                           statusOptions={["TOUS", ...STATUTS_CANDIDATURE, "A_CONFIRMER"]}
                           statusLabels={{ TOUS: t("status.tous"), A_CONFIRMER: t("convocation.interview_to_confirm"),
                               ...Object.fromEntries(STATUTS_CANDIDATURE.map((statut) => [statut, getStatusConfig(statut, t, "candidature").label])) }}
                           selectedTown={filtres.selectedTown} onTownChange={filtres.setSelectedTown}
                           availableTowns={filtres.availableTowns} />
                <div className="flex justify-end">
                    <Sort sortOrder={sortOrder} onSortChange={setSortOrder}
                          newestLabel={t("candidature.newest_first")}
                          oldestLabel={t("candidature.oldest_first")} />
                </div>
            </div>

            <div>
                    {actionError && <p role="alert" className="mb-3 text-sm text-red-700">{t(actionError)}</p>}

                    {filtres.itemsFiltres.length === 0 ? (
                        <EmptyContentMessage message={t(visibles.length === 0 ? "candidature.none" : "candidature.no_matching_candidatures")} />
                    ) : (
                        <ul className="flex flex-col gap-3">
                            {candidaturesTriees.map((c) => {
                                const aRepondre = c.statut === "A_CONFIRMER";
                                const enCours = submittingId === c.id;
                                const refusOuvert = refusingId === c.id;
                                const isFocused = focusedCardId === c.id;
                                const candidature = c.candidature;
                                const statutConfig = getStatusConfig(candidature.statut, t, "candidature");
                                return (
                                    <li key={c.candidatureId} id={c.id ? `entrevue-${c.id}` : `candidature-${c.candidatureId}`}>
                                        <div className={`w-full rounded-2xl border bg-white shadow-sm overflow-hidden transition-all duration-500 ${
                                            isFocused ? 'border-2 border-black ring-4 ring-gray-200' : 'border-gray-200'
                                        }`}>

                                            <div className="flex flex-wrap sm:flex-nowrap items-center gap-4 sm:gap-6 p-5 bg-[radial-gradient(circle_at_top_left,_#00CCCB33,_transparent_70%)]">
                                                <div className="hidden sm:flex size-16 shrink-0 items-center justify-center rounded-2xl bg-emerald-100 text-emerald-700">
                                                    <FaBriefcase className="size-8" aria-hidden="true" />
                                                </div>

                                                <div className="min-w-0 flex-1 text-[#043462]">
                                                    <p className="text-lg sm:text-xl font-bold break-words">{c.offreTitle}</p>
                                                    <p className="mt-1 font-semibold">{c.employeur?.businessName}</p>
                                                    {c.id && <div className="mt-2 flex flex-wrap items-center gap-x-5 gap-y-2 text-sm sm:text-base">
                                                        <span className="inline-flex items-center gap-2">
                                                            <FaCalendarAlt aria-hidden="true" />
                                                            {c.dateHeureEntrevue ? c.dateHeureEntrevue.split("T")[0] : "—"}
                                                        </span>
                                                        <span className="inline-flex items-center gap-2 sm:border-l sm:border-[#043462]/30 sm:pl-5">
                                                            <FaClock aria-hidden="true" />
                                                            {c.dateHeureEntrevue
                                                                ? new Date(c.dateHeureEntrevue).toLocaleTimeString(i18n.language, {
                                                                    hour: "2-digit", minute: "2-digit",
                                                                })
                                                                : "—"}
                                                        </span>
                                                        <span className="inline-flex items-center gap-2 sm:border-l sm:border-[#043462]/30 sm:pl-5">
                                                            <FaHourglassHalf aria-hidden="true" />
                                                            {c.duree ? t("convocation.duration_value", { minutes: c.duree }) : "—"}
                                                        </span>
                                                        <span className="inline-flex items-center gap-2 sm:border-l sm:border-[#043462]/30 sm:pl-5">
                                                            <FaMapMarkerAlt aria-hidden="true" />
                                                            {FORMAT_KEYS[c.format] ? t(FORMAT_KEYS[c.format]) : (c.format ?? "—")}
                                                        </span>
                                                    </div>}
                                                </div>

                                                <div className="ml-auto flex shrink-0 flex-col items-end gap-2">
                                                <span className={`inline-flex shrink-0 items-center gap-2 rounded-full border px-3 py-1 text-sm font-semibold ${aRepondre ? "bg-amber-100 text-amber-800 border-amber-200" : c.statut === "CONFIRMEE" && candidature.statut === "ENTREVUE_PLANIFIEE" ? "bg-green-100 text-green-700 border-green-200" : statutConfig.className}`}>
                                                    {c.statut === "CONFIRMEE" && <FaCheckCircle aria-hidden="true" />}
                                                    {aRepondre ? t("convocation.interview_to_confirm") : statutConfig.label}
                                                </span>

                                                {aRepondre && !refusOuvert && (
                                                    <div className="flex gap-2">
                                                        <Button variant="danger" size="small" type="button" disabled={enCours}
                                                                onClick={() => ouvrirRefus(c.id)}>
                                                            {t("convocation.reject")}
                                                        </Button>
                                                        <Button variant="success" size="small" type="button" disabled={enCours}
                                                                onClick={() => repondre(c.id, () => accepterConvocation(c.id))}>
                                                            {t("convocation.accept")}
                                                        </Button>
                                                    </div>
                                                )}
                                                </div>
                                            </div>

                                            <details className="collapse collapse-arrow rounded-none border-t border-gray-100">
                                                <summary className="collapse-title min-h-0 px-5 py-3 text-sm font-semibold text-[#043462]">
                                                    {t("convocation.application_progress")}
                                                </summary>
                                                <div className="collapse-content overflow-x-auto px-4">
                                                    {candidature ? (
                                                        <div className="min-w-max">
                                                            <CandidatureTimeline statut={candidature.statut} />
                                                        </div>
                                                    ) : (
                                                        <p role="alert" className="text-sm text-gray-500">{t("error.generic")}</p>
                                                    )}
                                                </div>
                                            </details>

                                            {aRepondre && refusOuvert && (
                                                <div className="w-full p-5 flex flex-col gap-2">
                                                    <MotifRefus
                                                        id={`motif-${c.id}`}
                                                        value={motifRefus}
                                                        onChange={(value) => { setMotifRefus(value); setMotifError(false); }}
                                                        error={motifError}
                                                        disabled={enCours}
                                                    />
                                                    <div className="flex justify-end gap-2 mt-2">
                                                        <Button variant="secondary" size="small" type="button" onClick={annulerRefus}>
                                                            {t("actions.cancel_refusal")}
                                                        </Button>
                                                        <Button variant="danger" size="small" type="button" disabled={enCours}
                                                                onClick={demanderConfirmation}>
                                                            {t("convocation.confirm_refusal")}
                                                        </Button>
                                                    </div>
                                                </div>
                                            )}
                                        </div>
                                    </li>
                                );
                            })}
                        </ul>
                    )}
            </div>

            {confirmRefusId !== null && (
                <Modal
                    title={t("convocation.confirm_refusal")}
                    description={t("convocation.refusal_confirmation")}
                    onClickCross={() => setConfirmRefusId(null)}
                    button={
                        <>
                            <Button role="annulation" icon={true} type="button" onClick={() => setConfirmRefusId(null)} className="w-50">{t("actions.cancel")}</Button>
                            <Button role="refus" icon={false} type="button" onClick={confirmerRefus}  className="w-1/2">{t("convocation.confirm_refusal")}</Button>
                        </>
                    }
                />
            )}
        </div>
    );
}

export default EtudiantCandidatures;
