import {render, screen} from "@testing-library/react";
import OffreDetailsDrawerContent from "../component/Offer/OffreDetailsDrawerContent.jsx";
import "@testing-library/jest-dom";
import {MemoryRouter} from "react-router-dom";

jest.mock("react-i18next", () => ({
    useTranslation: () => ({
        t: (key) => key,
    }),
}))

jest.mock("../api/etudiant.jsx", () => ({
    postulerOffre: jest.fn(),
}))

const mockOffer = {
    id: 1,
    title: "Stage en développement",
    domain: "INFORMATIQUE",
    startDate: "2026-01-01",
    endDate: "2026-04-01",
    displayEndDate: "2025-12-01",
    salary: 20,
    description: "Un super stage",
    statut: "ACCEPTEE",
};

describe("test du bouton Postuler", () => {
    it("devrait afficher le bouton postuler quand l'étudiant n'a pas encore postulé", async () => {
        render(
            <MemoryRouter>
                <OffreDetailsDrawerContent
                    offer={mockOffer}
                    showStatus={false}
                    hasApplied={false}
                />
            </MemoryRouter>
        );
        const boutonPostuler = screen.getByText("offre.apply");
        expect(boutonPostuler).toBeInTheDocument();
    });

    it("devrait afficher le message de candidature envoyée quand l'étudiant a déjà postulé", async () => {
        render(
            <MemoryRouter>
                <OffreDetailsDrawerContent
                    offer={mockOffer}
                    showStatus={false}
                    hasApplied={true}
                />
            </MemoryRouter>
        );
        const messageCandidatureEnvoyee = screen.getByText("offre.application_received");
        expect(messageCandidatureEnvoyee).toBeInTheDocument();
    });

    it("ne devrait pas afficher le bouton postuler quand l'étudiant a déjà postulé", async () => {
        render(
            <MemoryRouter>
                <OffreDetailsDrawerContent
                    offer={mockOffer}
                    showStatus={false}
                    hasApplied={true}
                />
            </MemoryRouter>
        );
        const boutonPostuler = screen.queryByText("offre.apply");
        expect(boutonPostuler).not.toBeInTheDocument();
    });
});