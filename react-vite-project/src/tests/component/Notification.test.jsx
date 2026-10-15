import { beforeAll, beforeEach, describe, vi } from "vitest";
import { createInstance } from "i18next";
import { getNotifications, marquerNotificationLue } from "../../api/notification.jsx";
import { getTousLesCvs } from "../../api/gestionnaire.jsx";
import traductionFr from "../../locales/fr/translation.json";
import { creerNotificationsMock, creerCvsMock } from "../../test/fixtures/notifications.js";

vi.mock("../../api/notification.jsx", () => ({
    getNotifications: vi.fn(),
    marquerNotificationLue: vi.fn(),
}));

vi.mock("../../api/gestionnaire.jsx", () => ({
    getTousLesCvs: vi.fn(),
}));

const testI18n = createInstance();

beforeAll(async () => {
    await testI18n.init({
        lng: "fr",
        fallbackLng: "fr",
        resources: { fr: { translation: traductionFr } },
        interpolation: { escapeValue: false },
    });
});

beforeEach(async () => {
    vi.resetAllMocks();
    await testI18n.changeLanguage("fr");

    getNotifications.mockResolvedValue(creerNotificationsMock());
    getTousLesCvs.mockResolvedValue(creerCvsMock());
    marquerNotificationLue.mockImplementation(async id => ({
        ...creerNotificationsMock().find(notification => notification.id === id),
        estLu: true,
    }));
});

describe("Notification", () => {

});