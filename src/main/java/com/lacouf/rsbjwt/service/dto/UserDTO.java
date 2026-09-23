package com.lacouf.rsbjwt.service.dto;

public sealed interface UserDTO permits EmployeurDTO, EtudiantDTO, GestionnaireDto, ProfesseurDTO {
    long id();
    String firstName();
    String lastName();
    String email();
    RoleDTO role();
}
