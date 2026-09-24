package com.lacouf.rsbjwt;

import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.repository.EtudiantRepository;
import com.lacouf.rsbjwt.repository.GestionnaireRepository;
//import com.lacouf.rsbjwt.repository.ProfesseurRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.service.EtudiantService;
import com.lacouf.rsbjwt.service.dto.InscriptionEtudiantDTO;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

@SpringBootApplication
public class ReactSpringSecurityJwtApplication implements CommandLineRunner {
    // TODO Remplacer les classe biblio par les classe appropriee
    private final GestionnaireRepository gestionnaireRepository;
    private final EtudiantRepository etudiantRepository;
   // private final ProfesseurRepository professeurRepository;
    private final UserAppRepository userAppRepository;

    private final PasswordEncoder passwordEncoder;
    private final EtudiantService etudiantService;

    public ReactSpringSecurityJwtApplication(GestionnaireRepository gestionnaireRepository, EtudiantRepository etudiantRepository, UserAppRepository userAppRepository, PasswordEncoder passwordEncoder, EtudiantService etudiantService) {
        this.gestionnaireRepository = gestionnaireRepository;
        this.etudiantRepository = etudiantRepository;
       // this.professeurRepository = professeurRepository;
        this.userAppRepository = userAppRepository;
        this.passwordEncoder = passwordEncoder;
        this.etudiantService = etudiantService;
    }

    public static void main(String[] args) {
        SpringApplication.run(ReactSpringSecurityJwtApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        InscriptionEtudiantDTO inscriptionEtudiantDTO = new InscriptionEtudiantDTO("Jeremy","Parkour", "450-659-2541", "HAHA@hotmail.com", "1886454", Departement.INFORMATIQUE.name(), "BONJOUR","BONJOUR");
        etudiantService.creerCompteEtudiant(inscriptionEtudiantDTO);
        /*  gestionnaireRepository.save(
                Gestionnaire.builder()
                        .firstName("Gerard")
                        .lastName("Biblio")
                        .email("l@l.com")
                        .password(passwordEncoder.encode("bib"))
                        .matricule("0000001")
                        .phoneNumber("123-456-7890")
                        .build()
        );*/
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