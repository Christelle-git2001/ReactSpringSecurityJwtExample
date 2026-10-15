import { fetchJson } from "./http.jsx";

export function getNotifications(userId, options = {}) {
    return fetchJson(`/notification/utilisateur/${userId}`, options);
}