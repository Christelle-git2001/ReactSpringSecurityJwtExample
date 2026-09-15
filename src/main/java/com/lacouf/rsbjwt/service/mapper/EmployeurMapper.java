package com.lacouf.rsbjwt.service.mapper;

import com.lacouf.rsbjwt.model.Employeur;
import com.lacouf.rsbjwt.service.dto.EmployeurDTO;
import com.lacouf.rsbjwt.service.dto.EmployeurInscriptionDTO;
import org.springframework.stereotype.Component;

@Component
public class EmployeurMapper {
    public static Employeur convertisseurInscriptionEmployeurDTOToEmployeur(EmployeurInscriptionDTO employeurInscriptionDTO){
        return Employeur.builder()
                .prenom(employeurInscriptionDTO.getPrenom())
                .nom(employeurInscriptionDTO.getNom())
                .ville(employeurInscriptionDTO.getVille())
                .telephone(employeurInscriptionDTO.getTelephone())
                .email(employeurInscriptionDTO.getEmail())
                .password(employeurInscriptionDTO.getPassword())
                .nomEntreprise(employeurInscriptionDTO.getNomEntreprise())
                .typeEntreprise(employeurInscriptionDTO.getTypeEntreprise())
                .secteurActivite(employeurInscriptionDTO.getSecteurActivite())
                .build();
    }

    public static EmployeurDTO convertisseurEmployeurToEmployeurDTO(Employeur employeur){
        EmployeurDTO employeurDTO = new EmployeurDTO();
        employeurDTO.setPrenom(employeur.getFirstName());
        employeurDTO.setNom(employeur.getLastName());
        employeurDTO.setVille(employeur.getVille());
        employeurDTO.setTelephone(employeur.getTelephone());
        employeurDTO.setEmail(employeur.getEmail());
        employeurDTO.setNomEntreprise(employeur.getNomEntreprise());
        employeurDTO.setSecteurActivite(employeur.getSecteurActivite());
        employeurDTO.setTypeEntreprise(employeur.getTypeEntreprise());
        return employeurDTO;
    }
}
