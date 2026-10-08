package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.Exception.AucunGestionnaireTrouver;
import com.lacouf.rsbjwt.Exception.EmployeurIntrouvable;
import com.lacouf.rsbjwt.Exception.EtudiantIntrouvableException;
import com.lacouf.rsbjwt.model.Employeur;
import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.Enum.Statut;
import com.lacouf.rsbjwt.model.Etudiant;
import com.lacouf.rsbjwt.model.Gestionnaire;
import com.lacouf.rsbjwt.model.OffreDeStage;
import com.lacouf.rsbjwt.repository.EmployeurRepository;
import com.lacouf.rsbjwt.repository.EtudiantRepository;
import com.lacouf.rsbjwt.repository.GestionnaireRepository;
import com.lacouf.rsbjwt.repository.OffreDeStageRepository;
import com.lacouf.rsbjwt.security.JwtTokenProvider;
import com.lacouf.rsbjwt.service.EmployeurService;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
@Profile("dev")
public class DevAuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final GestionnaireRepository gestionnaireRepository;
    private final EtudiantRepository etudiantRepository;
    private final EmployeurRepository employeurRepository;
    private final EmployeurService employeurService;
    private final OffreDeStageRepository offreDeStageRepository;

    public DevAuthController(JwtTokenProvider jwtTokenProvider, GestionnaireRepository gestionnaireRepository, EtudiantRepository etudiantRepository, EmployeurRepository employeurRepository, EmployeurService employeurService, OffreDeStageRepository offreDeStageRepository){
        this.jwtTokenProvider = jwtTokenProvider;
        this.gestionnaireRepository = gestionnaireRepository;
        this.etudiantRepository = etudiantRepository;
        this.employeurRepository = employeurRepository;
        this.employeurService = employeurService;
        this.offreDeStageRepository = offreDeStageRepository;
    }

    @PostMapping("/login/gestionnaire")
    public ResponseEntity<String> devLoginGestionnaire() throws Exception {
        Gestionnaire gestionnaire = gestionnaireRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(AucunGestionnaireTrouver::new);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                gestionnaire.getEmail(),
                null,
                gestionnaire.getAuthorities()
        );

        String token = jwtTokenProvider.generateToken(auth);

        return ResponseEntity.ok(token);
    }

    @PostMapping("/login/etudiant")
    public ResponseEntity<String> devLoginEtudiant() throws Exception {
        Etudiant etudiant = etudiantRepository.findByMatricule("6565656")
                .stream()
                .findFirst()
                .orElseThrow(EtudiantIntrouvableException::new);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                etudiant.getEmail(),
                null,
                etudiant.getAuthorities()
        );

        String token = jwtTokenProvider.generateToken(auth);

        return ResponseEntity.ok(token);
    }

    @PostMapping("/login/employeur")
    public ResponseEntity<String> devLoginEmployeur() throws Exception {
        Employeur employeur = employeurRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(EmployeurIntrouvable::new);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                employeur.getEmail(),
                null,
                employeur.getAuthorities()
        );

        String token = jwtTokenProvider.generateToken(auth);

        return ResponseEntity.ok(token);
    }

    @PostMapping("/create/offre")
    public ResponseEntity<HttpStatus> devCreeOffre() throws EmployeurIntrouvable {
        Employeur employeur = employeurRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(EmployeurIntrouvable::new);
        OffreDeStage offreDeStage = OffreDeStage.builder()
                .title("OffreDeStageGenerer")
                .description("Ce document est généré automatiquement")
                .salary(8.5)
                .domain(Departement.INFORMATIQUE)
                .startDate(LocalDate.now())
                .endDate(LocalDate.of(2026,12,25))
                .statut(Statut.EN_ATTENTE)
                .employeur(employeur)
                .storagePath("uploads/offres/bff2f672-69d6-4959-8356-6babf160a15f.pdf")
                .fileName("Malinois_Inc_offer.pdf")
                .build();
        offreDeStageRepository.save(offreDeStage);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
