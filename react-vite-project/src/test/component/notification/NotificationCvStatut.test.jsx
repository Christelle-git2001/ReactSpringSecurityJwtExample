import { createInstance } from "i18next";
import { getNotifications, marquerNotificationLue } from "../../../api/notification.jsx";
import traductionFr from "../../../locales/fr/translation.json";
import traductionEn from "../../../locales/en/translation.json";
import {findByText, fireEvent, render, screen, waitFor} from "@testing-library/react";
import { I18nextProvider } from "react-i18next";
import { MemoryRouter } from "react-router-dom";
import Notification from "../../../component/Notification/Notification.jsx";

jest.mock("../../../api/notification.jsx", () => ({
    getNotifications: jest.fn(),
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
            en: { translation: traductionEn }
        },
        interpolation: { escapeValue: false },
    });
});

beforeEach(async () => {
    jest.resetAllMocks();
    await testI18n.changeLanguage("fr");
});

function afficherNotifications(userId = 5) {
    return render(
        <I18nextProvider i18n={testI18n}>
            <MemoryRouter>
                <Notification userId={userId} onClose={jest.fn()} />
            </MemoryRouter>
        </I18nextProvider>
    );
}

describe("Notification - CV_STATUT_CHANGE", () => {

    it("affiche correctement une notification de CV refusé avec le bon texte", async () => {
        const mockNotifications = [
            {
                id: 1,
                notificationType: "CV_STATUT_CHANGE",
                titre: "notification.etudiant.validation.title_refusee",
                description: "notification.etudiant.validation.description_refusee",
                dateNotifiee: "2026-10-14T10:00:00",
                estLu: false,
                variables: {
                    STATUT_FICHIER: "REFUSEE"
                }
            }
        ];

        getNotifications.mockResolvedValue(mockNotifications);
        marquerNotificationLue.mockResolvedValue({});

        afficherNotifications();

        expect(await screen.findByText(/votre cv a été refusé/i)).toBeInTheDocument();
        expect(screen.getByText(/pour connaître les motifs du refus/i)).toBeInTheDocument();
    });

    it("affiche correctement une notification de CV accepté avec le bon texte", async () => {
        const mockNotifications = [
            {
                id: 1,
                notificationType: "CV_STATUT_CHANGE",
                titre: "notification.etudiant.validation.title_acceptee",
                description: "notification.etudiant.validation.description_acceptee",
                dateNotifiee: "2026-10-14T10:00:00",
                estLu: false,
                variables: {
                    STATUT_FICHIER: "ACCEPTEE"
                }
            }
        ];

        getNotifications.mockResolvedValue(mockNotifications);
        marquerNotificationLue.mockResolvedValue({});

        afficherNotifications();

        expect(await screen.findByText(/Votre CV a été accepté/i)).toBeInTheDocument();
        expect(screen.getByText(/Félicitations, votre CV a été validé !/i)).toBeInTheDocument();
    });

    it("marque la notification comme lue au clic", async () => {
        const mockNotifications = [
            {
                id: 2,
                notificationType: "CV_STATUT_CHANGE",
                titre: "notification.etudiant.validation.title_acceptee",
                description: "notification.etudiant.validation.description_acceptee",
                dateNotifiee: "2026-10-14T10:00:00",
                estLu: false,
                variables: {
                    STATUT_FICHIER: "ACCEPTEE"
                }
            }
        ];

        getNotifications.mockResolvedValue(mockNotifications);
        marquerNotificationLue.mockResolvedValue({});

        afficherNotifications();

        const item = await screen.findByText(/votre cv a été accepté/i);
        expect(item).toBeInTheDocument();

        fireEvent.click(item);

        await waitFor(() => {
            expect(marquerNotificationLue).toHaveBeenCalledWith(2);
        });
    });

    it("navigue vers la bonne route, marque comme lu et ferme le drawer au clic sur un CV accepté", async () => {
        const mockNotifications = [
            {
                id: 2,
                notificationType: "CV_STATUT_CHANGE",
                titre: "notification.etudiant.validation.title_acceptee",
                description: "notification.etudiant.validation.description_acceptee",
                dateNotifiee: "2026-10-14T10:00:00",
                estLu: false,
                variables: {
                    STATUT_FICHIER: "ACCEPTEE"
                }
            }
        ];

        getNotifications.mockResolvedValue(mockNotifications);
        marquerNotificationLue.mockResolvedValue({});

        const onCloseMock = jest.fn();
        render(
            <I18nextProvider i18n={testI18n}>
                <MemoryRouter>
                    <Notification userId={5} onClose={onCloseMock} />
                </MemoryRouter>
            </I18nextProvider>
        );

        const item = await screen.findByText(/votre cv a été accepté/i);
        fireEvent.click(item);

        await waitFor(() => {
            expect(marquerNotificationLue).toHaveBeenCalledWith(2);
            expect(mockNavigate).toHaveBeenCalledWith("/etudiant/cv#televerser");
            expect(onCloseMock).toHaveBeenCalledTimes(1);
        });
    });

    it("navigue vers la bonne route, marque comme lu et ferme le drawer au clic sur un CV refusee", async () => {
        const mockNotifications = [
            {
                id: 2,
                notificationType: "CV_STATUT_CHANGE",
                titre: "notification.etudiant.validation.title_refusee",
                description: "notification.etudiant.validation.description_refusee",
                dateNotifiee: "2026-10-14T10:00:00",
                estLu: false,
                variables: {
                    STATUT_FICHIER: "REFUSEE"
                }
            }
        ];

        getNotifications.mockResolvedValue(mockNotifications);
        marquerNotificationLue.mockResolvedValue({});

        const onCloseMock = jest.fn();
        render(
            <I18nextProvider i18n={testI18n}>
                <MemoryRouter>
                    <Notification userId={5} onClose={onCloseMock} />
                </MemoryRouter>
            </I18nextProvider>
        );

        const item = await screen.findByText(/pour connaître les motifs du refus/i);
        fireEvent.click(item);

        await waitFor(() => {
            expect(marquerNotificationLue).toHaveBeenCalledWith(2);
            expect(mockNavigate).toHaveBeenCalledWith("/etudiant/cv#televerser");
            expect(onCloseMock).toHaveBeenCalledTimes(1);
        });
    });

    it("affiche correctement le texte en anglais lorsque la langue change", async () => {
        await testI18n.changeLanguage("en");

        const mockNotifications = [
            {
                id: 1,
                notificationType: "CV_STATUT_CHANGE",
                titre: "notification.etudiant.validation.title_acceptee",
                description: "notification.etudiant.validation.description_acceptee",
                dateNotifiee: "2026-10-14T10:00:00",
                estLu: false,
                variables: { STATUT_FICHIER: "ACCEPTEE" }
            }
        ];

        getNotifications.mockResolvedValue(mockNotifications);
        afficherNotifications();

        expect(await screen.findByText(/Your CV has been accepted/i)).toBeInTheDocument();
    });

    it("n'ouvre pas la page et affiche une erreur si le marquage comme lu échoue lors du clic sur une notification de statut", async () => {
        const mockNotifications = [
            {
                id: 1,
                notificationType: "CV_STATUT_CHANGE",
                titre: "notification.etudiant.validation.title_acceptee",
                description: "notification.etudiant.validation.description_acceptee",
                dateNotifiee: "2026-10-14T10:00:00",
                estLu: false,
                variables: {
                    STATUT_FICHIER: "ACCEPTEE"
                }
            }
        ];

        getNotifications.mockResolvedValue(mockNotifications);
        marquerNotificationLue.mockRejectedValue(new Error("Erreur serveur"));

        const onCloseMock = jest.fn();
        afficherNotifications(5, onCloseMock);

        const item = await screen.findByText(/votre cv a été accepté/i);
        fireEvent.click(item);

        expect(await screen.findByRole("alert")).toHaveTextContent(
            traductionFr.notifications.read_error
        );
        expect(marquerNotificationLue).toHaveBeenCalledWith(1);
        expect(mockNavigate).not.toHaveBeenCalled();
        expect(onCloseMock).not.toHaveBeenCalled();
    });

    it("utilise la configuration par défaut (fallback) si le statut du fichier est absent ou inconnu", async () => {
        const mockNotifications = [
            {
                id: 3,
                notificationType: "CV_STATUT_CHANGE",
                titre: "notification.etudiant.validation.title_default",
                description: "notification.etudiant.validation.description_default",
                dateNotifiee: "2026-10-14T10:00:00",
                estLu: false,
                variables: {

                }
            }
        ];

        getNotifications.mockResolvedValue(mockNotifications);
        marquerNotificationLue.mockResolvedValue({});

        afficherNotifications();

        await waitFor(() => {
            expect(getNotifications).toHaveBeenCalled();
        });
        expect(await screen.findByText(/Vous avez reçu une nouvelle offre à valider./i)).toBeInTheDocument();
        expect(await screen.findByText(/Notifications/i)).toBeInTheDocument();
    });

    it("retire uniquement la notification cliquée de la liste et conserve les autres non lues", async () => {
        const mockNotifications = [
            {
                id: 10,
                notificationType: "CV_STATUT_CHANGE",
                titre: "notification.etudiant.validation.title_acceptee",
                description: "notification.etudiant.validation.description_acceptee",
                dateNotifiee: "2026-10-14T10:00:00",
                estLu: false,
                variables: { STATUT_FICHIER: "ACCEPTEE" }
            },
            {
                id: 11,
                notificationType: "CV_STATUT_CHANGE",
                titre: "notification.etudiant.validation.title_refusee",
                description: "notification.etudiant.validation.description_refusee",
                dateNotifiee: "2026-10-14T09:00:00",
                estLu: false,
                variables: { STATUT_FICHIER: "REFUSEE" }
            }
        ];

        getNotifications.mockResolvedValue(mockNotifications);
        marquerNotificationLue.mockResolvedValue({});

        afficherNotifications();

        const itemAccepte = await screen.findByText(/votre cv a été accepté/i);
        const itemRefuse = await screen.findByText(/votre cv a été refusé/i);

        expect(itemAccepte).toBeInTheDocument();
        expect(itemRefuse).toBeInTheDocument();

        fireEvent.click(itemAccepte);

        await waitFor(() => {
            expect(marquerNotificationLue).toHaveBeenCalledWith(10);
        });

        await waitFor(() => {
            expect(screen.queryByText(/votre cv a été accepté/i)).not.toBeInTheDocument();
            expect(screen.getByText(/votre cv a été refusé/i)).toBeInTheDocument();
        });
    });

});