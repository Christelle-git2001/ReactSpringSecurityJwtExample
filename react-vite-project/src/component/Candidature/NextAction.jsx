import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import {Role} from "../../enum/Role.jsx"
import { useNavigate } from "react-router-dom";
import Button from "../Design/Button.jsx";
import { retenirCandidaturePourEntrevue, signerContratEmployeur, obtenirConvocationEmployeur } from "../../api/employeur.jsx";
import {signerContratEtudiant} from "../../api/etudiant.jsx";

function NextAction({ candidature, contrat, retourPath, onSignatureSuccess, role }) {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [statutActuel, setStatutActuel] = useState(candidature?.statut);
    const [motifRefus, setMotifRefus] = useState("");
    const [motifError, setMotifError] = useState(false);
    const [modalOuverte, setModalOuverte] = useState(false);
    const [motDePasse, setMotDePasse] = useState("");
    const [erreurSignature, setErreurSignature] = useState("");

    useEffect(() => {
        if (statutActuel !== "ENTREVUE_REFUSEE" || !candidature?.id) return;
        let actif = true;
        obtenirConvocationEmployeur(candidature.id)
            .then((convocation) => { if (actif) setMotifRefus(convocation.motifRefus || ""); })
            .catch(() => { if (actif) setMotifError(true); });
        return () => { actif = false; };
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
                state: { selectedCandidatureId: candidature.id }
            });
        } catch (error) {
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
                updatedContrat = await signerContratEtudiant(contratId, motDePasse);
            } else if (role === Role.EMPLOYEUR) {
                updatedContrat = await signerContratEmployeur(contratId, motDePasse);
            }

            if (onSignatureSuccess) {
                onSignatureSuccess(updatedContrat);
            }

            setModalOuverte(false);
            setMotDePasse("");

        } catch (error) {
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
                case "ENTREVUE_REFUSEE":
                    return {
                        description: t("convocation.refused_by_student", {defaultValue: t("candidature.status.entrevue_refusee")}),
                        buttonText: null,
                        onClick: null,
                        disabled: false
                    };
                case "STAGE_TROUVE":
                    return {
                        description: t("process.action_wait_contract"),
                        buttonText: t("actions.waiting"),
                        onClick: () => {
                        },
                        disabled: true
                    };
                case "CONTRAT_CREE":
                    return {
                        description: t("process.action_sign_contract"),
                        buttonText: isSubmitting ? t("actions.signing") : t("actions.sign_contract"),
                        onClick: ouvrirModalSignature,
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

    return (
        <div className="w-full bg-cyan-50 border border-cyan-100 rounded-xl p-4 md:p-5 flex flex-col md:flex-row items-start md:items-center justify-between gap-4 shadow-sm">
            <div className="flex flex-col gap-1">
                <span className="text-xs font-bold uppercase tracking-wider text-cyan-800">
                    {isEmployeurSigned
                        ? t("process.contract_signed_title")
                        : t("process.next_action_title")}
                </span>
                <p className="text-sm text-gray-700">
                    {actionData.description}
                </p>
                {motifRefus && <p className="text-sm text-red-800 whitespace-pre-wrap break-words">
                    <strong>{t("convocation.refusal_reason")} : </strong>{motifRefus}
                </p>}
                {motifError && <p role="alert" className="text-sm text-red-700">{t("error.generic")}</p>}
            </div>

            <div className="flex items-center gap-3 whitespace-nowrap">
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

                <Button
                    type="button"
                    onClick={() => navigate(retourPath || "/employeur")}
                    variant="secondary"
                >
                    {t("actions.back")}
                </Button>
            </div>
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
                            <p role="alert" className="mt-2 text-sm text-red-600">
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
                                type="button"
                                onClick={handleSignerContrat}
                                disabled={isSubmitting || !motDePasse.trim()}
                                className="bg-cyan-600 text-white hover:bg-cyan-700"
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