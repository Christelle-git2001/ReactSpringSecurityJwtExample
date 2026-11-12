import { fetchJson } from "./http.jsx";

export function getNotifications(userId, options = {}) {
    return fetchJson(`/notification/utilisateur/${userId}`, options);
}

export function marquerNotificationLue(id) {
    return fetchJson(`/notification/${id}/lu`, {
        method: "PUT",
    });
}

export async function marquerNotificationsContratCommeLues(userId, candidatureId) {
    const notifications = await getNotifications(userId);
    const contrats = notifications.filter(notification =>
        notification.notificationType === "NOUVEAU_CONTRAT" && !notification.estLu
        && String(notification.variables?.CANDIDATURE_ID) === String(candidatureId));
    await Promise.all(contrats.map(notification => marquerNotificationLue(notification.id)));
    return contrats.length;
}
