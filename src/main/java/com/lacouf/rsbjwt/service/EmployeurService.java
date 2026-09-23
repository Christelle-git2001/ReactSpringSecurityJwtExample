package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.*;
import com.lacouf.rsbjwt.model.Employeur;
import com.lacouf.rsbjwt.repository.EmployeurRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.service.dto.EmployeurDTO;
import com.lacouf.rsbjwt.service.dto.InscriptionEmployeurDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.lacouf.rsbjwt.util.phoneNumberUtil.formatePhoneNumber;


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
    public EmployeurDTO creeCompteEmployeur(InscriptionEmployeurDTO inscriptionEmployeurDTO) throws EmailExistantException, MotDePasseNonCorrespondantException, NumeroTelephoneExistantException, ChampsObligatoiresManquants, FormatTelephoneNonValide {
       validerInscriptionEmployeur(inscriptionEmployeurDTO);
       String encodedPassword = passwordEncoder.encode(inscriptionEmployeurDTO.password());
       String formatedPhoneNumber = formatePhoneNumber(inscriptionEmployeurDTO.phone());
        Employeur employeur = inscriptionEmployeurDTO.toEntity(encodedPassword,formatedPhoneNumber);
        Employeur employeurCreer =  employeurRepository.save(employeur);
       return EmployeurDTO.of(employeurCreer);
    }


    private void validerInscriptionEmployeur(InscriptionEmployeurDTO inscriptionEmployeurDTO) throws EmailExistantException, MotDePasseNonCorrespondantException, NumeroTelephoneExistantException {
        if (!inscriptionEmployeurDTO.password().equals(inscriptionEmployeurDTO.passwordConfirmation()))
            throw new MotDePasseNonCorrespondantException();
        if (employeurExiste(inscriptionEmployeurDTO.email()))
            throw new EmailExistantException();
        if (userAppRepository.findByPhoneNumber(inscriptionEmployeurDTO.phone()).isPresent())
            throw new NumeroTelephoneExistantException();
    }

    private boolean employeurExiste(String email) {
        return userAppRepository.findUserAppByEmail(email.toLowerCase()).isPresent();
    }
}
