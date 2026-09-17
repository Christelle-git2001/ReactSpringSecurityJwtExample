package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.Employeur;
import com.lacouf.rsbjwt.model.Exceptions.MotDePasseNonCorrespondantException;
import com.lacouf.rsbjwt.model.Exceptions.EmailExistantException;
import com.lacouf.rsbjwt.repository.EmployeurRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.service.dto.EmployeurDTO;
import com.lacouf.rsbjwt.service.dto.EmployeurInscriptionDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.lacouf.rsbjwt.service.mapper.EmployeurMapper.convertisseurEmployeurToEmployeurDTO;
import static com.lacouf.rsbjwt.service.mapper.EmployeurMapper.convertisseurInscriptionEmployeurDTOToEmployeur;

@Service
@Transactional(readOnly = true)
public class EmployeurService {
    private final EmployeurRepository employeurRepository;
    private final UserAppRepository userAppRepository;

    public EmployeurService(EmployeurRepository employeurRepository, UserAppRepository userAppRepository) {
        this.employeurRepository = employeurRepository;
        this.userAppRepository = userAppRepository;
    }

    @Transactional
    public EmployeurDTO creeCompteEmployeur(EmployeurInscriptionDTO employeurInscriptionDTO) throws EmailExistantException, MotDePasseNonCorrespondantException {
       verificationInscriptionEmployeurDTO(employeurInscriptionDTO);
       Employeur employeur = convertisseurInscriptionEmployeurDTOToEmployeur(employeurInscriptionDTO);
       Employeur employeurCreer =  employeurRepository.save(employeur);
       return convertisseurEmployeurToEmployeurDTO(employeurCreer);
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
