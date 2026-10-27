import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { useLocation, useNavigate } from "react-router-dom";
import { FiStar } from "react-icons/fi";
import Button from "../Design/Button.jsx";
import { getDepartmentKey } from "../../utils/departementConverter.js";
import { approuverOffre, refuserOffre } from "../../api/gestionnaire.jsx";
import ShowDocument from "../PDF/ShowDocument.jsx";

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
    const retour = location.state?.from;

    const [errors, setErrors] = useState({});
    const [isLoading, setIsLoading] = useState(false);
    const [showRefusalReason, setShowRefusalReason] = useState(false);
    const [rejectionComment, setRejectionComment] = useState("");

    const isTargeted = Boolean(offre?.isTarget);

    const handleSubmit = async (e) => {
        if (e) e.preventDefault();
        setIsLoading(true);
        setErrors({});

        try {
            if (showRefusalReason) {
                if (!rejectionComment.trim()) {
                    setErrors({
                        rejectionComment: "offre.errors.rejection_required"
                    });
                    setIsLoading(false);
                    return;
                }
                await refuserOffre(offre.id, rejectionComment);
                navigate(retour);
            } else {
                await approuverOffre(offre.id);
                navigate(retour);
            }
        } catch (err) {
            console.error("Erreur lors de la mise à jour de l'offre:", err);
            if (err?.message === "UNAUTHORIZED" || err?.status === 401) {
                navigate("/login");
            }
            else {
                setErrors({
                    api: t("offre.errors.api_generic")
                });
            }
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


    const employeurDisplay = offre?.employeur
        ? `${
            offre.employeur.businessName ? ` ${offre.employeur.businessName}` : ""
        }`
        : null;

    const etudiantDisplay = offre?.etudiant
        ? `${offre.etudiant.firstName} ${offre.etudiant.lastName}`
        : null;

    return (
        <div className="w-full max-w-2xl mx-auto p-4 md:p-8">
            <div className="relative w-full rounded-xl bg-white p-6 shadow-md md:p-8">
                <div className="flex justify-between items-center mb-6">
                    <h2 className="text-xl font-bold text-[#043462]">
                        {t("offre.details_title")}
                    </h2>

                    {isTargeted && (
                        <div className="flex items-center gap-1.5 rounded-full bg-amber-100 px-3 py-1 text-xs font-semibold text-amber-800 border border-amber-300 shadow-sm">
                            <FiStar className="size-3.5 fill-amber-500 text-amber-500" />
                            <span>{t("offre.targeted")}</span>
                        </div>
                    )}
                </div>

                {errors.api && (
                    <div className="mb-4 rounded border border-red-400 bg-red-100 px-4 py-3 text-red-700">
                        {errors.api}
                    </div>
                )}

                <div className="grid grid-cols-1 gap-5 md:grid-cols-2">
                    {employeurDisplay && (
                        <div className={isTargeted ? "" : "md:col-span-2"}>
                            <ReadOnlyField
                                label={t("add_offer.targeted_company")}
                                value={employeurDisplay}
                            />
                        </div>
                    )}

                    {isTargeted && etudiantDisplay && (
                        <div>
                            <ReadOnlyField
                                label={t("add_offer.targeted_student")}
                                value={etudiantDisplay}
                            />
                        </div>
                    )}

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

                    <div>
                        <ReadOnlyField
                            label={t("offre.date_fin_affichage")}
                            value={offre?.displayEndDate}
                        />
                    </div>

                    <div className="md:col-span-2">
                        <div className="flex flex-col gap-1.5">
                            <span className="text-sm font-semibold text-[#043462]">
                                {t("offre.document", "Document")}
                            </span>

                            <div className="flex items-center gap-3 mt-1">
                                <ShowDocument
                                    offerId={offre?.id}
                                    fileName={offre?.fileName}
                                />
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
                        </div>
                    )}
                </div>

                <div className="mt-8 flex justify-end gap-3 border-t border-gray-100 pt-5">
                    <Button variant="custom"
                        type="button"
                        disabled={isLoading}
                        onClick={() => navigate(retour)}
                        className="bg-gray-500 text-white hover:bg-gray-600 disabled:opacity-50"
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
                            <Button variant="custom"
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

export default OfferDetails;