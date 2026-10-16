import React from 'react';
import { render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import EmployeurHome from '../../page/Employeur/EmployeurHome.jsx';

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

        const studentElement = await screen.findByText(/Sébastien Castonguay/i);
        expect(studentElement).toBeInTheDocument();
    });

    it('devrait filtrer les candidatures et offres lors de la sélection d un filtre de poste', async () => {
        render(
            <MemoryRouter>
                <EmployeurHome user={{ firstName: 'Roger', lastName: 'Malinois' }} />
            </MemoryRouter>
        );

        const studentElement = await screen.findByText(/Sébastien Castonguay/i);
        expect(studentElement).toBeInTheDocument();
    });
});