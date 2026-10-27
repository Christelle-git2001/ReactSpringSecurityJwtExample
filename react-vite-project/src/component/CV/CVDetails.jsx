import React, { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useLocation, useNavigate } from "react-router-dom";
import Button from "../Design/Button.jsx";
import { approuverCv, refuserCv, obtenirCvPDFGestionnaire } from "../../api/gestionnaire.jsx";
import { PDFContent } from "../PDF/PDFVisioneuse.jsx";
import { FaDownload } from "react-icons/fa";
import { declencherTelechargement } from "../../utils/filesUtils.jsx";

function CvDetails({ cv: cvProp }) {
    const location = useLocation();
    const navigate = useNavigate();
    const { t } = useTranslation();

    const cv = cvProp || location.state?.cv;
    const retour = location.state?.from || "/gestionnaire/cvs";

    const [errors, setErrors] = useState({});
    const [isLoading, setIsLoading] = useState(false);
    const [showRefusalReason, setShowRefusalReason] = useState(false);
    const [rejectionComment, setRejectionComment] = useState("");
    const [pdfBlobUrl, setPdfBlobUrl] = useState(null);
    const [isPdfLoading, setIsPdfLoading] = useState(true);

    useEffect(() => {
        let isMounted = true;

        if (cv?.id) {
            setIsPdfLoading(true);
            obtenirCvPDFGestionnaire(cv.id)
                .then(async (res) => {
                    if (!isMounted) return;

                    if (res && res.ok) {
                        const blob = await res.blob();
                        if (isMounted) {
                            setPdfBlobUrl(URL.createObjectURL(blob));
                        }
                    } else {
                        console.error("Erreur lors de la récupération de la réponse PDF");
                    }
                })
                .catch((err) => {
                    if (err?.message === "UNAUTHORIZED" || err?.status === 401) {
                        navigate("/login");
                    }
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
    }, [cv?.id]);

    const handleSubmit = async (e) => {
        if (e) e.preventDefault();
        setIsLoading(true);
        setErrors({});

        try {
            if (showRefusalReason) {
                if (!rejectionComment.trim()) {
                    setErrors({
                        rejectionComment: "cv.errors.rejection_required"
                    });
                    setIsLoading(false);
                    return;
                }
                await refuserCv(cv.id, rejectionComment);
                navigate(retour);
            } else {
                await approuverCv(cv.id);
                navigate(retour);
            }
        } catch (err) {
            console.error("Erreur lors de la mise à jour du CV:", err);
            if (err?.message === "UNAUTHORIZED" || err?.status === 401) {
                navigate("/login");
            }
            setErrors({
                api: t("cv.errors.api_generic")
            });
        } finally {
            setIsLoading(false);
        }
    };

    const inputStyle = (hasError) =>
        `w-full rounded-lg border px-3.5 py-2.5 text-sm transition-colors outline-none focus:ring-2 ${
            hasError
                ? "border-red-500 focus:ring-red-200"
                : "border-gray-300 focus:border-[#0ee1cc] focus:ring-[#0ee1cc]/20"
        }`;

    return (
        <div className="w-full max-w-5xl mx-auto p-4 md:p-8">
            <div className="w-full rounded-xl bg-white p-6 shadow-md md:p-8 flex flex-col gap-6">
                <h2 className="text-xl font-bold text-[#043462]">
                    {t("cv.details_title")}
                </h2>

                {errors.api && (
                    <div className="rounded border border-red-400 bg-red-100 px-4 py-3 text-red-700">
                        {errors.api}
                    </div>
                )}

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
                                {t("offre.document_error", "Impossible de charger le document.")}
                            </p>
                        )}
                    </div>
                </div>

                {showRefusalReason && (
                    <div className="flex flex-col gap-1.5 border-t border-red-200 pt-4 mt-2">
                        <label htmlFor="rejectionComment" className="text-sm font-semibold text-red-600">
                            {t("cv.rejection_comment")}
                        </label>
                        <textarea
                            id="rejectionComment"
                            name="rejectionComment"
                            maxLength={500}
                            rows="4"
                            value={rejectionComment}
                            onChange={(e) => setRejectionComment(e.target.value)}
                            placeholder={t("offre.rejection_placeholder")}
                            className={inputStyle(errors.rejectionComment)}
                        />
                        {errors.rejectionComment && (
                            <span className="text-xs font-medium text-red-500">
                                {t(errors.rejectionComment)}
                            </span>
                        )}
                    </div>
                )}

                <div className="mt-4 flex flex-wrap justify-end gap-3 border-t border-gray-100 pt-5">
                    <button
                        type="button"
                        disabled={isPdfLoading || !pdfBlobUrl}
                        onClick={() => declencherTelechargement(pdfBlobUrl, cv?.fileName || "cv.pdf")}
                        className="inline-flex items-center justify-center gap-2 rounded-md border border-transparent px-4 py-2 font-medium transition-colors mr-auto flex cursor-pointer items-center gap-2 bg-[#043462] text-white enabled:hover:bg-[#00CCCB] enabled:hover:text-[#043462] disabled:cursor-not-allowed disabled:opacity-50"
                    >
                        <FaDownload aria-hidden="true" />
                        {t("pdfVisio.download")}
                    </button>

                    <Button variant="secondary"
                        type="button"
                        disabled={isLoading}
                        onClick={() => navigate(retour)}
                        className="disabled:opacity-50"
                    >
                        {t("actions.back")}
                    </Button>

                    {!showRefusalReason ? (
                        <>
                            <Button variant="danger"
                                type="button"
                                disabled={isLoading}
                                onClick={() => setShowRefusalReason(true)}
                            >
                                {t("actions.reject")}
                            </Button>

                            <Button variant="success"
                                type="button"
                                disabled={isLoading}
                                onClick={handleSubmit}
                            >
                                {isLoading ? t("actions.loading") : t("actions.approve")}
                            </Button>
                        </>
                    ) : (
                        <>
                            <Button variant="secondary"
                                type="button"
                                disabled={isLoading}
                                onClick={() => {
                                    setShowRefusalReason(false);
                                    setRejectionComment("");
                                    setErrors({});
                                }}
                                className="disabled:opacity-50"
                            >
                                {t("actions.cancel_refusal")}
                            </Button>

                            <Button variant="danger"
                                type="button"
                                disabled={isLoading}
                                onClick={handleSubmit}
                            >
                                {isLoading ? t("actions.loading") : t("actions.confirm_rejection")}
                            </Button>
                        </>
                    )}
                </div>
            </div>
        </div>
    );
}

export default CvDetails;