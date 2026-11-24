package com.lacouf.rsbjwt.config;

import com.lacouf.rsbjwt.service.dto.cv.CvEtudiantDTO;
import com.lacouf.rsbjwt.service.dto.entrevue.CreationEntrevueDTO;
import com.lacouf.rsbjwt.service.dto.offre.CreationOffreDeStageDTO;
import com.lacouf.rsbjwt.service.dto.offre.OffreDeStageDTO;
import com.lacouf.rsbjwt.service.dto.utilisateur.*;
import com.lacouf.rsbjwt.exception.dates.DateFinAffichageApresDateDebutException;
import com.lacouf.rsbjwt.exception.utilisateur.EmployeurIntrouvable;
import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.model.Enum.*;
import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@Order(2)
public class CurrentSessionDataInitializer implements CommandLineRunner {

    private final GestionnaireRepository gestionnaireRepository;
    private final EtudiantRepository etudiantRepository;
    private final Environment environment;
    private final EtudiantService etudiantService;
    private final OffreDeStageRepository offreDeStageRepository;
    private final EmployeurRepository employeurRepository;
    private final EmployeurService employeurService;
    private final GestionnaireService gestionnaireService;
    private final CvEtudiantRepository cvEtudiantRepository;
    private final ProfesseurService professeurService;
    private final CandidatureRepository candidatureRepository;
    private final ContratRepository contratRepository;
    private final EntrevueRepository entrevueRepository;
    private final PDFService pdfService;
    private final InscriptionRepository inscriptionRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProfesseurRepository professeurRepository;

    public CurrentSessionDataInitializer(
            GestionnaireRepository gestionnaireRepository,
            EtudiantRepository etudiantRepository,
            Environment environment,
            EtudiantService etudiantService,
            OffreDeStageRepository offreDeStageRepository,
            EmployeurRepository employeurRepository,
            EmployeurService employeurService,
            GestionnaireService gestionnaireService,
            CvEtudiantRepository cvEtudiantRepository,
            ProfesseurService professeurService,
            CandidatureRepository candidatureRepository,
            ContratRepository contratRepository,
            EntrevueRepository entrevueRepository,
            PDFService pdfService,
            InscriptionRepository inscriptionRepository,
            PasswordEncoder passwordEncoder,
            ProfesseurRepository professeurRepository) {
        this.gestionnaireRepository = gestionnaireRepository;
        this.etudiantRepository = etudiantRepository;
        this.environment = environment;
        this.etudiantService = etudiantService;
        this.offreDeStageRepository = offreDeStageRepository;
        this.employeurRepository = employeurRepository;
        this.employeurService = employeurService;
        this.gestionnaireService = gestionnaireService;
        this.cvEtudiantRepository = cvEtudiantRepository;
        this.professeurService = professeurService;
        this.candidatureRepository = candidatureRepository;
        this.contratRepository = contratRepository;
        this.entrevueRepository = entrevueRepository;
        this.pdfService = pdfService;
        this.inscriptionRepository = inscriptionRepository;
        this.passwordEncoder = passwordEncoder;
        this.professeurRepository = professeurRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (Arrays.asList(environment.getActiveProfiles()).contains("test")) {
            return;
        }

        String emailAdrien = "Adrien@gmail.com";
        String emailNicolas = "Nicolas.Sarkozy@gmail.com";
        String employeurEmail = "Roger.Malinois@hotmail.com";

        if (gestionnaireRepository.findAll().isEmpty()) {
            try {
                GestionnaireDTO gestionnaireDto = new GestionnaireDTO(-8, "Pascal", "Belmont", "Pascal.Belmont@hotmail.com", "565-985-5858", "NoNeedButStillNeed", LocalDateTime.now());
                gestionnaireService.creerCompteGestionnaire(gestionnaireDto);
            } catch (Exception ignored) {}
        }

        // Sébastien (Inscrit Automne 2026)
        if (etudiantRepository.findByMatricule("6565656").isEmpty()) {
            try {
                InscriptionEtudiantDTO etudiantDTO = new InscriptionEtudiantDTO("Sebastien", "Castonguay", "450-716-1217", "Sebastien.Castonguay@hotmail.com", "6565656", Departement.INFORMATIQUE.name(), "NoNeedButStillNeed", "NoNeedButStillNeed");
                etudiantService.creerCompteEtudiant(etudiantDTO);
            } catch (Exception ignored) {}
        }
        ajouterInscriptionSecurisee("6565656", Saison.AUTOMNE, 2026);

        // --- SCÉNARIO DE RÉINSCRIPTION POUR JULIE ---
        // 1. S'assurer que Julie existe et possède son inscription de session passée (Hiver 2026)
        if (etudiantRepository.findByMatricule("5555555").isEmpty()) {
            Etudiant julie = Etudiant.builder()
                    .firstName("Julie")
                    .lastName("Reinscrite")
                    .phoneNumber("450-444-4444")
                    .email("julie.reinscrite@gmail.com")
                    .matricule("5555555")
                    .department(Departement.INFORMATIQUE)
                    .password(passwordEncoder.encode("NoNeedButStillNeed"))
                    .build();
            etudiantRepository.save(julie);
        }
        ajouterInscriptionSecurisee("5555555", Saison.HIVER, 2026);

        // 2. Déclencher la réinscription pour la session courante (Automne 2026)
        try {
            etudiantService.reinscrireEtudiant("julie.reinscrite@gmail.com");
            System.out.println(">>> Réinscription de Julie effectuée avec succès !");
        } catch (Exception e) {
            System.out.println("Erreur lors de la réinscription de Julie : " + e.getMessage());
        }
        // ---------------------------------------------

        // Adrien (Inscrit Automne 2026)
        if (etudiantRepository.findByMatricule("1111111").isEmpty()) {
            try {
                InscriptionEtudiantDTO etudiantDTO2 = new InscriptionEtudiantDTO("Adrien", "Agreste", "450-716-1216", emailAdrien, "1111111", Departement.INFORMATIQUE.name(), "NoNeedButStillNeed", "NoNeedButStillNeed");
                etudiantService.creerCompteEtudiant(etudiantDTO2);
            } catch (Exception ignored) {}
        }
        Etudiant etudiantAdrienObj = etudiantRepository.findByMatricule("1111111").orElseThrow();
        ajouterInscriptionSecurisee("1111111", Saison.AUTOMNE, 2026);

        // Marc (Inscrit Automne 2026)
        if (etudiantRepository.findByMatricule("9999999").isEmpty()) {
            try {
                InscriptionEtudiantDTO etudiantBadCvDTO = new InscriptionEtudiantDTO(
                        "Marc", "Disparu", "450-999-8888", "Marc.Disparu@hotmail.com",
                        "9999999", Departement.TRAVAIL_SOCIAL.name(), "NoNeed", "NoNeed"
                );
                etudiantService.creerCompteEtudiant(etudiantBadCvDTO);
            } catch (Exception ignored) {}
        }
        Etudiant etudiantBadCv = etudiantRepository.findByMatricule("9999999").orElseThrow();
        ajouterInscriptionSecurisee("9999999", Saison.AUTOMNE, 2026);

        // Christelle (Inscrite Automne 2026)
        if (etudiantRepository.findByMatricule("6565688").isEmpty()) {
            try {
                InscriptionEtudiantDTO etudiantNotifCvDTO = new InscriptionEtudiantDTO("Christelle", "Nouga", "450-716-1288", "chrisNouga@gmail.com", "6565688", Departement.INFORMATIQUE.name(), "motdepasse", "motdepasse");
                etudiantService.creerCompteEtudiant(etudiantNotifCvDTO);
            } catch (Exception ignored) {}
        }
        ajouterInscriptionSecurisee("6565688", Saison.AUTOMNE, 2026);

        if (employeurRepository.findByEmail(employeurEmail).isEmpty()) {
            try {
                InscriptionEmployeurDTO employeurDTO = new InscriptionEmployeurDTO("Roger", "Malinois", "450-874-6586", employeurEmail, "Laval", "Malinois inc", SecteurActivite.INFORMATIQUE, "NoNeedButStillNeed", "NoNeedButStillNeed");
                employeurService.creeCompteEmployeur(employeurDTO);
            } catch (Exception ignored) {}
        }

        try {
            InscriptionProfesseurDTO inscriptionProfesseurDTO = new InscriptionProfesseurDTO("Toru", "Shirazu", "Toru.Shirazu@claurendeau.qc.ca", "NoNeed", "NoNeed", "9325645", "698-585-6666", Departement.INFORMATIQUE.name());
            professeurService.creerCompteProfesseur(inscriptionProfesseurDTO);
        } catch (Exception ignored) {}

        try {
            InscriptionProfesseurDTO inscriptionProfesseurDTO2 = new InscriptionProfesseurDTO("Evie", "Frye", "Evie.Frye@claurendeau.qc.ca", "NoNeed", "NoNeed", "5486598", "698-585-6266", Departement.TRAVAIL_SOCIAL.name());
            professeurService.creerCompteProfesseur(inscriptionProfesseurDTO2);
        } catch (Exception ignored) {}

        CreationOffreDeStageDTO dto1 = new CreationOffreDeStageDTO(
                "Développeur Web Junior",
                "Développement front-end et back-end, revue de code, CI/CD.",
                22.50,
                Departement.INFORMATIQUE,
                LocalDate.now().plusDays(10),
                LocalDate.now().plusMonths(3),
                LocalDate.now().plusDays(7));

        CreationOffreDeStageDTO dto2 = new CreationOffreDeStageDTO(
                "Technicien Support Informatique",
                "Support aux utilisateurs, diagnostic matériel, installation de logiciels.",
                20.00,
                Departement.INFORMATIQUE,
                LocalDate.now().plusDays(5),
                LocalDate.now().plusMonths(2),
                LocalDate.now().plusDays(3));

        Employeur employeur = employeurRepository.findByEmail(employeurEmail).orElseThrow(EmployeurIntrouvable::new);

        for (CreationOffreDeStageDTO offreDemo : List.of(
                new CreationOffreDeStageDTO("Développeur Java", "Développement d'API Java et Spring Boot.",
                        24.0, Departement.INFORMATIQUE, LocalDate.now().plusDays(45),
                        LocalDate.now().plusMonths(4), LocalDate.now().plusDays(30)),
                new CreationOffreDeStageDTO("Développeur React", "Développement d'interfaces React et intégration d'API.",
                        23.0, Departement.INFORMATIQUE, LocalDate.now().plusDays(45),
                        LocalDate.now().plusMonths(4), LocalDate.now().plusDays(30)))) {
            boolean dejaCreee = offreDeStageRepository.findAll().stream().anyMatch(offre ->
                    offre.getTitle().equals(offreDemo.title()) && offre.getEmployeur() != null
                            && offre.getEmployeur().getId().equals(employeur.getId()));
            if (!dejaCreee) {
                MultipartFile pdf = new MockMultipartFile("file", "offre.pdf", "application/pdf",
                        pdfService.createOffreDeStagePdf(offreDemo, employeur).getInputStream());
                OffreDeStageDTO offreCreee = employeurService.creerOffre(offreDemo, pdf, employeurEmail);
                gestionnaireService.approuverOffre(offreCreee.id());
            }
        }

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

        if (offreDeStageRepository.findAll().stream().noneMatch(o -> o.getTitle().equals("Technician Reseaux"))) {
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
        }

        if (cvEtudiantRepository.findByEtudiant(etudiantBadCv).isEmpty()) {
            CvEtudiant cvEtudiant = new CvEtudiant("CV_Marc_Disparu", "application/pdf", 1024L, "none", LocalDateTime.now(), etudiantBadCv);
            cvEtudiantRepository.save(cvEtudiant);
        }

        try {
            CvEtudiantDTO cvAdrien = etudiantService.televerserCv(realCvAdrien, emailAdrien);
            gestionnaireService.approuverCurriculumVitae(cvAdrien.id());
        } catch (Exception ignored) {}

        if (devWebJr != null) {
            try { gestionnaireService.approuverOffre(devWebJr.id()); } catch (Exception ignored) {}
            try { etudiantService.postuler(devWebJr.id(), emailAdrien); } catch (Exception ignored) {}
        }
        if (techSuppInfo != null) {
            try { gestionnaireService.approuverOffre(techSuppInfo.id()); } catch (Exception ignored) {}
            try { etudiantService.postuler(techSuppInfo.id(), emailAdrien); } catch (Exception ignored) {}
        }

        // Nicolas (Inscrit Automne 2026)
        if (etudiantRepository.findByMatricule("2222222").isEmpty()) {
            try {
                InscriptionEtudiantDTO etudiantSarkozyDTO = new InscriptionEtudiantDTO(
                        "Nicolas", "Sarkozy", "450-555-5555", emailNicolas, "2222222",
                        Departement.INFORMATIQUE.name(), "a", "a"
                );
                etudiantService.creerCompteEtudiant(etudiantSarkozyDTO);
            } catch (Exception ignored) {}
        }
        Etudiant etudiantSarkoObj = etudiantRepository.findByMatricule("2222222").orElseThrow();
        ajouterInscriptionSecurisee("2222222", Saison.AUTOMNE, 2026);

        try {
            MultipartFile realCvSarko = new MockMultipartFile("file", "cv.pdf", "application/pdf", pdfService.createCvPdf(etudiantSarkoObj).getInputStream());
            CvEtudiantDTO cvNicolas = etudiantService.televerserCv(realCvSarko, emailNicolas);
            gestionnaireService.approuverCurriculumVitae(cvNicolas.id());
        } catch (Exception ignored) {}

        if (devWebJr != null) {
            try { etudiantService.postuler(devWebJr.id(), emailNicolas); } catch (Exception ignored) {}
        }

        Candidature candidatureSarko = candidatureRepository.findAll().stream()
                .filter(c -> c.getEtudiant().getEmail().equals(emailNicolas))
                .findFirst()
                .orElse(null);

        OffreDeStageDTO finalTechSuppInfo = techSuppInfo;
        Candidature candidatureAdrien = candidatureRepository.findAll().stream()
                .filter(c -> c.getEtudiant().getEmail().equals(emailAdrien) && finalTechSuppInfo != null && c.getOffreDeStage().getTitle().equals(finalTechSuppInfo.title()))
                .findAny()
                .orElse(null);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(employeurEmail, "password", Collections.emptyList())
        );

        if (candidatureSarko != null) {
            try { employeurService.retenirCandidaturePourEntrevue(candidatureSarko.getId()); } catch (Exception ignored) {}
        }
        if (candidatureAdrien != null) {
            try { employeurService.retenirCandidaturePourEntrevue(candidatureAdrien.getId()); } catch (Exception ignored) {}
        }

        if (candidatureSarko != null && entrevueRepository.findByCandidatureId(candidatureSarko.getId()).isEmpty()) {
            try {
                CreationEntrevueDTO entDTO = new CreationEntrevueDTO(
                        candidatureSarko.getId(),
                        FormatEntrevue.EN_LIGNE,
                        LocalDateTime.now().plusDays(3),
                        60
                );
                employeurService.creerConvocation(entDTO);
            } catch (Exception ignored) {}
        }

        if (candidatureAdrien != null && entrevueRepository.findByCandidatureId(candidatureAdrien.getId()).isEmpty()) {
            try {
                CreationEntrevueDTO entDTOAdrien = new CreationEntrevueDTO(
                        candidatureAdrien.getId(),
                        FormatEntrevue.EN_LIGNE,
                        LocalDateTime.now().plusDays(3),
                        60
                );
                employeurService.creerConvocation(entDTOAdrien);
            } catch (Exception ignored) {}
        }

        gestionnaireService.assignerProfesseur(professeurRepository.getProfesseurByMatricule("9325645").getId(), new AssignationProfesseurDTO(List.of(etudiantRepository.findByMatricule("2222222").get().getId())));

        SecurityContextHolder.clearContext();

        if (candidatureSarko != null) {
            try { preparerStageTrouve(candidatureSarko.getId()); } catch (Exception ignored) {}
        }
        preparerCandidaturePourEvaluation(etudiantSarkoObj, employeur);


        // Juzo (Inscrit Automne 2026)
        String emailJuzoSuzuya = "Juzo.Suzuya@gmail.com";
        if (etudiantRepository.findByMatricule("3333333").isEmpty()) {
            try {
                InscriptionEtudiantDTO etudiantJuzoDTO = new InscriptionEtudiantDTO(
                        "Juzo", "Suzuya", "450-555-5585", emailJuzoSuzuya, "3333333",
                        Departement.INFORMATIQUE.name(), "a", "a"
                );
                etudiantService.creerCompteEtudiant(etudiantJuzoDTO);
            } catch (Exception ignored) {}
        }
        ajouterInscriptionSecurisee("3333333", Saison.AUTOMNE, 2026);
        Etudiant etudiantJuzoObj = etudiantRepository.findByMatricule("3333333").orElseThrow();

        try {
            MultipartFile realCvJuzo = new MockMultipartFile("file", "cv.pdf", "application/pdf", pdfService.createCvPdf(etudiantJuzoObj).getInputStream());
            CvEtudiantDTO juzoSuzuyaCvDTO = etudiantService.televerserCv(realCvJuzo, emailJuzoSuzuya);
            gestionnaireService.approuverCurriculumVitae(juzoSuzuyaCvDTO.id());
        } catch (Exception ignored) {}

        if (techSuppInfo != null) {
            try { etudiantService.postuler(techSuppInfo.id(), emailJuzoSuzuya); } catch (Exception ignored) {}
        }

        Candidature candidatureJuzo = candidatureRepository.findAll().stream()
                .filter(c -> c.getEtudiant().getEmail().equals(emailJuzoSuzuya))
                .filter(c -> finalTechSuppInfo != null && c.getOffreDeStage().getId().equals(finalTechSuppInfo.id()))
                .findFirst()
                .orElse(null);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(employeurEmail, "password", Collections.emptyList())
        );

        if (candidatureJuzo != null) {
            try { employeurService.retenirCandidaturePourEntrevue(candidatureJuzo.getId()); } catch (Exception ignored) {}
            if (entrevueRepository.findByCandidatureId(candidatureJuzo.getId()).isEmpty()) {
                try {
                    CreationEntrevueDTO entDTOJuzo = new CreationEntrevueDTO(
                            candidatureJuzo.getId(),
                            FormatEntrevue.EN_LIGNE,
                            LocalDateTime.now().plusDays(3),
                            60
                    );
                    employeurService.creerConvocation(entDTOJuzo);
                } catch (Exception ignored) {}
            }
        }

        SecurityContextHolder.clearContext();

        if (candidatureJuzo != null) {
            try {
                preparerStageTrouve(candidatureJuzo.getId());
                etudiantService.demarrerContratStageTrouve(candidatureJuzo.getId());
            } catch (Exception ignored) {}
        }

        //Jacob Frye (Entrevue passée)
        String emailJacobFrye = "Jacob.Frye@gmail.com";
        if (etudiantRepository.findByMatricule("4444444").isEmpty()) {
            try {
                InscriptionEtudiantDTO etudiantJacobDTO = new InscriptionEtudiantDTO(
                        "Jacob",
                        "Frye",
                        "450-555-5525",
                        emailJacobFrye,
                        "4444444",
                        Departement.INFORMATIQUE.name(),
                        "a",
                        "a"
                );
                etudiantService.creerCompteEtudiant(etudiantJacobDTO);
            } catch (Exception ignored) {}
        }
        ajouterInscriptionSecurisee("4444444", Saison.AUTOMNE, 2026);
        Etudiant etudiantJacobObj = etudiantRepository.findByMatricule("4444444").orElseThrow();

        try {
            MultipartFile realCvJacob = new MockMultipartFile("file", "cv.pdf", "application/pdf", pdfService.createCvPdf(etudiantJacobObj).getInputStream());
            CvEtudiantDTO jacobFryeCvDTO = etudiantService.televerserCv(realCvJacob, emailJacobFrye);
            gestionnaireService.approuverCurriculumVitae(jacobFryeCvDTO.id());
        } catch (Exception ignored) {}

        if (devWebJr != null) {
            try { etudiantService.postuler(devWebJr.id(), emailJacobFrye); } catch (Exception ignored) {}
        }

        Candidature candidatureJacob = candidatureRepository.findAll().stream()
                .filter(c -> c.getEtudiant().getEmail().equals(emailJacobFrye))
                .findFirst()
                .orElse(null);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(employeurEmail, "password", Collections.emptyList())
        );

        if (candidatureJacob != null) {
            try {
                employeurService.retenirCandidaturePourEntrevue(candidatureJacob.getId());
            } catch (Exception ignored) {}

            try {
                candidatureJacob.setStatut(StatutCandidature.ENTREVUE_PLANIFIEE);
                candidatureRepository.save(candidatureJacob);
            } catch (Exception ignored) {}

            if (entrevueRepository.findByCandidatureId(candidatureJacob.getId()).isEmpty()) {
                try {
                    LocalDateTime dateEntrevueJacob = LocalDate.now().minusDays(2).atTime(14, 0);
                    Entrevue entrevueJacob = Entrevue.builder()
                            .candidature(candidatureJacob)
                            .format(FormatEntrevue.EN_LIGNE)
                            .dateHeureEntrevue(dateEntrevueJacob)
                            .duree(30)
                            .dateCreation(dateEntrevueJacob.minusDays(2))
                            .build();

                    entrevueJacob.setStatut(StatutEntrevue.CONFIRMEE);
                    entrevueJacob.setDateConfirmation(dateEntrevueJacob.minusDays(1));
                    entrevueJacob.setDateReponse(dateEntrevueJacob.minusDays(1));

                    entrevueRepository.save(entrevueJacob);
                } catch (Exception ignored) {}
            }
        }

        SecurityContextHolder.clearContext();

        if (candidatureJacob != null) {
            try {
                etudiantService.actualiserStatutEtudiant(candidatureJacob.getEtudiant());
            } catch (Exception ignored) {}
        }

        //Sephiroth Frye (Entrevue passée)
        String emailSephirothCrescent = "Sephiroth.Crescent@gmail.com";
        String matriculeSephiroth = "1010101";
        if (etudiantRepository.findByMatricule(matriculeSephiroth).isEmpty()) {
            try {
                InscriptionEtudiantDTO etudiantSephirothDTO = new InscriptionEtudiantDTO(
                        "Sephiroth",
                        "Crescent",
                        "450-542-9658",
                        emailSephirothCrescent,
                        matriculeSephiroth,
                        Departement.INFORMATIQUE.name(),
                        "a",
                        "a"
                );
                etudiantService.creerCompteEtudiant(etudiantSephirothDTO);
            } catch (Exception ignored) {}
        }
        ajouterInscriptionSecurisee(matriculeSephiroth, Saison.AUTOMNE, 2026);
        Etudiant etudiantSephirothObj = etudiantRepository.findByMatricule(matriculeSephiroth).orElseThrow();

        try {
            MultipartFile realCvSephiroth = new MockMultipartFile("file", "cv.pdf", "application/pdf", pdfService.createCvPdf(etudiantSephirothObj).getInputStream());
            CvEtudiantDTO SephirothCvDTO = etudiantService.televerserCv(realCvSephiroth, emailSephirothCrescent);
            gestionnaireService.approuverCurriculumVitae(SephirothCvDTO.id());
        } catch (Exception ignored) {}

        if (devWebJr != null) {
            try { etudiantService.postuler(devWebJr.id(), emailSephirothCrescent); } catch (Exception ignored) {}
        }

        Candidature candidatureSephiroth = candidatureRepository.findAll().stream()
                .filter(c -> c.getEtudiant().getEmail().equals(emailSephirothCrescent))
                .findFirst()
                .orElse(null);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(employeurEmail, "password", Collections.emptyList())
        );

        if (candidatureSephiroth != null) {
            try {
                employeurService.retenirCandidaturePourEntrevue(candidatureSephiroth.getId());
            } catch (Exception ignored) {}

            try {
                candidatureSephiroth.setStatut(StatutCandidature.ENTREVUE_PLANIFIEE);
                candidatureRepository.save(candidatureSephiroth);
            } catch (Exception ignored) {}

            if (entrevueRepository.findByCandidatureId(candidatureSephiroth.getId()).isEmpty()) {
                try {
                    LocalDateTime dateEntrevueSephiroth = LocalDate.now().minusDays(2).atTime(14, 0);
                    Entrevue entrevueSephiroth = Entrevue.builder()
                            .candidature(candidatureSephiroth)
                            .format(FormatEntrevue.EN_LIGNE)
                            .dateHeureEntrevue(dateEntrevueSephiroth)
                            .duree(30)
                            .dateCreation(dateEntrevueSephiroth.minusDays(2))
                            .build();

                    entrevueSephiroth.setStatut(StatutEntrevue.CONFIRMEE);
                    entrevueSephiroth.setDateConfirmation(dateEntrevueSephiroth.minusDays(1));
                    entrevueSephiroth.setDateReponse(dateEntrevueSephiroth.minusDays(1));

                    entrevueRepository.save(entrevueSephiroth);
                } catch (Exception ignored) {}
            }
        }

        SecurityContextHolder.clearContext();

        if (candidatureSephiroth != null) {
            try {
                etudiantService.actualiserStatutEtudiant(candidatureSephiroth.getEtudiant());
            } catch (Exception ignored) {}
        }
    }

    private void preparerStageTrouve(Long candidatureId) {
        Candidature candidature = candidatureRepository.findById(candidatureId).orElseThrow();
        Etudiant etudiant = etudiantRepository.findById(candidature.getEtudiant().getId()).orElseThrow();

        candidature.setStatut(StatutCandidature.EN_ATTENTE_DE_SIGNATURE);
        candidatureRepository.save(candidature);
        etudiant.setStatut(StatutEtudiant.EN_ATTENTE_DE_SIGNATURE);
        etudiant.setCandidatureStage(candidature);
        etudiantRepository.save(etudiant);

        Entrevue entrevue = entrevueRepository.findByCandidatureId(candidatureId).orElseGet(() -> null);
        if (entrevue != null) {
            entrevue.setDateHeureEntrevue(LocalDateTime.now().minusDays(1));
            entrevue.setStatut(StatutEntrevue.TERMINEE);
            entrevueRepository.save(entrevue);
        }
    }

    private void preparerCandidaturePourEvaluation(Etudiant etudiant, Employeur employeur) {
        LocalDate aujourdHui = LocalDate.now();
        String titreOffre = "Soutien Informatique";
        OffreDeStage offre = offreDeStageRepository.findAll().stream()
                .filter(o -> titreOffre.equals(o.getTitle()))
                .findFirst()
                .orElseGet(() -> OffreDeStage.builder()
                        .title(titreOffre)
                        .description("Offre de démonstration pour tester la notification d'évaluation à deux semaines de la fin du stage.")
                        .salary(20.0)
                        .domain(Departement.INFORMATIQUE)
                        .startDate(aujourdHui.minusMonths(3))
                        .endDate(aujourdHui.plusDays(14))
                        .displayEndDate(aujourdHui.minusDays(1))
                        .statut(Statut.ACCEPTEE)
                        .fileName("offre_test_evaluation.pdf")
                        .storagePath("uploads/offres")
                        .employeur(employeur)
                        .rejectionComment("")
                        .build());
        offre.setStartDate(aujourdHui.minusMonths(3));
        offre.setEndDate(aujourdHui.plusDays(14));
        offre.setDisplayEndDate(aujourdHui.minusDays(1));
        offre.setStatut(Statut.ACCEPTEE);
        offre.setEmployeur(employeur);
        offre = offreDeStageRepository.save(offre);

        OffreDeStage offreFinale = offre;
        Candidature candidature = candidatureRepository.findByOffreDeStage(offreFinale).stream()
                .filter(c -> c.getEtudiant().getId().equals(etudiant.getId()))
                .findFirst()
                .orElseGet(() -> Candidature.builder()
                        .etudiant(etudiant)
                        .offreDeStage(offreFinale)
                        .dateCandidature(LocalDateTime.now().minusMonths(3))
                        .build());
        candidature.setStatut(StatutCandidature.CONTRAT_CREE);
        candidature = candidatureRepository.save(candidature);

        etudiant.setStatut(StatutEtudiant.EN_ATTENTE_DE_SIGNATURE);
        etudiant.setCandidatureStage(candidature);
        etudiantRepository.save(etudiant);

        Gestionnaire gestionnaire = gestionnaireRepository.findAll().stream().findFirst().orElse(null);
        if (gestionnaire == null) return;

        Candidature candidaturePourContrat = candidature;
        Contrat contrat = contratRepository.findByCandidatureId(candidaturePourContrat.getId())
                .orElseGet(() -> Contrat.builder()
                        .candidature(candidaturePourContrat)
                        .gestionnaire(gestionnaire)
                        .build());
        LocalDateTime dateSignature = LocalDateTime.now().minusDays(2);
        if (contrat.getDateSignatureEtudiant() == null) contrat.setDateSignatureEtudiant(dateSignature);
        if (contrat.getDateSignatureEmployeur() == null) contrat.setDateSignatureEmployeur(dateSignature);
        if (contrat.getDateSignatureGestionnaire() == null) contrat.setDateSignatureGestionnaire(dateSignature);
        contratRepository.save(contrat);
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