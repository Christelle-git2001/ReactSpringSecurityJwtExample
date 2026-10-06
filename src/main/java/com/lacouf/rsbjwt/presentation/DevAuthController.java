package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.model.Etudiant;
import com.lacouf.rsbjwt.repository.EtudiantRepository;
import com.lacouf.rsbjwt.security.JwtTokenProvider;
import com.lacouf.rsbjwt.service.UserAppService;
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

    private final EtudiantRepository etudiantRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public DevAuthController(EtudiantRepository etudiantRepository, UserAppService userAppService, JwtTokenProvider jwtTokenProvider){
        this.etudiantRepository = etudiantRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/login/student")
    public ResponseEntity<String> devLoginStudent(){
        Etudiant etudiant = etudiantRepository.findAll()
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Aucun étudiant trouvé en BDD"));

        Authentication auth = new UsernamePasswordAuthenticationToken(
                etudiant.getEmail(),
                null,
                etudiant.getAuthorities()
        );

        String token = jwtTokenProvider.generateToken(auth);

        return ResponseEntity.ok(token);
    }
}
