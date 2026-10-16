import { fireEvent, render, screen } from "@testing-library/react";
import { I18nextProvider } from "react-i18next";
import { createInstance } from "i18next";
import Drawer from "../../component/Design/Drawer.jsx";
import traductionFr from "../../locales/fr/translation.json";

const testI18n = createInstance();

beforeAll(async () => {
    await testI18n.init({
        lng: "fr",
        fallbackLng: "fr",
        resources: { fr: { translation: traductionFr } },
        interpolation: { escapeValue: false },
    });
});

function afficherDrawer(isOpen = true, onClose = jest.fn()) {
    return render(
        <I18nextProvider i18n={testI18n}>
            <Drawer
                isOpen={isOpen}
                onClose={onClose}
                title="notifications.title"
                content={<p>Contenu de test</p>}
            />
        </I18nextProvider>
    );
}

describe("Drawer", () => {
    it("affiche le contenu quand le Drawer est ouvert", () => {
        afficherDrawer();

        expect(screen.getByText("Contenu de test")).toBeInTheDocument();
        expect(screen.getByText(traductionFr.notifications.title)).toBeInTheDocument();
        expect(screen.getByRole("button", {
            name: traductionFr.drawer.close,
        })).toBeInTheDocument();
    });

    it("appelle onClose au clic sur le bouton fermer", () => {
        const onClose = jest.fn();
        afficherDrawer(true, onClose);

        fireEvent.click(screen.getByRole("button", {
            name: traductionFr.drawer.close,
        }));

        expect(onClose).toHaveBeenCalledTimes(1);
    });

    it("masque le Drawer quand isOpen est false", () => {
        const { container } = afficherDrawer(false);

        expect(container.firstChild).toHaveClass("w-0", "opacity-0");
    });
});