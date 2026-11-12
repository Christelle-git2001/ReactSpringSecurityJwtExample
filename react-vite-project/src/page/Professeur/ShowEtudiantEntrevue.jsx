import ListItem from "../../component/Design/Lists/ListItem.jsx";
import ExpandableList from "../../component/Design/Lists/ExpandableList.jsx";
import {getStatusConfig} from "../../utils/getStatusConfig.jsx";
import {getResumeOffre} from "../../api/professeur.jsx";
import {useTranslation} from "react-i18next";
import {useNavigate} from "react-router-dom";
import {formaterDate} from "../../utils/cvFormatters.jsx";
import Button from "../../component/Design/Button.jsx";
import { HiEye } from "react-icons/hi";

const ShowEtudiantEntrevue = ({etudiant , candidatures, filtreText , filtreButton}) => {
    const navigate = useNavigate();
    const { t } = useTranslation();
    const voirResumeOffre = async (offreID) => {
        if (!offreID) return;
        try {
            const resumeOffre = await getResumeOffre(offreID);
            navigate("/professeur/resumeOffre", { state: {resumeOffre,etudiant,candidatures} });
        } catch (e) {
            console.error(t("error.generic"))
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
            <ExpandableList
                items={getSortedAndFilteredCandidatures(filteredCandidature, filtreText)}
                limit={candidatures.length}
                renderItem={(candidature) =>{
                    const statutCandidature = getStatusConfig(candidature.statut, t, "candidature");
                    return  (
                        <ListItem
                            onClick={() => voirResumeOffre(candidature.offreId)}
                            actionClassName="bg-[#043462] text-white hover:bg-[#03284d]"
                            actionLabel={t("etudiant.card_professeur.voir_detail")}
                            actionIcon={<HiEye className="size-4 shrink-0" aria-hidden="true" />}
                            badge={statutCandidature.label}
                            badgeClassName={statutCandidature.className + " text-sm"}
                            detailRows={[
                                { label: t("etudiant.card_professeur.employeur"), value: candidature.employeurBusinessName },
                                { label: t("etudiant.card_professeur.poste"), value: candidature.offreTitle },
                                { label: t("etudiant.card_professeur.date_application"), value : formaterDate(candidature.dateCandidature)}
                            ]}
                        />
                    )}}
            />
        </>
    )
};export default ShowEtudiantEntrevue;