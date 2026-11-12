package com.lacouf.rsbjwt;

import com.lacouf.rsbjwt.Exception.DateFinAffichageApresDateDebutException;
import com.lacouf.rsbjwt.Exception.EmployeurIntrouvable;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.model.Enum.*;
import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.service.*;
import com.lacouf.rsbjwt.service.dto.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

@SpringBootApplication
public class ReactSpringSecurityJwtApplication implements CommandLineRunner {
    private final GestionnaireRepository gestionnaireRepository;
    private final EtudiantRepository etudiantRepository;
    private final UserAppRepository userAppRepository;
    private final Environment environment;
    private final EtudiantService etudiantService;
    private final OffreDeStageRepository offreDeStageRepository;
    private final EmployeurRepository employeurRepository;
    private final EmployeurService employeurService;
    private final GestionnaireService gestionnaireService;
    private final CvEtudiantRepository cvEtudiantRepository;
    private final ProfesseurService professeurService;
    private final CandidatureRepository candidatureRepository;
    private final EntrevueRepository entrevueRepository;
    private final PDFService pdfService;
    private final InscriptionRepository inscriptionRepository;

    public ReactSpringSecurityJwtApplication(
            GestionnaireRepository gestionnaireRepository,
            EtudiantRepository etudiantRepository,
            UserAppRepository userAppRepository,
            Environment environment,
            EtudiantService etudiantService,
            OffreDeStageRepository offreDeStageRepository,
            EmployeurService employeurService,
            EmployeurRepository employeurRepository,
            GestionnaireService gestionnaireService,
            CvEtudiantRepository cvEtudiantRepository,
            ProfesseurService professeurService,
            CandidatureRepository candidatureRepository,
            EntrevueRepository entrevueRepository,
            PDFService pdfService,
            InscriptionRepository inscriptionRepository
    ) {
        this.gestionnaireRepository = gestionnaireRepository;
        this.etudiantRepository = etudiantRepository;
        this.userAppRepository = userAppRepository;
        this.environment = environment;
        this.etudiantService = etudiantService;
        this.employeurService = employeurService;
        this.offreDeStageRepository = offreDeStageRepository;
        this.employeurRepository = employeurRepository;
        this.gestionnaireService = gestionnaireService;
        this.cvEtudiantRepository = cvEtudiantRepository;
        this.professeurService = professeurService;
        this.candidatureRepository = candidatureRepository;
        this.entrevueRepository = entrevueRepository;
        this.pdfService = pdfService;
        this.inscriptionRepository = inscriptionRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(ReactSpringSecurityJwtApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (Arrays.asList(environment.getActiveProfiles()).contains("test")) {
            return;
        }

        // ==========================================
        // 1. CRÉATION DE LA SESSION ANTÉRIEURE (HIVER 2026)
        // ==========================================
        try {
            creerSessionHiver2026();
        } catch (Exception e) {
            System.out.println("Erreur session Hiver 2026: " + e.getMessage());
        }

        // ==========================================
        // 2. SESSION COURANTE (AUTOMNE 2026)
        // ==========================================
        String emailAdrien = "Adrien@gmail.com";
        String employeurEmail = "Roger.Malinois@hotmail.com";

        GestionnaireDTO gestionnaireDto = new GestionnaireDTO(-8, "Pascal", "Belmont", "Pascal.Belmont@hotmail.com", "565-985-5858", "NoNeedButStillNeed", LocalDateTime.now());
        gestionnaireService.creerCompteGestionnaire(gestionnaireDto);

        // Sébastien (Inscrit Automne 2026)
        InscriptionEtudiantDTO etudiantDTO = new InscriptionEtudiantDTO("Sebastien", "Castonguay", "450-716-1217", "Sebastien.Castonguay@hotmail.com", "6565656", Departement.INFORMATIQUE.name(), "NoNeedButStillNeed", "NoNeedButStillNeed");
        etudiantService.creerCompteEtudiant(etudiantDTO);
        ajouterInscriptionSecurisee("6565656", Saison.AUTOMNE, 2026);

        // Adrien (Inscrit Automne 2026)
        InscriptionEtudiantDTO etudiantDTO2 = new InscriptionEtudiantDTO("Adrien", "Agreste", "450-716-1216", emailAdrien, "1111111", Departement.INFORMATIQUE.name(), "NoNeedButStillNeed", "NoNeedButStillNeed");
        etudiantService.creerCompteEtudiant(etudiantDTO2);
        Etudiant etudiantAdrienObj = etudiantRepository.findByMatricule("1111111").orElseThrow();
        ajouterInscriptionSecurisee("1111111", Saison.AUTOMNE, 2026);

        // Marc (Inscrit Automne 2026)
        InscriptionEtudiantDTO etudiantBadCvDTO = new InscriptionEtudiantDTO(
                "Marc", "Disparu", "450-999-8888", "Marc.Disparu@hotmail.com",
                "9999999", Departement.TRAVAIL_SOCIAL.name(), "NoNeed", "NoNeed"
        );
        etudiantService.creerCompteEtudiant(etudiantBadCvDTO);
        Etudiant etudiantBadCv = etudiantRepository.findByMatricule("9999999").orElseThrow();
        ajouterInscriptionSecurisee("9999999", Saison.AUTOMNE, 2026);

        // Christelle (Inscrite Automne 2026)
        InscriptionEtudiantDTO etudiantNotifCvDTO = new InscriptionEtudiantDTO("Christelle", "Nouga", "450-716-1288", "chrisNouga@gmail.com", "6565688", Departement.INFORMATIQUE.name(), "motdepasse", "motdepasse");
        etudiantService.creerCompteEtudiant(etudiantNotifCvDTO);
        ajouterInscriptionSecurisee("6565688", Saison.AUTOMNE, 2026);

        // Création sécurisée de l'employeur s'il n'existe pas déjà (évite EmailExistantException)
        if (employeurRepository.findByEmail(employeurEmail).isEmpty()) {
            InscriptionEmployeurDTO employeurDTO = new InscriptionEmployeurDTO("Roger", "Malinois", "450-874-6586", employeurEmail, "Laval", "Malinois inc", SecteurActivite.INFORMATIQUE, "NoNeedButStillNeed", "NoNeedButStillNeed");
            employeurService.creeCompteEmployeur(employeurDTO);
        }

        InscriptionProfesseurDTO inscriptionProfesseurDTO = new InscriptionProfesseurDTO("Toru", "Shirazu", "Toru.Shirazu@claurendeau.qc.ca", "NoNeed", "NoNeed", "9325645", "698-585-6666", Departement.INFORMATIQUE.name());
        InscriptionProfesseurDTO inscriptionProfesseurDTO2 = new InscriptionProfesseurDTO("Evie", "Frye", "Evie.Frye@claurendeau.qc.ca", "NoNeed", "NoNeed", "5486598", "698-585-6266", Departement.TRAVAIL_SOCIAL.name());

        professeurService.creerCompteProfesseur(inscriptionProfesseurDTO);
        professeurService.creerCompteProfesseur(inscriptionProfesseurDTO2);

        CreationOffreDeStageDTO dto1 = new CreationOffreDeStageDTO(
                "Développeur Web Junior",
                "Développement front-end et back-end, revue de code, CI/CD.",
                22.50,
                Departement.INFORMATIQUE,
                LocalDate.now().plusDays(10),
                LocalDate.now().plusMonths(3),
                LocalDate.now().plusDays(7)
        );

        CreationOffreDeStageDTO dto2 = new CreationOffreDeStageDTO(
                "Technicien Support Informatique",
                "Support aux utilisateurs, diagnostic matériel, installation de logiciels.",
                20.00,
                Departement.INFORMATIQUE,
                LocalDate.now().plusDays(5),
                LocalDate.now().plusMonths(2),
                LocalDate.now().plusDays(3)
        );

        Employeur employeur = employeurRepository.findByEmail(employeurEmail).orElseThrow(EmployeurIntrouvable::new);

        MultipartFile realOffre1 = new MockMultipartFile("file", "offre.pdf", "application/pdf", pdfService.createOffreDeStagePdf(dto1, employeur).getInputStream());
        MultipartFile realOffre2 = new MockMultipartFile("file", "offre.pdf", "application/pdf", pdfService.createOffreDeStagePdf(dto2, employeur).getInputStream());
        MultipartFile realCvAdrien = new MockMultipartFile("file", "cv.pdf", "application/pdf", pdfService.createCvPdf(etudiantAdrienObj).getInputStream());

        OffreDeStageDTO devWebJr = null;
        try {
            devWebJr = employeurService.creerOffre(dto1, realOffre1, employeurEmail);
        } catch (DateFinAffichageApresDateDebutException e) {
            System.out.println(e.getMessage());
        }

        OffreDeStageDTO techSuppInfo = null;
        try {
            techSuppInfo = employeurService.creerOffre(dto2, realOffre2, employeurEmail);
        } catch (DateFinAffichageApresDateDebutException e) {
            System.out.println(e.getMessage());
        }

        OffreDeStage offreDeStage = new OffreDeStage("Technician Reseaux",
                "Support aux utilisateurs, diagnostic matériel, installation de logiciels.",
                20.0,
                Departement.INFORMATIQUE,
                LocalDate.now().plusDays(5),
                LocalDate.now().plusMonths(2),
                LocalDate.now().minusDays(2),
                Statut.ACCEPTEE,
                "offre.pdf",
                "uploads/offres",
                employeur,
                "",
                LocalDateTime.now()
        );
        offreDeStageRepository.save(offreDeStage);

        CvEtudiant cvEtudiant = new CvEtudiant("CV_Marc_Disparu", "application/pdf", 1024L, "none", LocalDateTime.now(), etudiantBadCv);

        CvEtudiantDTO cvAdrien = etudiantService.televerserCv(realCvAdrien, emailAdrien);
        gestionnaireService.approuverCurriculumVitae(cvAdrien.id());
        if (devWebJr != null) gestionnaireService.approuverOffre(devWebJr.id());
        if (techSuppInfo != null) gestionnaireService.approuverOffre(techSuppInfo.id());

        if (devWebJr != null) etudiantService.postuler(devWebJr.id(), emailAdrien);
        if (techSuppInfo != null) etudiantService.postuler(techSuppInfo.id(), emailAdrien);
        cvEtudiantRepository.save(cvEtudiant);

        // Nicolas (Inscrit Automne 2026)
        String emailNicolas = "Nicolas.Sarkozy@gmail.com";
        InscriptionEtudiantDTO etudiantSarkozyDTO = new InscriptionEtudiantDTO(
                "Nicolas", "Sarkozy", "450-555-5555", emailNicolas, "2222222",
                Departement.INFORMATIQUE.name(), "a", "a"
        );
        etudiantService.creerCompteEtudiant(etudiantSarkozyDTO);
        Etudiant etudiantSarkoObj = etudiantRepository.findByMatricule("2222222").orElseThrow();
        ajouterInscriptionSecurisee("2222222", Saison.AUTOMNE, 2026);

        MultipartFile realCvSarko = new MockMultipartFile("file", "cv.pdf", "application/pdf", pdfService.createCvPdf(etudiantSarkoObj).getInputStream());

        CvEtudiantDTO cvNicolas = etudiantService.televerserCv(realCvSarko, emailNicolas);
        gestionnaireService.approuverCurriculumVitae(cvNicolas.id());

        if (devWebJr != null) {
            etudiantService.postuler(devWebJr.id(), emailNicolas);
        }

        Candidature candidatureSarko = candidatureRepository.findAll().stream()
                .filter(c -> c.getEtudiant().getEmail().equals(emailNicolas))
                .findFirst()
                .orElseThrow();

        OffreDeStageDTO finalTechSuppInfo = techSuppInfo;
        Candidature candidatureAdrien = candidatureRepository.findAll().stream()
                .filter(c -> c.getEtudiant().getEmail().equals(emailAdrien) && finalTechSuppInfo != null && c.getOffreDeStage().getTitle().equals(finalTechSuppInfo.title()))
                .findAny()
                .orElseThrow();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(employeurEmail, "password", Collections.emptyList())
        );

        employeurService.retenirCandidaturePourEntrevue(candidatureSarko.getId());
        employeurService.retenirCandidaturePourEntrevue(candidatureAdrien.getId());

        CreationEntrevueDTO entDTO = new CreationEntrevueDTO(
                candidatureSarko.getId(),
                FormatEntrevue.EN_LIGNE,
                LocalDateTime.now().plusDays(3),
                60
        );
        CreationEntrevueDTO entDTOAdrien = new CreationEntrevueDTO(
                candidatureAdrien.getId(),
                FormatEntrevue.EN_LIGNE,
                LocalDateTime.now().plusDays(3),
                60
        );
        employeurService.creerConvocation(entDTO);
        employeurService.creerConvocation(entDTOAdrien);

        SecurityContextHolder.clearContext();

        candidatureSarko.setStatut(StatutCandidature.STAGE_TROUVE);
        candidatureRepository.save(candidatureSarko);

        // Juzo (Inscrit Automne 2026)
        String emailJuzoSuzuya = "Juzo.Suzuya@gmail.com";
        InscriptionEtudiantDTO etudiantJuzoDTO = new InscriptionEtudiantDTO(
                "Juzo", "Suzuya", "450-555-5585", emailJuzoSuzuya, "3333333",
                Departement.INFORMATIQUE.name(), "a", "a"
        );
        etudiantService.creerCompteEtudiant(etudiantJuzoDTO);
        ajouterInscriptionSecurisee("3333333", Saison.AUTOMNE, 2026);
        Etudiant etudiantJuzoObj = etudiantRepository.findByMatricule("3333333").orElseThrow();

        MultipartFile realCvJuzo = new MockMultipartFile("file", "cv.pdf", "application/pdf", pdfService.createCvPdf(etudiantJuzoObj).getInputStream());

        CvEtudiantDTO juzoSuzuyaCvDTO = etudiantService.televerserCv(realCvJuzo, emailJuzoSuzuya);
        gestionnaireService.approuverCurriculumVitae(juzoSuzuyaCvDTO.id());

        if (techSuppInfo != null) {
            etudiantService.postuler(techSuppInfo.id(), emailJuzoSuzuya);
        }

        Candidature candidatureJuzo = candidatureRepository.findAll().stream()
                .filter(c -> c.getEtudiant().getEmail().equals(emailJuzoSuzuya))
                .findFirst()
                .orElseThrow();

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(employeurEmail, "password", Collections.emptyList())
        );

        employeurService.retenirCandidaturePourEntrevue(candidatureJuzo.getId());

        CreationEntrevueDTO entDTOJuzo = new CreationEntrevueDTO(
                candidatureJuzo.getId(),
                FormatEntrevue.EN_LIGNE,
                LocalDateTime.now().plusDays(3),
                60
        );

        employeurService.creerConvocation(entDTOJuzo);
        SecurityContextHolder.clearContext();

        candidatureJuzo.setStatut(StatutCandidature.STAGE_TROUVE);
        candidatureRepository.save(candidatureJuzo);
    }

    private void creerSessionHiver2026() throws Exception {
        String employeurEmail = "Roger.Malinois@hotmail.com";

        // Création sécurisée de l'employeur si absent
        Employeur employeur = employeurRepository.findByEmail(employeurEmail).orElseGet(() -> {
            InscriptionEmployeurDTO empDTO = new InscriptionEmployeurDTO("Roger", "Malinois", "450-874-6586", employeurEmail, "Laval", "Malinois inc", SecteurActivite.INFORMATIQUE, "password", "password");
            try {
                employeurService.creeCompteEmployeur(empDTO);
            } catch (Exception ignored) {}
            return employeurRepository.findByEmail(employeurEmail).orElseThrow();
        });

        // 1. Création d'un étudiant exclusif à Hiver 2026
        InscriptionEtudiantDTO ancienUniqueDTO = new InscriptionEtudiantDTO(
                "Charles", "Ancien", "450-333-3333", "charles.ancien@gmail.com",
                "7777777", Departement.INFORMATIQUE.name(), "password", "password"
        );
        etudiantService.creerCompteEtudiant(ancienUniqueDTO);
        ajouterInscriptionSecurisee("7777777", Saison.HIVER, 2026);
        Etudiant etudiantCharles = etudiantRepository.findByMatricule("7777777").orElseThrow();

        // 2. Création d'un étudiant réinscrit (Hiver 2026 ET Automne 2026)
        InscriptionEtudiantDTO reinscritDTO = new InscriptionEtudiantDTO(
                "Julie", "Reinscrite", "450-444-4444", "julie.reinscrite@gmail.com",
                "5555555", Departement.INFORMATIQUE.name(), "password", "password"
        );
        etudiantService.creerCompteEtudiant(reinscritDTO);
        ajouterInscriptionSecurisee("5555555", Saison.HIVER, 2026);
        ajouterInscriptionSecurisee("5555555", Saison.AUTOMNE, 2026);
        Etudiant etudiantJulie = etudiantRepository.findByMatricule("5555555").orElseThrow();

        // 3. Création directe de l'offre de stage pour Hiver 2026 (contourne la validation des dates passées)
        CreationOffreDeStageDTO offreHiverDTO = new CreationOffreDeStageDTO(
                "Programmeur Fullstack (Hiver)",
                "Stage de développement web pour la session d'hiver.",
                24.00,
                Departement.INFORMATIQUE,
                LocalDate.of(2026, 5, 15),
                LocalDate.of(2026, 1, 15),
                LocalDate.of(2026, 1, 10)
        );

        OffreDeStage offreHiver = new OffreDeStage(
                offreHiverDTO.title(),
                offreHiverDTO.description(),
                offreHiverDTO.salary(),
                offreHiverDTO.domain(),
                offreHiverDTO.startDate(),
                offreHiverDTO.endDate(),
                offreHiverDTO.displayEndDate(),
                Statut.ACCEPTEE,
                "offre_hiver.pdf",
                "uploads/offres",
                employeur,
                "",
                LocalDateTime.of(2026, 1, 5, 10, 0)
        );

        OffreDeStage offreHiverSaved = offreDeStageRepository.save(offreHiver);

        // 4. CVs pour ces étudiants
        MultipartFile cvCharlesFile = new MockMultipartFile("file", "cv.pdf", "application/pdf", pdfService.createCvPdf(etudiantCharles).getInputStream());
        CvEtudiantDTO cvCharlesDTO = etudiantService.televerserCv(cvCharlesFile, "charles.ancien@gmail.com");
        gestionnaireService.approuverCurriculumVitae(cvCharlesDTO.id());

        MultipartFile cvJulieFile = new MockMultipartFile("file", "cv.pdf", "application/pdf", pdfService.createCvPdf(etudiantJulie).getInputStream());
        CvEtudiantDTO cvJulieDTO = etudiantService.televerserCv(cvJulieFile, "julie.reinscrite@gmail.com");
        gestionnaireService.approuverCurriculumVitae(cvJulieDTO.id());

        // 5. Candidatures
        etudiantService.postuler(offreHiverSaved.getId(), "charles.ancien@gmail.com");
        etudiantService.postuler(offreHiverSaved.getId(), "julie.reinscrite@gmail.com");

        Candidature candCharles = candidatureRepository.findAll().stream()
                .filter(c -> c.getEtudiant().getEmail().equals("charles.ancien@gmail.com") && c.getOffreDeStage().getId().equals(offreHiverSaved.getId()))
                .findFirst().orElseThrow();

        Candidature candJulie = candidatureRepository.findAll().stream()
                .filter(c -> c.getEtudiant().getEmail().equals("julie.reinscrite@gmail.com") && c.getOffreDeStage().getId().equals(offreHiverSaved.getId()))
                .findFirst().orElseThrow();

        // 6. Entrevues et Contrat Signé (Stage Trouvé)
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(employeurEmail, "password", Collections.emptyList())
        );

        employeurService.retenirCandidaturePourEntrevue(candCharles.getId());
        employeurService.retenirCandidaturePourEntrevue(candJulie.getId());

        employeurService.creerConvocation(new CreationEntrevueDTO(candCharles.getId(), FormatEntrevue.EN_LIGNE, LocalDateTime.of(2026, 2, 10, 10, 0), 60));
        employeurService.creerConvocation(new CreationEntrevueDTO(candJulie.getId(), FormatEntrevue.EN_LIGNE, LocalDateTime.of(2026, 2, 10, 11, 0), 60));

        SecurityContextHolder.clearContext();

        candCharles.setStatut(StatutCandidature.STAGE_TROUVE);
        candidatureRepository.save(candCharles);

        candJulie.setStatut(StatutCandidature.STAGE_TROUVE);
        candidatureRepository.save(candJulie);
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