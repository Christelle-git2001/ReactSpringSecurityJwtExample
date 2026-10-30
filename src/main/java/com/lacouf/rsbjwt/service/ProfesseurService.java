package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.*;
import com.lacouf.rsbjwt.model.Employeur;
import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.Professeur;
import com.lacouf.rsbjwt.repository.EtudiantRepository;
import com.lacouf.rsbjwt.repository.ProfesseurRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.security.exception.UserNotFoundException;
import com.lacouf.rsbjwt.service.dto.EtudiantDTO;
import com.lacouf.rsbjwt.service.dto.InscriptionProfesseurDTO;
import com.lacouf.rsbjwt.service.dto.ProfesseurDTO;
import com.lacouf.rsbjwt.utils.StockageFichierUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProfesseurService {

    private final ProfesseurRepository professeurRepository ;
    private final UserAppRepository userAppRepository ;
    private final PasswordEncoder passwordEncoder ;
    private final EtudiantRepository etudiantRepository;

    public ProfesseurService(ProfesseurRepository professeurRepository, UserAppRepository userAppRepository, PasswordEncoder passwordEncoder,
                             EtudiantRepository etudiantRepository) {
        this.professeurRepository = professeurRepository;
        this.userAppRepository = userAppRepository;
        this.passwordEncoder = passwordEncoder;
        this.etudiantRepository = etudiantRepository;
    }

    @Transactional
    public ProfesseurDTO creerCompteProfesseur(InscriptionProfesseurDTO inscriptionProfesseurDto) throws DepartementInvalideException, EmailExistantException, MatriculeExistantException, MotDePasseNonCorrespondantException, NumeroTelephoneExistantException {
        validerInscription(inscriptionProfesseurDto);

        Departement departement = StockageFichierUtils.normaliserDepartement(inscriptionProfesseurDto.department()) ;

        Professeur professeur = Professeur.builder()
                .firstName(inscriptionProfesseurDto.firstName())
                .lastName(inscriptionProfesseurDto.lastName())
                .email(inscriptionProfesseurDto.email())
                .password(passwordEncoder.encode(inscriptionProfesseurDto.password()))
                .matricule(inscriptionProfesseurDto.matricule())
                .phoneNumber(inscriptionProfesseurDto.phone())
                .department(departement)
                .build();

        return ProfesseurDTO.of(professeurRepository.save(professeur));

    }

    private void validerInscription(InscriptionProfesseurDTO inscriptionProfesseurDto) throws EmailExistantException, MatriculeExistantException, MotDePasseNonCorrespondantException, NumeroTelephoneExistantException {
        if(userAppRepository.findUserAppByEmail(inscriptionProfesseurDto.email()).isPresent()){
            throw new EmailExistantException();
        }

        if(professeurRepository.findByMatricule(inscriptionProfesseurDto.matricule()).isPresent()){
            throw new MatriculeExistantException() ;
        }

        if(! inscriptionProfesseurDto.password().equals(inscriptionProfesseurDto.passwordConfirmation())){
            throw new MotDePasseNonCorrespondantException() ;
        }

        if (userAppRepository.findByPhoneNumber(
                inscriptionProfesseurDto.phone()).isPresent()) {
            throw new NumeroTelephoneExistantException();
        }

    }

    public List<EtudiantDTO> getEtudiantsAssignes()
            throws UtilisateurIntrouvableException {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Professeur professeur = userAppRepository.findUserAppByEmail(email)
                .filter(Professeur.class::isInstance)
                .map(Professeur.class::cast)
                .orElseThrow(UtilisateurIntrouvableException::new);

        return etudiantRepository.findAllByProfesseurId(professeur.getId())
                .stream()
                .map(EtudiantDTO::of)
                .toList();
    }
}
