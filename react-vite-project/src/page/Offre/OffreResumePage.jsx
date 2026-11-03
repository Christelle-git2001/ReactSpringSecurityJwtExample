import {useLocation, useNavigate} from "react-router-dom";
import ProfileField from "../../component/Design/ProfileField.jsx";
import {useTranslation} from "react-i18next";
import React, {useEffect, useState} from "react";
import {obtenirOffrePDF} from "../../api/employeur.jsx";
import PDFOnPage from "../../component/PDF/PDFOnPage.jsx";

const OffreResumePage = () => {
    const navigate = useNavigate();
    const { t } = useTranslation();
    const [errors, setErrors] = useState({});
    const location = useLocation();
    const resumeOffre = location.state;
    const employeur = resumeOffre.employeurDTO;
    const [isPdfLoading, setIsPdfLoading] = useState(true);
    const [pdfBlobUrl, setPdfBlobUrl] = useState(null);

    useEffect(() => {
        let isMounted = true;

        if (resumeOffre?.idOffre) {
            setIsPdfLoading(true);
            obtenirOffrePDF(resumeOffre?.idOffre)
                .then(async (res) => {
                    if (!isMounted) return;

                    if (res && res.ok) {
                        const blob = await res.blob();
                        if (isMounted) {
                            setPdfBlobUrl(URL.createObjectURL(blob));
                        }
                    } else {
                        console.error("Erreur lors de la récupération de la réponse PDF");
                        setErrors(t("error.generic") + res.status)
                    }
                })
                .catch((err) => {
                    if (err?.message === "UNAUTHORIZED" || err?.status === 401) {
                        navigate("/login");
                    }
                    console.error("Erreur lors du chargement du PDF:", err);
                    setErrors(t("error.generic") + err.status)
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
    }, [resumeOffre?.offreId]);



    return (
        <div className={"p-10 w-full"}>
            {errors.api && (
                <div className="rounded border border-red-400 bg-red-100 px-4 py-3 text-red-700">
                    {errors.api}
                </div>
            )}
            <div className="max-w-4xl mx-auto p-6 rounded-2xl shadow-sm border border-base-200 text-center">
                <h2 className="text-2xl font-bold text-[#043462] border-b pb-4 mb-6">
                    {t("resume_offers.offers_section")}
                </h2>
                <div className="grid grid-cols-12">
                    <div className={"col-span-5"}>
                        <ProfileField label={t("resume_offers.offers_sous_section.title")} result={resumeOffre?.title}/>
                    </div>
                    <div className="col-span-2 flex justify-center">
                        <div className="divider divider-horizontal m-0"></div>
                    </div>
                    <div className={"col-span-5"}>
                        <ProfileField label={t("resume_offers.offers_sous_section.salaire")} result={t("resume_offers.offers_sous_section.salaire_value", { salary: resumeOffre?.salary })}/>
                    </div>
                    <div className="col-span-12">
                        <div className="divider"></div>
                    </div>
                    <div className={"col-span-12"}>
                        <ProfileField label={t("resume_offers.offers_sous_section.description")} result={resumeOffre?.descriptionOffre}/>
                    </div>
                    <div className="col-span-12">
                        <div className="divider"></div>
                    </div>
                    <div className={"col-span-5"}>
                        <ProfileField label={t("resume_offers.offers_sous_section.start_date")} sous_label={t("resume_offers.offers_sous_section.format_date")} result={resumeOffre?.startDate}/>
                    </div>
                    <div className="col-span-2 flex justify-center">
                        <div className="divider divider-horizontal m-0"></div>
                    </div>
                    <div className={"col-span-5"}>
                        <ProfileField label={t("resume_offers.offers_sous_section.end_date")} sous_label={t("resume_offers.offers_sous_section.format_date")} result={resumeOffre?.endDate}/>
                    </div>
                </div>
                <h2 className="text-2xl font-bold text-[#043462] border-b p-4 mb-6">
                    {t("resume_offers.employer_section")}
                </h2>
                <div className={"col-span-12"}>
                    <ProfileField label={t("general.nom")} result={employeur.firstName + " " + employeur.lastName}/>
                </div>
                <div className="col-span-12">
                    <div className="divider"></div>
                </div>
                <div className="grid grid-cols-12">
                    <div className={"col-span-5"}>
                        <ProfileField label={t("general.town")} result={employeur.town}/>
                    </div>
                    <div className="col-span-2 flex justify-center">
                        <div className="divider divider-horizontal m-0"></div>
                    </div>
                    <div className={"col-span-5"}>
                        <ProfileField label={t("general.phone")} result={employeur.phone}/>
                    </div>
                </div>
                <div className="col-span-12">
                    <div className="divider"></div>
                </div>
                <div className={"col-span-12"}>
                    <ProfileField label={t("general.email")} result={employeur.email}/>
                </div>
                <div className="col-span-12">
                    <div className="divider"></div>
                </div>
                <div className="grid grid-cols-12">

                    <div className={"col-span-5"}>
                        <ProfileField label={t("resume_offers.employer_sous_section.business_name")} result={employeur.businessName}/>
                    </div>
                    <div className="col-span-2 flex justify-center">
                        <div className="divider divider-horizontal m-0"></div>
                    </div>
                    <div className={"col-span-5"}>
                        <ProfileField label={t("general.departement")} result={t("departement." + employeur.businessSector)}/>
                    </div>
                </div>
                <h2 className="text-2xl font-bold text-[#043462] border-b p-4 mb-6">
                    {t("resume_offers.offers_sous_section.pdf")}
                </h2>
                <div className="grid grid-cols-12">
                    <div className="col-span-12 flex flex-col gap-1.5 w-full">
                            <PDFOnPage isPdfLoading={isPdfLoading} pdfBlobUrl={pdfBlobUrl}/>
                    </div>
                </div>
            </div>
        </div>
    )
};export default OffreResumePage;