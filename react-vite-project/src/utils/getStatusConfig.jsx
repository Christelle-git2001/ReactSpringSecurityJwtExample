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
            ENTREVUE_REFUSEE: "candidature.status.entrevue_refusee",
            EN_ATTENTE_REPONSE: "convocation.awaiting_decision",
            ACCEPTEE: "candidature.status.acceptee",
            REFUSEE: "candidature.status.refusee",
            OFFRE_DECLINEE: "candidature.status.offre_declinee",
            EN_ATTENTE_DE_SIGNATURE: "candidature.status.en_attente_de_signature",
            CONTRAT_CREE: "candidature.status.contrat_cree",
            CONTRAT_SIGNE: "candidature.status.contrat_signe"
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
        RETENUE_POUR_ENTREVUE: "bg-violet-100 text-violet-800 border-violet-200",
        ENTREVUE_PLANIFIEE: "bg-amber-100 text-amber-800 border-amber-200",
        ENTREVUE_REFUSEE: "bg-red-100 text-red-800 border-red-200",
        EN_ATTENTE_REPONSE: "bg-gradient-to-r from-amber-200 via-orange-100 to-yellow-50 text-amber-900 border-amber-300 shadow-sm",
        ACCEPTEE: "bg-emerald-100 text-emerald-800 border-emerald-200",
        REFUSEE: "bg-red-100 text-red-800 border-red-200",
        OFFRE_DECLINEE: "bg-slate-200 text-slate-700 border-slate-300",
        EN_ATTENTE_DE_SIGNATURE: "bg-cyan-800 text-white border-cyan-900",
        CONTRAT_CREE: "bg-blue-800 text-white border-blue-900",
        CONTRAT_SIGNE: "bg-emerald-800 text-white border-emerald-900",
    };

    const estCandidature = type === "candidature" || type === "candidatureEtudiant";
    const statusLabels = estCandidature ? labelKeys.candidature : labelKeys[type] || labelKeys.offre;
    const labelKey = type === "candidatureEtudiant" && statut === "CONTRAT_CREE"
        ? "candidature.status.signature_started" : statusLabels[statut];

    return {
        label: statut ? t(labelKey, statut) : "-",
        className: (estCandidature ? candidatureClassNames[statut] : classNames[statut]) || "badge-neutral",
    };
}

export const STATUTS_CANDIDATURE = ["SOUMISE", "RETENUE_POUR_ENTREVUE", "ENTREVUE_PLANIFIEE", "ENTREVUE_CONFIRMEE", "ENTREVUE_REFUSEE", "ACCEPTEE", "REFUSEE", "EN_ATTENTE_DE_SIGNATURE", "CONTRAT_CREE"];
