import React, { useState, useEffect, useCallback, Suspense } from "react";
import { useTranslation } from "react-i18next";
import { useLocation, useNavigate, useOutletContext } from "react-router-dom";
import { obtenirCvPDFGestionnaire, getContratByCandidatureId } from "../../api/gestionnaire.jsx";
import { obtenirContratPDF } from "../../api/pdf.jsx";
import { obtenirConvocationEmployeur } from "../../api/employeur.jsx";
import { PDFContent } from "../PDF/PDFVisioneuse.jsx";
import OffreDetailsDrawerContent from "../Offer/OffreDetailsDrawerContent.jsx";
import CandidatureTimeline from "./CandidatureTimeline.jsx";
import NextAction from "./NextAction.jsx";
import {useContrat} from "../hook/useContrat.jsx";
import {Role} from "../../enum/Role.jsx"

function CandidatureDetails({ candidature: candidatureProp, offer: offerProp, onOpenOffer }) {
    const location = useLocation();
    const { t, i18n } = useTranslation();
    const outletContext = useOutletContext();
    const openDrawer = outletContext?.openDrawer;

    const candidatureSource = candidatureProp || location.state?.candidature;
    const [decision, setDecision] = useState(null);
    useEffect(() => { setDecision(null); }, [candidatureSource?.id]);
    const candidature = decision ? { ...candidatureSource, statut: decision.statut } : candidatureSource;
    const offer = offerProp || location.state?.offer || candidature?.offre;
    const retour = location.state?.from || "/employeur";

    const [pdfBlobUrl, setPdfBlobUrl] = useState(null);
    const [isPdfLoading, setIsPdfLoading] = useState(true);


    const cvId = candidature?.cvId;
    const candidatureId = candidature?.id;
    const [statutEntrevue, setStatutEntrevue] = useState(null);
    const [erreurEntrevue, setErreurEntrevue] = useState(false);

    useEffect(() => {
        if (!candidatureId || candidature?.statut !== "ENTREVUE_PLANIFIEE") return;
        let actif = true;
        setStatutEntrevue(null);
        setErreurEntrevue(false);
        obtenirConvocationEmployeur(candidatureId)
            .then(entrevue => { if (actif) setStatutEntrevue(entrevue.statut); })
            .catch(() => { if (actif) setErreurEntrevue(true); });
        return () => { actif = false; };
    }, [candidatureId, candidature?.statut]);

    const {
        contrat,
        contratPdfBlobUrl,
        hasContrat,
        isContratLoading,
        error,
        rechargerContrat
    } = useContrat(candidatureId,candidature.statut);

    useEffect(() => {
        let isMounted = true;

        if (cvId) {
            setIsPdfLoading(true);
            obtenirCvPDFGestionnaire(cvId)
                .then(async (res) => {
                    if (!isMounted) return;

                    if (res && res.ok) {
                        const blob = await res.blob();
                        if (isMounted) {
                            setPdfBlobUrl(URL.createObjectURL(blob));
                        }
                    } else {
                        console.error("Erreur lors de la récupération du PDF de la candidature");
                    }
                })
                .catch((err) => {
                    console.error("Erreur lors du chargement du CV:", err);
                })
                .finally(() => {
                    if (isMounted) setIsPdfLoading(false);
                });
        }

        return () => {
            isMounted = false;
            if (pdfBlobUrl) {
                URL.revokeObjectURL(pdfBlobUrl);
            }
        };
    }, [cvId]);



    const getStudentName = () => {
        if (candidature?.etudiantFirstName || candidature?.etudiantLastName) {
            return `${candidature.etudiantFirstName || ""} ${candidature.etudiantLastName || ""}`.trim();
        }
        return t("candidature.student_unknown");
    };

    return (
        <>
            {error && (
                <div className="bg-red-500 p-5 font-bold text-center">
                    {error}
                </div>
            )}
            <div className="flex flex-col gap-1 mx-6 mt-3">
                <h2 className="text-2xl font-bold text-[#043462]">
                    {t("candidature.details_title")}
                </h2>
                {candidature && (
                    <div className="flex flex-wrap items-center gap-1.5 text-sm text-gray-600 font-medium">
                        <span>{getStudentName()}</span>
                        <span>—</span>
                        <span>{candidature?.offreTitle || offer?.title || t("candidature.offer_unknown")}</span>
                    </div>
                )}
            </div>
            {candidature && <CandidatureTimeline statut={candidature.statut} statutEntrevue={decision?.statutEntrevue ?? statutEntrevue} regrouperEntrevue />}
            {erreurEntrevue && <p role="alert" className="mx-6 text-sm text-red-700">{t("error.generic")}</p>}

            <div className="p-6 flex flex-col gap-6 max-w-5xl mx-auto w-full">
                <NextAction
                    role={Role.EMPLOYEUR}
                    candidature={candidature}
                    statutEntrevue={statutEntrevue}
                    contrat={contrat}
                    retourPath={retour}
                    onProcessChange={setDecision}
                    onSignatureSuccess={() => {
                        rechargerContrat();
                    }}
                />

                <div className="w-full mt-2">
                    <div className="collapse collapse-arrow bg-transparent">
                        <input type="checkbox" className="peer" defaultChecked />

                        <div className="collapse-title text-xl font-bold text-[#043462] px-0">
                            <span>{t("process.cv_view_title")}</span>
                        </div>

                        <div className="collapse-content transition-all duration-300 ease-in-out px-0">
                            <div className="w-full bg-white p-4 rounded-xl shadow-md border border-gray-100">
                                {isPdfLoading ? (
                                    <div className="flex items-center justify-center p-12 bg-gray-50 rounded-xl">
                                        <span className="loading loading-spinner loading-md text-[#043462]" />
                                    </div>
                                ) : pdfBlobUrl ? (
                                    <Suspense fallback={<div className="p-4 text-center">...</div>}>
                                        <PDFContent cvUrlAAffiche={pdfBlobUrl} enablePageScroll={true} />
                                    </Suspense>
                                ) : (
                                    <p className="text-xs text-gray-500 italic p-6 bg-gray-50 rounded-xl text-center">
                                        {t("offre.document_error")}
                                    </p>
                                )}
                            </div>
                        </div>
                    </div>
                </div>

                {hasContrat && (
                    <div className="w-full">
                        <div className="collapse collapse-arrow bg-transparent">
                            <input type="checkbox" className="peer" defaultChecked />

                            <div className="collapse-title text-xl font-bold text-[#043462] px-0">
                                <span>{t("process.contract_view_title")}</span>
                            </div>

                            <div className="collapse-content transition-all duration-300 ease-in-out px-0">
                                <div className="w-full bg-white p-4 rounded-xl shadow-md border border-gray-100">
                                    {isContratLoading ? (
                                        <div className="flex items-center justify-center p-12 bg-gray-50 rounded-xl">
                                            <span className="loading loading-spinner loading-md text-[#043462]" />
                                        </div>
                                    ) : contratPdfBlobUrl ? (
                                        <Suspense fallback={<div className="p-4 text-center">...</div>}>
                                            <PDFContent cvUrlAAffiche={contratPdfBlobUrl} enablePageScroll={true} />
                                        </Suspense>
                                    ) : (
                                        <p className="text-xs text-gray-500 italic p-6 bg-gray-50 rounded-xl text-center">
                                            {t("offre.document_error")}
                                        </p>
                                    )}
                                </div>
                            </div>
                        </div>
                    </div>
                )}
            </div>
        </>
    );
}

export default CandidatureDetails;