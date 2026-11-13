package com.lacouf.rsbjwt.service.chatbot.tools;

import com.lacouf.rsbjwt.model.Enum.ModeResultat;
import com.lacouf.rsbjwt.model.Enum.SignataireContrat;
import com.lacouf.rsbjwt.service.donnees.DonneesTemporellesService;
import com.lacouf.rsbjwt.service.dto.*;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Profile("chatbot")
public class ChatbotGestionnaireTemporelTools {
    private final DonneesTemporellesService donnees;

    public ChatbotGestionnaireTemporelTools(DonneesTemporellesService donnees) {
        this.donnees = donnees;
    }

    @Tool(description = "Donne la date et l'heure réelles du serveur avec son fuseau (Toronto par défaut), ainsi que le lundi et le dimanche de la semaine courante. Appeler avant d'interpréter aujourd'hui, cette semaine ou une autre période relative.")
    public ReferenceTemporelleDTO obtenirDateReference() {
        return donnees.obtenirDateReference();
    }

    @Tool(description = "Liste ou compte les offres créées entre deux jours inclus. La création n'est pas la publication après approbation. Pour aujourd'hui ou cette semaine, consulter d'abord obtenirDateReference.")
    public ResultatDonneesDTO consulterOffresCreeesEntre(
            @ToolParam(description = "Premier jour inclus, YYYY-MM-DD") LocalDate debut,
            @ToolParam(description = "Dernier jour inclus, YYYY-MM-DD") LocalDate fin,
            @ToolParam(description = "NOMBRE pour un comptage, LISTE pour les offres") ModeResultat mode) {
        verifierMode(mode);
        return mode == ModeResultat.NOMBRE ? ResultatDonneesDTO.nombre(donnees.compterOffresCreeesEntre(debut, fin))
                : ResultatDonneesDTO.liste(donnees.obtenirOffresCreeesEntre(debut, fin));
    }

    @Tool(description = "Liste ou compte les offres dont la FIN d'affichage est strictement après le jour fourni. Si la question dit seulement date d'affichage, demander si elle vise la création ou la fin d'affichage. Ne pas inventer l'année.")
    public ResultatDonneesDTO consulterOffresAvecFinAffichageApres(
            @ToolParam(description = "Jour exclu de la recherche, YYYY-MM-DD") LocalDate date,
            @ToolParam(description = "NOMBRE ou LISTE") ModeResultat mode) {
        verifierMode(mode);
        return mode == ModeResultat.NOMBRE ? ResultatDonneesDTO.nombre(donnees.compterOffresAvecFinAffichageApres(date))
                : ResultatDonneesDTO.liste(donnees.obtenirOffresAvecFinAffichageApres(date));
    }

    @Tool(description = "Liste les offres dont la fin d'affichage se situe entre les deux jours inclus. Inclut tous les statuts : indiquer le statut des offres et ne pas les présenter toutes comme disponibles.")
    public List<OffreDeStageDTO> obtenirOffresExpirantEntre(LocalDate debut, LocalDate fin) {
        return donnees.obtenirOffresExpirantEntre(debut, fin);
    }

    @Tool(description = "Liste les offres dont le début du stage se situe entre deux jours inclus. Les dates sont celles du stage, pas celles de création ou de fin d'affichage.")
    public List<OffreDeStageDTO> obtenirOffresAvecDebutStageEntre(LocalDate debut, LocalDate fin) {
        return donnees.obtenirOffresAvecDebutStageEntre(debut, fin);
    }

    @Tool(description = "Liste ou compte les candidatures déposées entre deux jours inclus. Ne confondre ni dépôt ni acceptation avec les dates d'entrevue.")
    public ResultatDonneesDTO consulterCandidaturesDeposeesEntre(LocalDate debut, LocalDate fin, ModeResultat mode) {
        verifierMode(mode);
        return mode == ModeResultat.NOMBRE ? ResultatDonneesDTO.nombre(donnees.compterCandidaturesDeposeesEntre(debut, fin))
                : ResultatDonneesDTO.liste(donnees.obtenirCandidaturesDeposeesEntre(debut, fin));
    }

    @Tool(description = "Liste ou compte les CV déposés entre deux jours inclus selon uploadDate, tous statuts confondus. Ce sont les dépôts des versions présentes en base, pas un historique des versions supprimées.")
    public ResultatDonneesDTO consulterCvDeposesEntre(LocalDate debut, LocalDate fin, ModeResultat mode) {
        verifierMode(mode);
        return mode == ModeResultat.NOMBRE ? ResultatDonneesDTO.nombre(donnees.compterCvDeposesEntre(debut, fin))
                : ResultatDonneesDTO.liste(donnees.obtenirCvDeposesEntre(debut, fin));
    }

    @Tool(description = "Liste ou compte les entrevues dont la date prévue se situe entre deux jours inclus, tous statuts enregistrés compris. Distinguer les refusées des rendez-vous maintenus et indiquer les statuts.")
    public ResultatDonneesDTO consulterEntrevuesEntre(LocalDate debut, LocalDate fin, ModeResultat mode) {
        verifierMode(mode);
        return mode == ModeResultat.NOMBRE ? ResultatDonneesDTO.nombre(donnees.compterEntrevuesEntre(debut, fin))
                : ResultatDonneesDTO.liste(donnees.obtenirEntrevuesEntre(debut, fin));
    }

    @Tool(description = "Liste ou compte les contrats signés entre deux jours inclus par le signataire choisi. Une signature individuelle ne signifie pas que les trois parties ont signé.")
    public ResultatDonneesDTO consulterContratsSignesEntre(SignataireContrat signataire, LocalDate debut, LocalDate fin, ModeResultat mode) {
        verifierMode(mode);
        return mode == ModeResultat.NOMBRE ? ResultatDonneesDTO.nombre(donnees.compterContratsSignesEntre(signataire, debut, fin))
                : ResultatDonneesDTO.liste(donnees.obtenirContratsSignesEntre(signataire, debut, fin));
    }

    private void verifierMode(ModeResultat mode) {
        if (mode == null) throw new IllegalArgumentException("chatbot.errors.mode_required");
    }
}