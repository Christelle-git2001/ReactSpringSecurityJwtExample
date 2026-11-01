import React, { useState, useEffect, useCallback } from "react";
import { useLocation, useParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import StepProcess from "./StepProcess";
import { getStatusConfig } from "../../utils/getStatusConfig.jsx";
import {
    demarrerProcessusDeSignatureDeContrat,
    getContratByCandidatureId,
    getEntrevueByCandidatureId,
    getTousLesCvs
} from "../../api/gestionnaire.jsx";
import { signerContratEmployeur } from "../../api/employeur.jsx";

const formatDateOnly = (dateString) => {
    if (!dateString) return null;
    return String(dateString).split("T")[0];
};

const formatTimeOnly = (dateString) => {
    if (!dateString) return null;
    const parts = String(dateString).split("T");
    if (parts.length < 2) return null;
    return parts[1].substring(0, 5);
};

function ViewProcess() {
    const { t } = useTranslation();
    const { candidatureId } = useParams();
    const location = useLocation();

    const [candidature] = useState(location.state?.candidature || null);
    const [entrevue, setEntrevue] = useState(null);
    const [contrat, setContrat] = useState(null);
    const [cvDetails, setCvDetails] = useState(null);
    const [error, setError] = useState(null);

    const chargerDonneesProcessus = useCallback(async () => {
        try {
            setError(null);
            try {
                const dataEntrevue = await getEntrevueByCandidatureId(candidatureId);
                setEntrevue(dataEntrevue);
            } catch {
                // TODO Optionnel ou introuvable
            }

            try {
                const dataContrat = await getContratByCandidatureId(candidatureId);
                setContrat(dataContrat);
            } catch {
                //TODO Pas encore démarré
            }

            if (candidature?.cvId) {
                try {
                    const tousLesCvs = await getTousLesCvs();
                    const cvTrouve = tousLesCvs.find(cv => String(cv.id) === String(candidature.cvId));
                    if (cvTrouve) {
                        setCvDetails(cvTrouve);
                    }
                } catch {
                    // TODO Erreur ignorée
                }
            }
        } catch {
            setError("error.generic");
        }
    }, [candidatureId, candidature?.cvId]);

    useEffect(() => {
        chargerDonneesProcessus();
    }, [chargerDonneesProcessus]);

    const isProcessStarted = Boolean(contrat);

    const candidatureStatusConfig = getStatusConfig(candidature?.statut, t, "candidature");
    const cvStatusConfig = getStatusConfig(cvDetails?.statut, t, "cv");

    const nomEtudiant = candidature?.etudiantFirstName && candidature?.etudiantLastName
        ? `${candidature.etudiantFirstName} ${candidature.etudiantLastName}`
        : candidature?.etudiantNom || t("candidature.student_unknown");

    const nomEmployeur = candidature?.employeurBusinessName || candidature?.employeurNom || t("candidature.employer_unknown");

    const cvDescriptionText = candidature?.cvId
        ? `${nomEtudiant} - ${cvStatusConfig.label}`
        : `${nomEtudiant} - ${t("process.no_cv")}`;

    const handleStartProcess = async () => {
        try {
            const nouveauContrat = await demarrerProcessusDeSignatureDeContrat(candidatureId);
            setContrat(nouveauContrat);
        } catch {
            setError("process.start_error");
        }
    };

    const handleSignContract = async () => {
        if (!contrat?.id) return;
        try {
            const contratMisAJour = await signerContratEmployeur(contrat.id);
            setContrat(contratMisAJour);
        } catch {
            setError("process.sign_error");
        }
    };

    const heureEntrevue = entrevue?.heure || formatTimeOnly(entrevue?.dateHeureEntrevue);

    return (
        <div className="p-6 flex flex-col gap-6 max-w-3xl mx-auto">
            <h2 className="text-2xl font-bold text-[#043462]">
                {t("process.title")}
            </h2>

            {error && <p role="alert" className="text-sm text-red-600">{t(error)}</p>}

            <div className="flex flex-col gap-3">

                <StepProcess
                    type="CV"
                    title={t("process.cv")}
                    description={cvDescriptionText}
                />

                <StepProcess
                    type="CANDIDATURE"
                    title={t("process.candidature")}
                    description={`${candidature?.offreTitle || t("candidature.offer_unknown")} (${nomEmployeur})`}
                    date={formatDateOnly(candidature?.dateCandidature)}
                />

                <StepProcess
                    type="ENTREVUE"
                    title={t("candidature.status.planifiee")}
                    description={
                        entrevue
                            ? `${entrevue.format === "EN_LIGNE" ? t("entrevue.mode_online_title") : t("entrevue.mode_onsite_title")} - ${heureEntrevue || ""}`
                            : t("process.no_interview")
                    }
                    date={formatDateOnly(entrevue?.dateHeureEntrevue)}
                />

                <StepProcess
                    type="STATUT"
                    title={t("process.stage_found")}
                    description={candidatureStatusConfig.label}
                />

                <StepProcess
                    type="CONTRAT"
                    isStarted={isProcessStarted}
                    title={t("process.contract_signatures")}
                    description={
                        isProcessStarted
                            ? `${t("process.employer_signed")} : ${formatDateOnly(contrat?.dateSignatureEmployeur) || "Non"} | ${t("process.student_signed")} : ${formatDateOnly(contrat?.dateSignatureEtudiant) || "Non"}`
                            : t("process.not_started")
                    }
                />
            </div>

            <div className="flex justify-end gap-3 pt-4 border-t border-gray-100">
                {!isProcessStarted ? (
                    <button
                        type="button"
                        onClick={handleStartProcess}
                        className="px-4 py-2 bg-cyan-600 text-white rounded-md hover:bg-cyan-700 transition-colors font-medium cursor-pointer"
                    >
                        {t("process.start_button")}
                    </button>
                ) : (
                    <button
                        type="button"
                        onClick={handleSignContract}
                        disabled={Boolean(contrat?.dateSignatureGestionnaire)}
                        className={`px-4 py-2 rounded-md font-medium text-white ${
                            contrat?.dateSignatureGestionnaire
                                ? "bg-gray-400 cursor-not-allowed"
                                : "bg-[#043462] hover:bg-[#03284d] cursor-pointer"
                        }`}
                    >
                        {contrat?.dateSignatureGestionnaire
                            ? t("process.already_signed")
                            : t("process.sign_button")}
                    </button>
                )}
            </div>
        </div>
    );
}

export default ViewProcess;