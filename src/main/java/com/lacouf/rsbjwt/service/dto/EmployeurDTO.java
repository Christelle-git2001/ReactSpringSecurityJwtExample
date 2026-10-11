package com.lacouf.rsbjwt.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.lacouf.rsbjwt.model.Employeur;
import com.lacouf.rsbjwt.model.Enum.SecteurActivite;
import java.time.LocalDateTime;

public record EmployeurDTO (
        long id,
        String firstName,
        String lastName,
        String phone,
        String email,
        String town,
        String businessName,
        SecteurActivite businessSector,
        LocalDateTime previousLoginAt)
    implements UserDTO {

    public static EmployeurDTO of(Employeur employeur){
        return new EmployeurDTO(
                employeur.getId(),
                employeur.getFirstName(),
                employeur.getLastName(),
                employeur.getPhoneNumber(),
                employeur.getEmail(),
                employeur.getTown(),
                employeur.getBusinessName(),
                employeur.getBusinessSector(),
                employeur.getPreviousLoginAt()
        );
    }

    @Override
    @JsonProperty("role")
    public RoleDTO role() {
        return RoleDTO.EMPLOYEUR;
    }

    public static EmployeurDTO empty() {
        return new EmployeurDTO(-1,null,null,null,
                null,null,null,null, null);
    }
}
