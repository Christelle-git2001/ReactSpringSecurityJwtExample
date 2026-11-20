import { getCandidatureStatus } from "./candidatureStatus.js";

export const calculerMetriquesEtudiant = (offres = [], candidatures = [], convocations = [], contrats = [], unreadOfferIds = [], idsPostules = [], user = {}) => {

    const nombreOffresDisponibles = offres.length;

    const nombreNouvellesOffres = offres.filter(o =>
        unreadOfferIds.includes(String(o.id)) &&
        !idsPostules.includes(o.id)
    ).length;

    const nombreOffresPostulees = idsPostules.length;

    const nombreConvocations = convocations.length;

    const stageTrouve = candidatures.some(c => {
        const statutCalcule = getCandidatureStatus(c, convocations.find(conv => conv.candidatureId === c.id));
        return statutCalcule === "STAGE_TROUVE" || c.statutEtudiant === "STAGE_TROUVE";
    }) || user?.statut === "STAGE_TROUVE";

    const nombreContratsASigner = contrats.filter(c => !c.dateSignatureEtudiant).length;

    return {
        nombreOffresDisponibles,
        nombreNouvellesOffres,
        nombreOffresPostulees,
        nombreConvocations,
        stageTrouve,
        nombreContratsASigner
    };
};