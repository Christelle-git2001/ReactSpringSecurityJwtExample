package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.Enum.SecteurActivite;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeurDTO {
    @NotBlank(message = "validation.nom.required")
    private String nom;
    @NotBlank(message = "validation.prenom.required")
    private String prenom;
    @NotBlank(message = "validation.ville.required")
    private String ville;
    @NotBlank(message = "validation.telephone.required")
    private String telephone;
    @NotBlank(message = "validation.email.required")
    @Email
    private String email;
    @NotBlank(message = "validation.nomEntreprise.required")
    private String nomEntreprise;
    @NotBlank(message = "validation.typeEntreprise.required")
    private String typeEntreprise;
    @NotBlank(message = "validation.secteurActivite.required")
    private SecteurActivite secteurActivite;
}
