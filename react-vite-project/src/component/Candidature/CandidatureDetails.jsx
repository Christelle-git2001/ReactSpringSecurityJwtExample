import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useLocation, useNavigate, useOutletContext } from "react-router-dom";
import Button from "../Design/Button.jsx";
import { obtenirCvPDFGestionnaire } from "../../api/gestionnaire.jsx";
import { retenirCandidaturePourEntrevue } from "../../api/employeur.jsx";
import { PDFContent } from "../PDF/PDFVisioneuse.jsx";
import OffreDetailsDrawerContent from "../Offer/OffreDetailsDrawerContent.jsx";
import CandidatureTimeline from "./CandidatureTimeline.jsx";

function CandidatureDetails({ candidature: candidatureProp, offer: offerProp, onOpenOffer }) {
    const location = useLocation();
    const navigate = useNavigate();
    const { t } = useTranslation();
    const outletContext = useOutletContext();
    const openDrawer = outletContext?.openDrawer;

    const candidature = candidatureProp || location.state?.candidature;
    const offer = offerProp || location.state?.offer || candidature?.offre;
    const retour = location.state?.from || "/employeur";

    const [pdfBlobUrl, setPdfBlobUrl] = useState(null);
    const [isPdfLoading, setIsPdfLoading] = useState(true);
    const [isSubmitting, setIsSubmitting] = useState(false);

    const [isRetenue, setIsRetenue] = useState(
        candidature?.statut === "RETENUE_POUR_ENTREVUE"
    );

    const cvId = candidature?.cvId;

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
                    console.error("Erreur lors du chargement du PDF:", err);
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

    const handleOfferClick = () => {
        if (onOpenOffer) {
            onOpenOffer(candidature);
        } else if (openDrawer && offer) {
            openDrawer(<OffreDetailsDrawerContent offer={offer} />);
        }
    };

    const handleConvoquer = async () => {
        if (!candidature?.id) return;

        try {
            setIsSubmitting(true);
            await retenirCandidaturePourEntrevue(candidature.id);
            setIsRetenue(true);
            navigate("/employeur/entrevues", {
                state: { selectedCandidatureId: candidature.id }
            });
        } catch (error) {
            console.error("Erreur lors de la mise en attente/retenue de la candidature :", error);
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <>
        {candidature && <CandidatureTimeline statut={candidature.statut} />}
        <div className="w-full max-w-5xl mx-auto p-4 md:p-8">
            <div className="w-full rounded-xl bg-white p-6 shadow-md md:p-8 flex flex-col gap-6">
                <div className="flex flex-col gap-1">
                    <h2 className="text-xl font-bold text-[#043462]">
                        {t("candidature.details_title")}
                    </h2>
                    {candidature && (
                        <div className="flex flex-wrap items-center gap-1.5 text-sm text-gray-600 font-medium">
                            <span>{getStudentName()}</span>
                            <span>—</span>
                            <button
                                type="button"
                                onClick={handleOfferClick}
                                className="font-semibold text-[#043462] hover:text-[#00CCCB] hover:underline bg-transparent border-none p-0 cursor-pointer text-left"
                            >
                                {candidature?.offreTitle || offer?.title || t("candidature.offer_unknown")}
                            </button>
                        </div>
                    )}
                </div>

                <div className="flex flex-col gap-1.5">
                    <div className="w-full mt-1">
                        {isPdfLoading ? (
                            <div className="flex items-center justify-center p-12 border rounded-xl bg-gray-50">
                                <span className="loading loading-spinner loading-md text-[#043462]" />
                            </div>
                        ) : pdfBlobUrl ? (
                            <PDFContent cvUrlAAffiche={pdfBlobUrl} enablePageScroll={true} />
                        ) : (
                            <p className="text-xs text-gray-500 italic p-6 border rounded-xl bg-gray-50 text-center">
                                {t("offre.document_error")}
                            </p>
                        )}
                    </div>
                </div>

                <div className="mt-4 flex justify-end gap-3 border-t border-gray-100 pt-5">
                    <Button variant="secondary"
                        type="button"
                        onClick={handleConvoquer}
                        disabled={isSubmitting || isRetenue}
                        className={`text-white transition-colors ${
                            isRetenue
                                ? "bg-blue-200 cursor-not-allowed opacity-60"
                                : "bg-blue-500 hover:bg-[#03284d]"
                        }`}
                    >
                        {t("actions.interview")}
                    </Button>
                    <Button
                        type="button"
                        onClick={() => navigate(retour)}
                        className="bg-gray-200 text-gray-700 hover:bg-gray-300"
                    >
                        {t("actions.back")}
                    </Button>
                </div>
            </div>
        </div>
        </>
    );
}

export default CandidatureDetails;