import { createInstance } from "i18next";
import { marquerNotificationLue } from "../../../api/notification.jsx";
import traductionFr from "../../../locales/fr/translation.json";
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
        resources: {
            fr: { translation: traductionFr },
        },
        interpolation: { escapeValue: false },
    });
});

beforeEach(async () => {
    jest.clearAllMocks();
    await testI18n.changeLanguage("fr");
    marquerNotificationLue.mockResolvedValue({ estLu: true });
});

function creerNotificationNouvelleOffre() {
    return {
        id: 20,
        notificationType: "NOUVELLE_OFFRE_STAGE",
        titre: "notification.etudiant.nouvelleOffre.titre",
        description: "notification.etudiant.nouvelleOffre.description",
        dateNotifiee: "2026-10-19T10:00:00",
        estLu: false,
        variables: {
            OFFRE_ID: "12",
            OFFRE_TITRE: "Développeur Web",
            ENTREPRISE_NOM: "Malinois Inc",
        },
    };
}

function afficherNotifications(notifications, onMarkAsRead = jest.fn()) {
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

    return { onMarkAsRead };
}

describe("NotificationNouvelleOffreApprouvee", () => {
    it("affiche une notification lorsqu'une nouvelle offre est disponible", async () => {
        afficherNotifications([creerNotificationNouvelleOffre()]);

        expect(
            await screen.findByText("Nouvelle offre de stage")
        ).toBeInTheDocument();
    });

    it("affiche le titre de l'offre et le nom de l'entreprise", async () => {
        afficherNotifications([creerNotificationNouvelleOffre()]);

        expect(
            await screen.findByText(
                /Une offre de Développeur Web est maintenant disponible chez Malinois Inc\.?/i
            )
        ).toBeInTheDocument();
    });

    it("marque la notification comme lue et navigue vers la page de l'étudiant", async () => {
        const { onMarkAsRead } = afficherNotifications([
            creerNotificationNouvelleOffre(),
        ]);

        const description = await screen.findByText(
            /Une offre de Développeur Web est maintenant disponible chez Malinois Inc\.?/i
        );

        fireEvent.click(
            within(description.closest("li")).getByRole("button", {
                name: /Nouvelle offre de stage/i,
            })
        );

        await waitFor(() => {
            expect(marquerNotificationLue).toHaveBeenCalledWith(20);
            expect(mockNavigate).toHaveBeenCalledWith("/etudiant");
        });

        expect(onMarkAsRead).toHaveBeenCalledWith(20);
    });

    it("ne navigue pas si le marquage comme lue échoue", async () => {
        marquerNotificationLue.mockRejectedValue(
            new Error("Erreur serveur")
        );

        afficherNotifications([creerNotificationNouvelleOffre()]);

        const description = await screen.findByText(
            /Une offre de Développeur Web est maintenant disponible chez Malinois Inc\.?/i
        );

        fireEvent.click(
            within(description.closest("li")).getByRole("button", {
                name: /Nouvelle offre de stage/i,
            })
        );

        expect(await screen.findByRole("alert")).toHaveTextContent(
            traductionFr.notifications.read_error
        );

        expect(marquerNotificationLue).toHaveBeenCalledWith(20);
        expect(mockNavigate).not.toHaveBeenCalled();
    });
});