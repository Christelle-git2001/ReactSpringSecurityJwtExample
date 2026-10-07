package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.Exception.AucunGestionnaireTrouver;
import com.lacouf.rsbjwt.Exception.EmployeurIntrouvable;
import com.lacouf.rsbjwt.Exception.EtudiantIntrouvableException;
import com.lacouf.rsbjwt.model.Employeur;
import com.lacouf.rsbjwt.model.Etudiant;
import com.lacouf.rsbjwt.model.Gestionnaire;
import com.lacouf.rsbjwt.repository.EmployeurRepository;
import com.lacouf.rsbjwt.repository.EtudiantRepository;
import com.lacouf.rsbjwt.repository.GestionnaireRepository;
import com.lacouf.rsbjwt.security.JwtTokenProvider;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
@Profile("dev")
public class DevAuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final GestionnaireRepository gestionnaireRepository;
    private final EtudiantRepository etudiantRepository;
    private final EmployeurRepository employeurRepository;

    public DevAuthController(JwtTokenProvider jwtTokenProvider, GestionnaireRepository gestionnaireRepository, EtudiantRepository etudiantRepository, EmployeurRepository employeurRepository){
        this.jwtTokenProvider = jwtTokenProvider;
        this.gestionnaireRepository = gestionnaireRepository;
        this.etudiantRepository = etudiantRepository;
        this.employeurRepository = employeurRepository;
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
}
