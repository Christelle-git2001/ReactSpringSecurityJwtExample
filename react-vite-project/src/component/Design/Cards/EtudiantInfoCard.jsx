import React, { useEffect, useState } from 'react';
import { FiHash } from "react-icons/fi";
import { useTranslation } from "react-i18next";
import EmptyContentMessage from "../EmptyContentMessage.jsx";
import { getCandidatureParEtudidant } from "../../../api/professeur.jsx";
import {getDepartmentKey} from "../../../utils/departementConverter.js";
import Button from "../Button.jsx";

const CardSectionHeader = ({ title }) => (
    <div className="pb-2 border-b-2  border-[#043462] mt-2 mb-2">
        <h2 className="text-[#043462] font-semibold text-center">
            {title}
        </h2>
    </div>
);

const CardSection = ({children}) => (
    <div>
        {children}
    </div>
)

const InfoRow = ({ label, value }) => {
    if (!value) return null;
    return (
        <div className="flex items-center text-sm">
            <span className="font-bold">{label} :</span>&nbsp;{value}
        </div>
    );
};

const EtudiantInfoCard = ({ etudiant, isNew, entrevueEtudiantSelectedSetter }) => {
    const { t } = useTranslation();
    const [candidatures, setCandidatures] = useState([]);
    const [error, setError] = useState("");

    const candidaturesAffichees = (() => {
        const stageTrouve = candidatures.find(c => c.statut === "STAGE_TROUVE");
        return stageTrouve ? [stageTrouve] : candidatures;
    })();

    useEffect(() => {
        const getInfoEtudiant = async () => {
            if (!etudiant?.id) return;
            setCandidatures(await getCandidatureParEtudidant(etudiant?.id));
        };
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

            <CardSection>
                <CardSectionHeader title={t("etudiant.card_professeur.information_communication")} />
                <InfoRow label={t("general.email")} value={etudiant?.email} />
                <InfoRow label={t("general.phone")} value={etudiant?.phoneNumber} />
            </CardSection>

            <CardSection>
                <CardSectionHeader title={t("etudiant.card_professeur.information_scolaire")} />
                <InfoRow label={t("general.departement")} value={t("departement." +getDepartmentKey(etudiant?.department))} />
            </CardSection>

            <CardSection>
                <CardSectionHeader title={t("etudiant.card_professeur.information_stage")} />
                {candidatures && candidatures.length > 0 ? (
                    <Button onClick={() => {
                        entrevueEtudiantSelectedSetter({
                        etudiant : etudiant,
                        candidatures : candidatures
                    })
                    }}
                            role="details" className="w-full">{t("etudiant.card_professeur.voir_entrevue")}</Button>
                ) : (
                    <EmptyContentMessage message={t("etudiant.card_professeur.aucune_candidature")} />
                )}
            </CardSection>

            {error && (
                <div className="bg-red-500 p-1 rounded text-white text-xs">
                    <p>{error}</p>
                </div>
            )}
        </div>
    );
};

export default EtudiantInfoCard;