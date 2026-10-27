import React, { useState } from "react";
import { useTranslation } from "react-i18next";
import { formaterDate } from "../../../utils/cvFormatters.jsx";

import CandidatureOfferDetails from "../../Offer/CandidatureOfferDetails.jsx";

function CandidatureCard({ candidature, offer, onView, onViewOffer }) {
    const { t } = useTranslation();
    const [showOffer, setShowOffer] = useState(false);

    const getStudentName = () => {
        if (candidature?.etudiantFirstName || candidature?.etudiantLastName) {
            return `${candidature.etudiantFirstName || ""} ${candidature.etudiantLastName || ""}`.trim();
        }
        return t("candidature.student_unknown");
    };

    return (
        <div className={`rounded-2xl border border-gray-200 bg-white p-3 transition-colors ${showOffer && offer ? "bg-[radial-gradient(circle_at_center,_#00CCCB33,_transparent_70%)]" : ""}`}>
            <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                <div className="flex items-center gap-4">
                    <div className="flex h-12 w-12 shrink-0 items-center justify-center rounded-xl bg-[#043462]/10 text-[#043462] font-bold text-lg">
                        {getStudentName().charAt(0)}
                    </div>
                    <div className="flex flex-col">
                        <h3 className="text-base font-bold text-[#043462]">
                            {getStudentName()}
                        </h3>
                    </div>
                </div>
                <div className="flex flex-col md:border-x md:border-gray-100 md:px-6 my-2 md:my-0">
                    {offer || onViewOffer ? (
                        <button
                            type="button"
                            onClick={() => {
                                setShowOffer(!showOffer);
                                onViewOffer?.(candidature);
                            }}
                            aria-label={t("dashboard.view_offer")}
                            data-tip={t("dashboard.view_offer")}
                            aria-expanded={showOffer && Boolean(offer)}
                            aria-controls={offer ? `candidature-offre-${candidature.id}` : undefined}
                            className="tooltip tooltip-bottom text-sm font-semibold text-[#043462] hover:text-[#00CCCB] hover:underline text-left cursor-pointer bg-transparent border-none p-0"
                        >
                            {candidature?.offreTitle || t("candidature.offer_unknown")}
                        </button>
                    ) : (
                        <p className="text-sm font-semibold text-gray-800">
                            {candidature?.offreTitle || t("candidature.offer_unknown")}
                        </p>
                    )}
                </div>
                <div className="flex items-center justify-between md:justify-end gap-6 pt-3 md:pt-0 border-t md:border-t-0 border-gray-100 shrink-0">
                    <div className="text-xs text-gray-500">
                        <span className="block text-[10px] uppercase font-semibold text-gray-400">
                            {t("candidature.date")}
                        </span>
                        <span className="text-gray-700 font-medium">
                            {formaterDate(candidature?.dateCandidature)}
                        </span>
                    </div>

                    {onView && (
                        <button
                            type="button"
                            onClick={() => onView(candidature)}
                            className="btn btn-sm shrink-0 border-none rounded-lg px-4 bg-[#043462] text-white hover:bg-[#03284d]"
                        >
                            {t("candidature.read")}
                        </button>
                    )}
                </div>
            </div>
            {showOffer && offer && (
                <CandidatureOfferDetails
                    offer={offer}
                    id={`candidature-offre-${candidature.id}`}
                    onClose={() => setShowOffer(false)}
                />
            )}
        </div>
    );
}

export default CandidatureCard;