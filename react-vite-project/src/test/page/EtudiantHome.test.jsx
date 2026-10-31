import { act, fireEvent, render, screen, waitFor, within } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import EtudiantHome from "../../page/Etudiant/EtudiantHome.jsx";
import { obtenirCandidaturesEtudiant, obtenirOffres, postulerOffre } from "../../api/etudiant.jsx";
import { obtenirOffrePDF } from "../../api/employeur.jsx";
import { getNotifications, marquerNotificationLue } from "../../api/notification.jsx";

jest.mock("react-i18next", () => ({ useTranslation: () => ({ t: (key) => key }) }));
jest.mock("../../component/CV/CvOnLandingPage.jsx", () => () => null);
jest.mock("../../api/etudiant.jsx", () => ({
    obtenirCandidaturesEtudiant: jest.fn(), obtenirOffres: jest.fn(), postulerOffre: jest.fn(),
}));
jest.mock("../../api/employeur.jsx", () => ({ obtenirOffrePDF: jest.fn() }));
jest.mock("../../api/notification.jsx", () => ({
    getNotifications: jest.fn(), marquerNotificationLue: jest.fn(),
}));
jest.mock("../../component/PDF/PDFVisioneuse.jsx", () => ({
    PDFVisioneuse: ({ onClose }) => <button onClick={onClose}>Fermer le PDF</button>,
}));

const user = { id: 10, department: "INFORMATIQUE" };
const offers = [
    { id: 2, title: "Ancienne offre", description: "Autre stage", salary: 20 },
    { id: 1, title: "Nouvelle offre", description: "Description du stage", salary: 20 },
];
const afficherAccueil = () => render(
    <MemoryRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
        <EtudiantHome user={user} />
    </MemoryRouter>
);

describe("Consultation des nouvelles offres", () => {
    let notifications;

    beforeEach(() => {
        jest.clearAllMocks();
        notifications = [{
            id: 100, notificationType: "NOUVELLE_OFFRE_STAGE",
            variables: { OFFRE_ID: "1" }, estLu: false,
        }];
        obtenirOffres.mockResolvedValue(offers);
        obtenirCandidaturesEtudiant.mockResolvedValue([]);
        getNotifications.mockImplementation(async () => notifications.map(n => ({ ...n })));
        marquerNotificationLue.mockImplementation(async (id) => {
            notifications.find(n => n.id === id).estLu = true;
        });
        obtenirOffrePDF.mockResolvedValue({ ok: true, blob: async () => new Blob(["PDF"]) });
        URL.createObjectURL = jest.fn(() => "blob:offre");
        URL.revokeObjectURL = jest.fn();
        jest.spyOn(HTMLAnchorElement.prototype, "click").mockImplementation(() => {});
        HTMLDialogElement.prototype.showModal = jest.fn(function () { this.open = true; });
        HTMLDialogElement.prototype.close = jest.fn(function () { this.open = false; });
    });

    afterEach(() => jest.restoreAllMocks());

    it.each(["actions.view", "actions.download"])(
        "enregistre la consultation via %s et la conserve à la reconnexion",
        async (action) => {
            const accueil = afficherAccueil();
            await screen.findByText("offre.new");
            const card = screen.getByText("Nouvelle offre").closest(".card");
            fireEvent.click(within(card).getByRole("button", { name: action }));

            await waitFor(() => expect(screen.queryByText("offre.new")).not.toBeInTheDocument());
            expect(marquerNotificationLue).toHaveBeenCalledWith(100);
            expect(screen.getByText("Nouvelle offre").closest(".card")).toBe(card);
            if (action === "actions.view") {
                await waitFor(() => expect(document.querySelector("dialog[open]")).toHaveAttribute("open"));
                fireEvent.click(within(document.querySelector("dialog[open]")).getByText("Fermer le PDF"));
                expect(document.querySelector("dialog[open]")).toBeNull();
            } else {
                await waitFor(() => expect(HTMLAnchorElement.prototype.click).toHaveBeenCalled());
            }

            accueil.unmount();
            afficherAccueil();
            await screen.findByText("Nouvelle offre");
            await waitFor(() => expect(getNotifications).toHaveBeenCalledTimes(3));
            expect(screen.queryByText("offre.new")).not.toBeInTheDocument();
            expect(screen.getByText("Ancienne offre")).toBeInTheDocument();
        }
    );

    it("conserve les offres non consultées comme nouvelles à la reconnexion", async () => {
        const accueil = afficherAccueil();
        await screen.findByText("offre.new");
        accueil.unmount();
        afficherAccueil();
        expect(await screen.findByText("offre.new")).toBeInTheDocument();
        expect(marquerNotificationLue).not.toHaveBeenCalled();
    });
    it("ne marque pas une offre comme lue si le PDF ne peut pas etre charge", async () => {
        obtenirOffrePDF.mockResolvedValue({ ok: false, status: 500 });
        afficherAccueil();
        await screen.findByText("offre.new");
        const card = screen.getByText("Nouvelle offre").closest(".card");
        await act(async () => fireEvent.click(within(card).getByRole("button", { name: "actions.view" })));
        expect(obtenirOffrePDF).toHaveBeenCalledWith(1);
        expect(marquerNotificationLue).not.toHaveBeenCalled();
        expect(screen.getByText("offre.new")).toBeInTheDocument();
    });

    it("ne marque pas la description comme lue au simple clic", async () => {
        afficherAccueil();
        await screen.findByText("offre.new");
        const card = screen.getByText("Nouvelle offre").closest(".card");
        fireEvent.click(within(card).getByText("offre.description"));
        expect(marquerNotificationLue).not.toHaveBeenCalled();
    });

    it("marque l'offre comme lue au clic sur Postuler avant la fin de la candidature", async () => {
        let terminerCandidature;
        postulerOffre.mockImplementation(() => new Promise(resolve => { terminerCandidature = resolve; }));
        afficherAccueil();
        await screen.findByText("offre.new");
        const card = screen.getByText("Nouvelle offre").closest(".card");
        fireEvent.click(within(card).getByRole("button", { name: "offre.apply" }));

        await waitFor(() => expect(screen.queryByText("offre.new")).not.toBeInTheDocument());
        expect(postulerOffre).toHaveBeenCalledWith(1);
        expect(marquerNotificationLue).toHaveBeenCalledWith(100);
        expect(within(card).queryByText("offre.application_received")).not.toBeInTheDocument();

        await act(async () => terminerCandidature({}));
        expect(within(card).getByText("offre.application_received")).toBeInTheDocument();
        expect(marquerNotificationLue).toHaveBeenCalledTimes(1);
    });

});