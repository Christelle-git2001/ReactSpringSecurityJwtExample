import { useLocation, useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import React, { useEffect, useState } from "react";
import { obtenirOffrePDF } from "../../api/employeur.jsx";
import Button from "../../component/Design/Button.jsx";
import {
    FaBriefcase, FaCalendarAlt, FaDollarSign, FaAlignLeft,
    FaArrowLeft
} from "react-icons/fa";

function InfoLine({ icon: Icon, label, value }) {
    return (
        <div className="flex items-start gap-3">
            <div className="mt-0.5 shrink-0 w-8 h-8 rounded-lg bg-[#043462]/10
                            flex items-center justify-center text-[#043462]">
                <Icon size={14} />
            </div>
            <div className="min-w-0">
                <p className="text-xs font-semibold uppercase tracking-wide text-gray-400">
                    {label}
                </p>
                <p className="text-sm font-medium text-gray-800 break-words mt-0.5">
                    {value || "—"}
                </p>
            </div>
        </div>
    );
}

function SectionCard({ title, children }) {
    return (
        <div className="rounded-2xl border border-gray-100 shadow-sm px-6 py-6
                        bg-[radial-gradient(circle_at_top_left,#00CCCB33,transparent_70%)]">
            <div className="flex items-center gap-3 mb-5">
                <span className="text-lg font-bold text-[#043462]">{title}</span>
                <div className="flex-1 h-px bg-[#043462]/15" />
            </div>
            {children}
        </div>
    );
}

const OffreResumePage = () => {
    const navigate    = useNavigate();
    const { t }       = useTranslation();
    const location    = useLocation();

    const resumeOffre = location.state?.resumeOffre;
    const employeur   = resumeOffre?.employeurDTO;

    const [isPdfLoading, setIsPdfLoading] = useState(true);
    const [pdfBlobUrl, setPdfBlobUrl]     = useState(null);
    const [errors, setErrors]             = useState({});

    useEffect(() => {
        if (!resumeOffre?.idOffre) return;
        let mounted = true;

        setIsPdfLoading(true);
        obtenirOffrePDF(resumeOffre.idOffre)
            .then(async res => {
                if (!mounted) return;
                if (res?.ok) {
                    const blob = await res.blob();
                    if (mounted) setPdfBlobUrl(URL.createObjectURL(blob));
                } else {
                    setErrors({ api: t("error.generic") + res.status });
                }
            })
            .catch(err => {
                if (err?.message === "UNAUTHORIZED" || err?.status === 401) navigate("/login");
                if (mounted) setErrors({ api: t("error.generic") + err.status });
            })
            .finally(() => { if (mounted) setIsPdfLoading(false); });

        return () => {
            mounted = false;
            if (pdfBlobUrl) URL.revokeObjectURL(pdfBlobUrl);
        };
    }, [resumeOffre?.idOffre]);

    const retour = () =>
        navigate("/professeur", {
            state: {
                etudiant:     location.state?.etudiant,
                candidatures: location.state?.candidatures,
            },
        });

    if (!resumeOffre) {
        return (
            <div className="p-10 text-center text-gray-500">
                {t("candidature.offer_unknown")}
            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-50 px-4 py-8">
            <div className="max-w-3xl mx-auto space-y-5">

                {/* Bouton retour */}
                <button
                    onClick={retour}
                    className="inline-flex items-center gap-2 text-sm font-medium
                               text-[#043462] hover:text-[#0a4a80] transition-colors"
                >
                    <FaArrowLeft size={12} />
                    {t("actions.back")}
                </button>

                {errors.api && (
                    <div className="rounded border border-red-400 bg-red-100 px-4 py-3 text-red-700 text-sm">
                        {errors.api}
                    </div>
                )}

                {/* Renseignements sur l'offre */}
                <SectionCard title={t("resume_offers.offers_section")}>
                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                        <InfoLine
                            icon={FaBriefcase}
                            label={t("resume_offers.offers_sous_section.title")}
                            value={resumeOffre.title}
                        />
                        <InfoLine
                            icon={FaDollarSign}
                            label={t("resume_offers.offers_sous_section.salaire")}
                            value={t("resume_offers.offers_sous_section.salaire_value", { salary: resumeOffre?.salary })}
                        />
                        <InfoLine
                            icon={FaCalendarAlt}
                            label={t("resume_offers.offers_sous_section.start_date")}
                            value={resumeOffre.startDate}
                        />
                        <InfoLine
                            icon={FaCalendarAlt}
                            label={t("resume_offers.offers_sous_section.end_date")}
                            value={resumeOffre.endDate}
                        />
                        <div className="sm:col-span-2">
                            <InfoLine
                                icon={FaAlignLeft}
                                label={t("resume_offers.offers_sous_section.description")}
                                value={resumeOffre.descriptionOffre}
                            />
                        </div>
                    </div>
                </SectionCard>




                {/* Bouton retour bas */}
                <div className="flex justify-end pb-4">
                    <Button icon={true} role="retour" onClick={retour}>
                        {t("actions.back")}
                    </Button>
                </div>

            </div>
        </div>
    );
};

export default OffreResumePage;