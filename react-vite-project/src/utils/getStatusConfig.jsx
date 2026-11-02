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
            ENTREVUE_CONFIRMEE: "candidature.status.confirmee",
            ENTREVUE_REFUSEE: "candidature.status.entrevue_refusee",
            ACCEPTEE: "candidature.status.acceptee",
            REFUSEE: "candidature.status.refusee",
            STAGE_TROUVE: "candidature.status.stage_trouve",
            CONTRAT_CREE: "candidature.status.contrat_cree"
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

    const candidatureClassNames = {
        SOUMISE: "bg-sky-100 text-sky-800 border-sky-200",
        RETENUE_POUR_ENTREVUE: "bg-violet-100 text-violet-700 border-violet-200",
        ENTREVUE_PLANIFIEE: "bg-amber-100 text-amber-800 border-amber-200",
        ENTREVUE_CONFIRMEE: "bg-emerald-100 text-emerald-800 border-emerald-200",
        ENTREVUE_REFUSEE: "bg-red-100 text-red-800 border-red-200",
        ACCEPTEE: "bg-emerald-100 text-emerald-800 border-emerald-200",
        REFUSEE: "bg-red-100 text-red-800 border-red-200",
        STAGE_TROUVE: "bg-cyan-100 text-cyan-800 border-cyan-200",
        CONTRAT_CREE: "bg-blue-100 text-blue-800 border-blue-200",
    };

    const statusLabels = labelKeys[type] || labelKeys.offre;

    return {
        label: statut ? t(statusLabels[statut], statut) : "-",
        className: (type === "candidature" ? candidatureClassNames[statut] : classNames[statut]) || "badge-neutral",
    };
}