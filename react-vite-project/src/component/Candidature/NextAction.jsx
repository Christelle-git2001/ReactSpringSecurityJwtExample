import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import Button from "../Design/Button.jsx";
import { retenirCandidaturePourEntrevue, signerContratEmployeur, obtenirConvocationEmployeur } from "../../api/employeur.jsx";

function NextAction({ candidature, contrat, retourPath, onSignatureSuccess }) {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [statutActuel, setStatutActuel] = useState(candidature?.statut);
    const [motifRefus, setMotifRefus] = useState("");
    const [motifError, setMotifError] = useState(false);

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

    useEffect(() => {
        const nouvelleDate = contrat?.dateSignatureEmployeur || candidature?.dateSignatureEmployeur;
        if (nouvelleDate) {
            setDateSignatureEmployeur(nouvelleDate);
        }
    }, [contrat, candidature]);

    const isEmployeurSigned = Boolean(dateSignatureEmployeur);

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

    const handleSignerContrat = async () => {
        const contratId = contrat?.id || candidature?.contratId;
        if (!contratId) return;
        try {
            setIsSubmitting(true);
            const updatedContrat = await signerContratEmployeur(contratId);

            const dateAujourdhui = updatedContrat?.dateSignatureEmployeur || new Date().toISOString().split("T")[0];
            setDateSignatureEmployeur(dateAujourdhui);

            if (onSignatureSuccess) {
                onSignatureSuccess(updatedContrat);
            }
        } catch (error) {
            console.error(error);
        } finally {
            setIsSubmitting(false);
        }
    };

    const renderActionContent = () => {
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
                    onClick: () => navigate("/employeur/entrevues", { state: { selectedCandidatureId: candidature?.id } }),
                    disabled: false
                };
            case "ENTREVUE_PLANIFIEE":
                return {
                    description: t("process.action_accept_refuse"),
                    buttonText: t("actions.manage_status"),
                    onClick: () => {},
                    disabled: false
                };
            case "ENTREVUE_REFUSEE":
                return {
                    description: t("convocation.refused_by_student", { defaultValue: t("candidature.status.entrevue_refusee") }),
                    buttonText: null,
                    onClick: null,
                    disabled: false
                };
            case "ACCEPTEE":
                return {
                    description: t(candidature.stageTrouve ? "process.action_wait_contract" : "process.action_wait_student_declaration"),
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
                    onClick: () => {},
                    disabled: false
                };
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
        </div>
    );
}

export default NextAction;