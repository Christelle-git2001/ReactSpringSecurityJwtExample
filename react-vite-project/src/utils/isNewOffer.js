export function isNewOffer(offer, previousLoginAt) {
    if (!offer?.createdAt) return false;

    const createdAt = new Date(offer.createdAt).getTime();
    if (isNaN(createdAt)) return false;
    if (!previousLoginAt) return true;

    return createdAt > new Date(previousLoginAt).getTime();
}