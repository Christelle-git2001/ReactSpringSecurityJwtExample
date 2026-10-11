export const isNewSince = (itemDate, referenceDate) => {
    if (!itemDate || !referenceDate) return false;

    return new Date(itemDate) > new Date(referenceDate);
};