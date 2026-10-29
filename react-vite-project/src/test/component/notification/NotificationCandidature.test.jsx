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

function afficherNotifications() {
    const notifications = creerNotificationsMock();
    const onMarkAsRead = jest.fn();

    render(
        <I18nextProvider i18n={testI18n}>
            <MemoryRouter>
                <Notification
                    notifications={notifications.filter(n => !n.estLu)}
                    onMarkAsRead={onMarkAsRead}
                />
            </MemoryRouter>
        </I18nextProvider>
    );

    return { onMarkAsRead, notifications };
}

const DESCRIPTION_CANDIDATURE = "Jean Tremblay a postulé à votre offre : Stage en développement";

describe("Notification de nouvelle candidature (employeur)", () => {
    it("ne navigue pas si le marquage comme lu échoue", async () => {
        marquerNotificationLue.mockRejectedValue(new Error("Erreur serveur"));
        afficherNotifications();

        const description = await screen.findByText(DESCRIPTION_CANDIDATURE);
        fireEvent.click(within(description.closest("li")).getByRole("button", {
                            name: /Nouvelle candidature/,
                        }));

        expect(await screen.findByRole("alert")).toHaveTextContent(
            traductionFr.notifications.read_error
        );
        expect(marquerNotificationLue).toHaveBeenCalledWith(1);
        expect(mockNavigate).not.toHaveBeenCalled();
    });

    it("navigue vers la page des candidatures après le clic", async () => {
        afficherNotifications();

        const description = await screen.findByText(DESCRIPTION_CANDIDATURE);

        fireEvent.click(
           within(description.closest("li")).getByRole("button", {
               name: /Nouvelle candidature/,
           })
        );

        await waitFor(() => {
            expect(mockNavigate).toHaveBeenCalledWith(
                "/employeur/candidatures/details"
            );
        });
    });

    it("marque la notification comme lue et navigue vers les candidatures", async () => {
        const { onMarkAsRead } = afficherNotifications();

        const description = await screen.findByText(DESCRIPTION_CANDIDATURE);

        fireEvent.click(
            within(description.closest("li")).getByRole("button", {
                name: /Nouvelle candidature/,
            })
        );

        await waitFor(() => {
            expect(marquerNotificationLue).toHaveBeenCalledWith(1);
        });

        expect(onMarkAsRead).toHaveBeenCalledWith(1);

        expect(mockNavigate).toHaveBeenCalledWith(
            "/employeur/candidatures/details"
        );
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