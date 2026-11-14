package com.lacouf.rsbjwt.config;

import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.model.Enum.*;
import com.lacouf.rsbjwt.model.auth.Credentials;
import com.lacouf.rsbjwt.model.auth.Role;
import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.service.*;
import com.lacouf.rsbjwt.service.dto.GestionnaireDTO;
import com.lacouf.rsbjwt.service.dto.InscriptionEmployeurDTO;
import com.lacouf.rsbjwt.service.dto.InscriptionProfesseurDTO;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final ProfesseurRepository professeurRepository;
    private final ProfesseurService professeurService;
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
    private final UserAppRepository userAppRepository;
    private final PasswordEncoder passwordEncoder;

    public Hiver2026DataInitializer(
            EmployeurRepository employeurRepository,
            EmployeurService employeurService,
            EtudiantRepository etudiantRepository,
            EtudiantService etudiantService,
            ProfesseurRepository professeurRepository,
            ProfesseurService professeurService,
            OffreDeStageRepository offreDeStageRepository,
            CandidatureRepository candidatureRepository,
            EntrevueRepository entrevueRepository,
            GestionnaireRepository gestionnaireRepository,
            GestionnaireService gestionnaireService,
            ContratRepository contratRepository,
            InscriptionRepository inscriptionRepository,
            CvEtudiantRepository cvEtudiantRepository,
            PDFService pdfService,
            Environment environment,
            UserAppRepository userAppRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.employeurRepository = employeurRepository;
        this.employeurService = employeurService;
        this.etudiantRepository = etudiantRepository;
        this.etudiantService = etudiantService;
        this.professeurRepository = professeurRepository;
        this.professeurService = professeurService;
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
        this.userAppRepository = userAppRepository;
        this.passwordEncoder = passwordEncoder;
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

        // S'assurer qu'un gestionnaire existe en base pour pouvoir lier les contrats
        if (gestionnaireRepository.findAll().isEmpty()) {
            try {
                GestionnaireDTO gestionnaireDto = new GestionnaireDTO(-8, "Pascal", "Belmont", "Pascal.Belmont@hotmail.com", "565-985-5858", "password", LocalDateTime.now());
                gestionnaireService.creerCompteGestionnaire(gestionnaireDto);
            } catch (Exception ignored) {}
        }

        Employeur employeur = employeurRepository.findByEmail(employeurEmail).orElseGet(() -> {
            InscriptionEmployeurDTO empDTO = new InscriptionEmployeurDTO("Roger", "Malinois", "450-874-6586", employeurEmail, "Laval", "Malinois inc", SecteurActivite.INFORMATIQUE, "password", "password");
            try {
                employeurService.creeCompteEmployeur(empDTO);
            } catch (Exception ignored) {}
            return employeurRepository.findByEmail(employeurEmail).orElseThrow();
        });

        // 0. Création ou récupération du professeur Toru
        Professeur toru = professeurRepository.findByMatricule("9325645").orElseGet(() -> {
            InscriptionProfesseurDTO profDTO = new InscriptionProfesseurDTO("Toru", "Shirazu", "Toru.Shirazu@claurendeau.qc.ca", "password", "password", "9325645", "698-585-6666", Departement.INFORMATIQUE.name());
            try {
                professeurService.creerCompteProfesseur(profDTO);
            } catch (Exception ignored) {}
            return professeurRepository.findByMatricule("9325645").orElseThrow();
        });
        ajouterInscriptionUtilisateurSecurise(toru.getEmail(), Saison.HIVER, 2026);

        // 1. Étudiants et inscriptions directes en repo pour Hiver 2026
        Etudiant etudiantCharles = etudiantRepository.findByMatricule("7777777").orElseGet(() -> {
            Etudiant e = Etudiant.builder()
                    .firstName("Charles")
                    .lastName("Ancien")
                    .phoneNumber("450-333-3333")
                    .email("charles.ancien@gmail.com")
                    .matricule("7777777")
                    .department(Departement.INFORMATIQUE)
                    .password(passwordEncoder.encode("password"))
                    .build();
            return etudiantRepository.save(e);
        });
        ajouterInscriptionSecurisee("7777777", Saison.HIVER, 2026);

        Etudiant etudiantJulie = etudiantRepository.findByMatricule("5555555").orElseGet(() -> {
            Etudiant e = Etudiant.builder()
                    .firstName("Julie")
                    .lastName("Reinscrite")
                    .phoneNumber("450-444-4444")
                    .email("julie.reinscrite@gmail.com")
                    .matricule("5555555")
                    .department(Departement.INFORMATIQUE)
                    .password(passwordEncoder.encode("password"))
                    .build();
            return etudiantRepository.save(e);
        });
        ajouterInscriptionSecurisee("5555555", Saison.HIVER, 2026);

        LocalDateTime dateAssignationHiver = LocalDateTime.of(2026, 2, 5, 10, 0);

        etudiantCharles.setProfesseur(toru);
        etudiantCharles.setProfessorAssignedAt(dateAssignationHiver);
        etudiantRepository.save(etudiantCharles);

        etudiantJulie.setProfesseur(toru);
        etudiantJulie.setProfessorAssignedAt(dateAssignationHiver);
        etudiantRepository.save(etudiantJulie);

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
        candCharles = candidatureRepository.save(candCharles);

        etudiantCharles.setStatut(StatutEtudiant.STAGE_TROUVE);
        etudiantCharles.setCandidatureStage(candCharles);
        etudiantRepository.save(etudiantCharles);

        Candidature candJulie = Candidature.builder()
                .etudiant(etudiantJulie)
                .offreDeStage(offreHiverSaved)
                .dateCandidature(LocalDateTime.of(2026, 1, 13, 9, 30))
                .build();
        candJulie.setStatut(StatutCandidature.STAGE_TROUVE);
        candJulie = candidatureRepository.save(candJulie);

        etudiantJulie.setStatut(StatutEtudiant.STAGE_TROUVE);
        etudiantJulie.setCandidatureStage(candJulie);
        etudiantRepository.save(etudiantJulie);

        // 5. Entrevues directes via le repository
        Entrevue entrevueCharles = Entrevue.builder()
                .candidature(candCharles)
                .format(FormatEntrevue.EN_LIGNE)
                .dateHeureEntrevue(LocalDateTime.of(2026, 2, 10, 10, 0))
                .dateCreation(LocalDateTime.of(2026, 1, 15, 10, 0))
                .duree(60)
                .build();
        entrevueCharles.setStatut(StatutEntrevue.TERMINEE);
        entrevueRepository.save(entrevueCharles);

        Entrevue entrevueJulie = Entrevue.builder()
                .candidature(candJulie)
                .format(FormatEntrevue.EN_LIGNE)
                .dateHeureEntrevue(LocalDateTime.of(2026, 2, 10, 11, 0))
                .dateCreation(LocalDateTime.of(2026, 1, 15, 10, 0))
                .duree(60)
                .build();
        entrevueJulie.setStatut(StatutEntrevue.TERMINEE);
        entrevueRepository.save(entrevueJulie);

        // 6. Contrats générés et signés
        Gestionnaire gestionnaire = gestionnaireRepository.findAll().stream().findFirst().orElse(null);

        if (gestionnaire != null) {
            if (contratRepository.findByCandidatureId(candCharles.getId()).isEmpty()) {
                Contrat contratCharles = Contrat.builder()
                        .candidature(candCharles)
                        .gestionnaire(gestionnaire)
                        .build();
                contratCharles.setDateSignatureEtudiant(LocalDateTime.of(2026, 2, 15, 10, 0));
                contratCharles.setDateSignatureEmployeur(LocalDateTime.of(2026, 2, 15, 11, 0));
                contratCharles.setDateSignatureGestionnaire(LocalDateTime.of(2026, 2, 15, 12, 0));
                contratRepository.save(contratCharles);
                candCharles.setStatut(StatutCandidature.CONTRAT_CREE);
                candidatureRepository.save(candCharles);
            }

            if (contratRepository.findByCandidatureId(candJulie.getId()).isEmpty()) {
                Contrat contratJulie = Contrat.builder()
                        .candidature(candJulie)
                        .gestionnaire(gestionnaire)
                        .build();
                contratJulie.setDateSignatureEtudiant(LocalDateTime.of(2026, 2, 16, 14, 0));
                contratJulie.setDateSignatureEmployeur(LocalDateTime.of(2026, 2, 16, 15, 0));
                contratRepository.save(contratJulie);
                candJulie.setStatut(StatutCandidature.CONTRAT_CREE);
                candidatureRepository.save(candJulie);
            }

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

    private void ajouterInscriptionUtilisateurSecurise(String email, Saison saison, int annee) {
        UserApp utilisateur = userAppRepository.findUserAppByEmail(email).orElseThrow();
        boolean dejaInscrit = inscriptionRepository.findAll().stream()
                .anyMatch(i -> i.getUtilisateur().getId().equals(utilisateur.getId())
                        && i.getSaison() == saison
                        && i.getAnnee() == annee);

        if (!dejaInscrit) {
            Inscription nouvelleInscription = new Inscription(utilisateur, saison, annee);
            inscriptionRepository.save(nouvelleInscription);
        }
    }
}