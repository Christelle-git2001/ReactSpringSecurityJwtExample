package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.model.Gestionnaire;
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

    public DevAuthController(JwtTokenProvider jwtTokenProvider, GestionnaireRepository gestionnaireRepository){
        this.jwtTokenProvider = jwtTokenProvider;
        this.gestionnaireRepository = gestionnaireRepository;
    }

    @PostMapping("/login/gestionnaire")
    public ResponseEntity<String> devLoginStudent() throws Exception {
        Gestionnaire gestionnaire = gestionnaireRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new Exception("Aucun gestionnaire trouvé en BDD"));

        Authentication auth = new UsernamePasswordAuthenticationToken(
                gestionnaire.getEmail(),
                null,
                gestionnaire.getAuthorities()
        );

        String token = jwtTokenProvider.generateToken(auth);

        return ResponseEntity.ok(token);
    }
}
