import { createInstance } from "i18next";
import { getNotifications, marquerNotificationLue } from "../../../api/notification.jsx";
import traductionFr from "../../../locales/fr/translation.json";
import traductionEn from "../../../locales/en/translation.json";
import {  fireEvent, render, screen, waitFor } from "@testing-library/react";
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


});