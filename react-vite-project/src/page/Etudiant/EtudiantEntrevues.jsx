import React, { useEffect, useState, useRef } from "react";
import { useTranslation } from "react-i18next";
import { useLocation } from "react-router-dom";
import { obtenirToutesConvocations, accepterConvocation, refuserConvocation } from "../../api/etudiant.jsx";
import { FaCalendarAlt, FaCheckCircle, FaClock, FaHourglassHalf, FaMapMarkerAlt } from "react-icons/fa";
import Button from "../../component/Design/Button.jsx";
import Modal from "../../component/Modal/Modal.jsx";
import SearchBar from "../../component/Design/Bars/SearchBar.jsx";
import FilterBar from "../../component/Design/Bars/FilterBar.jsx";
import EmptyContentMessage from "../../component/Design/EmptyContentMessage.jsx";
import { useFilters } from "../../utils/useFilters.jsx";

const FORMAT_KEYS = {
    EN_LIGNE: "entrevue.mode_online_title",
    PRESENTIEL: "entrevue.mode_onsite_title",
};

function EtudiantEntrevues() {
    const { t, i18n } = useTranslation();
    const location = useLocation();
    const focusId = location.state?.id;

    const [convocations, setConvocations] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(false);
    const [focusedCardId, setFocusedCardId] = useState(null);

    const [submittingId, setSubmittingId] = useState(null);
    const [actionError, setActionError] = useState(null);
    const [refusingId, setRefusingId] = useState(null);
    const [motifRefus, setMotifRefus] = useState("");
    const [motifError, setMotifError] = useState(false);
    const [confirmRefusId, setConfirmRefusId] = useState(null);

    const visibles = convocations.filter((c) => c.statut !== "REFUSEE");
    const filtres = useFilters(visibles, {
        getTown: (c) => c.employeur?.town,
        getSearchableText: (c) => `${c.offreTitle || ""} ${c.employeur?.businessName || ""} ${c.dateHeureEntrevue || ""}`,
    });

    const charger = () =>
        obtenirToutesConvocations()
            .then((data) => setConvocations(data ?? []))
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

                    {actionError && <p role="alert" className="mb-3 text-sm text-red-700">{t(actionError)}</p>}

                    {filtres.itemsFiltres.length === 0 ? (
                        <EmptyContentMessage message={t(visibles.length === 0 ? "convocation.none" : "convocation.no_matching")} />
                    ) : (
                        <ul className="flex flex-col gap-3">
                            {filtres.itemsFiltres.map((c) => {
                                const aRepondre = c.statut === "A_CONFIRMER";
                                const enCours = submittingId === c.id;
                                const refusOuvert = refusingId === c.id;
                                const isFocused = focusedCardId === c.id;
                                return (
                                    <li key={c.id} id={`entrevue-${c.id}`}>
                                        <div className={`w-full rounded-2xl border bg-white shadow-sm overflow-hidden transition-all duration-500 ${
                                            isFocused ? 'border-2 border-black ring-4 ring-gray-200' : 'border-gray-200'
                                        }`}>

                                            <div className="flex flex-wrap sm:flex-nowrap items-center gap-4 sm:gap-6 p-5 bg-[radial-gradient(circle_at_top_left,_#00CCCB33,_transparent_70%)]">
                                                <div className="hidden sm:flex size-16 shrink-0 items-center justify-center rounded-2xl bg-sky-100 text-[#043462]">
                                                    <FaCalendarAlt className="size-8" aria-hidden="true" />
                                                </div>

                                                <div className="min-w-0 flex-1 text-[#043462]">
                                                    <p className="text-lg sm:text-xl font-bold break-words">{c.offreTitle}</p>
                                                    <p className="mt-1 font-semibold">{c.employeur?.businessName}</p>
                                                    <div className="mt-2 flex flex-wrap items-center gap-x-5 gap-y-2 text-sm sm:text-base">
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
                                                    </div>
                                                </div>

                                                {aRepondre && !refusOuvert && (
                                                    <div className="ml-auto flex shrink-0 gap-2">
                                                        <Button variant="danger" type="button" disabled={enCours}
                                                                onClick={() => ouvrirRefus(c.id)}>
                                                            {t("convocation.reject")}
                                                        </Button>
                                                        <Button variant="success" type="button" disabled={enCours}
                                                                onClick={() => repondre(c.id, () => accepterConvocation(c.id))}>
                                                            {t("convocation.accept")}
                                                        </Button>
                                                    </div>
                                                )}

                                                {c.statut === "CONFIRMEE" && (
                                                    <span className="inline-flex shrink-0 items-center gap-2 rounded-full bg-green-100 px-4 py-2 text-sm font-semibold text-green-700">
                                                        <FaCheckCircle aria-hidden="true" />
                                                        {t("convocation.scheduled")}
                                                    </span>
                                                )}
                                            </div>

                                            {aRepondre && refusOuvert && (
                                                <div className="w-full bg-red-50 p-5 border-t border-red-200 flex flex-col gap-2">
                                                    <label htmlFor={`motif-${c.id}`} className="text-sm font-semibold text-red-600">
                                                        {t("convocation.refusal_reason")}
                                                    </label>
                                                    <textarea
                                                        id={`motif-${c.id}`}
                                                        maxLength={500}
                                                        rows={3}
                                                        value={motifRefus}
                                                        onChange={(e) => { setMotifRefus(e.target.value); setMotifError(false); }}
                                                        placeholder={t("offre.rejection_placeholder")}
                                                        aria-required="true"
                                                        aria-invalid={motifError}
                                                        className={`w-full rounded-lg border p-3 text-sm ${motifError ? "border-red-500" : "border-gray-300"}`}
                                                    />
                                                    {motifError && (
                                                        <p role="alert" className="text-xs font-medium text-red-500">
                                                            {t("convocation.refusal_reason_required")}
                                                        </p>
                                                    )}
                                                    <div className="flex justify-end gap-2 mt-2">
                                                        <Button variant="secondary" type="button" onClick={annulerRefus}>
                                                            {t("actions.cancel_refusal")}
                                                        </Button>
                                                        <Button variant="danger" type="button" disabled={enCours}
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
            </details>

            {confirmRefusId !== null && (
                <Modal
                    title={t("convocation.confirm_refusal")}
                    description={t("convocation.refusal_confirmation")}
                    agreeLabel={t("convocation.confirm_refusal")}
                    onClickCancel={() => setConfirmRefusId(null)}
                    onClickCross={() => setConfirmRefusId(null)}
                    onClickAgree={confirmerRefus}
                />
            )}
        </div>
    );
}

export default EtudiantEntrevues;