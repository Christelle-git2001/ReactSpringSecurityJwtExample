import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import {Role} from "../../enum/Role.jsx"
import { useNavigate } from "react-router-dom";
import Button from "../Design/Button.jsx";
import MotifRefus from "../Design/MotifRefus.jsx";
import { retenirCandidaturePourEntrevue, signerContratEmployeur, obtenirConvocationEmployeur, accepterCandidat, refuserCandidat } from "../../api/employeur.jsx";
import {signerContratEtudiant} from "../../api/etudiant.jsx";

function NextAction({ candidature, statutEntrevue, contrat, retourPath, onSignatureSuccess, role }) {
    const { t } = useTranslation();
    const navigate = useNavigate();

    const [isSubmitting, setIsSubmitting] = useState(false);
    const [statutActuel, setStatutActuel] = useState(candidature?.statut);
    const [motifRefus, setMotifRefus] = useState("");
    const [motifError, setMotifError] = useState(false);
    const [entrevueId, setEntrevueId] = useState(null);
    const [refusOuvert, setRefusOuvert] = useState(false);
    const [decisionError, setDecisionError] = useState(false);

    useEffect(() => {
        setStatutActuel(candidature?.statut);
    }, [candidature?.statut]);

    useEffect(() => {
        if (statutActuel !== "ENTREVUE_PLANIFIEE" || !candidature?.id) return;

        let actif = true;
        obtenirConvocationEmployeur(candidature.id)
            .then((convocation) => {
                if (!actif) return;
                setEntrevueId(convocation?.id ?? null);
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

    const handleAccepterCandidat = async () => {
        if (!entrevueId) return;

        setIsSubmitting(true);
        setDecisionError(false);
        try {
            await accepterCandidat(entrevueId);
            setStatutActuel("ACCEPTEE");
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
            setMotifRefus(motifRefus.trim());
            setStatutActuel("REFUSEE");
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
                        description: t("process.action_accept_refuse"),
                        buttonText: t("actions.manage_status"),
                        onClick: () => {
                        },
                        disabled: false
                    };
                case "ENTREVUE_CONFIRMEE": {
                    const terminee = statutEntrevue === "TERMINEE";
                    return {
                        description: t(terminee ? "process.action_accept_refuse" : "process.action_wait_interview"),
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
    const peutDeciderApresEntrevue = statutActuel === "ENTREVUE_PLANIFIEE" && ["CONFIRMEE", "TERMINEE"].includes(statutEntrevue);

    return (
        <div className="w-full bg-cyan-50 border border-cyan-100 rounded-xl p-4 md:p-5 flex flex-col md:flex-row items-start md:items-center justify-between gap-4 shadow-sm">
            <div className="flex flex-col gap-1">
                <span className="text-xs font-bold uppercase tracking-wider text-cyan-800">
                    {isEmployeurSigned
                        ? t("process.contract_signed_title")
                        : t("process.next_action_title")}
                </span>
                <p className="text-sm text-gray-700">{actionData.description}</p>

                {peutAfficherMotifRefus && (
                    <p className="text-sm text-red-800 whitespace-pre-wrap break-words">
                        <strong>{t("convocation.refusal_reason")} : </strong>
                        {motifAffiche}
                    </p>
                )}

                {decisionError && (
                    <p role="alert" className="text-sm text-red-700">
                        {t("error.generic")}
                    </p>
                )}
            </div>

            <div className="flex flex-row items-center justify-end gap-2 w-full md:w-auto">
                {peutDeciderApresEntrevue && (
                    <div className="flex items-center justify-end gap-2">
                        <Button type="button" variant="success" size="small" onClick={handleAccepterCandidat} disabled={isSubmitting}>
                            {t("actions.accept_candidate")}
                        </Button>
                        <Button
                            type="button"
                            variant="danger"
                            size="small"
                            onClick={() => {
                                setRefusOuvert((ouvert) => !ouvert);
                                setMotifError(false);
                                setDecisionError(false);
                            }}
                            disabled={isSubmitting}
                        >
                            {t("actions.reject_candidate")}
                        </Button>
                    </div>
                )}

                {actionData.buttonText && (
                    <Button
                        icon={false}
                        type="button"
                        onClick={actionData.onClick}
                        disabled={actionData.disabled}
                        className={`text-white transition-colors ${
                            actionData.disabled
                                ? "bg-gray-400 cursor-not-allowed opacity-70"
                                : "bg-cyan-600 hover:bg-cyan-700"
                        }`}
                    >
                        {actionData.buttonText}
                    </Button>
                )}

                {refusOuvert && statutActuel === "ENTREVUE_PLANIFIEE" && (
                    <div className="min-w-64 md:w-80">
                        <MotifRefus
                            value={motifRefus}
                            onChange={(value) => {
                                setMotifRefus(value);
                                setMotifError(false);
                            }}
                            error={motifError}
                            disabled={isSubmitting}
                            maxLength={1000}
                        />
                        <div className="mt-2 flex justify-end gap-2">
                            <Button
                                type="button"
                                variant="secondary"
                                size="small"
                                disabled={isSubmitting}
                                onClick={() => {
                                    setRefusOuvert(false);
                                    setMotifError(false);
                                }}
                            >
                                {t("actions.cancel")}
                            </Button>
                            <Button
                                type="button"
                                variant="danger"
                                size="small"
                                disabled={isSubmitting}
                                onClick={handleRefuserCandidat}
                            >
                                {t("actions.confirm_rejection")}
                            </Button>
                        </div>
                    </div>
                )}

                <Button
                    type="button"
                    onClick={() => navigate(retourPath || "/employeur")}
                    variant="secondary"
                >
                    {t("actions.back")}
                </Button>
            </div>
        </div>
    );
}

export default NextAction;