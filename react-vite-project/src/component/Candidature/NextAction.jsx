import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import {Role} from "../../enum/Role.jsx"
import { useNavigate } from "react-router-dom";
import Button from "../Design/Button.jsx";
import MotifRefus from "../Design/MotifRefus.jsx";
import { retenirCandidaturePourEntrevue, signerContratEmployeur, obtenirConvocationEmployeur, cloreEntrevue, accepterCandidat, refuserCandidat } from "../../api/employeur.jsx";
import {signerContratEtudiant} from "../../api/etudiant.jsx";
import { useNotificationTrigger } from "../Notification/UtilNotification.jsx";

function NextAction({ candidature, statutEntrevue, contrat, retourPath, onSignatureSuccess, onProcessChange, role }) {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const triggerNotifications = useNotificationTrigger(state => state.triggerRefresh);

    const [isSubmitting, setIsSubmitting] = useState(false);
    const [statutActuel, setStatutActuel] = useState(candidature?.statut);
    const [motifRefus, setMotifRefus] = useState("");
    const [motifError, setMotifError] = useState(false);
    const [entrevueId, setEntrevueId] = useState(null);
    const [refusOuvert, setRefusOuvert] = useState(false);
    const [decisionError, setDecisionError] = useState(false);
    const [entrevueFermee, setEntrevueFermee] = useState(false);
    const [entreeStatutLocal, setEntreeStatutLocal] = useState(statutEntrevue || null);

    const activeEntrevueStatut = entreeStatutLocal ?? statutEntrevue;

    useEffect(() => {
        setStatutActuel(candidature?.statut);
    }, [candidature?.statut]);

    useEffect(() => {
        setEntreeStatutLocal(statutEntrevue || null);
    }, [statutEntrevue]);

    useEffect(() => {
        if (statutActuel !== "ENTREVUE_PLANIFIEE" || !candidature?.id) return;

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

    const [dateSignatureEmployeur, setDateSignatureEmployeur] = useState(
        contrat?.dateSignatureEmployeur || candidature?.dateSignatureEmployeur || null
    );

    const [dateSignatureEtudiant, setDateSignatureEtudiant] = useState(
        contrat?.dateSignatureEtudiant || candidature?.dateSignatureEtudiant || null
    );

    useEffect(() => {
        switch (role) {
            case Role.ETUDIANT: {
                const nouvelleDateEtudiant = contrat?.dateSignatureEtudiant || candidature?.dateSignatureEtudiant;
                if (nouvelleDateEtudiant) {
                    setDateSignatureEtudiant(nouvelleDateEtudiant);
                }
                break;
            }
            case Role.EMPLOYEUR: {
                const nouvelleDateEmployeur = contrat?.dateSignatureEmployeur || candidature?.dateSignatureEmployeur;
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
            console.error(error);
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleSignerContrat = async () => {
        const contratId = contrat?.id || candidature?.contratId;
        if (!contratId) return;

        try {
            setIsSubmitting(true);
            let updatedContrat;

            if (role === Role.ETUDIANT) {
                updatedContrat = await signerContratEtudiant(contratId);
            } else if (role === Role.EMPLOYEUR) {
                updatedContrat = await signerContratEmployeur(contratId);
            }

            if (onSignatureSuccess) {
                onSignatureSuccess(updatedContrat);
            }
        } catch (error) {
            console.error(error);
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
            console.error("Erreur lors du refus de la candidature:", error);
            setDecisionError(true);
        } finally {
            setIsSubmitting(false);
        }
    };

    const renderActionContent = () => {
        if (role === Role.ETUDIANT){
            if (isEtudiantSigned) {
                return {
                    description: `${t("process.action_contract_signed_msg")} ${dateSignatureEtudiant}.`,
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
                        buttonText: isSubmitting ? t("actions.signing") : t("actions.sign_contract"),
                        onClick: handleSignerContrat,
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
                    description: `${t("process.action_contract_signed_msg")} ${dateSignatureEmployeur}.`,
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
                        description: t("convocation.refused_by_student", {defaultValue: t("candidature.status.entrevue_refusee")}),
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
                        buttonText: isSubmitting ? t("actions.signing") : t("actions.sign_contract"),
                        onClick: handleSignerContrat,
                        disabled: isSubmitting
                    };
                default:
                    return {
                        description: t("process.action_default"),
                        buttonText: t("actions.view_details"),
                        onClick: () => {
                        },
                        disabled: false
                    };
            }
        }
    };

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
                                className={`text-white ${actionData.disabled && actionData.onClick !== handleSignerContrat ? "bg-gray-400 opacity-70" : "bg-cyan-600 enabled:hover:bg-cyan-700 focus-visible:outline-cyan-600"}`}>
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
        </div>
    );
}

export default NextAction;