export function getCvStatusConfig(statut, t) {
    const STATUS_CONFIG = {
        EN_ATTENTE: { label: t("status.en_attente"), className: "badge-warning",},
        ACCEPTEE: { label: t("status.cv_accepte"), className: "badge-success",},
        REFUSEE: { label: t("status.cv_refuse"), className: "badge-error",},
    };

    return STATUS_CONFIG[statut] || {
        label: statut || "-",
        className: "badge-neutral",
    };
}