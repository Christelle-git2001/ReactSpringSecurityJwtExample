import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import EtudiantHome from "../../page/Etudiant/EtudiantHome.jsx";
import { isNewOffer } from "../../utils/isNewOffer.js";
import { obtenirCandidaturesEtudiant, obtenirOffres } from "../../api/etudiant.jsx";
import { getNotifications, marquerNotificationLue } from "../../api/notification.jsx";

jest.mock("react-i18next", () => ({ useTranslation: () => ({ t: (key) => key }) }));
jest.mock("../../component/CV/CvOnLandingPage.jsx", () => () => null);
jest.mock("../../component/PDF/PdfPreviewDialog.jsx", () => () => null);
jest.mock("../../api/etudiant.jsx", () => ({
    obtenirCandidaturesEtudiant: jest.fn(), obtenirOffres: jest.fn(), postulerOffre: jest.fn(),
}));
jest.mock("../../api/notification.jsx", () => ({
    getNotifications: jest.fn(), marquerNotificationLue: jest.fn(),
}));

const user = { id: 10, department: "INFORMATIQUE", previousLoginAt: "2026-10-29T09:00:00" };
const offers = [
    { id: 1, title: "Ancienne offre", description: "Ancien stage", salary: 20, createdAt: "2026-10-25T09:00:00" },
    { id: 2, title: "Nouvelle offre", description: "Nouveau stage", salary: 20, createdAt: "2026-10-30T09:00:00" },
];
const afficherAccueil = (etudiant = user) => render(
    <MemoryRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
        <EtudiantHome user={etudiant} />
    </MemoryRouter>
);
const titres = () => screen.getAllByRole("heading", { level: 2 }).map(titre => titre.textContent);

beforeEach(() => {
    jest.clearAllMocks();
    obtenirOffres.mockResolvedValue(offers);
    obtenirCandidaturesEtudiant.mockResolvedValue([]);
});

test("affiche d'abord les offres creees depuis la connexion precedente, sans utiliser les notifications", async () => {
    afficherAccueil();
    await screen.findByText("Nouvelle offre");
    expect(titres()).toEqual(["Nouvelle offre", "Ancienne offre"]);
    fireEvent.click(screen.getByText("Nouvelle offre"));
    expect(titres()).toEqual(["Nouvelle offre", "Ancienne offre"]);
    expect(getNotifications).not.toHaveBeenCalled();
    expect(marquerNotificationLue).not.toHaveBeenCalled();
});

test("une offre cesse d'etre nouvelle quand la connexion precedente depasse sa creation", async () => {
    const accueil = afficherAccueil();
    await screen.findByText("offre.new");
    accueil.unmount();
    afficherAccueil({ ...user, previousLoginAt: "2026-10-31T09:00:00" });
    await screen.findByText("Nouvelle offre");
    await waitFor(() => expect(titres()).toEqual(["Ancienne offre", "Nouvelle offre"]));
    expect(screen.queryByText("offre.new")).not.toBeInTheDocument();
});

test("isNewOffer gere la premiere connexion et les dates absentes ou invalides", () => {
    expect(isNewOffer(offers[0], null)).toBe(true);
    expect(isNewOffer(offers[1], null)).toBe(true);
    expect(isNewOffer({}, null)).toBe(false);
    expect(isNewOffer({ createdAt: "invalide" }, null)).toBe(false);
    expect(isNewOffer(offers[1], offers[1].createdAt)).toBe(false);
    expect(isNewOffer(offers[1], "invalide")).toBe(false);
});