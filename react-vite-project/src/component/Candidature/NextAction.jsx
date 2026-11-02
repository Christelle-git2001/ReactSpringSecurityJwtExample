import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";
import Button from "../Design/Button.jsx";
import { retenirCandidaturePourEntrevue, signerContratEmployeur } from "../../api/employeur.jsx";

function NextAction({ candidature, contrat, retourPath }) {
    const { t } = useTranslation();
    const navigate = useNavigate();
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [statutActuel, setStatutActuel] = useState(candidature?.statut);
    const dateSignatureEmployeur = contrat?.dateSignatureEmployeur || candidature?.dateSignatureEmployeur;
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
            await signerContratEmployeur(contratId);
            window.location.reload();
        } catch (error) {
            console.error( error);
        } finally {
            setIsSubmitting(false);
        }
    };

    const renderActionContent = () => {
        if (isEmployeurSigned) {
            return {
                description: `${t("process.action_contract_signed_msg", "Le contrat a été signé le")} ${dateSignatureEmployeur}.`,
                buttonText: null,
                onClick: null,
                disabled: false
            };
        }

        switch (statutActuel) {
            case "SOUMISE":
                return {
                    description: t("process.action_review_cv", "Le candidat a soumis sa candidature. Revoyez son CV et retenez-le pour une entrevue."),
                    buttonText: t("actions.interview", "Retenir pour entrevue"),
                    onClick: handleConvoquer,
                    disabled: isSubmitting
                };
            case "RETENUE_POUR_ENTREVUE":
                return {
                    description: t("process.action_plan_interview", "Le candidat est retenu. Planifiez l'entrevue dès maintenant."),
                    buttonText: t("actions.plan_interview", "Planifier une entrevue"),
                    onClick: () => navigate("/employeur/entrevues", { state: { selectedCandidatureId: candidature?.id } }),
                    disabled: false
                };
            case "ENTREVUE_PLANIFIEE":
                return {
                    description: t("process.action_accept_refuse", "L'entrevue est planifiée. Vous pourrez accepter ou refuser la candidature par la suite."),
                    buttonText: t("actions.manage_status", "Gérer le statut"),
                    onClick: () => {},
                    disabled: false
                };
            case "STAGE_TROUVE":
                return {
                    description: t("process.action_wait_contract", "Le stage a été trouvé. En attente de la création du contrat de stage."),
                    buttonText: t("actions.waiting", "En attente du contrat"),
                    onClick: () => {},
                    disabled: true
                };
            case "CONTRAT_CREE":
                return {
                    description: t("process.action_sign_contract", "Le contrat de stage a été créé. Veuillez procéder à la signature."),
                    buttonText: isSubmitting ? t("actions.signing", "Signature...") : t("actions.sign_contract", "Signer le contrat"),
                    onClick: handleSignerContrat,
                    disabled: isSubmitting
                };
            default:
                return {
                    description: t("process.action_default", "Suivi du processus de candidature en cours."),
                    buttonText: t("actions.view_details", "Voir les détails"),
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
                        ? t("process.contract_signed_title", "Contrat signé")
                        : t("process.next_action_title", "Prochaine action requise")}
                </span>
                <p className="text-sm text-gray-700">
                    {actionData.description}
                </p>
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
                    className="bg-gray-200 text-gray-700 hover:bg-gray-300"
                >
                    {t("actions.back", "Retour")}
                </Button>
            </div>
        </div>
    );
}

export default NextAction;