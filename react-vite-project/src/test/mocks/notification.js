export function creerNotificationsMock() {
    return [
        {
            id: 1,
            titre: "notification.employeur.nouvelleCandidature.title",
            description: "notification.employeur.nouvelleCandidature.description",
            notificationType: "NOUVELLE_CANDIDATURE",
            variables: {
                ETUDIANT_NOM: "Jean Tremblay",
                OFFRE_TITRE: "Stage en développement",
                CANDIDATURE_ID: "100"
            },
            estLu: false,
            dateNotifiee: "2026-10-20T09:33:00",
        },
        {
            id: 2,
            titre: "notification.gestionnaire.televerserCV.title",
            description: "notification.gestionnaire.televerserCV.description",
            notificationType: "CV_ATTENTE_VALIDATION",
            variables: { ETUDIANT_NOM: "Mercedes Belle" },
            estLu: false,
            dateNotifiee: "2026-10-12T11:01:00",
            urlCible: "/gestionnaire/cv/10",
        },
        {
            id: 3,
            titre: "notification.gestionnaire.televerserCV.title",
            description: "notification.gestionnaire.televerserCV.description",
            notificationType: "CV_ATTENTE_VALIDATION",
            variables: { ETUDIANT_NOM: "Junior Belle" },
            estLu: false,
            dateNotifiee: "2026-10-13T09:45:00",
            urlCible: "/gestionnaire/cv/20",
        },
        {
            id: 4,
            titre: "notification.gestionnaire.televerserCV.title",
            description: "notification.gestionnaire.televerserCV.description",
            notificationType: "CV_ATTENTE_VALIDATION",
            variables: { ETUDIANT_NOM: "Ouriela Belle" },
            estLu: true,
            dateNotifiee: "2026-10-11T08:00:00",
            urlCible: "/gestionnaire/cv/30",
        },
        {
            id: 4,
            titre: "notification.gestionnaire.offre.title",
            description: "notification.gestionnaire.offre.description",
            notificationType: "OFFRE_ATTENTE_VALIDATION",
            variables: { OFFRE_TITRE: "Stage en développement", OFFRE_ID: "1" },
            estLu: false,
            dateNotifiee: "2026-10-14T10:00:00",
            urlCible: "/gestionnaire/offres/1",
        },
    ];
}

export function creerCvsMock() {
    return [
        { id: 10, fileName: "mercedes.pdf", statut: "EN_ATTENTE" },
        { id: 20, fileName: "junior.pdf", statut: "EN_ATTENTE" },
    ];
}

export function creerOffresMock() {
    return [
        {
            id: 1,
            title: "Stage en développement",
            domain: "INFORMATIQUE",
            salary: 20,
            description: "Description de l'offre",
            startDate: "2026-01-01",
            endDate: "2026-04-01",
            displayEndDate: "2025-12-01",
            fileName: "offre.pdf",
        },
    ];
}

export function creerCandidaturesMock() {
    return [
        {
            id: 100,
            etudiantFirstName: "Jean",
            etudiantLastName: "Tremblay",
            etudiantDepartment: "INFORMATIQUE",
            offreId: 1,
            offreTitle: "Développeur Web",
            statut: "EN_ATTENTE"
        }
    ];
}