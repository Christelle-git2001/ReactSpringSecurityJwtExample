export const isNewSince = (itemDate, referenceDate) => {
    if (!itemDate) return false;
    if (!referenceDate) return true;

    return new Date(itemDate) > new Date(referenceDate);
};