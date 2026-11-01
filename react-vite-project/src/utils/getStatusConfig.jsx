export function getStatusConfig(statut, t, type = "offre") {
    const labelKeys = {
        offre: {
            EN_ATTENTE: "status.en_attente",
            ACCEPTEE: "status.acceptee",
            REFUSEE: "status.refusee",
        },
        cv: {
            EN_ATTENTE: "status.en_attente",
            ACCEPTEE: "status.cv_accepte",
            REFUSEE: "status.cv_refuse",
        },
        candidature: {
            SOUMISE: "candidature.status.soumise",
            RETENUE_POUR_ENTREVUE: "candidature.status.retenue",
            ENTREVUE_PLANIFIEE: "candidature.status.planifiee",
            ACCEPTEE: "candidature.status.acceptee",
            REFUSEE: "candidature.status.refusee",
            STAGE_TROUVE: "candidature.status.stage_trouve"
        },
    };

    const classNames = {
        EN_ATTENTE: "badge-warning",
        ACCEPTEE: "badge-success",
        REFUSEE: "badge-error",

        SOUMISE: "badge-info",
        RETENUE_POUR_ENTREVUE: "badge-accent",
        ENTREVUE_PLANIFIEE: "badge-warning",
    };

    const statusLabels = labelKeys[type] || labelKeys.offre;

    return {
        label: statut ? t(statusLabels[statut], statut) : "-",
        className: classNames[statut] || "badge-neutral",
    };
}