package com.lacouf.rsbjwt.service.mapper;

import org.springframework.stereotype.Component;

@Component
public class EmployeurMapper {
    public static Employeur convertisseurInscriptionEmployeurDTOToEmployeur(EmployeurInscriptionDTO employeurInscriptionDTO){
        return Employeur.builder()
                .firstName(employeurInscriptionDTO.firstName())
                .lastName(employeurInscriptionDTO.lastName())
                .town(employeurInscriptionDTO.town())
                .phone(employeurInscriptionDTO.phone())
                .email(employeurInscriptionDTO.email())
                .password(employeurInscriptionDTO.password())
                .businessName(employeurInscriptionDTO.businessName())
                .businessType(employeurInscriptionDTO.businesstype())
                .businessSector(employeurInscriptionDTO.businessSector())
                .build();
    }

    public static EmployeurDTO convertisseurEmployeurToEmployeurDTO(Employeur employeur){
        return EmployeurDTO.fromEntity(employeur);
    }
}
