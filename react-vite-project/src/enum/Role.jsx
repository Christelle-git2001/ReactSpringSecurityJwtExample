export const Role = {
    EMPLOYEUR: 'EMPLOYEUR',
    PROFESSEUR: 'PROFESSEUR',
    ETUDIANT: 'ETUDIANT',
    GESTIONNAIRE : 'GESTIONNAIRE'
};

export const removeSuffixRole = (role) => {
    if (!role) return "";
    const value = typeof role === "object" ? role.name : role;
    return value?.toString().replace("ROLE_", "") ?? "";
};