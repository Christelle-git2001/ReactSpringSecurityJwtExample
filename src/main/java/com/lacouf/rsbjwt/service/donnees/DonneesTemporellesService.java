package com.lacouf.rsbjwt.service.donnees;

import com.lacouf.rsbjwt.model.Enum.SignataireContrat;
import com.lacouf.rsbjwt.model.Enum.StatutCandidature;
import com.lacouf.rsbjwt.model.Enum.StatutEntrevue;
import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.service.EtudiantService;
import com.lacouf.rsbjwt.service.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class DonneesTemporellesService {
    private final OffreDeStageRepository offres;
    private final CandidatureRepository candidatures;
    private final CvEtudiantRepository cvs;
    private final EntrevueRepository entrevues;
    private final ContratRepository contrats;
    private final Clock horloge;

    @Autowired
    public DonneesTemporellesService(OffreDeStageRepository offres, CandidatureRepository candidatures,
                                    CvEtudiantRepository cvs, EntrevueRepository entrevues,
                                    ContratRepository contrats,
                                    @Value("${ose.chatbot.time-zone:America/Toronto}") String fuseau) {
        this(offres, candidatures, cvs, entrevues, contrats, Clock.system(ZoneId.of(fuseau)));
    }

    DonneesTemporellesService(OffreDeStageRepository offres, CandidatureRepository candidatures,
                             CvEtudiantRepository cvs, EntrevueRepository entrevues,
                             ContratRepository contrats, Clock horloge) {
        this.offres = offres;
        this.candidatures = candidatures;
        this.cvs = cvs;
        this.entrevues = entrevues;
        this.contrats = contrats;
        this.horloge = horloge;
    }

    public List<OffreDeStageDTO> obtenirOffresCreeesEntre(LocalDate debut, LocalDate fin) {
        verifierPeriode(debut, fin);
        return offres.findByCreatedAtGreaterThanEqualAndCreatedAtLessThanOrderByCreatedAtAsc(debut.atStartOfDay(), fin.plusDays(1).atStartOfDay())
                .stream().map(OffreDeStageDTO::of).toList();
    }

    public long compterOffresCreeesEntre(LocalDate debut, LocalDate fin) {
        verifierPeriode(debut, fin);
        return offres.countByCreatedAtGreaterThanEqualAndCreatedAtLessThan(debut.atStartOfDay(), fin.plusDays(1).atStartOfDay());
    }

    public List<OffreDeStageDTO> obtenirOffresAvecFinAffichageApres(LocalDate date) {
        verifierDate(date);
        return offres.findByDisplayEndDateAfterOrderByDisplayEndDateAsc(date).stream().map(OffreDeStageDTO::of).toList();
    }

    public long compterOffresAvecFinAffichageApres(LocalDate date) {
        verifierDate(date);
        return offres.countByDisplayEndDateAfter(date);
    }

    public List<OffreDeStageDTO> obtenirOffresExpirantEntre(LocalDate debut, LocalDate fin) {
        verifierPeriode(debut, fin);
        return offres.findByDisplayEndDateBetweenOrderByDisplayEndDateAsc(debut, fin).stream().map(OffreDeStageDTO::of).toList();
    }

    public List<OffreDeStageDTO> obtenirOffresAvecDebutStageEntre(LocalDate debut, LocalDate fin) {
        verifierPeriode(debut, fin);
        return offres.findByStartDateBetweenOrderByStartDateAsc(debut, fin).stream().map(OffreDeStageDTO::of).toList();
    }

    public List<CandidatureDTO> obtenirCandidaturesDeposeesEntre(LocalDate debut, LocalDate fin) {
        verifierPeriode(debut, fin);
        return candidatures.findByDateCandidatureGreaterThanEqualAndDateCandidatureLessThanOrderByDateCandidatureAsc(debut.atStartOfDay(), fin.plusDays(1).atStartOfDay())
                .stream().map(c -> CandidatureDTO.of(c, c.getStatut() == StatutCandidature.CONTRAT_CREE
                        ? contrats.findByCandidatureId(c.getId()).orElse(null) : null)).toList();
    }

    public long compterCandidaturesDeposeesEntre(LocalDate debut, LocalDate fin) {
        verifierPeriode(debut, fin);
        return candidatures.countByDateCandidatureGreaterThanEqualAndDateCandidatureLessThan(debut.atStartOfDay(), fin.plusDays(1).atStartOfDay());
    }

    public List<CvEtudiantDTO> obtenirCvDeposesEntre(LocalDate debut, LocalDate fin) {
        verifierPeriode(debut, fin);
        return cvs.findByUploadDateGreaterThanEqualAndUploadDateLessThanOrderByUploadDateAsc(debut.atStartOfDay(), fin.plusDays(1).atStartOfDay())
                .stream().map(CvEtudiantDTO::of).toList();
    }

    public long compterCvDeposesEntre(LocalDate debut, LocalDate fin) {
        verifierPeriode(debut, fin);
        return cvs.countByUploadDateGreaterThanEqualAndUploadDateLessThan(debut.atStartOfDay(), fin.plusDays(1).atStartOfDay());
    }

    public List<EntrevueDTO> obtenirEntrevuesEntre(LocalDate debut, LocalDate fin) {
        verifierPeriode(debut, fin);
        return entrevues.findByDateHeureEntrevueGreaterThanEqualAndDateHeureEntrevueLessThanOrderByDateHeureEntrevueAsc(debut.atStartOfDay(), fin.plusDays(1).atStartOfDay())
                .stream().map(EntrevueDTO::of).toList();
    }

    public long compterEntrevuesEntre(LocalDate debut, LocalDate fin) {
        verifierPeriode(debut, fin);
        return entrevues.countByDateHeureEntrevueGreaterThanEqualAndDateHeureEntrevueLessThan(debut.atStartOfDay(), fin.plusDays(1).atStartOfDay());
    }

    public List<ResumeContratDTO> obtenirContratsSignesEntre(SignataireContrat signataire, LocalDate debut, LocalDate fin) {
        verifierSignataire(signataire);
        verifierPeriode(debut, fin);
        return contrats.findSignesEntre(signataire.name(), debut.atStartOfDay(), fin.plusDays(1).atStartOfDay())
                .stream().map(ResumeContratDTO::of).toList();
    }

    public long compterContratsSignesEntre(SignataireContrat signataire, LocalDate debut, LocalDate fin) {
        verifierSignataire(signataire);
        verifierPeriode(debut, fin);
        return contrats.countSignesEntre(signataire.name(), debut.atStartOfDay(), fin.plusDays(1).atStartOfDay());
    }

    public List<EntrevueDTO> rechercherEntrevues(String poste, String entreprise) {
        if (poste == null || poste.isBlank() || poste.length() > 200
                || entreprise == null || entreprise.isBlank() || entreprise.length() > 200) {
            throw new IllegalArgumentException("chatbot.errors.search_required");
        }
        String[] motsPoste = normaliser(poste).split("\\s+");
        String[] motsEntreprise = normaliser(entreprise).split("\\s+");
        if (motsPoste[0].isBlank() || motsEntreprise[0].isBlank()) {
            throw new IllegalArgumentException("chatbot.errors.search_required");
        }
        return entrevues.findByCandidatureOffreDeStageEmployeurBusinessNameContainingIgnoreCaseOrderByDateHeureEntrevueAsc(motsEntreprise[0])
                .stream().filter(e -> contientTousLesMots(e.getCandidature().getOffreDeStage().getTitle(), motsPoste))
                .filter(e -> contientTousLesMots(e.getCandidature().getOffreDeStage().getEmployeur().getBusinessName(), motsEntreprise))
                .map(EntrevueDTO::of).toList();
    }

    public TempsAvantEntrevueDTO calculerTempsAvantEntrevue(Long entrevueId) {
        if (entrevueId == null || entrevueId <= 0) throw new IllegalArgumentException("chatbot.errors.invalid_interview");
        var entrevue = entrevues.findById(entrevueId)
                .orElseThrow(() -> new IllegalArgumentException("chatbot.errors.interview_missing"));
        ZonedDateTime maintenant = ZonedDateTime.now(horloge);
        ZonedDateTime debut = entrevue.getDateHeureEntrevue().atZone(horloge.getZone());
        ZonedDateTime fin = debut.plusMinutes(entrevue.getDuree());
        String etat = EtudiantService.actionsCandidatureDesactivees(entrevue.getCandidature()) ? "INDISPONIBLE"
                : entrevue.getStatut() == StatutEntrevue.REFUSEE ? "REFUSEE"
                : entrevue.getStatut() == StatutEntrevue.TERMINEE ? "TERMINEE"
                : maintenant.isBefore(debut) ? "A_VENIR" : maintenant.isBefore(fin) ? "EN_COURS" : "PASSEE";
        return new TempsAvantEntrevueDTO(EntrevueDTO.of(entrevue), etat,
                Duration.between(maintenant, debut).getSeconds(), Duration.between(maintenant, fin).getSeconds(), horloge.getZone().getId());
    }

    public ReferenceTemporelleDTO obtenirDateReference() {
        LocalDateTime maintenant = LocalDateTime.now(horloge);
        LocalDate lundi = maintenant.toLocalDate().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return new ReferenceTemporelleDTO(maintenant.toLocalDate(), maintenant, horloge.getZone().getId(), lundi, lundi.plusDays(6));
    }

    private void verifierPeriode(LocalDate debut, LocalDate fin) {
        verifierDate(debut);
        verifierDate(fin);
        if (fin.isBefore(debut)) throw new IllegalArgumentException("chatbot.errors.invalid_dates");
    }

    private void verifierDate(LocalDate date) {
        if (date == null || date.getYear() < 1 || date.getYear() > 9998) {
            throw new IllegalArgumentException("chatbot.errors.invalid_dates");
        }
    }

    private void verifierSignataire(SignataireContrat signataire) {
        if (signataire == null) throw new IllegalArgumentException("chatbot.errors.signer_required");
    }

    private String normaliser(String texte) {
        return Normalizer.normalize(texte == null ? "" : texte, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT).replaceAll("[^\\p{L}\\p{N}]+", " ").trim();
    }

    private boolean contientTousLesMots(String texte, String[] mots) {
        String normalise = normaliser(texte);
        return Arrays.stream(mots).allMatch(normalise::contains);
    }
}