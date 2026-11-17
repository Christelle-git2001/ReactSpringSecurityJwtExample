package com.lacouf.rsbjwt.service.dto.utilisateur;

public sealed interface UserDTO permits EmployeurDTO, EtudiantDTO, GestionnaireDTO, ProfesseurDTO {
    long id();
    String firstName();
    String lastName();
    String email();
    RoleDTO role();
}