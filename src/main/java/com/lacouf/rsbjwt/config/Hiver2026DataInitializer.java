package com.lacouf.rsbjwt.config;

import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.model.Enum.*;
import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.service.dto.InscriptionEmployeurDTO;
import com.lacouf.rsbjwt.service.dto.InscriptionEtudiantDTO;
import com.lacouf.rsbjwt.service.EmployeurService;
import com.lacouf.rsbjwt.service.EtudiantService;
import com.lacouf.rsbjwt.service.GestionnaireService;
import com.lacouf.rsbjwt.service.PDFService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component
@Order(1)
public class Hiver2026DataInitializer implements CommandLineRunner {

    private final EmployeurRepository employeurRepository;
    private final EmployeurService employeurService;
    private final EtudiantRepository etudiantRepository;
    private final EtudiantService etudiantService;
    private final OffreDeStageRepository offreDeStageRepository;
    private final CandidatureRepository candidatureRepository;
    private final EntrevueRepository entrevueRepository;
    private final GestionnaireRepository gestionnaireRepository;
    private final GestionnaireService gestionnaireService;
    private final ContratRepository contratRepository;
    private final InscriptionRepository inscriptionRepository;
    private final CvEtudiantRepository cvEtudiantRepository;
    private final PDFService pdfService;
    private final Environment environment;

    public Hiver2026DataInitializer(
            EmployeurRepository employeurRepository,
            EmployeurService employeurService,
            EtudiantRepository etudiantRepository,
            EtudiantService etudiantService,
            OffreDeStageRepository offreDeStageRepository,
            CandidatureRepository candidatureRepository,
            EntrevueRepository entrevueRepository,
            GestionnaireRepository gestionnaireRepository,
            GestionnaireService gestionnaireService,
            ContratRepository contratRepository,
            InscriptionRepository inscriptionRepository,
            CvEtudiantRepository cvEtudiantRepository,
            PDFService pdfService,
            Environment environment
    ) {
        this.employeurRepository = employeurRepository;
        this.employeurService = employeurService;
        this.etudiantRepository = etudiantRepository;
        this.etudiantService = etudiantService;
        this.offreDeStageRepository = offreDeStageRepository;
        this.candidatureRepository = candidatureRepository;
        this.entrevueRepository = entrevueRepository;
        this.gestionnaireRepository = gestionnaireRepository;
        this.gestionnaireService = gestionnaireService;
        this.contratRepository = contratRepository;
        this.inscriptionRepository = inscriptionRepository;
        this.cvEtudiantRepository = cvEtudiantRepository;
        this.pdfService = pdfService;
        this.environment = environment;
    }

    @Override
    public void run(String... args) throws Exception {
        if (Arrays.asList(environment.getActiveProfiles()).contains("test")) {
            return;
        }

        try {
            creerSessionHiver2026();
        } catch (Exception e) {
            System.out.println("Erreur session Hiver 2026: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void creerSessionHiver2026() throws Exception {
        String employeurEmail = "Roger.Malinois@hotmail.com";

        Employeur employeur = employeurRepository.findByEmail(employeurEmail).orElseGet(() -> {
            InscriptionEmployeurDTO empDTO = new InscriptionEmployeurDTO("Roger", "Malinois", "450-874-6586", employeurEmail, "Laval", "Malinois inc", SecteurActivite.INFORMATIQUE, "password", "password");
            try {
                employeurService.creeCompteEmployeur(empDTO);
            } catch (Exception ignored) {}
            return employeurRepository.findByEmail(employeurEmail).orElseThrow();
        });

        // 1. Étudiants et inscriptions pour Hiver 2026
        Etudiant etudiantCharles = etudiantRepository.findByMatricule("7777777").orElseGet(() -> {
            InscriptionEtudiantDTO dto = new InscriptionEtudiantDTO("Charles", "Ancien", "450-333-3333", "charles.ancien@gmail.com", "7777777", Departement.INFORMATIQUE.name(), "password", "password");
            try { etudiantService.creerCompteEtudiant(dto); } catch (Exception ignored) {}
            return etudiantRepository.findByMatricule("7777777").orElseThrow();
        });
        ajouterInscriptionSecurisee("7777777", Saison.HIVER, 2026);

        Etudiant etudiantJulie = etudiantRepository.findByMatricule("5555555").orElseGet(() -> {
            InscriptionEtudiantDTO dto = new InscriptionEtudiantDTO("Julie", "Reinscrite", "450-444-4444", "julie.reinscrite@gmail.com", "5555555", Departement.INFORMATIQUE.name(), "password", "password");
            try { etudiantService.creerCompteEtudiant(dto); } catch (Exception ignored) {}
            return etudiantRepository.findByMatricule("5555555").orElseThrow();
        });
        ajouterInscriptionSecurisee("5555555", Saison.HIVER, 2026);
        ajouterInscriptionSecurisee("5555555", Saison.AUTOMNE, 2026);

        // 2. Création et approbation des CVs pour Charles et Julie
        creerEtApprouverCv(etudiantCharles, "charles.ancien@gmail.com");
        creerEtApprouverCv(etudiantJulie, "julie.reinscrite@gmail.com");

        // 3. Offre de stage ancrée en Hiver 2026
        OffreDeStage offreHiver = new OffreDeStage(
                "Programmeur Fullstack (Hiver)",
                "Stage de développement web pour la session d'hiver.",
                24.00,
                Departement.INFORMATIQUE,
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 5, 15),
                LocalDate.of(2026, 1, 15),
                Statut.ACCEPTEE,
                "offre_hiver.pdf",
                "uploads/offres",
                employeur,
                "",
                LocalDateTime.of(2026, 1, 10, 10, 0)
        );
        OffreDeStage offreHiverSaved = offreDeStageRepository.save(offreHiver);

        // 4. Candidatures directes via le builder
        Candidature candCharles = Candidature.builder()
                .etudiant(etudiantCharles)
                .offreDeStage(offreHiverSaved)
                .dateCandidature(LocalDateTime.of(2026, 1, 12, 14, 0))
                .build();
        candCharles.setStatut(StatutCandidature.STAGE_TROUVE);
        candidatureRepository.save(candCharles);

        Candidature candJulie = Candidature.builder()
                .etudiant(etudiantJulie)
                .offreDeStage(offreHiverSaved)
                .dateCandidature(LocalDateTime.of(2026, 1, 13, 9, 30))
                .build();
        candJulie.setStatut(StatutCandidature.STAGE_TROUVE);
        candidatureRepository.save(candJulie);

        // 5. Entrevues directes via le repository
        Entrevue entrevueCharles = Entrevue.builder()
                .candidature(candCharles)
                .format(FormatEntrevue.EN_LIGNE)
                .dateHeureEntrevue(LocalDateTime.of(2026, 2, 10, 10, 0))
                .duree(60)
                .build();
        entrevueRepository.save(entrevueCharles);

        Entrevue entrevueJulie = Entrevue.builder()
                .candidature(candJulie)
                .format(FormatEntrevue.EN_LIGNE)
                .dateHeureEntrevue(LocalDateTime.of(2026, 2, 10, 11, 0))
                .duree(60)
                .build();
        entrevueRepository.save(entrevueJulie);

        // 6. Contrats générés et signés
        Gestionnaire gestionnaire = gestionnaireRepository.findAll().stream().findFirst().orElse(null);

        if (gestionnaire != null) {
            // Contrat Charles (Signé par les 3 parties)
            Contrat contratCharles = Contrat.builder()
                    .candidature(candCharles)
                    .gestionnaire(gestionnaire)
                    .build();
            contratCharles.setDateSignatureEtudiant(LocalDateTime.of(2026, 2, 15, 10, 0));
            contratCharles.setDateSignatureEmployeur(LocalDateTime.of(2026, 2, 15, 11, 0));
            contratCharles.setDateSignatureGestionnaire(LocalDateTime.of(2026, 2, 15, 12, 0));
            contratRepository.save(contratCharles);

            // Contrat Julie (Signé par l'étudiant et l'employeur)
            Contrat contratJulie = Contrat.builder()
                    .candidature(candJulie)
                    .gestionnaire(gestionnaire)
                    .build();
            contratJulie.setDateSignatureEtudiant(LocalDateTime.of(2026, 2, 16, 14, 0));
            contratJulie.setDateSignatureEmployeur(LocalDateTime.of(2026, 2, 16, 15, 0));
            contratRepository.save(contratJulie);

            System.out.println("Session Hiver 2026 initialisée avec succès !");
        }
    }

    private void creerEtApprouverCv(Etudiant etudiant, String email) {
        try {
            MultipartFile realCv = new MockMultipartFile("file", "cv.pdf", "application/pdf", pdfService.createCvPdf(etudiant).getInputStream());
            var cvDto = etudiantService.televerserCv(realCv, email);
            gestionnaireService.approuverCurriculumVitae(cvDto.id());
        } catch (Exception e) {
            System.out.println("Erreur lors de la création/approbation du CV pour " + email + ": " + e.getMessage());
        }
    }

    private void ajouterInscriptionSecurisee(String matricule, Saison saison, int annee) {
        Etudiant etudiant = etudiantRepository.findByMatricule(matricule).orElseThrow();
        boolean dejaInscrit = inscriptionRepository.findAll().stream()
                .anyMatch(i -> i.getUtilisateur().getId().equals(etudiant.getId())
                        && i.getSaison() == saison
                        && i.getAnnee() == annee);

        if (!dejaInscrit) {
            Inscription nouvelleInscription = new Inscription(etudiant, saison, annee);
            inscriptionRepository.save(nouvelleInscription);
        }
    }
}