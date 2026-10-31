import { createInstance } from "i18next";
import { marquerNotificationLue } from "../../../api/notification.jsx";
import traductionFr from "../../../locales/fr/translation.json";
import traductionEn from "../../../locales/en/translation.json";
import { fireEvent, render, screen, waitFor, within } from "@testing-library/react";
import { I18nextProvider } from "react-i18next";
import { MemoryRouter } from "react-router-dom";
import Notification from "../../../component/Notification/Notification.jsx";
import { useState } from "react";

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
            en: { translation: traductionEn },
        },
        interpolation: { escapeValue: false },
    });
});

beforeEach(async () => {
    jest.clearAllMocks();
    await testI18n.changeLanguage("fr");
    marquerNotificationLue.mockResolvedValue({ estLu: true });
});

function creerNotificationStatut(id, statut) {
    return {
        id,
        notificationType: "CV_STATUT_CHANGE",
        titre: statut === "ACCEPTEE"
            ? "notification.etudiant.validation.title_acceptee"
            : statut === "REFUSEE"
                ? "notification.etudiant.validation.title_refusee"
                : "notification.etudiant.validation.title_default",
        description: statut === "ACCEPTEE"
            ? "notification.etudiant.validation.description_acceptee"
            : statut === "REFUSEE"
                ? "notification.etudiant.validation.description_refusee"
                : "notification.etudiant.validation.description_default",
        dateNotifiee: "2026-10-14T10:00:00",
        estLu: false,
        variables: statut ? { STATUT_FICHIER: statut } : {},
    };
}

function afficherNotifications(notificationsInitiales, onMarkAsRead = jest.fn()) {
    function NotificationTest() {
        const [notifications, setNotifications] = useState(notificationsInitiales);

        const gererNotificationLue = (id) => {
            setNotifications((liste) =>
                liste.filter((notification) => notification.id !== id)
            );
            onMarkAsRead(id);
        };

        return (
            <I18nextProvider i18n={testI18n}>
                <MemoryRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
                    <Notification
                        notifications={notifications}
                        onMarkAsRead={gererNotificationLue}
                    />
                </MemoryRouter>
            </I18nextProvider>
        );
    }

    render(<NotificationTest />);

    return { onMarkAsRead };
}

describe("Notification - CV_STATUT_CHANGE", () => {
    it("affiche correctement une notification de CV refusé avec le bon texte", async () => {
        afficherNotifications([creerNotificationStatut(1, "REFUSEE")]);

        expect(
            await screen.findByText(/votre cv a été refusé/i)
        ).toBeInTheDocument();

        expect(
            screen.getByText(/pour connaître les motifs du refus/i)
        ).toBeInTheDocument();
    });

    it("affiche correctement une notification de CV accepté avec le bon texte", async () => {
        afficherNotifications([creerNotificationStatut(1, "ACCEPTEE")]);

        expect(
            await screen.findByText(/votre cv a été accepté/i)
        ).toBeInTheDocument();

        expect(
            screen.getByText(/félicitations, votre cv a été validé !/i)
        ).toBeInTheDocument();
    });

    it("marque la notification comme lue au clic", async () => {
        afficherNotifications([creerNotificationStatut(2, "ACCEPTEE")]);

        const description = await screen.findByText(/votre cv a été accepté/i);

        fireEvent.click(
            within(description.closest("li")).getByRole("button", {
                name: /cv a été accepté/i,
            })
        );

        await waitFor(() => {
            expect(marquerNotificationLue).toHaveBeenCalledWith(2);
        });
    });

    it("navigue vers la bonne route et marque comme lue au clic sur un CV accepté", async () => {
        const { onMarkAsRead } = afficherNotifications([
            creerNotificationStatut(2, "ACCEPTEE"),
        ]);

        const description = await screen.findByText(/votre cv a été accepté/i);

        fireEvent.click(
            within(description.closest("li")).getByRole("button", {
                name: /cv a été accepté/i,
            })
        );

        await waitFor(() => {
            expect(marquerNotificationLue).toHaveBeenCalledWith(2);
            expect(mockNavigate).toHaveBeenCalledWith(
                "/etudiant/cv#televerser",
                { state: { from: "/gestionnaire" } }
            );
        });

        expect(onMarkAsRead).toHaveBeenCalledWith(2);
    });

    it("navigue vers la bonne route et marque comme lue une notification de CV refusé", async () => {
        const { onMarkAsRead } = afficherNotifications([
            creerNotificationStatut(3, "REFUSEE"),
        ]);

        const description = await screen.findByText(/pour connaître les motifs du refus/i);

        fireEvent.click(
            within(description.closest("li")).getByRole("button", {
                name: /cv a été refusé/i,
            })
        );

        await waitFor(() => {
            expect(marquerNotificationLue).toHaveBeenCalledWith(3);
            expect(mockNavigate).toHaveBeenCalledWith(
                "/etudiant/cv#televerser",
                { state: { from: "/gestionnaire" } }
            );
        });

        expect(onMarkAsRead).toHaveBeenCalledWith(3);
    });

    it("affiche correctement le texte en anglais lorsque la langue change", async () => {
        await testI18n.changeLanguage("en");

        afficherNotifications([creerNotificationStatut(1, "ACCEPTEE")]);

        expect(
            await screen.findByText(/your cv has been accepted/i)
        ).toBeInTheDocument();
    });

    it("navigue pas et affiche une erreur si le marquage comme lu échoue", async () => {
        marquerNotificationLue.mockRejectedValue(new Error("Erreur serveur"));

        afficherNotifications([creerNotificationStatut(1, "ACCEPTEE")]);

        const description = await screen.findByText(/votre cv a été accepté/i);

        fireEvent.click(
            within(description.closest("li")).getByRole("button", {
                name: /cv a été accepté/i,
            })
        );

        expect(await screen.findByRole("alert")).toHaveTextContent(
            traductionFr.notifications.read_error
        );

        expect(marquerNotificationLue).toHaveBeenCalledWith(1);
        expect(mockNavigate).not.toHaveBeenCalled();

    });

    it("utilise le texte par défaut si le statut du fichier est absent ou inconnu", async () => {
        afficherNotifications([creerNotificationStatut(3, undefined)]);

        expect(
            await screen.findByText(/vous avez reçu une nouvelle offre à valider/i)
        ).toBeInTheDocument();

        expect(screen.getByRole("heading", { name: "Notifications" })).toBeInTheDocument();
    });

    it("retire uniquement la notification cliquée et conserve les autres non lues", async () => {
        afficherNotifications([
            creerNotificationStatut(10, "ACCEPTEE"),
            {
                ...creerNotificationStatut(11, "REFUSEE"),
                dateNotifiee: "2026-10-14T09:00:00",
            },
        ]);

        const descriptionAcceptee = await screen.findByText(/votre cv a été accepté/i);
        const descriptionRefusee = await screen.findByText(/votre cv a été refusé/i);

        expect(descriptionAcceptee).toBeInTheDocument();
        expect(descriptionRefusee).toBeInTheDocument();

        fireEvent.click(
            within(descriptionAcceptee.closest("li")).getByRole("button", {
                name: /cv a été accepté/i,
            })
        );

        await waitFor(() => {
            expect(marquerNotificationLue).toHaveBeenCalledWith(10);
        });

        await waitFor(() => {
            expect(
                screen.queryByText(/votre cv a été accepté/i)
            ).not.toBeInTheDocument();

            expect(
                screen.getByText(/votre cv a été refusé/i)
            ).toBeInTheDocument();
        });
    });
});