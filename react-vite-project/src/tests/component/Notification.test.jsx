import { createInstance } from "i18next";
import { getNotifications, marquerNotificationLue } from "../../api/notification.jsx";
import { obtenirCvGestionnaire } from "../../api/gestionnaire.jsx";
import traductionFr from "../../locales/fr/translation.json";
import { creerNotificationsMock, creerCvsMock } from "../mocks/notification.js";
import { act, fireEvent, render, screen, waitFor, within } from "@testing-library/react";
import { I18nextProvider } from "react-i18next";
import { MemoryRouter } from "react-router-dom";
import Notification from "../../component/Notification/Notification.jsx";

jest.mock("../../api/notification.jsx", () => ({
    getNotifications: jest.fn(),
    marquerNotificationLue: jest.fn(),
}));

jest.mock("../../api/gestionnaire.jsx", () => ({
    obtenirCvGestionnaire: jest.fn(),
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
    jest.resetAllMocks();
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
                <Notification userId={5} onClose={jest.fn()} />
            </MemoryRouter>
        </I18nextProvider>
    );
}

describe("Notification", () => {
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

    it("ne navigue pas si le marquage comme lu échoue", async () => {
        marquerNotificationLue.mockRejectedValue(new Error("Erreur serveur"));
        afficherNotifications();

        const description = await screen.findByText("Junior Belle a téléversé un CV.");
        fireEvent.click(within(description.closest("li")).getByRole("button", {
            name: "Nouveau CV à valider",
        }));

        expect(await screen.findByRole("alert")).toHaveTextContent(
            traductionFr.notifications.read_error
        );
        expect(marquerNotificationLue).toHaveBeenCalledWith(2);
        expect(mockNavigate).not.toHaveBeenCalled();
    });

    it("affiche une erreur si la récupération du CV échoue", async () => {
        obtenirCvGestionnaire.mockRejectedValue(new Error("Erreur serveur"));
        afficherNotifications();

        const description = await screen.findByText("Junior Belle a téléversé un CV.");
        fireEvent.click(within(description.closest("li")).getByRole("button", {
            name: "Nouveau CV à valider",
        }));

        expect(await screen.findByRole("alert")).toHaveTextContent(
            traductionFr.notifications.open_error
        );
        expect(mockNavigate).not.toHaveBeenCalled();
        expect(marquerNotificationLue).not.toHaveBeenCalled();
    });

    it("charge le CV cible et ouvre la page de details avec ses donnees", async () => {
        afficherNotifications();

        const description = await screen.findByText("Junior Belle a téléversé un CV.");
        fireEvent.click(within(description.closest("li")).getByRole("button", {
            name: "Nouveau CV à valider",
        }));

        await waitFor(() => expect(mockNavigate).toHaveBeenCalledWith(
            "/gestionnaire/cvs/details",
            { state: { cv: creerCvsMock()[1], from: "/gestionnaire" } }
        ));
        expect(obtenirCvGestionnaire).toHaveBeenCalledWith("/gestionnaire/cv/20");
        expect(marquerNotificationLue).toHaveBeenCalledWith(2);
    });

    it("conserve la notification et reste sur place si le CV est introuvable", async () => {
        obtenirCvGestionnaire.mockRejectedValue(Object.assign(new Error(), { status: 404 }));
        afficherNotifications();

        const description = await screen.findByText("Junior Belle a téléversé un CV.");
        fireEvent.click(within(description.closest("li")).getByRole("button", {
            name: "Nouveau CV à valider",
        }));

        expect(await screen.findByRole("alert")).toHaveTextContent(
            traductionFr.notifications.cv_unavailable
        );
        expect(mockNavigate).not.toHaveBeenCalled();
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