import React from 'react';
import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import CandidatureDetails from '../../component/Candidature/CandidatureDetails.jsx';
import CandidatureTimeline from '../../component/Candidature/CandidatureTimeline.jsx';
import { accepterCandidat, obtenirConvocationEmployeur } from '../../api/employeur.jsx';

jest.mock('../../api/employeur.jsx', () => ({
    obtenirConvocationEmployeur: jest.fn(),
    accepterCandidat: jest.fn(),
}));

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
    })),
    getContratByCandidatureId: jest.fn(() => Promise.resolve(null))
}));

jest.mock('../../api/pdf.jsx', () => ({
    obtenirContratPDF: jest.fn(() => Promise.resolve({
        ok: true,
        blob: () => Promise.resolve(new Blob(['dummy contract'], { type: 'application/pdf' }))
    }))
}));

jest.mock('react-i18next', () => ({
    useTranslation: () => ({
        t: (key, fallback) => fallback || key,
        i18n: { language: 'fr' },
    }),
}));

describe('Composant CandidatureDetails', () => {
    beforeEach(() => jest.clearAllMocks());
    const mockCandidature = {
        id: 1,
        etudiantFirstName: 'Sébastien',
        etudiantLastName: 'Castonguay',
        offreTitle: 'Développeur Web Junior',
        cvId: 10
    };

    it('devrait afficher les détails de la candidature et charger le PDF', async () => {
        render(
            <MemoryRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
                <CandidatureDetails candidature={mockCandidature} />
            </MemoryRouter>
        );

        expect(screen.getByText(/Sébastien Castonguay/i)).toBeInTheDocument();
        expect(screen.getByText(/Développeur Web Junior/i)).toBeInTheDocument();

        const pdfElement = await screen.findByTestId('pdf-content');
        expect(pdfElement).toBeInTheDocument();
    });

    it('active Stage trouve lorsque la candidature porte ce statut', async () => {
        render(
            <MemoryRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
                <CandidatureDetails candidature={{ ...mockCandidature, statut: 'STAGE_TROUVE' }} />
            </MemoryRouter>
        );

        await screen.findByTestId('pdf-content');
        expect(screen.getByText('candidature.status.stage_trouve')).toHaveClass('font-bold');
        expect(screen.getByText('candidature.status.contrat')).not.toHaveClass('font-bold');
        expect(screen.getByText('candidature.interview_states.TERMINEE')).toHaveClass('text-white');
        expect(screen.getByText('process.action_wait_contract')).toBeInTheDocument();
    });

    it('conserve Stage trouve actif apres la creation du contrat', async () => {
        render(
            <MemoryRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
                <CandidatureDetails candidature={{ ...mockCandidature, statut: 'CONTRAT_CREE' }} />
            </MemoryRouter>
        );

        await screen.findByTestId('pdf-content');
        expect(screen.getByText('candidature.status.stage_trouve')).toHaveClass('font-bold');
        expect(screen.getByText('candidature.status.contrat')).toHaveClass('font-bold');
    });

    it('propose la decision employeur quand lentrevue est terminee', async () => {
        obtenirConvocationEmployeur.mockResolvedValue({ id: 50, statut: 'TERMINEE' });
        accepterCandidat.mockResolvedValue({});
        render(
            <MemoryRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
                <CandidatureDetails candidature={{ ...mockCandidature, statut: 'ENTREVUE_PLANIFIEE' }} />
            </MemoryRouter>
        );

        await waitFor(() => expect(screen.getByText('candidature.interview_states.TERMINEE')).toHaveClass('text-white'));
        const accepter = screen.getByRole('button', { name: 'actions.accept_candidate' });
        expect(accepter).toBeEnabled();
        expect(screen.getByRole('button', { name: 'actions.reject_candidate' })).toBeEnabled();

        fireEvent.click(accepter);
        await waitFor(() => expect(accepterCandidat).toHaveBeenCalledWith(50));
        await waitFor(() => expect(screen.getByText('candidature.status.acceptee')).toHaveClass('font-bold'));
        expect(screen.queryByRole('button', { name: 'actions.accept_candidate' })).not.toBeInTheDocument();
    });

    it('active la confirmation apres la reponse et la conserve apres la fin de lentrevue', () => {
        const { rerender } = render(
            <CandidatureTimeline statut="ENTREVUE_PLANIFIEE" statutEntrevue="A_CONFIRMER" afficherRetenue={false} />
        );
        expect(screen.getByText('candidature.status.planifiee')).toHaveClass('font-bold');
        expect(screen.getByText('candidature.status.confirmee')).not.toHaveClass('font-bold');
        expect(screen.queryByText('candidature.status.retenue')).not.toBeInTheDocument();

        rerender(<CandidatureTimeline statut="ENTREVUE_PLANIFIEE" statutEntrevue="CONFIRMEE" afficherRetenue={false} />);
        expect(screen.getByText('candidature.status.confirmee')).toHaveClass('font-bold');

        rerender(<CandidatureTimeline statut="ENTREVUE_PLANIFIEE" statutEntrevue="TERMINEE" afficherRetenue={false} />);
        expect(screen.getByText('candidature.status.confirmee')).toHaveClass('font-bold');
    });

    it('ne confirme pas lentrevue quand letudiant refuse la convocation', () => {
        render(<CandidatureTimeline statut="ENTREVUE_REFUSEE" statutEntrevue="REFUSEE" afficherRetenue={false} />);

        expect(screen.getByText('candidature.status.entrevue_refusee')).toHaveClass('text-red-800');
        expect(screen.getByText('candidature.status.confirmee')).not.toHaveClass('font-bold');
    });
});
