import { useLocation } from "react-router-dom";
import React, {Suspense, useState, useEffect} from "react";
import { marquerNotificationsContratCommeLues } from "../../api/notification.jsx";
import { useNotificationTrigger } from "../../component/Notification/UtilNotification.jsx";
import {useTranslation} from "react-i18next";
import {useContrat} from "../../component/hook/useContrat.jsx";
import {PDFContent} from "../../component/PDF/PDFVisioneuse.jsx";
import NextAction from "../../component/Candidature/NextAction.jsx";
import {Role} from "../../enum/Role.jsx"

const ContratEtudiant = ({ user }) => {
    const triggerNotifications = useNotificationTrigger(state => state.triggerRefresh);
    const { t } = useTranslation();
    const location = useLocation();
    const { candidature } = location.state || {};
    const retour = location.state?.from || "/etudiant";

    const {
        contrat,
        contratPdfBlobUrl,
        hasContrat,
        isContratLoading,
        error,
        rechargerContrat
    } = useContrat(candidature?.id);

    useEffect(() => {
        if (!contrat?.dateSignatureEtudiant || !user?.id || !candidature?.id) return;
        marquerNotificationsContratCommeLues(user.id, candidature.id)
            .then(nombre => { if (nombre > 0) triggerNotifications(); })
            .catch(console.error);
    }, [contrat?.dateSignatureEtudiant, user?.id, candidature?.id, triggerNotifications]);

    const getStudentName = () => {
        if (candidature?.etudiantFirstName || candidature?.etudiantLastName) {
            return `${candidature.etudiantFirstName || ""} ${candidature.etudiantLastName || ""}`.trim();
        }
        return t("candidature.student_unknown");
    };

    return (
        <div>
            {error && (
                <div className="bg-red-500">
                    {error}
                </div>
            )}
            <div className="flex flex-col gap-1 mx-6 mt-3">
                <h2 className="text-2xl font-bold text-[#043462]">
                    {t("etudiant.contrat_detail.title")}
                </h2>
                {candidature && (
                    <div className="flex flex-wrap items-center gap-1.5 text-sm text-gray-600 font-medium">
                        <span>{getStudentName()}</span>
                        <span>—</span>
                        <span>{candidature?.offreTitle || t("candidature.offer_unknown")}</span>
                    </div>
                )}

                <div className="p-6 flex flex-col gap-6 max-w-5xl mx-auto w-full">
                    <NextAction
                        role ={Role.ETUDIANT}
                        candidature={candidature}
                        contrat={contrat}
                        retourPath={retour}
                        onSignatureSuccess={() => {
                            rechargerContrat();
                        }}
                    />
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
        </div>
    )
};export default ContratEtudiant;