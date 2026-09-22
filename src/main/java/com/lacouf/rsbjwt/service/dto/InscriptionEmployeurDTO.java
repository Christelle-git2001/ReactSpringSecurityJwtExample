package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Employeur;
import com.lacouf.rsbjwt.model.Enum.SecteurActivite;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.springframework.security.crypto.password.PasswordEncoder;

public record InscriptionEmployeurDTO(
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
                                      String businesstype,
                                      @NotBlank(message = "validation.password.required")
                                      @Pattern(
                                              regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[^\\w\\s]).+$",
                                              message = "validation.password.invalid"
                                      )
                                      String password,
                                      @NotBlank(message = "validation.passwordConfirmation.required")
                                      String passwordConfirmation)
{
    public Employeur toEntity(PasswordEncoder passwordEncoder) {
        return Employeur.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(phone)
                .town(town)
                .businessName(businessName)
                .businessType(businesstype)
                .businessSector(businessSector)
                .password(passwordEncoder.encode(password))
                .build();
    }
}
