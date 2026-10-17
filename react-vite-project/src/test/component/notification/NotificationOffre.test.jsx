import { createInstance } from "i18next";
import { getNotifications, marquerNotificationLue } from "../../../api/notification.jsx";
import { obtenirOffreGestionnaire } from "../../../api/gestionnaire.jsx";
import traductionFr from "../../../locales/fr/translation.json";
import { creerNotificationsMock, creerOffresMock } from "../../mocks/notification.js";
import { fireEvent, render, screen, waitFor, within } from "@testing-library/react";
import { I18nextProvider } from "react-i18next";
import { MemoryRouter } from "react-router-dom";
import Notification from "../../../component/Notification/Notification.jsx";

jest.mock("../../../api/notification.jsx", () => ({
    getNotifications: jest.fn(),
    marquerNotificationLue: jest.fn(),
}));

jest.mock("../../../api/gestionnaire.jsx", () => ({
    obtenirCvGestionnaire: jest.fn(),
    obtenirOffreGestionnaire: jest.fn(),
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

    obtenirOffreGestionnaire.mockImplementation(async path =>
        creerOffresMock().find(offre => path === `/gestionnaire/offres/${offre.id}`)
    );

    marquerNotificationLue.mockImplementation(async id => ({
        ...creerNotificationsMock().find(notification => notification.id === id),
        estLu: true,
    }));
});

function afficherNotifications(onClose = jest.fn()) {
    render(
        <I18nextProvider i18n={testI18n}>
            <MemoryRouter>
                <Notification userId={5} onClose={onClose} />
            </MemoryRouter>
        </I18nextProvider>
    );
    return { onClose };
}

describe("Notification de nouvelle offre (gestionnaire)", () => {
    it("ne navigue pas si le marquage comme lu échoue", async () => {
        marquerNotificationLue.mockRejectedValue(new Error("Erreur serveur"));
        afficherNotifications();

        const description = await screen.findByText("Une nouvelle offre de stage pour le poste de 'Stage en développement' a été soumise.");
        fireEvent.click(within(description.closest("li")).getByRole("button", {
            name: "Nouvelle offre à valider",
        }));

        expect(await screen.findByRole("alert")).toHaveTextContent(
            traductionFr.notifications.read_error
        );
        expect(marquerNotificationLue).toHaveBeenCalledWith(4);
        expect(mockNavigate).not.toHaveBeenCalled();
    });

    it("affiche une erreur si la récupération de l'offre échoue", async () => {
        obtenirOffreGestionnaire.mockRejectedValue(new Error("Erreur serveur"));
        afficherNotifications();

        const description = await screen.findByText("Une nouvelle offre de stage pour le poste de 'Stage en développement' a été soumise.");
        fireEvent.click(within(description.closest("li")).getByRole("button", {
            name: "Nouvelle offre à valider",
        }));

        expect(await screen.findByRole("alert")).toHaveTextContent(
            traductionFr.notifications.open_error
        );
        expect(mockNavigate).not.toHaveBeenCalled();
        expect(marquerNotificationLue).not.toHaveBeenCalled();
    });

    it("ouvre l'offre, marque la notification comme lue et demande la fermeture du Drawer", async () => {
        const { onClose } = afficherNotifications();

        const description = await screen.findByText("Une nouvelle offre de stage pour le poste de 'Stage en développement' a été soumise.");
        fireEvent.click(within(description.closest("li")).getByRole("button", {
            name: "Nouvelle offre à valider",
        }));

        await waitFor(() => expect(mockNavigate).toHaveBeenCalledWith(
            "/gestionnaire/offres/details",
            { state: { offer: creerOffresMock()[0], from: "/gestionnaire" } }
        ));
        expect(obtenirOffreGestionnaire).toHaveBeenCalledWith("/gestionnaire/offres/1");
        expect(marquerNotificationLue).toHaveBeenCalledWith(4);
        expect(onClose).toHaveBeenCalledTimes(1);
        expect(screen.queryByText("Une nouvelle offre de stage pour le poste de 'Stage en développement' a été soumise.")).not.toBeInTheDocument();
    });

    it("conserve la notification et reste sur place si l'offre est introuvable", async () => {
        obtenirOffreGestionnaire.mockRejectedValue(Object.assign(new Error(), { status: 404 }));
        afficherNotifications();

        const description = await screen.findByText("Une nouvelle offre de stage pour le poste de 'Stage en développement' a été soumise.");
        fireEvent.click(within(description.closest("li")).getByRole("button", {
            name: "Nouvelle offre à valider",
        }));

        expect(await screen.findByRole("alert")).toHaveTextContent(
            traductionFr.notifications.offer_unavailable
        );
        expect(mockNavigate).not.toHaveBeenCalled();
        expect(marquerNotificationLue).not.toHaveBeenCalled();
    });

    it("affiche les titres et descriptions traduits", async () => {
        afficherNotifications();

        expect(
            await screen.findByText("Une nouvelle offre de stage pour le poste de 'Stage en développement' a été soumise.")
        ).toBeInTheDocument();

        expect(
            screen.getAllByText("Nouvelle offre à valider")
        ).toHaveLength(1);

        expect(getNotifications).toHaveBeenCalledWith(5);
    });
});