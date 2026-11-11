import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { Role } from "../../enum/Role.jsx";
import { useNavigate } from "react-router-dom";
import Button from "../Design/Button.jsx";
import MotifRefus from "../Design/MotifRefus.jsx";
import {
    retenirCandidaturePourEntrevue,
    signerContratEmployeur,
    obtenirConvocationEmployeur,
    cloreEntrevue,
    accepterCandidat,
    refuserCandidat
} from "../../api/employeur.jsx";
import {signerContratEtudiant} from "../../api/etudiant.jsx";
import { useNotificationTrigger } from "../Notification/UtilNotification.jsx";
import { actionsCandidatureDesactivees } from "../../utils/candidatureActions.js";
import ActionsDesactivees from "./ActionsDesactivees.jsx";

function NextAction({
    candidature,
    statutEntrevue,
    contrat,
    retourPath,
    onSignatureSuccess,
    onProcessChange,
    role
}) {
    const { t, i18n } = useTranslation();
    const navigate = useNavigate();
    const triggerNotifications = useNotificationTrigger(state => state.triggerRefresh);

    const [isSubmitting, setIsSubmitting] = useState(false);
    const [statutActuel, setStatutActuel] = useState(candidature?.statut);
    const [motifRefus, setMotifRefus] = useState("");
    const [motifError, setMotifError] = useState(false);
    const [entrevueId, setEntrevueId] = useState(null);
    const [refusOuvert, setRefusOuvert] = useState(false);
    const [decisionError, setDecisionError] = useState(false);
    const [actionsBloquees, setActionsBloquees] = useState(false);
    const [entrevueFermee, setEntrevueFermee] = useState(false);
    const [entreeStatutLocal, setEntreeStatutLocal] = useState(statutEntrevue || null);

    const activeEntrevueStatut = entreeStatutLocal ?? statutEntrevue;

    useEffect(() => {
        setStatutActuel(candidature?.statut);
        setActionsBloquees(actionsCandidatureDesactivees(candidature));
    }, [candidature?.id, candidature?.statut, candidature?.statutEtudiant]);

    const [modalOuverte, setModalOuverte] = useState(false);
    const [motDePasse, setMotDePasse] = useState("");
    const [erreurSignature, setErreurSignature] = useState("");

    const [dateSignatureEmployeur, setDateSignatureEmployeur] = useState(
        contrat?.dateSignatureEmployeur ||
        candidature?.dateSignatureEmployeur ||
        null
    );

    const [dateSignatureEtudiant, setDateSignatureEtudiant] = useState(
        contrat?.dateSignatureEtudiant ||
        candidature?.dateSignatureEtudiant ||
        null
    );

    const formatDateSignature = (dateString) => {
        if (!dateString) return "";

        const date = new Date(dateString);

        return date.toLocaleString(
            i18n.language === "en" ? "en-CA" : "fr-CA",
            {
                day: "2-digit",
                month: "2-digit",
                year: "numeric",
                hour: "2-digit",
                minute: "2-digit",
                hour12: false
            }
        );
    };

    useEffect(() => {
        setEntreeStatutLocal(statutEntrevue || null);
    }, [statutEntrevue]);

    useEffect(() => {
        if (!["ENTREVUE_PLANIFIEE", "ENTREVUE_CONFIRMEE", "ENTREVUE_REFUSEE"].includes(statutActuel) || !candidature?.id) return;

        let actif = true;

        obtenirConvocationEmployeur(candidature.id)
            .then((convocation) => {
                if (!actif) return;
                setEntrevueId(convocation?.id ?? null);
                setEntreeStatutLocal(convocation?.statut ?? statutEntrevue ?? null);
                setMotifRefus(convocation?.motifRefus || "");
            })
            .catch(() => {
                if (actif) {
                    setDecisionError(true);
                }
            });

        return () => {
            actif = false;
        };
    }, [candidature?.id, statutActuel]);

    useEffect(() => {
        switch (role) {
            case Role.ETUDIANT: {
                const nouvelleDateEtudiant =
                    contrat?.dateSignatureEtudiant ||
                    candidature?.dateSignatureEtudiant;

                if (nouvelleDateEtudiant) {
                    setDateSignatureEtudiant(nouvelleDateEtudiant);
                }
                break;
            }

            case Role.EMPLOYEUR: {
                const nouvelleDateEmployeur =
                    contrat?.dateSignatureEmployeur ||
                    candidature?.dateSignatureEmployeur;

                if (nouvelleDateEmployeur) {
                    setDateSignatureEmployeur(nouvelleDateEmployeur);
                }
                break;
            }

            default:
                break;
        }
    }, [contrat, candidature, role]);

    const isEmployeurSigned = Boolean(dateSignatureEmployeur);
    const isEtudiantSigned = Boolean(dateSignatureEtudiant);

    const handleConvoquer = async () => {
        if (!candidature?.id) return;

        try {
            setIsSubmitting(true);

            await retenirCandidaturePourEntrevue(candidature.id);

            setStatutActuel("RETENUE_POUR_ENTREVUE");

            navigate("/employeur/entrevues", {
                state: { selectedCandidatureId: candidature.id },
            });
        } catch (error) {
            if (error?.message === "error.internship_found_locked") setActionsBloquees(true);
            console.error(error);
        } finally {
            setIsSubmitting(false);
        }
    };

    const ouvrirModalSignature = () => {
        setMotDePasse("");
        setErreurSignature("");
        setModalOuverte(true);
    };

    const handleSignerContrat = async () => {
        const contratId = contrat?.id || candidature?.contratId;
        if (!contratId || !motDePasse.trim()) return;

        try {
            setIsSubmitting(true);
            setErreurSignature("");

            let updatedContrat;

            if (role === Role.ETUDIANT) {
                updatedContrat = await signerContratEtudiant(
                    contratId,
                    motDePasse
                );
            } else if (role === Role.EMPLOYEUR) {
                updatedContrat = await signerContratEmployeur(
                    contratId,
                    motDePasse
                );
            }

            if (onSignatureSuccess) {
                onSignatureSuccess(updatedContrat);
            }

            setModalOuverte(false);
            setMotDePasse("");

        } catch (error) {
            if (error?.message === "error.internship_found_locked") setActionsBloquees(true);
            console.error(error);

            setErreurSignature(
                t(error.message, {
                    defaultValue: t("error.generic")
                })
            );
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleCloreEntrevue = async () => {
        if (!entrevueId) return;

        setIsSubmitting(true);
        setDecisionError(false);
        try {
            await cloreEntrevue(entrevueId);
            triggerNotifications();
            setEntrevueFermee(true);
            setEntreeStatutLocal("TERMINEE");
            setStatutActuel("ENTREVUE_PLANIFIEE");
            setRefusOuvert(false);
            onProcessChange?.({ statut: "ENTREVUE_PLANIFIEE", statutEntrevue: "TERMINEE" });
        } catch (error) {
            if (error?.message === "error.internship_found_locked") setActionsBloquees(true);
            console.error("Erreur lors de la clôture de l'entretien:", error);
            setDecisionError(true);
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleAccepterCandidat = async () => {
        if (!entrevueId) return;

        setIsSubmitting(true);
        setDecisionError(false);
        try {
            await accepterCandidat(entrevueId);
            triggerNotifications();
            setEntreeStatutLocal("TERMINEE");
            setStatutActuel("ACCEPTEE");
            onProcessChange?.({ statut: "ACCEPTEE", statutEntrevue: "TERMINEE" });
            setRefusOuvert(false);
        } catch (error) {
            if (error?.message === "error.internship_found_locked") setActionsBloquees(true);
            console.error("Erreur lors de l'acceptation de la candidature:", error);
            setDecisionError(true);
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleRefuserCandidat = async () => {
        if (!entrevueId) return;
        if (!motifRefus.trim()) {
            setMotifError(true);
            return;
        }

        setIsSubmitting(true);
        setDecisionError(false);
        try {
            await refuserCandidat(entrevueId, motifRefus.trim());
            triggerNotifications();
            setMotifRefus(motifRefus.trim());
            setEntreeStatutLocal("TERMINEE");
            setStatutActuel("REFUSEE");
            onProcessChange?.({ statut: "REFUSEE", statutEntrevue: "TERMINEE" });
            setRefusOuvert(false);
        } catch (error) {
            if (error?.message === "error.internship_found_locked") setActionsBloquees(true);
            console.error("Erreur lors du refus de la candidature:", error);
            setDecisionError(true);
        } finally {
            setIsSubmitting(false);
        }
    };

    const renderActionContent = () => {

        if (role === Role.ETUDIANT) {

            if (isEtudiantSigned) {
                return {
                    description: `${t("process.action_contract_signed_msg")} ${formatDateSignature(dateSignatureEtudiant)}.`,
                    buttonText: null,
                    onClick: null,
                    disabled: false
                };
            }

            switch (statutActuel) {
                case "CONTRAT_CREE":
                case "EN_ATTENTE_SIGNATURE_ETUDIANT":
                    return {
                        description: t("process.action_student_sign_contract"),
                        buttonText: isSubmitting
                            ? t("actions.signing")
                            : t("actions.sign_contract"),
                        onClick: ouvrirModalSignature,
                        disabled: isSubmitting
                    };

                default:
                    return {
                        description: t("process.action_default_student"),
                        buttonText: null,
                        onClick: null,
                        disabled: false
                    };
            }
        }

        if (role === Role.EMPLOYEUR) {
            if (isEmployeurSigned) {
                return {
                    description: `${t("process.action_contract_signed_msg")} ${formatDateSignature(dateSignatureEmployeur)}.`,
                    buttonText: null,
                    onClick: null,
                    disabled: false
                };
            }

            switch (statutActuel) {
                case "SOUMISE":
                    return {
                        description: t("process.action_review_cv"),
                        buttonText: t("actions.interview"),
                        onClick: handleConvoquer,
                        disabled: isSubmitting
                    };

                case "RETENUE_POUR_ENTREVUE":
                    return {
                        description: t("process.action_plan_interview"),
                        buttonText: t("actions.plan_interview"),
                        onClick: () => navigate("/employeur/entrevues", {state: {selectedCandidatureId: candidature?.id}}),
                        disabled: false
                    };

                case "ENTREVUE_PLANIFIEE":
                    return {
                        description: t(activeEntrevueStatut === "TERMINEE" ? "process.action_accept_refuse"
                            : activeEntrevueStatut === "CONFIRMEE" ? "process.action_close_interview" : "process.action_wait_interview"),
                        buttonText: null,
                        onClick: null,
                        disabled: false
                    };
                case "ENTREVUE_CONFIRMEE": {
                    const terminee = activeEntrevueStatut === "TERMINEE";
                    return {
                        description: t(terminee ? "process.action_accept_refuse" : "process.action_close_interview"),
                        buttonText: null,
                        onClick: null,
                        disabled: false,
                    };
                }

                case "ENTREVUE_REFUSEE":
                    return {
                        description: t(
                            "convocation.refused_by_student",
                            {
                                defaultValue: t(
                                    "candidature.status.entrevue_refusee"
                                )
                            }
                        ),
                        buttonText: null,
                        onClick: null,
                        disabled: false
                    };
                case "ACCEPTEE":
                    return {
                        description: t("process.action_wait_student_declaration"),
                        buttonText: null,
                        onClick: null,
                        disabled: false,
                    };
                case "REFUSEE":
                    return { description: t("process.action_application_refused"), buttonText: null, disabled: false };

                case "STAGE_TROUVE":
                    return {
                        description: t("process.action_wait_contract"),
                        buttonText: t("actions.waiting"),
                        onClick: () => {},
                        disabled: true
                    };

                case "CONTRAT_CREE":
                    return {
                        description: t("process.action_sign_contract"),
                        buttonText: isSubmitting
                            ? t("actions.signing")
                            : t("actions.sign_contract"),
                        onClick: ouvrirModalSignature,
                        disabled: isSubmitting
                    };

                default:
                    return {
                        description: t("process.action_default"),
                        buttonText: t("actions.view_details"),
                        onClick: () => {},
                        disabled: false
                    };
            }
        }
    };

    if (actionsBloquees || actionsCandidatureDesactivees(candidature)) {
        return <div className="space-y-3 rounded-xl border border-slate-300 bg-slate-50 p-4">
            <ActionsDesactivees />
            <div className="flex justify-end"><Button variant="secondary" size="small"
                onClick={() => navigate(retourPath || "/employeur")}>{t("actions.back")}</Button></div>
        </div>;
    }

    const actionData = renderActionContent();
    const motifAffiche = motifRefus || candidature?.motifRefus || "";
    const peutAfficherMotifRefus = ["REFUSEE", "ENTREVUE_REFUSEE"].includes(statutActuel) && motifAffiche;
    const entrevueCloturee = activeEntrevueStatut === "TERMINEE" || entrevueFermee;
    const estEtapeEntrevue = ["ENTREVUE_PLANIFIEE", "ENTREVUE_CONFIRMEE"].includes(statutActuel);
    const peutDeciderApresEntrevue = role === Role.EMPLOYEUR && estEtapeEntrevue && entrevueCloturee;
    const peutCloreEntrevue = role === Role.EMPLOYEUR && estEtapeEntrevue && activeEntrevueStatut === "CONFIRMEE" && !entrevueCloturee;

    return (
        <div className="w-full overflow-hidden rounded-xl border border-cyan-100 bg-white shadow-sm">
            <div className="bg-cyan-50 p-4 md:p-5 flex flex-col xl:flex-row items-start xl:items-center justify-between gap-4">
                <div className="flex min-w-0 flex-1 flex-col gap-1">
                    <span className="text-xs font-bold uppercase tracking-wider text-cyan-800">
                        {isEmployeurSigned || isEtudiantSigned ? t("process.contract_signed_title") : t("process.next_action_title")}
                    </span>
                    <p className="text-sm text-gray-700">{actionData.description}</p>
                </div>
                <div className="flex flex-wrap sm:flex-nowrap items-center justify-end gap-2 w-full xl:w-auto xl:shrink-0 [&>button]:whitespace-nowrap">
                    {peutCloreEntrevue && (
                        <Button type="button" variant="custom" icon={false} className="bg-cyan-600 text-white enabled:hover:bg-cyan-700 focus-visible:outline-cyan-600" onClick={handleCloreEntrevue} disabled={isSubmitting || !entrevueId}>
                            {t("actions.close_interview")}
                        </Button>
                    )}
                    {peutDeciderApresEntrevue && (
                        <>
                            <Button variant="success" size="small" onClick={handleAccepterCandidat} disabled={isSubmitting || !entrevueId}>
                                {t("actions.accept_candidate")}
                            </Button>
                            <Button variant="danger" size="small" onClick={() => {
                                setRefusOuvert(true);
                                setMotifError(false);
                                setDecisionError(false);
                            }} disabled={isSubmitting || !entrevueId}>
                                {t("actions.reject_candidate")}
                            </Button>
                        </>
                    )}
                    {actionData.buttonText && (
                        <Button variant="custom" icon={false} type="button" onClick={actionData.onClick} disabled={actionData.disabled}
                                className={`text-white ${actionData.disabled && actionData.onClick !== ouvrirModalSignature ? "bg-gray-400 opacity-70" : "bg-cyan-600 enabled:hover:bg-cyan-700 focus-visible:outline-cyan-600"}`}>
                            {actionData.buttonText}
                        </Button>
                    )}
                    <Button type="button" size="small" onClick={() => navigate(retourPath || "/employeur")} variant="secondary">
                        {t("actions.back")}
                    </Button>
                </div>
            </div>
            {peutDeciderApresEntrevue && refusOuvert && (
                <div className="w-full bg-white p-5">
                    <MotifRefus value={motifRefus} onChange={value => { setMotifRefus(value); setMotifError(false); }}
                                error={motifError} disabled={isSubmitting} maxLength={1000} />
                    <div className="flex justify-end gap-2 mt-2">
                        <Button variant="secondary" size="small" disabled={isSubmitting} onClick={() => { setRefusOuvert(false); setMotifError(false); }}>
                            {t("actions.cancel_refusal")}
                        </Button>
                        <Button variant="danger" size="small" disabled={isSubmitting} onClick={handleRefuserCandidat}>
                            {t("actions.confirm_rejection")}
                        </Button>
                    </div>
                </div>
            )}
            {peutAfficherMotifRefus && (
                <div className="border-t border-gray-100 bg-white px-5 py-3 text-sm text-[#043462]">
                    <strong className="text-red-600">{t("convocation.refusal_reason")} : </strong>
                    <span className="whitespace-pre-wrap break-words">{motifAffiche}</span>
                </div>
            )}
            {decisionError && <p role="alert" className="px-5 py-3 text-sm text-red-700">{t("error.generic")}</p>}
            {modalOuverte && (
                <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">

                    <div className="w-full max-w-md rounded-xl bg-white p-6 shadow-xl">

                        <h2 className="text-lg font-bold text-gray-900">
                            {t("contrat.confirm_signature")}
                        </h2>

                        <p className="mt-2 text-sm text-gray-600">
                            {t("contrat.enter_password")}
                        </p>

                        <input
                            type="password"
                            value={motDePasse}
                            onChange={(e) => setMotDePasse(e.target.value)}
                            placeholder={t("register.password")}
                            className="mt-4 w-full rounded-lg border border-gray-300 p-3 focus:border-cyan-600 focus:outline-none"
                        />

                        {erreurSignature && (
                            <p
                                role="alert"
                                className="mt-2 text-sm text-red-600"
                            >
                                {erreurSignature}
                            </p>
                        )}

                        <div className="mt-6 flex justify-end gap-3">

                            <Button
                                type="button"
                                variant="secondary"
                                onClick={() => setModalOuverte(false)}
                                disabled={isSubmitting}
                            >
                                {t("actions.cancel")}
                            </Button>

                            <Button
                                icon={false}
                                type="button"
                                onClick={handleSignerContrat}
                                disabled={isSubmitting || !motDePasse.trim()}
                                className="bg-[#5CE1E6] text-[#043462] hover:bg-[#043462] hover:text-white transition-colors"
                            >
                                {isSubmitting
                                    ? t("actions.signing")
                                    : t("actions.confirm")}
                            </Button>

                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default NextAction;
