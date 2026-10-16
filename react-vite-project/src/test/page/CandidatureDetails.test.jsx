import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import CandidatureDetails from '../../component/Candidature/CandidatureDetails.jsx';

beforeAll(() => {
    global.URL.createObjectURL = jest.fn(() => 'blob:http://localhost/fake-blob-url');
    global.URL.revokeObjectURL = jest.fn();
});

jest.mock('react-pdf', () => ({
    Document: () => <div>Document PDF Mock</div>,
    Page: () => <div>Page PDF Mock</div>,
    pdfjs: { GlobalWorkerOptions: { workerSrc: '' } }
}));

jest.mock('../../component/PDF/PDFVisioneuse.jsx', () => ({
    PDFContent: () => <div data-testid="pdf-content">Contenu PDF Mock</div>
}));

jest.mock('../../api/gestionnaire.jsx', () => ({
    obtenirCvPDFGestionnaire: jest.fn(() => Promise.resolve({
        ok: true,
        blob: () => Promise.resolve(new Blob(['dummy content'], { type: 'application/pdf' }))
    }))
}));

jest.mock('react-i18next', () => ({
    useTranslation: () => ({
        t: (key, fallback) => fallback || key,
    }),
}));

describe('Composant CandidatureDetails', () => {
    const mockCandidature = {
        id: 1,
        etudiantFirstName: 'Sébastien',
        etudiantLastName: 'Castonguay',
        offreTitle: 'Développeur Web Junior',
        cvId: 10
    };

    it('devrait afficher les détails de la candidature et charger le PDF', async () => {
        render(
            <MemoryRouter>
                <CandidatureDetails candidature={mockCandidature} />
            </MemoryRouter>
        );

        expect(screen.getByText(/Sébastien Castonguay/i)).toBeInTheDocument();
        expect(screen.getByText(/Développeur Web Junior/i)).toBeInTheDocument();

        const pdfElement = await screen.findByTestId('pdf-content');
        expect(pdfElement).toBeInTheDocument();
    });

    it('devrait appeler onOpenOffer au clic sur le titre de l offre', async () => {
        const handleOpenOffer = jest.fn();
        render(
            <MemoryRouter>
                <CandidatureDetails candidature={mockCandidature} onOpenOffer={handleOpenOffer} />
            </MemoryRouter>
        );

        const offerButton = screen.getByText(/Développeur Web Junior/i);
        fireEvent.click(offerButton);

        expect(handleOpenOffer).toHaveBeenCalledWith(mockCandidature);
    });
});