import {render, screen} from "@testing-library/react";
import OffreDetailsDrawerContent from "../component/Offer/OffreDetailsDrawerContent.jsx";
import "@testing-library/jest-dom";

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
        render(<OffreDetailsDrawerContent
            offer={mockOffer}
            showStatus={false}
            hasApplied={false}
        />);
        const boutonPostuler = screen.getByText("offre.apply");
        expect(boutonPostuler).toBeInTheDocument();
    });

    it("devrait afficher le message de déjà postulé quand l'étudiant a déjà postulé", async () => {
        render(<OffreDetailsDrawerContent
            offer={mockOffer}
            showStatus={false}
            hasApplied={true}
        />);
        const messageDejaPostule = screen.getByText("offre.already_applied");
        expect(messageDejaPostule).toBeInTheDocument();
    });

    it("ne devrait pas afficher le bouton postuler quand l'étudiant a déjà postulé", async () => {
        render(<OffreDetailsDrawerContent
            offer={mockOffer}
            showStatus={false}
            hasApplied={true}
        />);
        const boutonPostuler = screen.queryByText("offre.apply");
        expect(boutonPostuler).not.toBeInTheDocument();
    });
});