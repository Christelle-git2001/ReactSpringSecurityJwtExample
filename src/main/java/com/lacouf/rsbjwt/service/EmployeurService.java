package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.EmailExistantException;
import com.lacouf.rsbjwt.Exception.MotDePasseNonCorrespondantException;
import com.lacouf.rsbjwt.Exception.NumeroTelephoneExistantException;
import com.lacouf.rsbjwt.model.Employeur;
import com.lacouf.rsbjwt.repository.EmployeurRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.service.dto.EmployeurDTO;
import com.lacouf.rsbjwt.service.dto.InscriptionEmployeurDTO;
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
    public EmployeurDTO creeCompteEmployeur(InscriptionEmployeurDTO inscriptionEmployeurDTO) throws EmailExistantException, MotDePasseNonCorrespondantException, NumeroTelephoneExistantException {
       validerInscriptionEmployeur(inscriptionEmployeurDTO);

       /* Je sais qu'on avait regarder tantôt pour le toEntity(), mais je pensais qu'il buildait
       l'objet au complet. Dans toEntity, tu créais l'objet ensuite tu dois le modifier avec des setters
       Je trouve que c'est préférable de créer un objet complet dès le départ, mais on pourra en discuté.

       Employeur employeur = employeurInscriptionDTO.toEntity();

       // Les crédentials sont set à la création du user

       Credentials credentialEmployeur = employeur.getCredentials();
       Credentials credentials = new Credentials(employeur.getEmail(), passwordEncoder.encode(employeur.getPassword()), credentialEmployeur.getRole());
       employeur.setCredentials(credentials);

        */

        // AU pire on peut faire une méthode créer employeur

        Employeur employeur = Employeur.builder()
                .firstName(inscriptionEmployeurDTO.firstName())
                .lastName(inscriptionEmployeurDTO.lastName())
                .email(inscriptionEmployeurDTO.email())
                .phone(inscriptionEmployeurDTO.phone())
                .town(inscriptionEmployeurDTO.town())
                .businessName(inscriptionEmployeurDTO.businessName())
                .businessType(inscriptionEmployeurDTO.businesstype())
                .businessSector(inscriptionEmployeurDTO.businessSector())
                .password(passwordEncoder.encode(inscriptionEmployeurDTO.password()))
                .build();

        Employeur employeurCreer =  employeurRepository.save(employeur);
       return EmployeurDTO.of(employeurCreer);
    }


    private void validerInscriptionEmployeur(InscriptionEmployeurDTO inscriptionEmployeurDTO) throws EmailExistantException, MotDePasseNonCorrespondantException, NumeroTelephoneExistantException {
        if (!inscriptionEmployeurDTO.password().equals(inscriptionEmployeurDTO.passwordConfirmation()))
            throw new MotDePasseNonCorrespondantException();
        if (employeurExiste(inscriptionEmployeurDTO.email()))
            throw new EmailExistantException();
        // J'ai ajouté l'exception du numéro de telephone, je ne sais pas comment tu veux le formater
        if (userAppRepository.findByPhoneNumber(
                inscriptionEmployeurDTO.phone()).isPresent()) {
            throw new NumeroTelephoneExistantException();
        }
    }

    private boolean employeurExiste(String email) {
        return userAppRepository.findUserAppByEmail(email.toLowerCase()).isPresent();
    }
}
