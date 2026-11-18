import { useState } from "react";
import ListItem from "../../component/Design/Lists/ListItem.jsx";
import ExpandableList from "../../component/Design/Lists/ExpandableList.jsx";
import {getStatusConfig} from "../../utils/getStatusConfig.jsx";
import {getResumeOffre, soumettreEvaluationMilieuStage} from "../../api/professeur.jsx";
import {useTranslation} from "react-i18next";
import {useNavigate} from "react-router-dom";
import {formaterDate} from "../../utils/cvFormatters.jsx";
import EvaluationMilieuStage from "../../component/Evaluation/EvaluationMilieuStage.jsx";
import { HiEye } from "react-icons/hi";
import { MdRateReview } from "react-icons/md";


// Statuts pour lesquels le professeur peut évaluer le milieu de stage
const STATUTS_EVALUABLES = ["CONTRAT_CREE"];

const ShowEtudiantEntrevue = ({etudiant , candidatures, filtreText , filtreButton}) => {
    const navigate = useNavigate();
    const { t } = useTranslation();

    // Évaluation en cours : { candidature, resumeOffre }
    const [evaluationOuverte, setEvaluationOuverte] = useState(null);
    // Évaluations soumises, par id de candidature
    const [evaluations, setEvaluations] = useState({});
    const [chargementId, setChargementId] = useState(null);
    const [erreur, setErreur] = useState(false);

    const voirResumeOffre = async (offreID) => {
        if (!offreID) return;
        try {
            const resumeOffre = await getResumeOffre(offreID);
            navigate("/professeur/resumeOffre", { state: {resumeOffre,etudiant,candidatures} });
        } catch (e) {
            console.error(t("error.generic"))
        }
    };

    // Charge le résumé de l'offre (employeur, dates, salaire) pour pré-remplir le formulaire
    const ouvrirEvaluation = async (candidature) => {
        setErreur(false);
        setChargementId(candidature.id);
        try {
            const resumeOffre = await getResumeOffre(candidature.offreId);
            setEvaluationOuverte({ candidature, resumeOffre });
        } catch (e) {
            setErreur(true);
        } finally {
            setChargementId(null);
        }
    };

    const filteredCandidature = filtreButton.itemsFiltres.filter((c) => {
        if (!filtreButton.selectedStatus || filtreButton.selectedStatus === "TOUS") return true;
        return c.statut === filtreButton.selectedStatus;
    });

    const getSortedAndFilteredCandidatures = (candidatures, filtreText) => {
        const STATUTS_PRIORITAIRES = ["STAGE_TROUVE", "CONTRAT_CREE"];

        return candidatures
            .filter((c) =>
                Object.values(c).some((valeur) =>
                    String(valeur).toLowerCase().includes(String(filtreText).toLowerCase())
                )
            )
            .sort((a, b) => {
                const aPrioritaire = STATUTS_PRIORITAIRES.includes(a.statut);
                const bPrioritaire = STATUTS_PRIORITAIRES.includes(b.statut);

                if (aPrioritaire && !bPrioritaire) return -1;
                if (!aPrioritaire && bPrioritaire) return 1;
                return 0;
            });
    };

    return(
        <>
            <h2 className="text-center text-xl font-semibold mb-5">
                {t("etudiant.card_professeur.candidatures_etudiant_titre", {
                    prenom: etudiant?.firstName,
                    nom: etudiant?.lastName
                })}
            </h2>

            {erreur && (
                <p role="alert" className="mb-3 rounded-xl border border-red-300 bg-red-100 px-4 py-3 text-sm text-red-700">
                    {t("error.generic")}
                </p>
            )}

            <ExpandableList
                items={getSortedAndFilteredCandidatures(filteredCandidature, filtreText)}
                limit={candidatures.length}
                renderItem={(candidature) => {
                    const statutCandidature = getStatusConfig(candidature.statut, t, "candidature");
                    const evaluationExistante = evaluations[candidature.id];
                    const peutEvaluer = Boolean(evaluationExistante) || STATUTS_EVALUABLES.includes(candidature.statut);

                    return (
                        <ListItem
                            onClick={() => voirResumeOffre(candidature.offreId)}
                            actionClassName="bg-[#043462] text-white hover:bg-[#03284d]"
                            actionLabel={t("etudiant.card_professeur.voir_detail")}
                            actionIcon={<HiEye className="size-4 shrink-0" aria-hidden="true" />}
                            secondaryAction={peutEvaluer ? {
                                label: evaluationExistante ? t("evaluation.view") : t("actions.evaluate_site"),
                                icon: <MdRateReview className="size-4 shrink-0" aria-hidden="true" />,
                                onClick: () => ouvrirEvaluation(candidature),
                                disabled: chargementId === candidature.id,
                                className: evaluationExistante
                                    ? "bg-white text-[#043462] border border-[#043462]/30 hover:bg-[#043462]/5"
                                    : "bg-[#00CCCB] text-[#043462] hover:bg-[#00b3b2]",
                            } : null}
                            badge={statutCandidature.label}
                            badgeClassName={statutCandidature.className + " text-sm"}
                            detailRows={[
                                { label: t("etudiant.card_professeur.employeur"), value: candidature.employeurBusinessName },
                                { label: t("etudiant.card_professeur.poste"), value: candidature.offreTitle },
                                { label: t("etudiant.card_professeur.date_application"), value : formaterDate(candidature.dateCandidature)}
                            ]}
                        />
                    );
                }}
            />

            {evaluationOuverte && (
                <EvaluationMilieuStage
                    etudiant={etudiant}
                    employeur={evaluationOuverte.resumeOffre?.employeurDTO}
                    offre={evaluationOuverte.resumeOffre}
                    evaluationExistante={evaluations[evaluationOuverte.candidature.id]}
                    onSubmit={(payload) => soumettreEvaluationMilieuStage(evaluationOuverte.candidature.id, payload)}
                    onClose={() => setEvaluationOuverte(null)}
                    onSuccess={(evaluation) => {
                        setEvaluations(prev => ({ ...prev, [evaluationOuverte.candidature.id]: evaluation }));
                        setEvaluationOuverte(null);
                    }}
                />
            )}
        </>
    )
};export default ShowEtudiantEntrevue;