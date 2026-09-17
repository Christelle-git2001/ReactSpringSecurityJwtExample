package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Employeur;
import com.lacouf.rsbjwt.model.Enum.SecteurActivite;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EmployeurDTO(
                           @NotBlank(message = "validation.prenom.required")
                           String firstName,
                           @NotBlank(message = "validation.nom.required")
                           String lastName,
                           @NotBlank(message = "validation.telephone.required")
                           String phone,
                           @NotBlank(message = "validation.email.required")
                           @Email
                           String email,
                           @NotBlank(message = "validation.ville.required")
                           String town,
                           @NotBlank(message = "validation.nomEntreprise.required")
                           String businessName,
                           @NotNull(message = "validation.secteurActivite.required")
                           SecteurActivite businessSector,
                           @NotBlank(message = "validation.typeEntreprise.required")
                           String businessType )
{
    public static EmployeurDTO fromEntity(Employeur employeur){
        return new EmployeurDTO(
                employeur.getFirstName(),
                employeur.getLastName(),
                employeur.getPhone(),
                employeur.getEmail(),
                employeur.getTown(),
                employeur.getBusinessName(),
                employeur.getBusinessSector(),
                employeur.getBusinessType()
        );
    }


}
