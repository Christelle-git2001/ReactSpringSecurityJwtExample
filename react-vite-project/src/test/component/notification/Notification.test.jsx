import { createInstance } from "i18next";
import { getNotifications } from "../../../api/notification.jsx";
import traductionFr from "../../../locales/fr/translation.json";
import { creerNotificationsMock } from "../../mocks/notification.js";
import { act, fireEvent, render, screen, waitFor } from "@testing-library/react";
import { I18nextProvider } from "react-i18next";
import { MemoryRouter } from "react-router-dom";
import Notification from "../../../component/Notification/Notification.jsx";

jest.mock("../../../api/notification.jsx", () => ({
    getNotifications: jest.fn(),
    marquerNotificationLue: jest.fn(),
}));

jest.mock("../../../api/gestionnaire.jsx", () => ({
    obtenirCvGestionnaire: jest.fn(),
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
    jest.resetAllMocks();
    await testI18n.changeLanguage("fr");

    getNotifications.mockResolvedValue(creerNotificationsMock());

});

function afficherNotifications() {
    return render(
        <I18nextProvider i18n={testI18n}>
            <MemoryRouter>
                <Notification userId={5} onClose={jest.fn()} />
            </MemoryRouter>
        </I18nextProvider>
    );
}

describe("Notification - comportements communs", () => {
    it("recharge les notifications quand userId change", async () => {
        const { rerender } = render(
            <I18nextProvider i18n={testI18n}>
                <MemoryRouter>
                    <Notification userId={5} />
                </MemoryRouter>
            </I18nextProvider>
        );

        await screen.findByText("Mercedes Belle a téléversé un CV.");
        expect(getNotifications).toHaveBeenCalledWith(5);
        getNotifications.mockResolvedValue([]);

        await act(async () => {
            rerender(
                <I18nextProvider i18n={testI18n}>
                    <MemoryRouter>
                        <Notification userId={10} />
                    </MemoryRouter>
                </I18nextProvider>
            );
        });

        await waitFor(() => {
            expect(getNotifications).toHaveBeenCalledWith(10);
            expect(screen.queryByText("Mercedes Belle a téléversé un CV.")).not.toBeInTheDocument();
        });
        expect(screen.queryByRole("listitem")).not.toBeInTheDocument();
        expect(screen.getByText("Aucune notification")).toBeInTheDocument();
    });

    it("gère une réponse null de l'API", async () => {
        getNotifications.mockResolvedValue(null);

        await act(async () => {
            afficherNotifications();
        });

        expect(getNotifications).toHaveBeenCalledWith(5);
        expect(screen.getByText("Aucune notification")).toBeInTheDocument();
        expect(screen.queryByRole("listitem")).not.toBeInTheDocument();
        expect(screen.queryByRole("alert")).not.toBeInTheDocument();
    });

    it("revient à la liste après une erreur sans recharger les notifications", async () => {
        getNotifications.mockRejectedValue(new Error("Erreur serveur"));
        afficherNotifications();

        expect(await screen.findByRole("alert")).toHaveTextContent(
            traductionFr.notifications.load_error
        );
        expect(getNotifications).toHaveBeenCalledTimes(1);

        fireEvent.click(screen.getByRole("button", {
            name: traductionFr.actions.back,
        }));

        expect(screen.queryByRole("alert")).not.toBeInTheDocument();
        expect(screen.getByText("Aucune notification")).toBeInTheDocument();
        expect(getNotifications).toHaveBeenCalledTimes(1);
    });

    it("affiche les notifications les plus récentes en premier", async () => {
        afficherNotifications();

        const items = await screen.findAllByRole("listitem");

        expect(items[0]).toHaveTextContent("Junior Belle");
        expect(items[1]).toHaveTextContent("Mercedes Belle");
    });

    it("inverse le tri des notifications au clic", async () => {
        afficherNotifications();

        await screen.findAllByRole("listitem");
        fireEvent.click(screen.getByRole("button", {
            name: traductionFr.notifications.oldest_first,
        }));

        const items = screen.getAllByRole("listitem");
        expect(items[0]).toHaveTextContent("Mercedes Belle");
        expect(items[1]).toHaveTextContent("Junior Belle");
    });

    it("affiche une erreur si le chargement des notifications échoue", async () => {
        getNotifications.mockRejectedValue(new Error("Erreur serveur"));
        afficherNotifications();

        expect(await screen.findByRole("alert")).toHaveTextContent(
            traductionFr.notifications.load_error
        );
        expect(screen.queryByRole("listitem")).not.toBeInTheDocument();
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