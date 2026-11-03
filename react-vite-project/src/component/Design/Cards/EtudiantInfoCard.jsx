import React, {useEffect, useState} from 'react';
import { FiHash } from "react-icons/fi";
import {useTranslation} from "react-i18next";
import EmptyContentMessage from "../EmptyContentMessage.jsx";
import {getCandidatureParEtudidant, getResumeOffre} from "../../../api/professeur.jsx";
import ExpandableList from "../Lists/ExpandableList.jsx";
import ListItem from "../Lists/ListItem.jsx";
import {useNavigate} from "react-router-dom";

const EtudiantInfoCard = ({ etudiant, isNew }) => {
    const navigate = useNavigate();
    const { t, i18n } = useTranslation();
    const [candidatures,setCandidatures] = useState([])
    const [error,setError] = useState("");

    const voirResumeOffre = async (offreID) => {
        if (!offreID)
            return
        try {
            const resumeOffre = await getResumeOffre(offreID);
            console.log(resumeOffre)
            navigate("/professeur/resumeOffre", {state : resumeOffre})
        }catch (e){
            setError(t("generic.error"))
        }


    }
    const candidaturesAffichees = (() => {
        const stageTrouve = candidatures.find(c => c.statut === "STAGE_TROUVE");
        return stageTrouve ? [stageTrouve] : candidatures;
    })();

    useEffect(() => {
        const getInfoEtudiant = async () => {
            if (!etudiant?.id) return;
            setCandidatures(await getCandidatureParEtudidant(etudiant?.id))
        }
        getInfoEtudiant();
    }, [etudiant?.id]);
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
            {candidatures ? (
                <ExpandableList items={candidaturesAffichees} renderItem={(candidature) => (
                    <ListItem onClick={() => voirResumeOffre(candidature.offreId)} detailRows={[
                        {label : t("etudiant.card_professeur.employeur"), value : candidature.employeurBusinessName},
                        {label : t("etudiant.card_professeur.statut_candidature"), value : t("candidature.status." + candidature.statut.toLowerCase())}
                    ]
                    } variant="compact"/>
                )}/>
            ):(
                <EmptyContentMessage message={t("etudiant.card_professeur.aucune_candidature")}/>
            )}
            {error && (
                <div className="bg-red-500 p-1">
                    <p>{error}</p>
                </div>
            )}
        </div>
    );
};

export default EtudiantInfoCard;