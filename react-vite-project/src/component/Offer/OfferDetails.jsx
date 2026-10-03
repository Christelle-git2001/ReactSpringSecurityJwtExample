import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { useLocation, useNavigate } from "react-router-dom";
import Button from "../Design/Button.jsx";
import { getDepartmentKey } from "../../utils/departementConverter.js";
import { approuverOffre, refuserOffre } from "../../api/gestionnaire.jsx";

function ReadOnlyField({ label, value, isMultiline = false }) {
    const { t } = useTranslation();

    return (
        <div className="flex flex-col gap-1.5">
            <span className="text-sm font-semibold text-[#043462]">
                {label}
            </span>
            <div
                className={`w-full rounded-lg border border-gray-200 bg-gray-50/70 px-3.5 py-2.5 text-sm text-gray-800 font-medium ${
                    isMultiline ? "whitespace-pre-line min-h-[100px]" : ""
                }`}
            >
                {value || <span className="italic text-gray-400">{t("common.not_provided")}</span>}
            </div>
        </div>
    );
}

function OfferDetails({ offre: offreProp }) {
    const location = useLocation();
    const navigate = useNavigate();
    const { t } = useTranslation();

    const offre = offreProp || location.state?.offer;

    const [errors, setErrors] = useState({});
    const [isLoading, setIsLoading] = useState(false);
    const [showRefusalReason, setShowRefusalReason] = useState(false);
    const [rejectionComment, setRejectionComment] = useState("");

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setErrors({});

        try {
            if (showRefusalReason) {
                if (!rejectionComment.trim()) {
                    setErrors({
                        rejectionComment: t("offre.errors.rejection_required")
                    });
                    setIsLoading(false);
                    return;
                }
                await refuserOffre(offre.id, rejectionComment);
                navigate("/gestionnaire");
            } else {
                await approuverOffre(offre.id);
                navigate("/gestionnaire");
            }
        } catch (err) {
            console.error("Erreur lors de la mise à jour de l'offre:", err);
            setErrors({
                api: t("offre.errors.api_generic")
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
        <div className="w-full max-w-2xl mx-auto p-4 md:p-8">
            <form
                onSubmit={handleSubmit}
                className="w-full rounded-xl bg-white p-6 shadow-md md:p-8"
            >
                <h2 className="mb-6 text-xl font-bold text-[#043462]">
                    {t("offre.details_title")}
                </h2>

                {errors.api && (
                    <div className="mb-4 rounded border border-red-400 bg-red-100 px-4 py-3 text-red-700">
                        {errors.api}
                    </div>
                )}

                <div className="grid grid-cols-1 gap-5 md:grid-cols-2">
                    <div className="md:col-span-2">
                        <ReadOnlyField
                            label={t("offre.offer_title")}
                            value={offre?.title}
                        />
                    </div>
                    <div>
                        <ReadOnlyField
                            label={t("offre.domaine")}
                            value={
                                offre?.domain
                                    ? t(`departement.${getDepartmentKey(offre.domain)}`, typeof offre.domain === 'object' ? offre.domain.name : offre.domain)
                                    : ""
                            }
                        />
                    </div>
                    <div>
                        <ReadOnlyField
                            label={t("offre.salary")}
                            value={offre?.salary ? `${offre.salary} $/h` : ""}
                        />
                    </div>
                    <div className="md:col-span-2">
                        <ReadOnlyField
                            label={t("offre.description")}
                            value={offre?.description}
                            isMultiline
                        />
                    </div>
                    <div>
                        <ReadOnlyField
                            label={t("offre.date_debut")}
                            value={offre?.startDate}
                        />
                    </div>
                    <div>
                        <ReadOnlyField
                            label={t("offre.date_fin")}
                            value={offre?.endDate}
                        />
                    </div>
                    <div className="md:col-span-2">
                        <div className="flex flex-col gap-1.5">
                            <span className="text-sm font-semibold text-[#043462]">
                                {t("offre.document", "Document")}
                            </span>

                            <div className="flex items-center gap-3 mt-1">
                                {/* Show pdf */}
                            </div>
                        </div>
                    </div>
                    {showRefusalReason && (
                        <div className="md:col-span-2">
                            <div className="flex flex-col gap-1.5 border-t border-red-200 pt-4 mt-2">
                                <label htmlFor="rejectionComment" className="text-sm font-semibold text-red-600">
                                    {t("offre.rejection_comment")}
                                </label>
                                <textarea
                                    id="rejectionComment"
                                    name="rejectionComment"
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
                        </div>
                    )}

                </div>
                <div className="mt-8 flex justify-end gap-3 border-t border-gray-100 pt-5">
                    <Button
                        type="button"
                        disabled={isLoading}
                        onClick={() => navigate("/gestionnaire")}
                        className="bg-gray-500 text-white hover:bg-gray-600 disabled:opacity-50"
                    >
                        {t("actions.back")}
                    </Button>

                    {!showRefusalReason ? (
                        <>
                            <Button
                                type="button"
                                disabled={isLoading}
                                onClick={() => setShowRefusalReason(true)}
                                className="bg-red-600 text-white hover:bg-red-700 disabled:opacity-50"
                            >
                                {t("actions.reject", "Refuser")}
                            </Button>

                            <Button
                                type="submit"
                                disabled={isLoading}
                                className="bg-green-600 text-white hover:bg-green-800 disabled:opacity-50"
                            >
                                {isLoading ? t("actions.loading") : t("actions.approve")}
                            </Button>
                        </>
                    ) : (
                        <>
                            <Button
                                type="button"
                                disabled={isLoading}
                                onClick={() => {
                                    setShowRefusalReason(false);
                                    setRejectionComment("");
                                    setErrors({});
                                }}
                                className="bg-gray-400 text-white hover:bg-gray-500 disabled:opacity-50"
                            >
                                {t("actions.cancel_refusal")}
                            </Button>

                            <Button
                                type="submit"
                                disabled={isLoading}
                                className="bg-red-600 text-white hover:bg-red-700 disabled:opacity-50"
                            >
                                {isLoading ? t("actions.loading") : t("actions.confirm_rejection")}
                            </Button>
                        </>
                    )}
                </div>
            </form>
        </div>
    );
}

export default OfferDetails;