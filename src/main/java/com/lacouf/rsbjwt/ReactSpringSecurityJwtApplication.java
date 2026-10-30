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
import com.lacouf.rsbjwt.service.EmployeurService;
import com.lacouf.rsbjwt.service.EtudiantService;
import com.lacouf.rsbjwt.service.GestionnaireService;
import com.lacouf.rsbjwt.service.ProfesseurService;
import com.lacouf.rsbjwt.service.dto.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
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
    private final ProfesseurService professeurService;

    public ReactSpringSecurityJwtApplication(GestionnaireRepository gestionnaireRepository, EtudiantRepository etudiantRepository,
                                             UserAppRepository userAppRepository, Environment environment, EtudiantService etudiantService,
                                             OffreDeStageRepository offreDeStageRepository, EmployeurService employeurService,
                                             EmployeurRepository employeurRepository, GestionnaireService gestionnaireService, CvEtudiantRepository cvEtudiantRepository, ProfesseurService professeurService) {
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
    }

    public static void main(String[] args) {
        SpringApplication.run(ReactSpringSecurityJwtApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        if (Arrays.asList(environment.getActiveProfiles()).contains("test")) {
            return;
        }

        String emailAdrien = "Adrien@gmail.com" ;

        GestionnaireDTO gestionnaireDto = new GestionnaireDTO(-8,"Pascal","Belmont","Pascal.Belmont@hotmail.com","565-985-5858","NoNeedButStillNeed", LocalDateTime.now());
        gestionnaireService.creerCompteGestionnaire(gestionnaireDto);

        InscriptionEtudiantDTO etudiantDTO = new InscriptionEtudiantDTO( "Sebastien","Castonguay","450-716-1217","Sebastien.Castonguay@hotmail.com","6565656",Departement.INFORMATIQUE.name(),"NoNeedButStillNeed","NoNeedButStillNeed");
        etudiantService.creerCompteEtudiant(etudiantDTO);

        InscriptionEtudiantDTO etudiantDTO2 = new InscriptionEtudiantDTO( "Adrien","Agreste","450-716-1216",emailAdrien,"1111111",Departement.INFORMATIQUE.name(),"NoNeedButStillNeed","NoNeedButStillNeed");
        etudiantService.creerCompteEtudiant(etudiantDTO2);

        InscriptionEtudiantDTO etudiantBadCvDTO = new InscriptionEtudiantDTO(
                "Marc", "Disparu", "450-999-8888", "Marc.Disparu@hotmail.com",
                "9999999", Departement.INFORMATIQUE.name(), "NoNeed", "NoNeed"
        );
        etudiantService.creerCompteEtudiant(etudiantBadCvDTO);
        Etudiant etudiantBadCv = etudiantRepository.findByMatricule("9999999").orElseThrow();

        InscriptionEtudiantDTO etudiantNotifCvDTO = new InscriptionEtudiantDTO( "Christelle","Nouga","450-716-1288","chrisNouga@gmail.com","6565688",Departement.INFORMATIQUE.name(),"motdepasse","motdepasse");
        etudiantService.creerCompteEtudiant(etudiantNotifCvDTO);

        InscriptionEmployeurDTO employeurDTO = new InscriptionEmployeurDTO("Roger","Malinois","450-874-6586","Roger.Malinois@hotmail.com","Laval","Malinois inc",SecteurActivite.INFORMATIQUE,"NoNeedButStillNeed","NoNeedButStillNeed");
        employeurService.creeCompteEmployeur(employeurDTO);

        InscriptionProfesseurDTO inscriptionProfesseurDTO = new InscriptionProfesseurDTO("Toru","Shirazu","Toru.Shirazu@claurendeau.qc.ca","NoNeed","NoNeed","9325645","698-585-6666",Departement.INFORMATIQUE.name());
        InscriptionProfesseurDTO inscriptionProfesseurDTO2 = new InscriptionProfesseurDTO("Evie","Frye","Evie.Frye@claurendeau.qc.ca","NoNeed","NoNeed","5486598","698-585-6266",Departement.TRAVAIL_SOCIAL.name());

        professeurService.creerCompteProfesseur(inscriptionProfesseurDTO);
        professeurService.creerCompteProfesseur(inscriptionProfesseurDTO2);

        String minimalCV = "%PDF-1.4\n" +
                "1 0 obj << /Type /Catalog /Pages 2 0 R >> endobj\n" +
                "2 0 obj << /Type /Pages /Kids [3 0 R] /Count 1 >> endobj\n" +
                "3 0 obj << /Type /Page /Parent 2 0 R /MediaBox [0 0 300 300] " +
                "/Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >> endobj\n" +
                "4 0 obj << /Type /Font /Subtype /Type1 /BaseFont /Helvetica >> endobj\n" +
                "5 0 obj << /Length 200 >> stream\n" +
                "BT /F1 14 Tf 20 260 Td (Adrien Agreste - Dev Java) Tj ET\n" +
                "BT /F1 12 Tf 20 230 Td (Competences: Java, SQL, Git) Tj ET\n" +
                "BT /F1 12 Tf 20 200 Td (Experience: Refonte UI, Debugging) Tj ET\n" +
                "BT /F1 12 Tf 20 170 Td (Education: Tech. Info Montreal) Tj ET\n" +
                "endstream\n" +
                "endobj\n" +
                "xref\n" +
                "0 6\n" +
                "0000000000 65535 f \n" +
                "0000000010 00000 n \n" +
                "0000000053 00000 n \n" +
                "0000000100 00000 n \n" +
                "0000000200 00000 n \n" +
                "0000000260 00000 n \n" +
                "trailer << /Size 6 /Root 1 0 R >>\n" +
                "startxref\n" +
                "330\n" +
                "%%EOF";

        String minimalOffre = "%PDF-1.4\n" +
                "1 0 obj << /Type /Catalog /Pages 2 0 R >> endobj\n" +
                "2 0 obj << /Type /Pages /Kids [3 0 R] /Count 1 >> endobj\n" +
                "3 0 obj << /Type /Page /Parent 2 0 R /MediaBox [0 0 300 300] " +
                "/Resources << /Font << /F1 4 0 R >> >> /Contents 5 0 R >> endobj\n" +
                "4 0 obj << /Type /Font /Subtype /Type1 /BaseFont /Helvetica >> endobj\n" +
                "5 0 obj << /Length 220 >> stream\n" +
                "BT /F1 14 Tf 20 260 Td (Offre de stage - Dev Java) Tj ET\n" +
                "BT /F1 12 Tf 20 230 Td (Entreprise: Malinois inc) Tj ET\n" +
                "BT /F1 12 Tf 20 200 Td (Taches: Refonte UI, Debugging, Seed files) Tj ET\n" +
                "BT /F1 12 Tf 20 170 Td (Debut: Janvier 2027 - Duree: 12 semaines) Tj ET\n" +
                "endstream\n" +
                "endobj\n" +
                "xref\n" +
                "0 6\n" +
                "0000000000 65535 f \n" +
                "0000000010 00000 n \n" +
                "0000000053 00000 n \n" +
                "0000000100 00000 n \n" +
                "0000000200 00000 n \n" +
                "0000000265 00000 n \n" +
                "trailer << /Size 6 /Root 1 0 R >>\n" +
                "startxref\n" +
                "330\n" +
                "%%EOF";



        MultipartFile fakeOffre = new MockMultipartFile(
                "file",
                "offre.pdf",
                "application/pdf",
                minimalOffre.getBytes()
        );

        MultipartFile fakeCV = new MockMultipartFile(
                "file",
                "cv.pdf",
                "application/pdf",
                minimalCV.getBytes()
        );

        String employeurEmail = "Roger.Malinois@hotmail.com";

        OffreDeStageDTO devWebJr = null ;

        CreationOffreDeStageDTO dto1 = new CreationOffreDeStageDTO(
                "Développeur Web Junior",
                "Développement front-end et back-end, revue de code, CI/CD.",
                22.50,
                Departement.INFORMATIQUE,
                LocalDate.now().plusDays(10),
                LocalDate.now().plusMonths(3),
                LocalDate.now().plusDays(7)
        );

        try {
           devWebJr = employeurService.creerOffre(dto1, fakeOffre, employeurEmail);
        } catch (DateFinAffichageApresDateDebutException e) {
            System.out.println(e.getMessage());
        }

        CreationOffreDeStageDTO dto2 = new CreationOffreDeStageDTO(
                "Technicien Support Informatique",
                "Support aux utilisateurs, diagnostic matériel, installation de logiciels.",
                20.00,
                Departement.INFORMATIQUE,
                LocalDate.now().plusDays(5),
                LocalDate.now().plusMonths(2),
                LocalDate.now().plusDays(3)
        );

        try {
            employeurService.creerOffre(dto2, fakeOffre, employeurEmail);
        } catch (DateFinAffichageApresDateDebutException e) {
            System.out.println(e.getMessage());
        }

        Employeur employeur = employeurRepository.findByEmail("Roger.Malinois@hotmail.com").orElseThrow(EmployeurIntrouvable::new);
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

       CvEtudiant cvEtudiant = new CvEtudiant("CV_Marc_Disparu","application/pdf",1024L,"none",LocalDateTime.now(),etudiantBadCv);


       CvEtudiantDTO cvAdrien = etudiantService.televerserCv(fakeCV, "Adrien@gmail.com");
       gestionnaireService.approuverCurriculumVitae(cvAdrien.id());
       gestionnaireService.approuverOffre(devWebJr.id());

       etudiantService.postuler(devWebJr.id(),emailAdrien);
       cvEtudiantRepository.save(cvEtudiant);

    }
}