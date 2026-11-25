package com.lacouf.rsbjwt.service.donnees;

import com.lacouf.rsbjwt.model.Enum.Saison;
import com.lacouf.rsbjwt.model.Etudiant;
import com.lacouf.rsbjwt.model.Professeur;
import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.repository.ProfesseurRepository;
import com.lacouf.rsbjwt.service.SessionService;
import com.lacouf.rsbjwt.service.dto.session.SessionDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DonneesProfesseurServiceTest {
    @Mock ProfesseurRepository professeurRepository;
    @Mock
    SessionService sessionService;
    @InjectMocks DonneesProfesseurService donneesProfesseurService;

    @Test
    void retourneLesProfesseursSousFormeDeDto() {
        var professeur = new Professeur();
        professeur.setId(7L);
        professeur.setFirstName("Toru");
        professeur.setDepartment(Departement.INFORMATIQUE);
        when(professeurRepository.findAll()).thenReturn(List.of(professeur));

        assertThat(donneesProfesseurService.obtenirProfesseurs()).singleElement().satisfies(dto -> {
            assertThat(dto.id()).isEqualTo(7L);
            assertThat(dto.firstName()).isEqualTo("Toru");
            assertThat(dto.department()).isEqualTo(Departement.INFORMATIQUE.getLabel());
        });
    }

    @Test
    void rechercheLesProfesseursSansEtudiants() {
        var professeur = new Professeur();
        professeur.setId(8L);
        professeur.setDepartment(Departement.INFORMATIQUE);
        when(professeurRepository.findByEtudiantsIsEmpty()).thenReturn(List.of(professeur));

        assertThat(donneesProfesseurService.obtenirProfesseursSansEtudiants()).singleElement()
                .satisfies(dto -> assertThat(dto.id()).isEqualTo(8L));
    }

    @Test
    void compteLesProfesseursInscritsDansLaSessionActuelle() {
        SessionDTO session = new SessionDTO(Saison.AUTOMNE, 2026);
        var p1 = new Professeur();
        p1.setId(1L);
        var p2 = new Professeur();
        p2.setId(2L);
        List<Professeur> tous = List.of(p1, p2);

        when(professeurRepository.findAll()).thenReturn(tous);
        when(sessionService.getSessionActuelle()).thenReturn(session);
        when(sessionService.filtrerUtilisateursParSession(tous, Saison.AUTOMNE, 2026))
                .thenReturn(List.of(p1));

        assertThat(donneesProfesseurService.countProfesseurInscritSessionActuelle()).isEqualTo(1L);
    }

    @Test
    void compteLesProfesseursAvecEtudiantDansLaSessionActuelle() {
        SessionDTO session = new SessionDTO(Saison.AUTOMNE, 2026);
        var p1 = new Professeur();
        p1.setId(1L);
        List<Professeur> avecEtudiants = List.of(p1);

        when(professeurRepository.findByEtudiantsIsNotEmpty()).thenReturn(avecEtudiants);
        when(sessionService.getSessionActuelle()).thenReturn(session);
        when(sessionService.filtrerUtilisateursParSession(avecEtudiants, Saison.AUTOMNE, 2026))
                .thenReturn(avecEtudiants);

        assertThat(donneesProfesseurService.countProfesseurAvecEtudiantSessionActuelle()).isEqualTo(1L);
    }

    @Test
    void compteLesProfesseursSansEtudiantDansLaSessionActuelle() {
        SessionDTO session = new SessionDTO(Saison.AUTOMNE, 2026);
        var p1 = new Professeur();
        p1.setId(1L);
        List<Professeur> sansEtudiants = List.of(p1);

        when(professeurRepository.findByEtudiantsIsEmpty()).thenReturn(sansEtudiants);
        when(sessionService.getSessionActuelle()).thenReturn(session);
        when(sessionService.filtrerUtilisateursParSession(sansEtudiants, Saison.AUTOMNE, 2026))
                .thenReturn(sansEtudiants);

        assertThat(donneesProfesseurService.countProfesseurSansEtudiantSessionActuelle()).isEqualTo(1L);
    }

    @Test
    void compterMoyenneEtudiantParProfesseurRetourneZeroQuandAucunProfesseurDansLaSession() {
        SessionDTO session = new SessionDTO(Saison.AUTOMNE, 2026);
        var p1 = new Professeur();
        p1.setId(1L);
        List<Professeur> tous = List.of(p1);

        when(professeurRepository.findAll()).thenReturn(tous);
        when(sessionService.getSessionActuelle()).thenReturn(session);
        when(sessionService.filtrerUtilisateursParSession(tous, Saison.AUTOMNE, 2026))
                .thenReturn(List.of());

        assertThat(donneesProfesseurService.compterMoyenneEtudiantParProfesseur()).isEqualTo(0L);
    }

    @Test
    void compterMoyenneEtudiantParProfesseurCalculeLaMoyenneArrondieDansLaSession() {
        SessionDTO session = new SessionDTO(Saison.AUTOMNE, 2026);
        var p1 = new Professeur();
        p1.setId(1L);
        p1.setEtudiants(List.of(new Etudiant(), new Etudiant(), new Etudiant()));

        var p2 = new Professeur();
        p2.setId(2L);
        p2.setEtudiants(List.of(new Etudiant(), new Etudiant()));

        List<Professeur> tous = List.of(p1, p2);
        when(professeurRepository.findAll()).thenReturn(tous);
        when(sessionService.getSessionActuelle()).thenReturn(session);
        when(sessionService.filtrerUtilisateursParSession(tous, Saison.AUTOMNE, 2026))
                .thenReturn(tous);

        assertThat(donneesProfesseurService.compterMoyenneEtudiantParProfesseur()).isEqualTo(3L);
    }

    @Test
    void compterProfesseurSansEtudiantRetourneLeNombreAttendu() {
        when(professeurRepository.countProfesseurByEtudiantsIsEmpty()).thenReturn(2L);

        assertThat(donneesProfesseurService.compterProfesseurSansEtudiant()).isEqualTo(2L);
        verify(professeurRepository).countProfesseurByEtudiantsIsEmpty();
    }

    @Test
    void compterProfesseurAvecEtudiantRetourneLeNombreAttendu() {
        when(professeurRepository.countProfesseurByEtudiantsIsNotEmpty()).thenReturn(5L);

        assertThat(donneesProfesseurService.compterProfesseurAvecEtudiant()).isEqualTo(5L);
        verify(professeurRepository).countProfesseurByEtudiantsIsNotEmpty();
    }

    @Test
    void compterProfesseurSansEtudiantRetourneZeroQuandAucunProfesseurSansEtudiant() {
        when(professeurRepository.countProfesseurByEtudiantsIsEmpty()).thenReturn(0L);

        assertThat(donneesProfesseurService.compterProfesseurSansEtudiant()).isZero();
        verify(professeurRepository).countProfesseurByEtudiantsIsEmpty();
    }

    @Test
    void compterProfesseurAvecEtudiantRetourneZeroQuandAucunProfesseurAvecEtudiant() {
        when(professeurRepository.countProfesseurByEtudiantsIsNotEmpty()).thenReturn(0L);

        assertThat(donneesProfesseurService.compterProfesseurAvecEtudiant()).isZero();
        verify(professeurRepository).countProfesseurByEtudiantsIsNotEmpty();
    }
}