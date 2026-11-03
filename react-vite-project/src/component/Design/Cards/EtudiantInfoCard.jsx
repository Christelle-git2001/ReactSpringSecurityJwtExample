import React, {useEffect, useState} from 'react';
import { FiHash } from "react-icons/fi";
import {useTranslation} from "react-i18next";
import EmptyContentMessage from "../EmptyContentMessage.jsx";

const EtudiantInfoCard = ({ etudiant, isNew }) => {
    const { t, i18n } = useTranslation();
    const [candidature,setCandidature] = useState([])

    useEffect(() => {
        const getInfoEtudiant = async () => {
            setCandidature()
        }
        getInfoEtudiant();
    }, []);
    return (
        <div className="card relative w-full bg-base-100 shadow-sm border border-gray-200 p-4 gap-3 bg-[radial-gradient(circle_at_top_left,_#00CCCB15,_transparent_70%)]">
            {isNew && (
                <div className="badge badge-success">{t("professeur.new_student")}</div>
            )}
            <div className="flex justify-between items-start">
                <div>
                    <h2 className="text-base font-bold text-[#043462]">
                        {etudiant?.firstName} {etudiant?.lastName}
                    </h2>
                </div>
                {etudiant?.matricule && (
                    <div className="flex items-center gap-1 text-[11px] text-gray-500 font-mono bg-gray-50 px-1.5 py-0.5 rounded border border-gray-200">
                        <FiHash className="size-3 text-gray-400" />
                        <span>{etudiant.matricule}</span>
                    </div>
                )}
            </div>
            <div className="pb-2 border-b-2 border-[#043462]">
                <h2 className="text-[#043462]">
                    {t("etudiant.card_professeur.information_communication")}
                </h2>
            </div>
            {etudiant?.email && (
                <div className="flex items-center text-sm">
                    <span className="font-bold">{t("general.email")} :</span>{"\u00A0"}{etudiant.email}
                </div>
            )}
            {etudiant?.phoneNumber && (
                <div className="flex items-center text-sm">
                    <span className="font-bold">{t("general.phone")} :</span>{"\u00A0"}{etudiant.phoneNumber}
                </div>
            )}
            <div className="pb-2 border-b-2 border-[#043462]">
                <h2 className="text-[#043462]">
                    {t("etudiant.card_professeur.information_scolaire")}
                </h2>
            </div>
            {etudiant?.department && (
                <div className="flex items-center text-sm">
                    <span className="font-bold">{t("general.departement")} :</span>{"\u00A0"}{etudiant.department}
                </div>
            )}
            <div className="pb-2 border-b-2 border-[#043462]">
                <h2 className="text-[#043462]">
                    {t("etudiant.card_professeur.information_stage")}
                </h2>
            </div>
            {candidature ? (
                <div className="text-sm p-0 pt-2">
                    <span className="font-bold">{t("process.stage_found")} :</span>{"\u00A0"}{}
                </div>
            ):(
                <EmptyContentMessage message={t("etudiant.card_professeur.aucune_candidature")}/>
            )}
        </div>
    );
};

export default EtudiantInfoCard;