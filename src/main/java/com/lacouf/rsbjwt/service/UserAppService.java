package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.*;
import com.lacouf.rsbjwt.repository.*;
import com.lacouf.rsbjwt.service.dto.*;
import com.lacouf.rsbjwt.security.JwtTokenProvider;
import com.lacouf.rsbjwt.security.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAppService {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserAppRepository userAppRepository;
    private final GestionnaireRepository gestionnaireRepository;
    private final EmployeurRepository employeurRepository;
    private final EtudiantRepository etudiantRepository;
    private final ProfesseurRepository professeurRepository;

    public String authenticateUser(LoginDTO loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password()));
        final String token = jwtTokenProvider.generateToken(authentication);
        System.out.println("JWT Token " + token);
        return token;
    }

    public UserDTO getMe(String token) {
        token = token.startsWith("Bearer") ? token.substring(7) : token;
        String email = jwtTokenProvider.getEmailFromJWT(token);
        UserApp user = userAppRepository.findUserAppByEmail(email).orElseThrow(UserNotFoundException::new);
        return switch(user.getRole()){
            case GESTIONNAIRE -> getGestionnaireDto(user.getId());
            case ETUDIANT -> getEtudiantDTO(user.getId());
            case PROFESSEUR -> getProfesseurDTO(user.getId());
            case EMPLOYEUR -> getEmployeurDTO(user.getId());
        };
    }

    private GestionnaireDto getGestionnaireDto(Long id) {
        final Optional<Gestionnaire> gestionnaireOptional = gestionnaireRepository.findById(id);
        return gestionnaireOptional.isPresent() ?
                GestionnaireDto.create(gestionnaireOptional.get()) :
                GestionnaireDto.empty();
    }

    private EtudiantDTO getEtudiantDTO(Long id) {
        final Optional<Etudiant> etudiantOptional = etudiantRepository.findById(id);
        return etudiantOptional.map(EtudiantDTO::of).orElseGet(EtudiantDTO::empty);
    }

    private EmployeurDTO getEmployeurDTO(Long id) {
        final Optional<Employeur> employeurOptional = employeurRepository.findById(id);
        return employeurOptional.map(EmployeurDTO::of).orElseGet(EmployeurDTO::empty);
    }

    private ProfesseurDTO getProfesseurDTO(Long id) {
        final Optional<Professeur> professeurOptional = professeurRepository.findById(id);
        return professeurOptional.map(ProfesseurDTO::of).orElseGet(ProfesseurDTO::empty);
    }
}
