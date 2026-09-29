// TODO MOCK TEMPORAIRE — remplacer  quand le backend sera prêt

// Simule une liste d'offres pour un employeur
const MOCK_OFFRES = [
    {
        id: 1,
        title: "Développeur Front-End",
        salary: 35,
        domain: "Informatique",
        startDate: "2024-10-01",
        endDate: "2025-01-01",
        fileName: "description.pdf",
        status: "EN_ATTENTE",
        description: "Développement d'interfaces web en React, intégration d'API REST, collaboration avec l'équipe UX."
    },
    {
        id: 2,
        title: "Technicien Réseau",
        salary: 30,
        domain: "Réseau",
        startDate: "2024-11-15",
        endDate: "2025-02-15",
        fileName: null,
        status: "ACCEPTEE",
        description: "Maintenance des infrastructures réseau, configuration des routeurs et switches, support aux utilisateurs."
    },
    {
        id: 3,
        title: "Technicien Réseau",
        salary: 30,
        domain: "Réseau",
        startDate: "2024-11-15",
        endDate: "2025-02-15",
        fileName: null,
        status: "REFUSEE",
        description: "Maintenance des infrastructures réseau, configuration des routeurs et switches, support aux utilisateurs."
    }
];

export async function fetchOffresEmployeur(employeurId) {
    await new Promise(resolve => setTimeout(resolve, 300));
    return MOCK_OFFRES;
}

export async function fetchOffre(id) {
    await new Promise(resolve => setTimeout(resolve, 200));
    return MOCK_OFFRES.find(o => o.id === id) || null;
}
