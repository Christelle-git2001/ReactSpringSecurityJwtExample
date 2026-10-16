import { beforeAll, beforeEach, describe, vi, expect, it } from "vitest";
import { createInstance } from "i18next";
import { getNotifications, marquerNotificationLue } from "../../api/notification.jsx";
import { obtenirCvGestionnaire } from "../../api/gestionnaire.jsx";
import traductionFr from "../../locales/fr/translation.json";
import { creerNotificationsMock, creerCvsMock } from "../mocks/notification.js";
import { act, fireEvent, render, screen, waitFor } from "@testing-library/react";
import { I18nextProvider } from "react-i18next";
import { MemoryRouter } from "react-router-dom";
import Notification from "../../component/Notification/Notification.jsx";

vi.mock("../../api/notification.jsx", () => ({
    getNotifications: vi.fn(),
    marquerNotificationLue: vi.fn(),
}));

vi.mock("../../api/gestionnaire.jsx", () => ({
    obtenirCvGestionnaire: vi.fn(),
}));

const { navigate } = vi.hoisted(() => ({ navigate: vi.fn() }));

vi.mock("react-router-dom", async (importOriginal) => ({
    ...await importOriginal(),
    useNavigate: () => navigate,
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
    obtenirCvGestionnaire.mockImplementation(async path =>
        creerCvsMock().find(cv => path === `/gestionnaire/cv/${cv.id}`)
    );
    marquerNotificationLue.mockImplementation(async id => ({
        ...creerNotificationsMock().find(notification => notification.id === id),
        estLu: true,
    }));
});

function afficherNotifications() {
    return render(
        <I18nextProvider i18n={testI18n}>
            <MemoryRouter>
                <Notification userId={5} onClose={vi.fn()} />
            </MemoryRouter>
        </I18nextProvider>
    );
}

describe("Notification", () => {
    it("charge le CV cible et ouvre la page de details avec ses donnees", async () => {
        afficherNotifications();

        const items = await screen.findAllByRole("listitem");
        fireEvent.click(items[0].querySelector("button"));

        await waitFor(() => expect(navigate).toHaveBeenCalledWith(
            "/gestionnaire/cvs/details",
            { state: { cv: creerCvsMock()[1], from: "/gestionnaire" } }
        ));
        expect(obtenirCvGestionnaire).toHaveBeenCalledWith("/gestionnaire/cv/20");
        expect(marquerNotificationLue).toHaveBeenCalledWith(2);
    });

    it("conserve la notification et reste sur place si le CV est introuvable", async () => {
        obtenirCvGestionnaire.mockRejectedValue(Object.assign(new Error(), { status: 404 }));
        afficherNotifications();

        const items = await screen.findAllByRole("listitem");
        fireEvent.click(items[0].querySelector("button"));

        expect(await screen.findByRole("alert")).toHaveTextContent(
            traductionFr.notifications.cv_unavailable
        );
        expect(navigate).not.toHaveBeenCalled();
        expect(marquerNotificationLue).not.toHaveBeenCalled();
    });

    it("affiche les titres et descriptions traduits", async () => {
        afficherNotifications();

        expect(
            await screen.findByText("Mercedes Belle a téléversé un CV.")
        ).toBeInTheDocument();

        expect(
            screen.getByText("Junior Belle a téléversé un CV.")
        ).toBeInTheDocument();

        expect(
            screen.getAllByText("Nouveau CV à valider")
        ).toHaveLength(2);

        expect(getNotifications).toHaveBeenCalledWith(5);
    });

    it("masque les notifications déjà lues", async () => {
        afficherNotifications();

        await screen.findByText("Mercedes Belle a téléversé un CV.");

        expect(
            screen.queryByText("Ouriela Belle a téléversé un CV.")
        ).not.toBeInTheDocument();

        expect(screen.getAllByRole("listitem")).toHaveLength(2);
    });

    it("affiche un message quand l’API ne retourne aucune notification", async () => {
        getNotifications.mockResolvedValue([]);

        // Attendre la réponse API et la mise à jour du composant.
        await act(async () => {
            afficherNotifications();
        });

        expect(getNotifications).toHaveBeenCalledWith(5);

        expect(
            screen.getByText("Aucune notification")
        ).toBeInTheDocument();

        expect(screen.queryByRole("listitem")).not.toBeInTheDocument();
    });
});
