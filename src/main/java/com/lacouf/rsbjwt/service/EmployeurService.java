package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.EmailExistantException;
import com.lacouf.rsbjwt.Exception.MotDePasseNonCorrespondantException;
import com.lacouf.rsbjwt.model.Employeur;
import com.lacouf.rsbjwt.model.auth.Credentials;
import com.lacouf.rsbjwt.repository.EmployeurRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.service.dto.EmployeurDTO;
import com.lacouf.rsbjwt.service.dto.EmployeurInscriptionDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
public class EmployeurService {
    private final EmployeurRepository employeurRepository;
    private final UserAppRepository userAppRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeurService(EmployeurRepository employeurRepository, UserAppRepository userAppRepository, PasswordEncoder passwordEncoder) {
        this.employeurRepository = employeurRepository;
        this.userAppRepository = userAppRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public EmployeurDTO creeCompteEmployeur(EmployeurInscriptionDTO employeurInscriptionDTO) throws EmailExistantException, MotDePasseNonCorrespondantException {
       verificationInscriptionEmployeurDTO(employeurInscriptionDTO);
       Employeur employeur = employeurInscriptionDTO.toEntity();
       Credentials credentialEmployeur = employeur.getCredentials();
       Credentials credentials = new Credentials(employeur.getEmail(), passwordEncoder.encode(employeur.getPassword()), credentialEmployeur.getRole());
       employeur.setCredentials(credentials);
       Employeur employeurCreer =  employeurRepository.save(employeur);
       return EmployeurDTO.fromEntity(employeurCreer);
    }


    private void verificationInscriptionEmployeurDTO(EmployeurInscriptionDTO employeurInscriptionDTO) throws EmailExistantException, MotDePasseNonCorrespondantException {
        if (!employeurInscriptionDTO.password().equals(employeurInscriptionDTO.passwordConfirmation()))
            throw new MotDePasseNonCorrespondantException();
        if (employeurExiste(employeurInscriptionDTO.email()))
            throw new EmailExistantException();
    }

    private boolean employeurExiste(String email) {
        return userAppRepository.findUserAppByEmail(email.toLowerCase()).isPresent();
    }
}
