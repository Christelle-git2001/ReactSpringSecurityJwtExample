package com.lacouf.rsbjwt;

import com.lacouf.rsbjwt.Exception.DateFinAffichageApresDateDebutException;
import com.lacouf.rsbjwt.Exception.EmployeurIntrouvable;
import com.lacouf.rsbjwt.model.CvEtudiant;
import com.lacouf.rsbjwt.model.Employeur;
import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.Enum.SecteurActivite;
import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.model.Etudiant;
import com.lacouf.rsbjwt.model.OffreDeStage;
import com.lacouf.rsbjwt.repository.*;
//import com.lacouf.rsbjwt.repository.ProfesseurRepository;
import com.lacouf.rsbjwt.service.EmployeurService;
import com.lacouf.rsbjwt.service.EtudiantService;
import com.lacouf.rsbjwt.service.GestionnaireService;
import com.lacouf.rsbjwt.service.dto.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;



import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

@SpringBootApplication
public class ReactSpringSecurityJwtApplication implements CommandLineRunner {
    // TODO Remplacer les classe biblio par les classe appropriee
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

    public ReactSpringSecurityJwtApplication(GestionnaireRepository gestionnaireRepository, EtudiantRepository etudiantRepository,
                                             UserAppRepository userAppRepository,  Environment environment, EtudiantService etudiantService,
                                             OffreDeStageRepository offreDeStageRepository, EmployeurService employeurService,
                                             EmployeurRepository employeurRepository, GestionnaireService gestionnaireService, CvEtudiantRepository cvEtudiantRepository) {
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
    }

    public static void main(String[] args) {
        SpringApplication.run(ReactSpringSecurityJwtApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (Arrays.asList(environment.getActiveProfiles()).contains("test")) {
            return;
        }

        GestionnaireDTO gestionnaireDto = new GestionnaireDTO(-8,"Pascal","Belmont","Pascal.Belmont@hotmail.com","565-985-5858","NoNeedButStillNeed", LocalDateTime.now());
        gestionnaireService.creerCompteGestionnaire(gestionnaireDto);

        InscriptionEtudiantDTO etudiantDTO = new InscriptionEtudiantDTO( "Sebastien","Castonguay","450-716-1217","Sebastien.Castonguay@hotmail.com","6565656",Departement.INFORMATIQUE.name(),"NoNeedButStillNeed","NoNeedButStillNeed");
        etudiantService.creerCompteEtudiant(etudiantDTO);

        InscriptionEtudiantDTO etudiantBadCvDTO = new InscriptionEtudiantDTO(
                "Marc", "Disparu", "450-999-8888", "Marc.Disparu@hotmail.com",
                "9999999", Departement.INFORMATIQUE.name(), "NoNeed", "NoNeed"
        );
        etudiantService.creerCompteEtudiant(etudiantBadCvDTO);
        Etudiant etudiantBadCv = etudiantRepository.findByMatricule("9999999").orElseThrow();

        InscriptionEmployeurDTO employeurDTO = new InscriptionEmployeurDTO("Roger","Malinois","450-874-6586","Roger.Malinois@hotmail.com","Laval","Malinois inc",SecteurActivite.INFORMATIQUE,"NoNeedButStillNeed","NoNeedButStillNeed");
        employeurService.creeCompteEmployeur(employeurDTO);

        String minimalPdf = "%PDF-1.4\n" +
                "1 0 obj << /Type /Catalog /Pages 2 0 R >> endobj\n" +
                "2 0 obj << /Type /Pages /Kids [3 0 R] /Count 1 >> endobj\n" +
                "3 0 obj << /Type /Page /Parent 2 0 R /MediaBox [0 0 300 144] >> endobj\n" +
                "xref\n" +
                "0 4\n" +
                "0000000000 65535 f \n" +
                "0000000010 00000 n \n" +
                "0000000053 00000 n \n" +
                "0000000100 00000 n \n" +
                "trailer << /Size 4 /Root 1 0 R >>\n" +
                "startxref\n" +
                "147\n" +
                "%%EOF";

        MultipartFile fakePdf = new MockMultipartFile(
                "file",
                "offre.pdf",
                "application/pdf",
                minimalPdf.getBytes()
        );

        String employeurEmail = "Roger.Malinois@hotmail.com";

        CreationOffreDeStageDTO dto1 = new CreationOffreDeStageDTO(
                "Développeur Web Junior",
                "Développement front-end et back-end, revue de code, CI/CD.",
                22.50,
                Departement.INFORMATIQUE,
                LocalDate.now().plusDays(10),   // Début dans 10 jours
                LocalDate.now().plusMonths(3),  // Fin dans 3 mois
                LocalDate.now().plusDays(7)     // Affichage jusqu'à 3 jours avant le début
        );

        try {
            employeurService.creerOffre(dto1, fakePdf, employeurEmail);
        } catch (DateFinAffichageApresDateDebutException e) {
            System.out.println(e.getMessage());
        }

        CreationOffreDeStageDTO dto2 = new CreationOffreDeStageDTO(
                "Technicien Support Informatique",
                "Support aux utilisateurs, diagnostic matériel, installation de logiciels.",
                20.00,
                Departement.INFORMATIQUE,
                LocalDate.now().plusDays(5),    // Début dans 5 jours
                LocalDate.now().plusMonths(2),  // Fin dans 2 mois
                LocalDate.now().plusDays(3)     // Affichage jusqu'à 2 jours avant le début
        );

        try {
            employeurService.creerOffre(dto2, fakePdf, employeurEmail);
        } catch (DateFinAffichageApresDateDebutException e) {
            System.out.println(e.getMessage());
        }

        Employeur employeur = employeurRepository.findByEmail("Roger.Malinois@hotmail.com").orElseThrow(EmployeurIntrouvable::new);
        OffreDeStage offreDeStage = new OffreDeStage("Technician Reseaux",
                "Support aux utilisateurs, diagnostic matériel, installation de logiciels.",
                20.0,
                Departement.INFORMATIQUE,
                LocalDate.now().plusDays(5),    // Début dans 5 jours
                LocalDate.now().plusMonths(2),  // Fin dans 2 mois
                LocalDate.now().minusDays(2),    // Affichage -2 jour
                Statut.ACCEPTEE,
                 "offre.pdf",
                "uploads/offres",
                employeur,
                "",
                LocalDateTime.now()
        );


       offreDeStageRepository.save(offreDeStage);

       CvEtudiant cvEtudiant = new CvEtudiant("CV_Marc_Disparu","application/pdf",1024L,"none",LocalDateTime.now(),etudiantBadCv);

       cvEtudiantRepository.save(cvEtudiant);

    }
}