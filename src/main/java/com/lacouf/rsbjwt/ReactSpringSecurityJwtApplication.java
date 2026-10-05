package com.lacouf.rsbjwt;

import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.Enum.SecteurActivite;
import com.lacouf.rsbjwt.repository.*;
//import com.lacouf.rsbjwt.repository.ProfesseurRepository;
import com.lacouf.rsbjwt.service.EmployeurService;
import com.lacouf.rsbjwt.service.EtudiantService;
import com.lacouf.rsbjwt.service.GestionnaireService;
import com.lacouf.rsbjwt.service.dto.CreationOffreDeStageDTO;
import com.lacouf.rsbjwt.service.dto.GestionnaireDto;
import com.lacouf.rsbjwt.service.dto.InscriptionEmployeurDTO;
import com.lacouf.rsbjwt.service.dto.InscriptionEtudiantDTO;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@SpringBootApplication
public class ReactSpringSecurityJwtApplication implements CommandLineRunner {
    // TODO Remplacer les classe biblio par les classe appropriee
    private final GestionnaireRepository gestionnaireRepository;
    private final EtudiantRepository etudiantRepository;
   // private final ProfesseurRepository professeurRepository;
    private final UserAppRepository userAppRepository;

    private final PasswordEncoder passwordEncoder;
    private final EtudiantService etudiantService;
    private final OffreDeStageRepository offreDeStageRepository;
    private final EmployeurRepository employeurRepository;
    private final EmployeurService employeurService;
    private final GestionnaireService gestionnaireService;

    public ReactSpringSecurityJwtApplication(GestionnaireRepository gestionnaireRepository, EtudiantRepository etudiantRepository,
                                             UserAppRepository userAppRepository, PasswordEncoder passwordEncoder, EtudiantService etudiantService,
                                             OffreDeStageRepository offreDeStageRepository, EmployeurService employeurService,
                                             EmployeurRepository employeurRepository, GestionnaireService gestionnaireService) {
        this.gestionnaireRepository = gestionnaireRepository;
        this.etudiantRepository = etudiantRepository;
       // this.professeurRepository = professeurRepository;
        this.userAppRepository = userAppRepository;
        this.passwordEncoder = passwordEncoder;
        this.etudiantService = etudiantService;
        this.employeurService = employeurService;
        this.offreDeStageRepository = offreDeStageRepository;
        this.employeurRepository = employeurRepository;
        this.gestionnaireService = gestionnaireService;
    }

    public static void main(String[] args) {
        SpringApplication.run(ReactSpringSecurityJwtApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
//        InscriptionEtudiantDTO inscriptionEtudiantDTO = new InscriptionEtudiantDTO("Jeremy","Parkour", "450-659-2541", "HAHA@hotmail.com", "1886454", Departement.INFORMATIQUE.name(), "BONJOUR","BONJOUR");
//        etudiantService.creerCompteEtudiant(inscriptionEtudiantDTO);
//       CreationOffreDeStageDTO creationOffreDeStageDTO = new CreationOffreDeStageDTO("Infirmerie","préposé",19.25,Departement.INFORMATIQUE,LocalDate.of(2026,10,1),LocalDate.of(2026,10,15));

       //InscriptionEmployeurDTO inscriptionEmployeurDTO2 = new InscriptionEmployeurDTO("Jean","Dupont","444-555-6666","sisi@gmail.com","Clinique Test",SecteurActivite.FINANCE.name(),SecteurActivite.AEROSPATIAL,"Test1234!","Test1234!");
       //employeurService.creeCompteEmployeur(inscriptionEmployeurDTO2);

        GestionnaireDto gestionnaireDto = new GestionnaireDto(-8,"Pascal","Belmont","Pascal.Belmont@hotmail.com","565-985-5858","NoNeedButStillNeed");
        gestionnaireService.creerCompteGestionnaire(gestionnaireDto);
        System.out.println(userAppRepository.findUserAppByEmail("Pascal.Belmont@hotmail.com"));

/*        Employeur employeurTest = employeurRepository.save(
                Employeur.builder()
                        .firstName("Jean")
                        .lastName("Dupont")
                        .email("employeur@test.com")
                        .password(passwordEncoder.encode("Test1234!"))
                        .phone("450-000-0000")
                        .town("Montréal")
                        .businessName("Clinique Test")
                        .businessSector(SecteurActivite.FINANCE)
                        .build()
        );
*/
        //employeurService.creerOffre(creationOffreDeStageDTO,null,employeurTest.getEmail());
        /*
        emprunteurRepository.save(
                Emprunteur.builder()
                        .firstName("Isidor")
                        .lastName("Teurteur")
                        .email("ll@l.com")
                        .password(passwordEncoder.encode("bib"))
                        .since(LocalDate.of(2020, 10,20))
                        .build()
        );
        preposeRepository.save(
                Prepose.builder()
                        .firstName("Chandeuse")
                        .lastName("Lixor")
                        .email("lll@l.com")
                        .password(passwordEncoder.encode("bib"))
                        .passeKey("12345")
                        .build()
        );
        final Optional<UserApp> userAppByEmail = userAppRepository.findUserAppByEmail("l@l.com");
        userAppByEmail.ifPresent(userApp -> System.out.println("user " + userAppByEmail));*/

        //}
}
}