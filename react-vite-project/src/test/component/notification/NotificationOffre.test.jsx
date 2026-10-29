import { createInstance } from "i18next";
import { marquerNotificationLue } from "../../../api/notification.jsx";
import traductionFr from "../../../locales/fr/translation.json";
import { creerNotificationsMock } from "../../mocks/notification.js";
import { fireEvent, render, screen, waitFor, within } from "@testing-library/react";
import { I18nextProvider } from "react-i18next";
import { MemoryRouter } from "react-router-dom";
import Notification from "../../../component/Notification/Notification.jsx";

jest.mock("../../../api/notification.jsx", () => ({
    marquerNotificationLue: jest.fn(),
}));

const mockNavigate = jest.fn();

jest.mock("react-router-dom", () => ({
    ...jest.requireActual("react-router-dom"),
    useNavigate: () => mockNavigate,
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
    jest.clearAllMocks();
    await testI18n.changeLanguage("fr");
    marquerNotificationLue.mockResolvedValue({ estLu: true });
});

function afficherNotifications(onMarkAsRead = jest.fn()) {
    const notifications = creerNotificationsMock().filter(
        notification =>
            !notification.estLu &&
            notification.notificationType === "OFFRE_ATTENTE_VALIDATION"
    );

    render(
        <I18nextProvider i18n={testI18n}>
            <MemoryRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
                <Notification
                    notifications={notifications}
                    onMarkAsRead={onMarkAsRead}
                />
            </MemoryRouter>
        </I18nextProvider>
    );

    return { onMarkAsRead, notifications };
}

describe("Notification de nouvelle offre (gestionnaire)", () => {
    it("ne navigue pas si le marquage comme lu échoue", async () => {
        marquerNotificationLue.mockRejectedValue(new Error("Erreur serveur"));
        afficherNotifications();

        const description = await screen.findByText(
            "L'offre « Stage en développement » est en attente de validation."
        );

        fireEvent.click(
            within(description.closest("li")).getByRole("button", {
                name: /Nouvelle offre à valider/,
            })
        );

        expect(await screen.findByRole("alert")).toHaveTextContent(
            traductionFr.notifications.read_error
        );
        expect(marquerNotificationLue).toHaveBeenCalledWith(5);
        expect(mockNavigate).not.toHaveBeenCalled();
    });

    it("navigue vers la page des offres après le clic", async () => {
        afficherNotifications();

        const description = await screen.findByText(
            "L'offre « Stage en développement » est en attente de validation."
        );

        fireEvent.click(
            within(description.closest("li")).getByRole("button", {
                name: /Nouvelle offre à valider/,
            })
        );

        await waitFor(() => {
            expect(mockNavigate).toHaveBeenCalledWith(
                "/gestionnaire/offres/details"
            );
        });
    });

    it("marque la notification comme lue et navigue vers les offres", async () => {
        const { onMarkAsRead } = afficherNotifications();

        const description = await screen.findByText(
            "L'offre « Stage en développement » est en attente de validation."
        );

        fireEvent.click(
            within(description.closest("li")).getByRole("button", {
                name: /Nouvelle offre à valider/,
            })
        );

        await waitFor(() => {
            expect(marquerNotificationLue).toHaveBeenCalledWith(5);
        });

        expect(onMarkAsRead).toHaveBeenCalledWith(5);
        expect(mockNavigate).toHaveBeenCalledWith(
            "/gestionnaire/offres/details"
        );
    });

    it("affiche les titres et descriptions traduits", async () => {
        afficherNotifications();

        expect(
            await screen.findByText(
                "L'offre « Stage en développement » est en attente de validation."
            )
        ).toBeInTheDocument();

        expect(
            screen.getAllByText("Nouvelle offre à valider")
        ).toHaveLength(1);
    });
});