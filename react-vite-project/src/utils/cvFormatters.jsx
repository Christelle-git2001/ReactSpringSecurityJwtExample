export const formaterTailleFichier = (octets) => {
    if (!octets || octets === 0) return "0 KB";

    const kb = octets / 1024;

    if (kb >= 1024) {
        return `${(kb / 1024).toFixed(1)} MB`;
    }

    return `${Math.round(kb)} KB`;
};

export const formaterDate = (isoString) => {
    if (!isoString) return "";

    const date = new Date(isoString);

    const annee = date.getFullYear();
    const mois = String(date.getMonth() + 1).padStart(2, "0");
    const jour = String(date.getDate()).padStart(2, "0");
    const heures = String(date.getHours()).padStart(2, "0");
    const minutes = String(date.getMinutes()).padStart(2, "0");

    return `${annee}-${mois}-${jour} ${heures}:${minutes}`;
};