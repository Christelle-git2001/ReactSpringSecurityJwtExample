import { createInstance } from "i18next";
import { getNotifications, marquerNotificationLue } from "../../../api/notification.jsx";
import { getCandidaturesEmployeur, getOffresEmployeur } from "../../../api/employeur.jsx";
import traductionFr from "../../../locales/fr/translation.json";
import { creerNotificationsMock, creerCandidaturesMock, creerOffresMock } from "../../mocks/notification.js";
import { fireEvent, render, screen, waitFor, within } from "@testing-library/react";
import { I18nextProvider } from "react-i18next";
import { MemoryRouter } from "react-router-dom";
import Notification from "../../../component/Notification/Notification.jsx";

jest.mock("../../../api/notification.jsx", () => ({
    getNotifications: jest.fn(),
    marquerNotificationLue: jest.fn(),
}));

jest.mock("../../../api/employeur.jsx", () => ({
    getCandidaturesEmployeur: jest.fn(),
    getOffresEmployeur: jest.fn(),
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

    getCandidaturesEmployeur.mockResolvedValue(creerCandidaturesMock());
    getOffresEmployeur.mockResolvedValue(creerOffresMock());

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

const DESCRIPTION_CANDIDATURE = "Jean Tremblay a postulé à votre offre : Stage en développement";

describe("Notification de nouvelle candidature (employeur)", () => {
    it("ne navigue pas si le marquage comme lu échoue", async () => {
        marquerNotificationLue.mockRejectedValue(new Error("Erreur serveur"));
        afficherNotifications();

        const description = await screen.findByText(DESCRIPTION_CANDIDATURE);
        fireEvent.click(within(description.closest("li")).getByRole("button", {
            name: "Nouvelle candidature",
        }));

        expect(await screen.findByRole("alert")).toHaveTextContent(
            traductionFr.notifications.read_error
        );
        expect(marquerNotificationLue).toHaveBeenCalledWith(1);
        expect(mockNavigate).not.toHaveBeenCalled();
    });

    it("affiche une erreur si la récupération de la candidature échoue", async () => {
        getCandidaturesEmployeur.mockRejectedValue(new Error("Erreur réseau"));
        afficherNotifications();

        const description = await screen.findByText(DESCRIPTION_CANDIDATURE);
        fireEvent.click(within(description.closest("li")).getByRole("button", {
            name: "Nouvelle candidature",
        }));

        expect(await screen.findByRole("alert")).toHaveTextContent(
            traductionFr.notifications.open_error
        );
        expect(mockNavigate).not.toHaveBeenCalled();
        expect(marquerNotificationLue).not.toHaveBeenCalled();
    });

    it("ouvre la candidature, marque la notification comme lue et demande la fermeture du Drawer", async () => {
        const { onClose } = afficherNotifications();

        const description = await screen.findByText(DESCRIPTION_CANDIDATURE);
        fireEvent.click(within(description.closest("li")).getByRole("button", {
            name: "Nouvelle candidature",
        }));

        await waitFor(() => expect(mockNavigate).toHaveBeenCalledWith(
            "/employeur/candidatures/details",
            {
                state: {
                    candidature: creerCandidaturesMock()[0],
                    offer: creerOffresMock()[0],
                    from: "/employeur"
                }
            }
        ));

        expect(getCandidaturesEmployeur).toHaveBeenCalled();
        expect(getOffresEmployeur).toHaveBeenCalled();
        expect(marquerNotificationLue).toHaveBeenCalledWith(1);
        expect(onClose).toHaveBeenCalledTimes(1);
    });

    it("affiche les titres et descriptions traduits", async () => {
        afficherNotifications();

        expect(
            await screen.findByText(DESCRIPTION_CANDIDATURE)
        ).toBeInTheDocument();

        expect(
            screen.getAllByText("Nouvelle candidature")
        ).toHaveLength(1);
    });
});