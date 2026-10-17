import { fetchJson } from "./http.jsx";

export function getNotifications(userId, options = {}) {
    return fetchJson(`/notification/utilisateur/${userId}`, options);
}

export function marquerNotificationLue(id) {
    return fetchJson(`/notification/${id}/lu`, {
        method: "PUT",
    });
}