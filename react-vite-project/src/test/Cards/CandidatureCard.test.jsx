import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import CandidatureCard from './../../component/Design/Cards/CandidatureCard.jsx';


jest.mock('react-i18next', () => ({
    useTranslation: () => ({
        t: (key, fallback) => fallback || key,
    }),
}));

const mockViewPDF = jest.fn();
const mockDownloadPDF = jest.fn();
jest.mock('../../component/PDF/PdfPreviewDialog.jsx', () => ({
    __esModule: true,
    default: () => null,
}));
jest.mock('../../utils/filesUtils.jsx', () => ({
    usePdfDocument: () => ({
        viewPDF: mockViewPDF,
        gererTelechargement: mockDownloadPDF,
        dialogRef: { current: null },
    }),
}));

describe('Composant CandidatureCard', () => {
    const mockCandidature = {
        id: 1,
        etudiantFirstName: 'Sébastien',
        etudiantLastName: 'Castonguay',
        offreTitle: 'Développeur Web Junior',
        dateCandidature: '2026-04-15'
    };

    it('devrait afficher correctement les informations de la candidature', () => {
        render(<CandidatureCard candidature={mockCandidature} />);
        expect(screen.getByText('Sébastien Castonguay')).toBeInTheDocument();
        expect(screen.getByText('Développeur Web Junior')).toBeInTheDocument();
    });

    it('devrait appeler la fonction onView au clic sur le bouton de lecture', () => {
        const handleView = jest.fn();
        render(<CandidatureCard candidature={mockCandidature} onView={handleView} />);

        const readButton = screen.getByRole('button', { name: /candidature.read/i });
        fireEvent.click(readButton);

        expect(handleView).toHaveBeenCalledWith(mockCandidature);
    });

    it('devrait appeler la fonction onViewOffer au clic sur le titre de l offre', () => {
        const handleViewOffer = jest.fn();
        render(<CandidatureCard candidature={mockCandidature} onViewOffer={handleViewOffer} />);

        const offerButton = screen.getByText('Développeur Web Junior');
        fireEvent.click(offerButton);

        expect(handleViewOffer).toHaveBeenCalledWith(mockCandidature);
    });
});

describe('Détails de l’offre dans la candidature', () => {
    const candidature = { id: 12, offreId: 5, offreTitle: 'Stage informatique' };
    const offer = { id: 5, title: 'Stage informatique', description: 'Développer une application.', statut: 'ACCEPTEE', fileName: 'stage.pdf' };

    it('ouvre les détails sans statut et les ferme par le lien ou le X', () => {
        const { container } = render(<CandidatureCard candidature={candidature} offer={offer} />);
        const link = screen.getByRole('button', { name: 'dashboard.view_offer' });
        expect(screen.queryByText(offer.description)).not.toBeInTheDocument();
        fireEvent.click(link);
        expect(screen.getByText(offer.description)).toBeVisible();
        expect(link).toHaveAttribute('aria-expanded', 'true');
        expect(container.firstChild).toHaveClass('bg-[radial-gradient(circle_at_center,_#00CCCB33,_transparent_70%)]');
        expect(screen.queryByText('status.acceptee')).not.toBeInTheDocument();
        expect(screen.queryByText('ACCEPTEE')).not.toBeInTheDocument();
        fireEvent.click(link);
        expect(screen.queryByText(offer.description)).not.toBeInTheDocument();
        expect(container.firstChild).toHaveClass('bg-white');
        fireEvent.click(link);
        fireEvent.click(screen.getByRole('button', { name: 'drawer.close' }));
        expect(screen.queryByText(offer.description)).not.toBeInTheDocument();
        expect(link).toHaveAttribute('aria-expanded', 'false');
    });

    it('permet de visualiser et télécharger le document de cette offre', () => {
        render(<CandidatureCard candidature={candidature} offer={offer} />);
        fireEvent.click(screen.getByRole('button', { name: 'dashboard.view_offer' }));
        fireEvent.click(screen.getByRole('button', { name: 'documents.preview' }));
        expect(mockViewPDF).toHaveBeenCalledTimes(1);
        fireEvent.click(screen.getByRole('button', { name: 'documents.download' }));
        expect(mockDownloadPDF).toHaveBeenCalledWith('stage.pdf');
    });

    it('affiche le badge et l’étudiant ciblé seulement pour une offre ciblée', () => {
        const targetedOffer = { ...offer, isTarget: true, etudiantName: 'Alice Martin', salary: 22 };
        const { rerender } = render(<CandidatureCard candidature={candidature} offer={targetedOffer} />);
        fireEvent.click(screen.getByRole('button', { name: 'dashboard.view_offer' }));
        expect(screen.getByText('Ciblée')).toBeVisible();
        expect(screen.getByText('Alice Martin')).toBeVisible();
        expect(screen.getByText('22 $ / h')).toBeVisible();
        rerender(<CandidatureCard candidature={candidature} offer={{ ...targetedOffer, isTarget: false }} />);
        expect(screen.queryByText('Ciblée')).not.toBeInTheDocument();
        expect(screen.queryByText('Alice Martin')).not.toBeInTheDocument();
    });
});