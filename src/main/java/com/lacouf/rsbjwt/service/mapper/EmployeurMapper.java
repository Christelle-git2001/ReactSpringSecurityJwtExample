package com.lacouf.rsbjwt.service.mapper;

import com.lacouf.rsbjwt.model.Employeur;
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
}
