import React, { useEffect, useRef, useState } from "react";
import { useTranslation } from "react-i18next";
import { useLocation } from "react-router-dom";
import { obtenirToutesConvocations, obtenirCandidaturesEtudiant, accepterConvocation, refuserConvocation, declarerStageTrouve } from "../../api/etudiant.jsx";
import { FaBriefcase, FaCalendarAlt, FaCheckCircle, FaClock, FaHourglassHalf, FaMapMarkerAlt, FaVideo, FaUsers } from "react-icons/fa";
import Button from "../Design/Button.jsx";
import Modal from "../Modal/Modal.jsx";
import EmptyContentMessage from "../Design/EmptyContentMessage.jsx";
import CandidatureTimeline from "./CandidatureTimeline.jsx";
import StatutEtudiant from "./StatutEtudiant.jsx";
import Agenda from "../Entrevue/Agenda.jsx";
import Sort from "../Design/Sort.jsx";
import MotifRefus from "../Design/MotifRefus.jsx";
import { useFilters } from "../../utils/useFilters.jsx";
import { getStatusConfig } from "../../utils/getStatusConfig.jsx";

import { getCandidatureStatus } from "../../utils/candidatureStatus.js";

const FORMAT_KEYS = {
    EN_LIGNE: "entrevue.mode_online_title",
    PRESENTIEL: "entrevue.mode_onsite_title",
};

function MesCandidatures({ candidatures: candidaturesProp, onCandidaturesChange, loading: loadingProp = false, convocations: convocationsProp, onConvocationsChange, filters, offres = [] }) {
    const { t, i18n } = useTranslation();
    const location = useLocation();
    const focusId = location.state?.id;
    const focusCandidatureId = location.state?.candidatureId;

    const [localConvocations, setLocalConvocations] = useState([]);
    const convocations = convocationsProp ?? localConvocations;
    const setConvocations = liste => { setLocalConvocations(liste); onConvocationsChange?.(liste); };
    const [localCandidatures, setLocalCandidatures] = useState([]);
    const candidatures = candidaturesProp ?? localCandidatures;
    const setCandidatures = (value) => {
        const liste = typeof value === "function" ? value(candidatures) : value;
        setLocalCandidatures(liste);
        onCandidaturesChange?.(liste);
    };
    const [candidaturesOuvertes, setCandidaturesOuvertes] = useState(true);
    const [agendaOuvert, setAgendaOuvert] = useState(false);
    const [selectedDate, setSelectedDate] = useState("");
    const [sortOrder, setSortOrder] = useState("desc");
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(false);
    const [focusedCardId, setFocusedCardId] = useState(null);
    const [focusedCandidatureId, setFocusedCandidatureId] = useState(null);
    const [scrollRequest, setScrollRequest] = useState(0);

    const [submittingId, setSubmittingId] = useState(null);
    const [actionError, setActionError] = useState(null);
    const [refusingId, setRefusingId] = useState(null);
    const suivisRefs = useRef(new Map());
    const [motifRefus, setMotifRefus] = useState("");
    const [motifError, setMotifError] = useState(false);
    const [confirmRefusId, setConfirmRefusId] = useState(null);
    const [declarationCandidature, setDeclarationCandidature] = useState(null);
    const [statutDeclare, setStatutDeclare] = useState("");
    const [declarationErreur, setDeclarationErreur] = useState(false);

    const visibles = candidatures.map((candidature) => {
        const convocation = convocations.find((c) => c.candidatureId === candidature.id);
        return {
            ...convocation,
            candidature,
            candidatureId: candidature.id,
            offreTitle: candidature.offreTitle,
            employeur: convocation?.employeur ?? { businessName: candidature.employeurBusinessName },
            statutFiltre: getCandidatureStatus(candidature, convocation),
        };
    });
    const filtres = useFilters(visibles, {
        getStatus: (c) => c.statutFiltre,
        getTown: (c) => c.employeur?.town ?? offres.find(o => o.id === c.candidature.offreId)?.employeur?.town,
        getSearchableText: (c) => `${c.offreTitle || ""} ${c.employeur?.businessName || ""} ${offres.find(o => o.id === c.candidature.offreId)?.description || ""}`,
    }, filters);

    const candidaturesTriees = [...filtres.itemsFiltres].sort((a, b) => {
        const comparaison = (a.candidature.dateCandidature ?? "").localeCompare(b.candidature.dateCandidature ?? "");
        return sortOrder === "asc" ? comparaison : -comparaison;
    });
    const visibleCardIds = candidaturesTriees.map(c => `${c.candidatureId}:${c.id ?? ""}`).join(",");

    const charger = () =>
        Promise.all([obtenirToutesConvocations(), obtenirCandidaturesEtudiant()])
            .then(([data, liste]) => {
                setConvocations(data ?? []);
                setCandidatures(liste ?? []);
            })
            .catch(() => setError(true));

    useEffect(() => {
        let actif = true;
        const chargement = candidaturesProp === undefined
            ? charger()
            : convocationsProp === undefined
                ? obtenirToutesConvocations().then(data => { if (actif) setConvocations(data ?? []); })
                : Promise.resolve();
        chargement.catch(() => { if (actif) setError(true); })
            .finally(() => { if (actif) setLoading(false); });
        return () => { actif = false; };
    }, []);

    useEffect(() => {
        if (!focusId && !focusCandidatureId) return;
        setCandidaturesOuvertes(true);
        setFocusedCardId(focusId ? Number(focusId) : null);
        setFocusedCandidatureId(focusCandidatureId ? Number(focusCandidatureId) : null);
        setScrollRequest(request => request + 1);
        filtres.resetFilters();
        charger();
    }, [location.key]);

    useEffect(() => {
        if (loading || loadingProp || !candidaturesOuvertes) return;
        const carte = focusedCandidatureId !== null
            ? document.querySelector('[data-candidature-id="' + focusedCandidatureId + '"]')
            : document.getElementById('entrevue-' + focusedCardId);
        if (!carte) return;
        // Wait for the opened section and reset filters to reach the layout.
        const frame = requestAnimationFrame(() => {
            carte.scrollIntoView({ behavior: "smooth", block: "start" });
        });
        return () => cancelAnimationFrame(frame);
    }, [focusedCardId, focusedCandidatureId, scrollRequest, loading, loadingProp, candidaturesOuvertes, visibleCardIds]);

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

    const confirmerStage = async () => {
        if (!declarationCandidature || statutDeclare !== "STAGE_TROUVE" || submittingId !== null) return;
        setSubmittingId(declarationCandidature.id);
        setDeclarationErreur(false);
        try {
            const decision = await declarerStageTrouve(declarationCandidature.id);
            setCandidatures(liste => liste.map(c => ({
                ...(c.id === decision.id ? decision : c),
                statutEtudiant: decision.statutEtudiant,
            })));
            setDeclarationCandidature(null);
        } catch {
            setDeclarationErreur(true);
        } finally {
            setSubmittingId(null);
        }
    };

    const ouvrirRefus = (id, candidatureId) => {
        suivisRefs.current.get(candidatureId)?.removeAttribute("open");
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

    if (loading || loadingProp) {
        return <div className="p-8 text-center"><span className="loading loading-spinner" /></div>;
    }

    if (error) {
        return <p role="alert" className="p-8 text-center text-red-700">{t("error.generic")}</p>;
    }

    return (
        <div className="w-full">
            <details open={candidaturesOuvertes} onToggle={event => setCandidaturesOuvertes(event.currentTarget.open)}
                     className="collapse collapse-arrow overflow-visible transition-none bg-transparent">
                <summary className="collapse-title px-0 text-xl font-bold text-[#043462]">
                    {t("candidature.my_applications")} ({filtres.itemsFiltres.length})
                </summary>
                <div className="collapse-content overflow-visible transition-none px-0">
                    <div className="my-4 h-px w-full bg-[#043462]" aria-hidden="true" />
                    <div className="mb-4 flex flex-wrap justify-end gap-3">
                        <Button variant="secondary" size="small" icon={false} aria-expanded={agendaOuvert} aria-controls="agenda-candidatures"
                                onClick={() => setAgendaOuvert(!agendaOuvert)}>
                            <FaCalendarAlt aria-hidden="true" />{t(agendaOuvert ? "candidature.hide_agenda" : "candidature.view_agenda")}
                        </Button>
                        <Sort className="border border-[#043462]/20 bg-white text-[#043462] shadow-sm"
                              sortOrder={sortOrder} onSortChange={setSortOrder}
                              newestLabel={t("candidature.newest_first")} oldestLabel={t("candidature.oldest_first")} />
                    </div>
            {agendaOuvert && (
                <section id="agenda-candidatures" className="mb-6">
                    <Agenda compact entrevuesList={convocations.filter(c => c.statut !== "REFUSEE").map(c => ({
                        id: c.id, candidatureId: c.candidatureId,
                        posteTitre: c.offreTitle, candidatNom: c.employeur?.businessName,
                        date: c.dateHeureEntrevue?.split("T")[0], heure: c.dateHeureEntrevue?.split("T")[1]?.slice(0, 5),
                        duree: c.duree, mode: c.format,
                    }))} selectedDate={selectedDate} onDateSelect={setSelectedDate}
                            onViewCandidature={entrevue => {
                                filtres.resetFilters();
                                setCandidaturesOuvertes(true);
                                setFocusedCandidatureId(null);
                                setFocusedCardId(entrevue.id);
                                setScrollRequest(request => request + 1);
                            }} />
                </section>
            )}

            <div>
                    {actionError && <p role="alert" className="mb-3 text-sm text-red-700">{t(actionError)}</p>}

                    {filtres.itemsFiltres.length === 0 ? (
                        <EmptyContentMessage message={t(visibles.length === 0 ? "candidature.none" : "candidature.no_matching_candidatures")} />
                    ) : (
                        <ul aria-label={t("candidature.my_applications")} className="flex flex-col gap-3">
                            {candidaturesTriees.map((c) => {
                                const aRepondre = c.statutFiltre === "A_CONFIRMER";
                                const enCours = submittingId !== null;
                                const refusOuvert = refusingId === c.id;
                                const isFocused = (focusedCardId !== null && focusedCardId === c.id) || focusedCandidatureId === c.candidatureId;
                                const candidature = c.candidature;
                                const statutConfig = getStatusConfig(c.statutFiltre, t, "candidatureEtudiant");
                                return (
                                    <li key={c.candidatureId} data-candidature-id={c.candidatureId} id={c.id ? `entrevue-${c.id}` : `candidature-${c.candidatureId}`} className="scroll-mt-6">
                                        <div className={`w-full rounded-2xl border border-gray-200 bg-white overflow-hidden transition-shadow duration-300 ${
                                            isFocused ? 'shadow-[0_0_0_3px_#043462,0_6px_22px_#021b36aa]' : 'shadow-sm'
                                        }`}>

                                            <div className="flow-root bg-[radial-gradient(circle_at_top_left,_#00CCCB33,_transparent_70%)]">
                                            <div className="flex flex-wrap sm:flex-nowrap items-center gap-4 sm:gap-6 p-5">
                                                <div className="hidden sm:flex size-12 shrink-0 items-center justify-center rounded-2xl bg-emerald-100 text-emerald-700">
                                                    <FaBriefcase className="size-6" aria-hidden="true" />
                                                </div>

                                                <div className="min-w-0 flex-1 text-[#043462]">
                                                    <p className="text-base font-bold break-words">{c.offreTitle}</p>
                                                    <p className="mt-1 text-sm font-semibold">{c.employeur?.businessName}</p>
                                                    {candidature.dateCandidature && <p className="mt-1 text-xs text-[#043462]/70">
                                                        {t("candidature.date")} : {candidature.dateCandidature.split("T")[0]}
                                                    </p>}
                                                </div>

                                                <div className="ml-auto flex shrink-0 flex-col items-end gap-2">
                                                <span className={`inline-flex shrink-0 items-center gap-2 rounded-full border px-3 py-1 text-sm font-semibold ${aRepondre ? "bg-amber-100 text-amber-800 border-amber-200" : c.statut === "CONFIRMEE" && candidature.statut === "ENTREVUE_PLANIFIEE" ? "bg-green-100 text-green-700 border-green-200" : statutConfig.className}`}>
                                                    {c.statut === "CONFIRMEE" && <FaCheckCircle aria-hidden="true" />}
                                                    {aRepondre ? t("candidature.status.planifiee") : statutConfig.label}
                                                </span>

                                                {candidature.statut === "ACCEPTEE" && candidature.statutEtudiant !== "STAGE_TROUVE" && (
                                                    <Button size="small" type="button" disabled={enCours}
                                                            onClick={() => {
                                                                setDeclarationCandidature(candidature);
                                                                setStatutDeclare(candidature.statutEtudiant ?? "EN_RECHERCHE");
                                                                setDeclarationErreur(false);
                                                            }}>
                                                        {t("candidature.declare_stage_found")}
                                                    </Button>
                                                )}


                                                </div>
                                            </div>

                                            {c.id && (
                                                <section aria-label={t("candidature.interview_details")} className="mx-4 mb-3 overflow-hidden rounded-lg border border-[#043462]/15 bg-white">
                                                    <h2 className="flex items-center gap-2 border-b border-[#043462]/10 px-3 py-2 text-sm font-bold text-[#043462]">
                                                        <FaUsers aria-hidden="true" />{t("candidature.interview_details")}
                                                    </h2>
                                                    <div className="flex flex-wrap items-center justify-between gap-3 px-3 py-3 text-[#043462]">
                                                    <div className="flex flex-wrap items-center gap-x-5 gap-y-2 text-sm">
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
                                                            {c.format === "EN_LIGNE" ? <FaVideo aria-hidden="true" /> : <FaMapMarkerAlt aria-hidden="true" />}
                                                            {FORMAT_KEYS[c.format] ? t(FORMAT_KEYS[c.format]) : (c.format ?? "—")}
                                                        </span>
                                                    </div>
                                                        <div className="ml-auto flex flex-col items-end gap-2">
                                                            {aRepondre && <p className="rounded-full border border-amber-300 bg-amber-100 px-3 py-1 text-xs font-semibold text-amber-900">{t("convocation.interview_to_confirm")}</p>}
                                                {aRepondre && !refusOuvert && (
                                                    <div className="flex gap-2">
                                                        <Button variant="danger" size="small" type="button" disabled={enCours}
                                                                onClick={() => ouvrirRefus(c.id, c.candidatureId)}>
                                                            {t("convocation.reject")}
                                                        </Button>
                                                        <Button variant="success" size="small" type="button" disabled={enCours}
                                                                onClick={() => repondre(c.id, () => accepterConvocation(c.id))}>
                                                            {t("convocation.accept")}
                                                        </Button>
                                                    </div>
                                                )}
                                                            {!aRepondre && <span className={`inline-flex items-center gap-1.5 rounded-full border px-2.5 py-1 text-xs font-semibold ${c.statut === "REFUSEE" ? "border-red-200 bg-red-100 text-red-800" : c.statut === "CONFIRMEE" ? "border-green-200 bg-green-100 text-green-800" : "border-slate-300 bg-slate-100 text-slate-700"}`}>
                                                                {c.statut === "TERMINEE" && <FaCheckCircle aria-hidden="true" />}
                                                                {t(c.statut === "TERMINEE" ? "candidature.interview_completed" : c.statut === "CONFIRMEE" ? "candidature.status.confirmee" : c.statut === "REFUSEE" ? "candidature.status.entrevue_refusee" : "candidature.status.planifiee")}
                                                            </span>}
                                                        </div>
                                                    </div>
                                                </section>
                                            )}

                                            </div>

                                            <details ref={element => {
                                                if (element) suivisRefs.current.set(c.candidatureId, element);
                                                else suivisRefs.current.delete(c.candidatureId);
                                            }} className="collapse collapse-arrow rounded-none border-t border-gray-100">
                                                <summary className="collapse-title min-h-0 px-5 py-3 text-sm font-semibold text-[#043462]">
                                                    {t("convocation.application_progress")}
                                                </summary>
                                                <div className="collapse-content overflow-x-auto px-4">
                                                    {candidature ? (
                                                        <div className="min-w-max py-2">
                                                            <CandidatureTimeline statut={candidature.statut} statutEntrevue={c.statut} contratSigne={candidature.contratSigne} afficherRetenue={false} regrouperEntrevue afficherSignature />

                                                        </div>
                                                    ) : (
                                                        <p role="alert" className="text-sm text-gray-500">{t("error.generic")}</p>
                                                    )}
                                                </div>
                                            </details>

                                            {["REFUSEE", "ENTREVUE_REFUSEE"].includes(candidature.statut) && (candidature.motifRefus || c.motifRefus) && (
                                                <div className="border-t border-gray-100 bg-white px-5 py-3 text-sm text-[#043462]">
                                                    <strong className="text-red-600">{t("convocation.refusal_reason")} : </strong>
                                                    <span className="whitespace-pre-wrap break-words">{candidature.motifRefus || c.motifRefus}</span>
                                                </div>
                                            )}

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

                </div>
            </details>

            {declarationCandidature && (
                <Modal
                    title={t("student_status.declaration_title")}
                    description={t("student_status.declaration_description")}
                    agreeLabel={t("student_status.confirm")}
                    agreeDisabled={statutDeclare !== "STAGE_TROUVE" || submittingId !== null}
                    onClickCancel={() => { if (submittingId === null) setDeclarationCandidature(null); }}
                    onClickCross={() => { if (submittingId === null) setDeclarationCandidature(null); }}
                    onClickAgree={confirmerStage}
                >
                    <div className="mt-4 space-y-3">
                        <p className="text-center text-sm font-medium text-[#043462]">
                            {declarationCandidature.offreTitle} — {declarationCandidature.employeurBusinessName}
                        </p>
                        <StatutEtudiant value={statutDeclare} loading={submittingId !== null} onChange={setStatutDeclare} />
                        {declarationErreur && <p role="alert" className="text-sm text-red-700">{t("error.generic")}</p>}
                    </div>
                </Modal>
            )}

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

export default MesCandidatures;
