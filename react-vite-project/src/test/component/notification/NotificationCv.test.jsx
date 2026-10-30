import { createInstance } from "i18next";
import { marquerNotificationLue } from "../../../api/notification.jsx";
import traductionFr from "../../../locales/fr/translation.json";
import { creerNotificationsMock } from "../../mocks/notification.js";
import { fireEvent, render, screen, waitFor, within } from "@testing-library/react";
import { I18nextProvider } from "react-i18next";
import { MemoryRouter } from "react-router-dom";
import Notification from "../../../component/Notification/Notification.jsx";
import {obtenirCvGestionnaire, obtenirOffreGestionnaire} from "../../../api/gestionnaire.jsx";

jest.mock("../../../api/gestionnaire.jsx", () => ({
    obtenirCvGestionnaire: jest.fn(),
    obtenirOffreGestionnaire: jest.fn(),
}));

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
    obtenirCvGestionnaire.mockResolvedValue({ id: 20, fileName: "junior.pdf", statut: "EN_ATTENTE" });
    obtenirOffreGestionnaire.mockResolvedValue({ id: 1, title: "Stage en développement" });
});

function afficherNotifications() {
    const notifications = creerNotificationsMock();
    const onMarkAsRead = jest.fn();

    render(
        <I18nextProvider i18n={testI18n}>
            <MemoryRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
                <Notification
                    notifications={notifications.filter(n => !n.estLu)}
                    onMarkAsRead={onMarkAsRead}
                />
            </MemoryRouter>
        </I18nextProvider>
    );

    return { onMarkAsRead, notifications };
}

describe("Notification de nouveau CV (gestionnaire)", () => {
    it("ne navigue pas si le marquage comme lu échoue", async () => {
        marquerNotificationLue.mockRejectedValue(new Error("Erreur serveur"));
        afficherNotifications();

        const description = await screen.findByText("Junior Belle a téléversé un CV.");

        fireEvent.click(
            within(description.closest("li")).getByRole("button", {
                name: /Nouveau CV à valider/,
            })
        );

        expect(await screen.findByRole("alert")).toHaveTextContent(
            traductionFr.notifications.read_error
        );
        expect(marquerNotificationLue).toHaveBeenCalledWith(3);
        expect(mockNavigate).not.toHaveBeenCalled();

    });

    it("navigue vers la page des CV après le clic", async () => {
        afficherNotifications();

        const description = await screen.findByText("Junior Belle a téléversé un CV.");

        fireEvent.click(
            within(description.closest("li")).getByRole("button", {
                name: /Nouveau CV à valider/,
            })
        );

        await waitFor(() => {
            expect(mockNavigate).toHaveBeenCalledWith("/gestionnaire/cvs/details", {
                state: { cv: expect.any(Object), from: "/gestionnaire" },
            });
        });
    });

    it("marque la notification comme lue et navigue vers les CV", async () => {
        const { onMarkAsRead } = afficherNotifications();

        const description = await screen.findByText("Junior Belle a téléversé un CV.");

        fireEvent.click(
            within(description.closest("li")).getByRole("button", {
                name: /Nouveau CV à valider/,
            })
        );

        await waitFor(() => {
            expect(marquerNotificationLue).toHaveBeenCalledWith(3);
        });

        expect(onMarkAsRead).toHaveBeenCalledWith(3);

        expect(mockNavigate).toHaveBeenCalledWith("/gestionnaire/cvs/details", {
            state: { cv: expect.any(Object), from: "/gestionnaire" },
        });
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
    });
});