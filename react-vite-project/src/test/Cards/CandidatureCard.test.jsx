import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import CandidatureCard from './../../component/Design/Cards/CandidatureCard.jsx';


jest.mock('react-i18next', () => ({
    useTranslation: () => ({
        t: (key, fallback) => fallback || key,
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