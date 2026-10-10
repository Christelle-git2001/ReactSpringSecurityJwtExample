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
    };

    const classNames = {
        EN_ATTENTE: "badge-warning",
        ACCEPTEE: "badge-success",
        REFUSEE: "badge-error",
    };

    const statusLabels = labelKeys[type] || labelKeys.offre;

    return {
        label: statut ? t(statusLabels[statut], statut) : "-",
        className: classNames[statut] || "badge-neutral",
    };
}