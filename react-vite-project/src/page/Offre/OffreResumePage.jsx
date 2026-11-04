import {useLocation, useNavigate} from "react-router-dom";
import ProfileField from "../../component/Design/Profile/ProfileField.jsx";
import {useTranslation} from "react-i18next";
import React, {useEffect, useState} from "react";
import {obtenirOffrePDF} from "../../api/employeur.jsx";
import PDFOnPage from "../../component/PDF/PDFOnPage.jsx";
import ProfileSection from "../../component/ProfileSection.jsx";
import ProfileRow1x2 from "../../component/Design/Profile/ProfileRow1x2.jsx";
import DividerHorizontal from "../../component/Design/Profile/DividerHorizontal.jsx";
import Button from "../../component/Design/Button.jsx";
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
                <ProfileSection title={t("resume_offers.offers_section")}>
                    <ProfileRow1x2
                        left={
                            <ProfileField label={t("resume_offers.offers_sous_section.title")} result={resumeOffre?.title}/>
                        }
                        right={
                            <ProfileField label={t("resume_offers.offers_sous_section.salaire")} result={t("resume_offers.offers_sous_section.salaire_value", { salary: resumeOffre?.salary })}/>
                        }
                    />
                    <DividerHorizontal/>
                    <div className={"col-span-12"}>
                        <ProfileField label={t("resume_offers.offers_sous_section.description")} result={resumeOffre?.descriptionOffre}/>
                    </div>
                    <DividerHorizontal/>
                    <ProfileRow1x2
                        left={
                            <ProfileField label={t("resume_offers.offers_sous_section.start_date")} sous_label={t("resume_offers.offers_sous_section.format_date")} result={resumeOffre?.startDate}/>
                        }
                        right={
                            <ProfileField label={t("resume_offers.offers_sous_section.end_date")} sous_label={t("resume_offers.offers_sous_section.format_date")} result={resumeOffre?.endDate}/>
                        }
                    />
                </ProfileSection>
                <ProfileSection title={t("resume_offers.employer_section")}>
                    <div className={"col-span-12"}>
                        <ProfileField label={t("general.nom")} result={employeur.firstName + " " + employeur.lastName}/>
                    </div>
                    <DividerHorizontal/>
                    <ProfileRow1x2
                        left={
                            <ProfileField label={t("general.town")} result={employeur.town}/>
                        }
                        right={
                            <ProfileField label={t("general.phone")} result={employeur.phone}/>
                        }
                    />
                    <DividerHorizontal/>
                    <div className={"col-span-12"}>
                        <ProfileField label={t("general.email")} result={employeur.email}/>
                    </div>
                    <DividerHorizontal/>
                    <ProfileRow1x2
                        left={
                            <ProfileField label={t("resume_offers.employer_sous_section.business_name")} result={employeur.businessName}/>
                        }
                        right={
                            <ProfileField label={t("general.departement")} result={t("departement." + employeur.businessSector)}/>
                        }
                    />
                </ProfileSection>
                <ProfileSection title={t("resume_offers.offers_sous_section.pdf")}>
                    <div className="col-span-12 flex flex-col gap-1.5 w-full">
                        <PDFOnPage isPdfLoading={isPdfLoading} pdfBlobUrl={pdfBlobUrl}/>
                    </div>
                </ProfileSection>
                <div className="flex justify-end mt-2">
                    <Button onClick={() => navigate("/professeur")} variant="secondary">Retour</Button>
                </div>
            </div>
        </div>
    )
};export default OffreResumePage;