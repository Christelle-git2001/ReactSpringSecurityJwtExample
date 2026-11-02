import React, { useState, useEffect, useCallback, Suspense } from "react";
import { useTranslation } from "react-i18next";
import { useLocation, useNavigate, useOutletContext } from "react-router-dom";
import { obtenirCvPDFGestionnaire, getContratByCandidatureId } from "../../api/gestionnaire.jsx";
import { obtenirContratPDF } from "../../api/pdf.jsx";
import { PDFContent } from "../PDF/PDFVisioneuse.jsx";
import OffreDetailsDrawerContent from "../Offer/OffreDetailsDrawerContent.jsx";
import CandidatureTimeline from "./CandidatureTimeline.jsx";
import NextAction from "./NextAction.jsx";

function CandidatureDetails({ candidature: candidatureProp, offer: offerProp, onOpenOffer }) {
    const location = useLocation();
    const { t, i18n } = useTranslation();
    const outletContext = useOutletContext();
    const openDrawer = outletContext?.openDrawer;

    const candidature = candidatureProp || location.state?.candidature;
    const offer = offerProp || location.state?.offer || candidature?.offre;
    const retour = location.state?.from || "/employeur";

    const [pdfBlobUrl, setPdfBlobUrl] = useState(null);
    const [isPdfLoading, setIsPdfLoading] = useState(true);

    const [contrat, setContrat] = useState(null);
    const [contratPdfBlobUrl, setContratPdfBlobUrl] = useState(null);
    const [isContratLoading, setIsContratLoading] = useState(false);
    const [hasContrat, setHasContrat] = useState(false);

    const cvId = candidature?.cvId;
    const candidatureId = candidature?.id;

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

    const chargerContratSiExiste = useCallback(async () => {
        if (!candidatureId) return;
        setIsContratLoading(true);
        try {
            let contratData = null;

            try {
                contratData = await getContratByCandidatureId(candidatureId);
            } catch {
                // Aucun contrat généré pour l'instant
            }

            if (contratData && contratData.id) {
                setContrat(contratData);
                const currentLang = i18n.language || "fr";
                const res = await obtenirContratPDF(contratData.id, currentLang);
                if (res && res.ok) {
                    const blob = await res.blob();
                    setContratPdfBlobUrl(URL.createObjectURL(blob));
                    setHasContrat(true);
                }
            }
        } catch (err) {
            console.error(err);
        } finally {
            setIsContratLoading(false);
        }
    }, [candidatureId, i18n.language]);

    useEffect(() => {
        chargerContratSiExiste();
        return () => {
            if (contratPdfBlobUrl) {
                URL.revokeObjectURL(contratPdfBlobUrl);
            }
        };
    }, [chargerContratSiExiste]);

    const getStudentName = () => {
        if (candidature?.etudiantFirstName || candidature?.etudiantLastName) {
            return `${candidature.etudiantFirstName || ""} ${candidature.etudiantLastName || ""}`.trim();
        }
        return t("candidature.student_unknown");
    };

    return (
        <>
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
            {candidature && <CandidatureTimeline statut={candidature.statut} />}

            <div className="p-6 flex flex-col gap-6 max-w-5xl mx-auto w-full">
                <NextAction
                    candidature={candidature}
                    contrat={contrat}
                    retourPath={retour}
                    onSignatureSuccess={() => {
                        chargerContratSiExiste();
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