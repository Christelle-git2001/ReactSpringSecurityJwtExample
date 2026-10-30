import { createInstance } from "i18next";
import { marquerNotificationLue } from "../../../api/notification.jsx";
import traductionFr from "../../../locales/fr/translation.json";
import { creerNotificationsMock } from "../../mocks/notification.js";
import { fireEvent, render, screen, waitFor, within } from "@testing-library/react";
import { I18nextProvider } from "react-i18next";
import { MemoryRouter } from "react-router-dom";
import Notification from "../../../component/Notification/Notification.jsx";
import {obtenirCvGestionnaire, obtenirOffreGestionnaire} from "../../../api/gestionnaire.jsx";
import {getCandidaturesEmployeur, getOffresEmployeur} from "../../../api/employeur.jsx";

jest.mock("../../../api/gestionnaire.jsx", () => ({
    obtenirCvGestionnaire: jest.fn(),
    obtenirOffreGestionnaire: jest.fn(),
}));
jest.mock("../../../api/employeur.jsx", () => ({
    getCandidaturesEmployeur: jest.fn(),
    getOffresEmployeur: jest.fn(),
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
    getCandidaturesEmployeur.mockResolvedValue([
        { id: 100, offreId: 1, etudiantFirstName: "Jean", etudiantLastName: "Tremblay" }
    ]);
    getOffresEmployeur.mockResolvedValue([
        { id: 1, title: "Stage en développement" }
    ]);
});

function afficherNotifications(notifications = [], onMarkAsRead = jest.fn()) {
    return render(
        <I18nextProvider i18n={testI18n}>
            <MemoryRouter>
                <Notification
                    notifications={notifications}
                    onMarkAsRead={onMarkAsRead}
                />
            </MemoryRouter>
        </I18nextProvider>
    );
}

describe("Notification - comportements communs", () => {
    it("affiche les notifications les plus récentes en premier", () => {
        afficherNotifications(creerNotificationsMock().filter(n => !n.estLu));

        const items = screen.getAllByRole("listitem");

        expect(items).toHaveLength(4);
        expect(items[0]).toHaveTextContent("Stage en développement");
        expect(items[1]).toHaveTextContent("Nouvelle offre à valider");
        expect(items[2]).toHaveTextContent("Junior Belle");
        expect(items[3]).toHaveTextContent("Mercedes Belle");
    });

    it("inverse le tri des notifications au clic", () => {
        afficherNotifications(creerNotificationsMock().filter(n => !n.estLu));

        fireEvent.click(screen.getByRole("button", {
            name: traductionFr.notifications.oldest_first,
        }));

        const items = screen.getAllByRole("listitem");

        expect(items[0]).toHaveTextContent("Mercedes Belle");
        expect(items[1]).toHaveTextContent("Junior Belle");
        expect(items[2]).toHaveTextContent("Nouvelle offre à valider");
        expect(items[3]).toHaveTextContent("Stage en développement");
    });

    it("masque les notifications déjà lues", () => {
        afficherNotifications(creerNotificationsMock().filter(n => !n.estLu));

        expect(screen.queryByText("Ouriela Belle a téléversé un CV."))
            .not.toBeInTheDocument();

        expect(screen.getAllByRole("listitem")).toHaveLength(4);
    });

    it("affiche un message quand il n'y a aucune notification", () => {
        afficherNotifications([]);

        expect(screen.getByText("Aucune notification")).toBeInTheDocument();
        expect(screen.getAllByRole("listitem")).toHaveLength(1);
    });

    it("marque une notification comme lue et navigue vers sa route", async () => {
        const onMarkAsRead = jest.fn();
        afficherNotifications(creerNotificationsMock().filter(n => !n.estLu), onMarkAsRead);

        const description = screen.getByText(
            "Jean Tremblay a postulé à votre offre : Stage en développement"
        );

        fireEvent.click(within(description.closest("li")).getByRole("button"));

        await waitFor(() => {
            expect(marquerNotificationLue).toHaveBeenCalledWith(1);
        });

        expect(onMarkAsRead).toHaveBeenCalledWith(1);
        expect(mockNavigate).toHaveBeenCalledWith(
            "/employeur/candidatures/details",
            {
                state: {
                    candidature: expect.any(Object),
                    offer: expect.any(Object),
                    from: "/employeur"
                }
            }
        );
    });

    it("affiche une erreur lorsque le marquage comme lu échoue", async () => {
        marquerNotificationLue.mockRejectedValue(new Error("Erreur serveur"));

        afficherNotifications(creerNotificationsMock().filter(n => !n.estLu));

        const description = screen.getByText(
            "Jean Tremblay a postulé à votre offre : Stage en développement"
        );

        fireEvent.click(within(description.closest("li")).getByRole("button"));

        expect(await screen.findByRole("alert")).toHaveTextContent(
            traductionFr.notifications.read_error
        );

        expect(mockNavigate).not.toHaveBeenCalled();
    });
});
