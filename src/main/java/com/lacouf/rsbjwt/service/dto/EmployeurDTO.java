package com.lacouf.rsbjwt.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lacouf.rsbjwt.model.Employeur;
import com.lacouf.rsbjwt.model.Enum.SecteurActivite;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EmployeurDTO (
                            long id,
                           String firstName,
                           String lastName,
                           String phone,
                           String email,
                           String town,
                           String businessName,
                           SecteurActivite businessSector) implements UserDTO
{
    public static EmployeurDTO of(Employeur employeur){
        return new EmployeurDTO(
                employeur.getId(),
                employeur.getFirstName(),
                employeur.getLastName(),
                employeur.getPhoneNumber(),
                employeur.getEmail(),
                employeur.getTown(),
                employeur.getBusinessName(),
                employeur.getBusinessSector()
        );
    }

    @Override
    @JsonProperty("role")
    public RoleDTO role() {
        return RoleDTO.EMPLOYEUR;
    }

    public static EmployeurDTO empty() {
        return new EmployeurDTO(-1,null,null,null,null,null,null,null);
    }
}
