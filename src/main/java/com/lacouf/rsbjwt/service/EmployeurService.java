package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.model.Employeur;
import com.lacouf.rsbjwt.model.Exceptions.ConfirmationMotDePasseEchouer;
import com.lacouf.rsbjwt.model.Exceptions.EmployeurExistant;
import com.lacouf.rsbjwt.repository.EmployeurRepository;
import com.lacouf.rsbjwt.service.dto.EmployeurDTO;
import com.lacouf.rsbjwt.service.dto.EmployeurInscriptionDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.lacouf.rsbjwt.service.mapper.EmployeurMapper.convertisseurEmployeurToEmployeurDTO;
import static com.lacouf.rsbjwt.service.mapper.EmployeurMapper.convertisseurInscriptionEmployeurDTOToEmployeur;

@Service
@Transactional(readOnly = true)
public class EmployeurService {
    private final EmployeurRepository employeurRepository;

    public EmployeurService(EmployeurRepository employeurRepository) {
        this.employeurRepository = employeurRepository;
    }

    @Transactional
    public EmployeurDTO inscription(EmployeurInscriptionDTO employeurInscriptionDTO) throws EmployeurExistant, ConfirmationMotDePasseEchouer {
       verificationEmployeurInscriptionDTO(employeurInscriptionDTO);
       Employeur employeur = convertisseurInscriptionEmployeurDTOToEmployeur(employeurInscriptionDTO);
       Employeur employeurCreer =  employeurRepository.save(employeur);
       return convertisseurEmployeurToEmployeurDTO(employeurCreer);
    }


    private void verificationEmployeurInscriptionDTO(EmployeurInscriptionDTO employeurInscriptionDTO) throws EmployeurExistant, ConfirmationMotDePasseEchouer {
        if (!employeurInscriptionDTO.getPassword().equals(employeurInscriptionDTO.getPasswordConfirmation()))
            throw new ConfirmationMotDePasseEchouer("Les mot de passe ne sont pas identique");
        if (employeurExiste(employeurInscriptionDTO.getEmail()))
            throw new EmployeurExistant("Employeur existe déjà");
    }

    private boolean employeurExiste(String email) {
        return employeurRepository.existeParEmail(email.toLowerCase());
    }
}
