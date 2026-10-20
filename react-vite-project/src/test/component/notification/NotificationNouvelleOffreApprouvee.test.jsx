
import { createInstance } from "i18next";
import {
    fireEvent,
    render,
    screen,
    waitFor
} from "@testing-library/react";
import { I18nextProvider } from "react-i18next";
import { MemoryRouter } from "react-router-dom";

import {
    getNotifications,
    marquerNotificationLue
} from "../../../api/notification.jsx";

import traductionFr from "../../../locales/fr/translation.json";
import traductionEn from "../../../locales/en/translation.json";

import Notification from "../../../component/Notification/Notification.jsx";

jest.mock("../../../api/notification.jsx", () => ({
    getNotifications: jest.fn(),
    marquerNotificationLue: jest.fn()
}));

const mockNavigate = jest.fn();

jest.mock("react-router-dom", () => ({
    ...jest.requireActual("react-router-dom"),
    useNavigate: () => mockNavigate
}));

const testI18n = createInstance();

beforeAll(async () => {
    await testI18n.init({
        lng: "fr",
        fallbackLng: "fr",
        resources: {
            fr: { translation: traductionFr },
            en: { translation: traductionEn }
        },
        interpolation: {
            escapeValue: false
        }
    });
});

beforeEach(async () => {
    jest.resetAllMocks();
    await testI18n.changeLanguage("fr");

    getNotifications.mockResolvedValue([]);
    marquerNotificationLue.mockResolvedValue({});
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
            ENTREPRISE_NOM: "Malinois Inc"
        }
    };
}

function afficherNotifications(onClose = jest.fn()) {
    return render(
        <I18nextProvider i18n={testI18n}>
            <MemoryRouter>
                <Notification
                    userId={5}
                    onClose={onClose}
                />
            </MemoryRouter>
        </I18nextProvider>
    );
}

describe("NotificationNouvelleOffreApprouvee", () => {

    it("affiche une notification lorsqu'une nouvelle offre est disponible", async () => {
        getNotifications.mockResolvedValue([
            creerNotificationNouvelleOffre()
        ]);

        afficherNotifications();

        expect(
            await screen.findByText("Nouvelle offre de stage")
        ).toBeInTheDocument();
    });

    it("affiche le titre de l'offre et le nom de l'entreprise", async () => {
        getNotifications.mockResolvedValue([
            creerNotificationNouvelleOffre()
        ]);

        afficherNotifications();

        expect(
            await screen.findByText(
                /Une offre de Développeur Web est maintenant disponible chez Malinois Inc\./i
            )
        ).toBeInTheDocument();
    });

    it("marque la notification comme lue et ouvre la page de l'étudiant au clic", async () => {
        getNotifications.mockResolvedValue([
            creerNotificationNouvelleOffre()
        ]);

        const onCloseMock = jest.fn();

        afficherNotifications(onCloseMock);

        const description = await screen.findByText(
            /Une offre de Développeur Web est maintenant disponible chez Malinois Inc\./i
        );

        fireEvent.click(description);

        await waitFor(() => {
            expect(marquerNotificationLue).toHaveBeenCalledWith(20);

            expect(mockNavigate).toHaveBeenCalledWith(
                "/etudiant",
                {
                    state: {
                        offreId: "12",
                        from: "/etudiant"
                    }
                }
            );

            expect(onCloseMock).toHaveBeenCalledTimes(1);
        });
    });

    it("navigue pas vers l'offre si le marquage comme lue échoue", async () => {
        getNotifications.mockResolvedValue([
            creerNotificationNouvelleOffre()
        ]);

        marquerNotificationLue.mockRejectedValue(
            new Error("Erreur serveur")
        );

        const onCloseMock = jest.fn();

        afficherNotifications(onCloseMock);

        const description = await screen.findByText(
            /Une offre de Développeur Web est maintenant disponible chez Malinois Inc\./i
        );

        fireEvent.click(description);

        expect(await screen.findByRole("alert")).toHaveTextContent(
            traductionFr.notifications.read_error
        );

        expect(marquerNotificationLue).toHaveBeenCalledWith(20);
        expect(mockNavigate).not.toHaveBeenCalled();
        expect(onCloseMock).not.toHaveBeenCalled();
    });

});
