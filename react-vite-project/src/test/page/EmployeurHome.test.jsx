import React from 'react';
import { fireEvent, render, screen, waitFor, within } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import EmployeurHome from '../../page/Employeur/EmployeurHome.jsx';
import OffreStageCard from '../../component/Design/Cards/OffreStageCard.jsx';
import { getOffresEmployeur } from '../../api/employeur.jsx';
import { getNotifications, marquerNotificationLue } from '../../api/notification.jsx';

jest.mock('react-pdf', () => ({
    Document: () => <div>Document PDF Mock</div>,
    Page: () => <div>Page PDF Mock</div>,
    pdfjs: { GlobalWorkerOptions: { workerSrc: '' } }
}));

const mockOpenDrawer = jest.fn();
const mockNavigate = jest.fn();

jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'),
    useOutletContext: () => ({ openDrawer: mockOpenDrawer }),
    useNavigate: () => mockNavigate,
}));

jest.mock('../../api/employeur.jsx', () => ({
    getOffresEmployeur: jest.fn(() => Promise.resolve([
        { id: 1, title: 'Développeur Web Junior', statut: 'ACCEPTEE', domain: 'INFORMATIQUE', description: 'Stage dev web' }
    ])),
    getCandidaturesEmployeur: jest.fn(() => Promise.resolve([
        {
            id: 101,
            offreId: 1,
            etudiantFirstName: 'Sébastien',
            etudiantLastName: 'Castonguay',
            offreTitle: 'Développeur Web Junior',
            etudiantDepartment: 'INFORMATIQUE'
        }
    ]))
}));

jest.mock('react-i18next', () => ({
    useTranslation: () => ({
        t: (key, fallback) => fallback || key,
        i18n: { changeLanguage: () => new Promise(() => {}) },
    }),
}));

jest.mock('../../api/notification.jsx', () => ({
    getNotifications: jest.fn(() => Promise.resolve([])),
    marquerNotificationLue: jest.fn(() => Promise.resolve({})),
}));

beforeEach(() => jest.clearAllMocks());

describe('EmployeurHome - Gestion des candidatures et offres', () => {

    it('devrait afficher correctement le message de bienvenue et les données chargées', async () => {
        render(
            <MemoryRouter>
                <EmployeurHome user={{ firstName: 'Roger', lastName: 'Malinois' }} />
            </MemoryRouter>
        );

        await waitFor(() => {
            expect(screen.getByText(/Roger Malinois/i)).toBeInTheDocument();
        });

        const offerElements = await screen.findAllByText(/Développeur Web Junior/i);
        expect(offerElements.length).toBeGreaterThan(0);

        const studentElement = await within(document.getElementById("candidatures-section")).findByText(/Sébastien Castonguay/i);
        expect(studentElement).toBeInTheDocument();
    });

    it('devrait filtrer les candidatures et offres lors de la sélection d un filtre de poste', async () => {
        render(
            <MemoryRouter>
                <EmployeurHome user={{ firstName: 'Roger', lastName: 'Malinois' }} />
            </MemoryRouter>
        );

        const studentElement = await within(document.getElementById("candidatures-section")).findByText(/Sébastien Castonguay/i);
        expect(studentElement).toBeInTheDocument();
    });
});

describe('Offres directement dans les cartes employeur', () => {
    it('affiche seulement les candidatures associées à l’offre', () => {
        render(
            <MemoryRouter>
                <OffreStageCard
                    offer={{ id: 1 }}
                    candidatures={[
                        { id: 101, offreId: 1, etudiantFirstName: 'Sébastien', etudiantLastName: 'Castonguay' },
                        { id: 102, offreId: 2, etudiantFirstName: 'Autre', etudiantLastName: 'Étudiant' },
                    ]}
                />
            </MemoryRouter>
        );
        fireEvent.click(screen.getByText('candidature.list_title (1)'));
        expect(screen.getByRole('button', { name: 'Sébastien Castonguay' })).toBeVisible();
        expect(screen.queryByText('Autre Étudiant')).not.toBeInTheDocument();
    });

    it('ouvre une candidature et marque sa notification comme lue', async () => {
        getNotifications.mockResolvedValueOnce([
            { id: 17, notificationType: 'NOUVELLE_CANDIDATURE', variables: { CANDIDATURE_ID: '101' }, estLu: false },
        ]);
        render(
            <MemoryRouter>
                <EmployeurHome user={{ id: 5, firstName: 'Roger' }} />
            </MemoryRouter>
        );
        fireEvent.click(await screen.findByText('candidature.list_title (1)'));
        fireEvent.click(screen.getByRole('button', { name: 'Sébastien Castonguay' }));
        await waitFor(() => expect(mockNavigate).toHaveBeenCalledWith('/employeur/candidatures/details', {
            state: {
                candidature: expect.objectContaining({ id: 101 }),
                offer: expect.objectContaining({ id: 1 }),
                from: '/employeur',
            },
        }));
        expect(marquerNotificationLue).toHaveBeenCalledWith(17);
    });

    it('affiche un message si aucune candidature n’est reçue', () => {
        render(<MemoryRouter><OffreStageCard offer={{ id: 1 }} /></MemoryRouter>);
        fireEvent.click(screen.getByText('candidature.list_title (0)'));
        expect(screen.getByText('candidature.no_candidatures')).toBeVisible();
    });

    it('affiche le commentaire de refus d’une offre refusée', () => {
        render(
            <MemoryRouter>
                <OffreStageCard offer={{ id: 1, statut: 'REFUSEE', rejectionComment: 'Précisez les tâches.' }} />
            </MemoryRouter>
        );
        expect(screen.getByText('Précisez les tâches.')).toBeVisible();
    });

    it('n’affiche pas le commentaire de refus d’une offre acceptée', () => {
        render(
            <MemoryRouter>
                <OffreStageCard offer={{ id: 1, statut: 'ACCEPTEE', rejectionComment: 'Précisez les tâches.' }} />
            </MemoryRouter>
        );
        expect(screen.queryByText('Précisez les tâches.')).not.toBeInTheDocument();
    });

    it('filtre les offres selon le statut choisi', async () => {
        getOffresEmployeur.mockResolvedValueOnce([
            { id: 1, title: 'Offre acceptée', statut: 'ACCEPTEE', domain: 'INFORMATIQUE' },
            { id: 2, title: 'Offre refusée', statut: 'REFUSEE', domain: 'INFORMATIQUE' },
        ]);
        render(
            <MemoryRouter>
                <EmployeurHome user={{ firstName: 'Roger' }} />
            </MemoryRouter>
        );
        await screen.findByText('Offre acceptée');
        expect(screen.getByText('Offre refusée')).toBeVisible();
        fireEvent.click(screen.getByText('actions.filter_by'));
        fireEvent.click(screen.getByRole('checkbox', { name: 'status.refusee' }));
        expect(screen.getByText('Offre refusée')).toBeVisible();
        expect(screen.queryByText('Offre acceptée')).not.toBeInTheDocument();
    });
});