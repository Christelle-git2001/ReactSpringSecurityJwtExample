const JOURS_AVANT_FIN_STAGE = 14;

export function verifierDisponibiliteEvaluation(endDate, aujourdHui = new Date()) {
    if (!endDate) return false;

    const dateFin = new Date(`${endDate}T00:00:00`);
    if (Number.isNaN(dateFin.getTime())) return false;

    const dateOuverture = new Date(dateFin);
    dateOuverture.setDate(dateOuverture.getDate() - JOURS_AVANT_FIN_STAGE);

    const dateCourante = new Date(aujourdHui);
    dateCourante.setHours(0, 0, 0, 0);

    return dateCourante.getTime() >= dateOuverture.getTime();
}