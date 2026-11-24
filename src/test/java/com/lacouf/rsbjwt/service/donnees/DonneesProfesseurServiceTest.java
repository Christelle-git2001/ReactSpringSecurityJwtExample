package com.lacouf.rsbjwt.service.donnees;

import com.lacouf.rsbjwt.model.Professeur;
import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.repository.ProfesseurRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DonneesProfesseurServiceTest {
    @Mock ProfesseurRepository professeurs;
    @InjectMocks DonneesProfesseurService service;

    @Test
    void retourneLesProfesseursSousFormeDeDto() {
        var professeur = new Professeur();
        professeur.setId(7L);
        professeur.setFirstName("Toru");
        professeur.setDepartment(Departement.INFORMATIQUE);
        when(professeurs.findAll()).thenReturn(List.of(professeur));

        assertThat(service.obtenirProfesseurs()).singleElement().satisfies(dto -> {
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
        when(professeurs.findByEtudiantsIsEmpty()).thenReturn(List.of(professeur));

        assertThat(service.obtenirProfesseursSansEtudiants()).singleElement()
                .satisfies(dto -> assertThat(dto.id()).isEqualTo(8L));
    }

    @Test
    void obtenirProfesseurAvecEtudiantRetourneLesDto() {
        var professeur = new Professeur();
        professeur.setId(7L);
        professeur.setFirstName("Toru");
        professeur.setDepartment(Departement.INFORMATIQUE);
        when(professeurs.findByEtudiantsIsNotEmpty()).thenReturn(List.of(professeur));

        assertThat(service.obtenirProfesseurAvecEtudiant()).singleElement()
                .satisfies(dto -> assertThat(dto.id()).isEqualTo(7L));
    }

    @Test
    void compterProfesseurSansEtudiantRetourneLeNombreAttendu() {
        when(professeurs.countProfesseurByEtudiantsIsEmpty()).thenReturn(2L);

        assertThat(service.compterProfesseurSansEtudiant()).isEqualTo(2L);
    }

    @Test
    void compterProfesseurAvecEtudiantRetourneLeNombreAttendu() {
        when(professeurs.countProfesseurByEtudiantsIsNotEmpty()).thenReturn(5L);

        assertThat(service.compterProfesseurAvecEtudiant()).isEqualTo(5L);
    }

    @Test
    void compterMoyenneEtudiantParProfesseurCalculeLaMoyenneSurTousLesProfesseurs() {
        var p1 = new Professeur();
        var p2 = new Professeur();
        var p3 = new Professeur();
        when(professeurs.findAll()).thenReturn(List.of(p1, p2, p3));

        assertThat(service.compterMoyenneEtudiantParProfesseur()).isEqualTo(2L);
    }

    @Test
    void compterMoyenneEtudiantParProfesseurRetourneZeroSiAucunProfesseur() {
        when(professeurs.findAll()).thenReturn(List.of());

        assertThat(service.compterMoyenneEtudiantParProfesseur()).isEqualTo(0L);
    }
}