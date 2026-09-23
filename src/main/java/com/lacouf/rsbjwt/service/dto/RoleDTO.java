package com.lacouf.rsbjwt.service.dto;

import com.lacouf.rsbjwt.model.auth.Role;

public record RoleDTO(String name, String label) {
    public static final RoleDTO ETUDIANT = new RoleDTO(Role.ETUDIANT.name(), "Étudiant");
    public static final RoleDTO GESTIONNAIRE = new RoleDTO(Role.GESTIONNAIRE.name(), "Gestionnaire");
    public static final RoleDTO EMPLOYEUR = new RoleDTO(Role.EMPLOYEUR.name(), "Employeur");
    public static final RoleDTO PROFESSEUR = new RoleDTO(Role.PROFESSEUR.name(), "Professeur");

}