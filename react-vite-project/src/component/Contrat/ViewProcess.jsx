import React, { useState, useEffect, useCallback, Suspense } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { useTranslation } from "react-i18next";
import StepProcess from "./StepProcess";
import Button from "../Design/Button.jsx";
import { getStatusConfig } from "../../utils/getStatusConfig.jsx";
import {
    demarrerProcessusDeSignatureDeContrat,
    getContratByCandidatureId,
    getEntrevueByCandidatureId,
    getTousLesCvs,
    signerContratGestionnaire
} from "../../api/gestionnaire.jsx";
import { obtenirContratPDF } from "../../api/pdf.jsx";
import { PDFContent } from "../PDF/PDFVisioneuse.jsx";

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
    const { t, i18n } = useTranslation();
    const { candidatureId } = useParams();
    const location = useLocation();
    const navigate = useNavigate();

    const [candidature] = useState(location.state?.candidature || null);
    const retour = location.state?.from || "/gestionnaire";

    const [entrevue, setEntrevue] = useState(null);
    const [contrat, setContrat] = useState(null);
    const [cvDetails, setCvDetails] = useState(null);
    const [error, setError] = useState(null);

    const [pdfBlobUrl, setPdfBlobUrl] = useState(null);
    const [isPdfLoading, setIsPdfLoading] = useState(false);
    const [isSigning, setIsSigning] = useState(false);

    const chargerPdfContrat = useCallback(async (contratId) => {
        setIsPdfLoading(true);
        try {
            const currentLang = i18n.language || "fr";
            const response = await obtenirContratPDF(contratId, currentLang);
            if (response && response.ok) {
                const blob = await response.blob();
                setPdfBlobUrl((prevUrl) => {
                    if (prevUrl) URL.revokeObjectURL(prevUrl);
                    return URL.createObjectURL(blob);
                });
            } else {
                setError("process.pdf_load_error");
            }
        } catch {
            setError("process.pdf_load_error");
        } finally {
            setIsPdfLoading(false);
        }
    }, [i18n.language]);

    const chargerDonneesProcessus = useCallback(async () => {
        try {
            setError(null);
            try {
                const dataEntrevue = await getEntrevueByCandidatureId(candidatureId);
                setEntrevue(dataEntrevue);
            } catch {

            }

            try {
                const dataContrat = await getContratByCandidatureId(candidatureId);
                setContrat(dataContrat);
                if (dataContrat?.id) {
                    chargerPdfContrat(dataContrat.id);
                }
            } catch {

            }

            if (candidature?.cvId) {
                try {
                    const tousLesCvs = await getTousLesCvs();
                    const cvTrouve = tousLesCvs.find(cv => String(cv.id) === String(candidature.cvId));
                    if (cvTrouve) {
                        setCvDetails(cvTrouve);
                    }
                } catch {

                }
            }
        } catch {
            setError("error.generic");
        }
    }, [candidatureId, candidature?.cvId, chargerPdfContrat]);

    useEffect(() => {
        chargerDonneesProcessus();
    }, [chargerDonneesProcessus]);

    useEffect(() => {
        return () => {
            if (pdfBlobUrl) {
                URL.revokeObjectURL(pdfBlobUrl);
            }
        };
    }, [pdfBlobUrl]);

    const isProcessStarted = Boolean(contrat);

    const candidatureStatusConfig = getStatusConfig(candidature?.statut, t, "candidature");
    const cvStatusConfig = getStatusConfig(cvDetails?.statut, t, "cv");

    const nomEtudiant = candidature?.etudiantFirstName && candidature?.etudiantLastName
        ? `${candidature.etudiantFirstName} ${candidature.etudiantLastName}`
        : candidature?.etudiantNom || t("candidature.student_unknown");

    const nomEmployeur = candidature?.employeurBusinessName || candidature?.employeurNom || t("candidature.employer_unknown");

    const cvDescriptionText = candidature?.cvId
        ? `${nomEtudiant}\n${cvStatusConfig.label}`
        : `${nomEtudiant}\n${t("process.no_cv")}`;

    const handleStartProcess = async () => {
        try {
            const nouveauContrat = await demarrerProcessusDeSignatureDeContrat(candidatureId);
            setContrat(nouveauContrat);
            if (nouveauContrat?.id) {
                chargerPdfContrat(nouveauContrat.id);
            }
        } catch {
            setError("process.start_error");
        }
    };

    const handleSignContract = async () => {
        if (!contrat?.id) return;
        setIsSigning(true);
        setError(null);
        try {
            const contratMisAJour = await signerContratGestionnaire(contrat.id);
            setContrat(contratMisAJour);
            if (contratMisAJour?.id) {
                chargerPdfContrat(contratMisAJour.id);
            }
        } catch {
            setError("process.sign_error");
        } finally {
            setIsSigning(false);
        }
    };

    const heureEntrevue = entrevue?.heure || formatTimeOnly(entrevue?.dateHeureEntrevue);

    const isEmployeurSigned = Boolean(contrat?.dateSignatureEmployeur);
    const isEtudiantSigned = Boolean(contrat?.dateSignatureEtudiant);
    const isGestionnaireSigned = Boolean(contrat?.dateSignatureGestionnaire);

    const isBothPartiesSigned = isEmployeurSigned && isEtudiantSigned;
    const isSignButtonDisabled = isGestionnaireSigned || !isBothPartiesSigned;

    return (
        <div className="p-6 flex flex-col gap-6 max-w-[100rem] mx-auto w-full">
            <h2 className="text-2xl font-bold text-[#043462] m-4">
                {t("process.title")}
            </h2>

            {error && <p role="alert" className="text-sm text-red-600">{t(error)}</p>}

            <div className="grid grid-cols-1 lg:grid-cols-7 gap-8 items-start w-full">

                <div className="lg:col-span-3 flex flex-col gap-6 lg:sticky lg:top-6 w-full">
                    <div className="flex flex-col gap-3 mt-6">
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
                                    ? `${entrevue.format === "EN_LIGNE" ? t("entrevue.mode_online_title") : t("entrevue.mode_onsite_title")}\n${heureEntrevue || ""}`
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
                                isProcessStarted ? (
                                    <span className="block space-y-1 mt-1">
                                        <span className="block">
                                            <strong className="text-[#043462]">{t("process.employer_signed", "Employeur")} :</strong>{" "}
                                            {formatDateOnly(contrat?.dateSignatureEmployeur) || t("process.no_signature", "Non")}
                                        </span>
                                        <span className="block">
                                            <strong className="text-[#043462]">{t("process.student_signed", "Étudiant")} :</strong>{" "}
                                            {formatDateOnly(contrat?.dateSignatureEtudiant) || t("process.no_signature", "Non")}
                                        </span>
                                        <span className="block">
                                            <strong className="text-[#043462]">{t("process.manager_signed", "Gestionnaire")} :</strong>{" "}
                                            {formatDateOnly(contrat?.dateSignatureGestionnaire) || t("process.no_signature", "Non")}
                                        </span>
                                    </span>
                                ) : (
                                    t("process.not_started")
                                )
                            }
                        />
                    </div>

                    {!isProcessStarted && (
                        <div className="flex justify-end gap-3 pt-4 border-t border-gray-100">
                            <Button
                                type="button"
                                onClick={() => navigate(retour)}
                                variant="secondary"
                            >
                                {t("actions.back", "Retour")}
                            </Button>
                            <button
                                type="button"
                                onClick={handleStartProcess}
                                className="px-4 py-2 bg-cyan-600 text-white rounded-md hover:bg-cyan-700 transition-colors font-medium cursor-pointer"
                            >
                                {t("process.start_button")}
                            </button>
                        </div>
                    )}
                </div>

                <div className="lg:col-span-4 w-full h-full bg-gray-50/50 border border-gray-200/60 p-4 sm:p-6 rounded-xl flex flex-col justify-center items-center min-h-[500px]">
                    {isProcessStarted ? (
                        <div className="w-full flex flex-col gap-4 h-full">
                            {isPdfLoading ? (
                                <div className="flex items-center justify-center p-12 bg-white rounded-xl shadow-sm w-full min-h-[400px]">
                                    <span className="loading loading-spinner loading-md text-[#043462]" />
                                </div>
                            ) : pdfBlobUrl ? (
                                <div className="w-full bg-white p-4 rounded-xl shadow-sm border border-gray-100 flex-1">
                                    <Suspense fallback={
                                        <div className="flex items-center justify-center p-12 bg-white rounded-xl shadow-sm w-full">
                                            <span className="loading loading-spinner loading-md text-[#043462]" />
                                        </div>
                                    }>
                                        <PDFContent cvUrlAAffiche={pdfBlobUrl} enablePageScroll={true} />
                                    </Suspense>
                                </div>
                            ) : (
                                <p className="text-xs text-gray-500 italic p-6 bg-white rounded-xl text-center shadow-sm">
                                    {t("offre.document_error")}
                                </p>
                            )}

                            <div className="flex justify-end gap-3 pt-3 border-t border-gray-200 w-full">
                                <Button
                                    type="button"
                                    onClick={() => navigate(retour)}
                                    variant="secondary"
                                >
                                    {t("actions.back", "Retour")}
                                </Button>
                                <button
                                    type="button"
                                    onClick={handleSignContract}
                                    disabled={isSignButtonDisabled || isSigning}
                                    title={
                                        !isBothPartiesSigned && !isGestionnaireSigned
                                            ? t("process.waiting_parties_signatures")
                                            : ""
                                    }
                                    className={`px-4 py-2 rounded-md font-medium text-white transition-colors flex items-center justify-center min-w-[100px] ${
                                        isSignButtonDisabled || isSigning
                                            ? "bg-gray-400 cursor-not-allowed opacity-70"
                                            : "bg-[#043462] hover:bg-[#03284d] cursor-pointer"
                                    }`}
                                >
                                    {isSigning ? (
                                        <span className="loading loading-spinner loading-sm text-white" />
                                    ) : isGestionnaireSigned ? (
                                        t("process.already_signed")
                                    ) : (
                                        t("process.sign_button")
                                    )}
                                </button>
                            </div>
                        </div>
                    ) : (
                        <div className="w-full h-full flex items-center justify-center text-center p-8 text-gray-500 italic">
                            {t("process.no_contrat")}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}

export default ViewProcess;