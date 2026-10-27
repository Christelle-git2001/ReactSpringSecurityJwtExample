import {fireEvent, render, screen, waitFor} from "@testing-library/react";
import OffreDetailsDrawerContent from "../component/Offer/OffreDetailsDrawerContent.jsx";
import OffreEtudiantCard from "../component/Design/Cards/OffreEtudiantCard.jsx";
import { postulerOffre } from "../api/etudiant.jsx";
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

jest.mock("../component/PDF/PdfPreviewDialog.jsx", () => ({
    __esModule: true,
    default: () => null,
}));


const mockViewPDF = jest.fn();
const mockDownloadPDF = jest.fn();
jest.mock("../utils/filesUtils.jsx", () => ({
    usePdfDocument: () => ({
        pdfUrl: null,
        dialogRef: { current: null },
        viewPDF: mockViewPDF,
        fermerModal: jest.fn(),
        gererTelechargement: mockDownloadPDF,
    }),
}));

describe("Offre étudiant directement dans la carte", () => {
    beforeEach(() => {
        jest.clearAllMocks();
        postulerOffre.mockResolvedValue({});
    });

    it("affiche la description et Postuler sans bouton Voir les détails", () => {
        render(
            <MemoryRouter>
                <OffreEtudiantCard offer={mockOffer} />
            </MemoryRouter>
        );
        expect(screen.getByText("Un super stage")).toBeVisible();
        expect(screen.getByText("offre.apply")).toBeInTheDocument();
        expect(screen.queryByText("offre.details")).not.toBeInTheDocument();
    });

    it("permet toujours de postuler et signale la consultation de l'offre", async () => {
        const onApplied = jest.fn();
        const onRead = jest.fn();
        render(
            <MemoryRouter>
                <OffreEtudiantCard offer={mockOffer} onApplied={onApplied} onRead={onRead} />
            </MemoryRouter>
        );
        fireEvent.click(screen.getByText("offre.apply"));
        await waitFor(() => expect(onApplied).toHaveBeenCalledWith(1));
        expect(postulerOffre).toHaveBeenCalledWith(1);
        expect(onRead).toHaveBeenCalledWith(1);
        expect(screen.getByText("offre.application_received")).toBeInTheDocument();
    });

    it("conserve le message de candidature déjà envoyée", () => {
        render(
            <MemoryRouter>
                <OffreEtudiantCard offer={mockOffer} hasApplied />
            </MemoryRouter>
        );
        expect(screen.getByText("Un super stage")).toBeVisible();
        expect(screen.getByText("offre.application_received")).toBeInTheDocument();
        expect(screen.queryByText("offre.apply")).not.toBeInTheDocument();
    });
});