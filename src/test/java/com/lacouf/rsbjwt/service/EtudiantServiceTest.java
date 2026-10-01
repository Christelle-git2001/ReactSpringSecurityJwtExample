package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.Exception.EmailExistantException;
import com.lacouf.rsbjwt.Exception.MatriculeExistantException;
import com.lacouf.rsbjwt.Exception.MotDePasseNonCorrespondantException;
import com.lacouf.rsbjwt.Exception.NumeroTelephoneExistantException;
import com.lacouf.rsbjwt.model.CvEtudiant;
import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.model.Etudiant;
import com.lacouf.rsbjwt.repository.CvEtudiantRepository;
import com.lacouf.rsbjwt.repository.EtudiantRepository;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.service.dto.CvEtudiantDTO;
import com.lacouf.rsbjwt.service.dto.EtudiantDTO;
import com.lacouf.rsbjwt.service.dto.InscriptionEtudiantDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EtudiantServiceTest {

    @Mock
    private EtudiantRepository etudiantRepository;

    @Mock
    private UserAppRepository userAppRepository;

    @InjectMocks
    private EtudiantService etudiantService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CvEtudiantRepository cvEtudiantRepository;

    InscriptionEtudiantDTO inscriptionEtudiantDTO;
    Etudiant etudiant;

    @BeforeEach
    void init() {
        inscriptionEtudiantDTO = new InscriptionEtudiantDTO(
                "Christelle",
                "Altineus",
                "438-297-8191",
                "christelle@gmail.com",
                "2226252",
                "INFORMATIQUE",
                "111111",
                "111111"
        );

        etudiant = Etudiant.builder()
                .firstName("Steve")
                .lastName("Jean")
                .phoneNumber("514-327-9021")
                .matricule("214578")
                .department(Departement.INFORMATIQUE)
                .email("steveJean@gmail.com")
                .password("Passewod123")
                .build();
        etudiant.setId(1L);
    }

    @AfterEach
    void nettoyerFichiersCvDuDossierUploads() throws Exception {
        Path dossier = Paths.get("uploads", "cvs");
        if (Files.exists(dossier)) {
            try (var fichiers = Files.list(dossier)) {
                fichiers.forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (Exception ignored) {
                    }
                });
            }
        }
    }

    @Test
    void doitCreerCompteEtudiant() throws Exception{

        when(etudiantRepository.save(any(Etudiant.class)))
                .thenReturn(etudiant);

        EtudiantDTO result =
                etudiantService.creerCompteEtudiant(inscriptionEtudiantDTO);

        verify(etudiantRepository, times(1))
                .save(any(Etudiant.class));

        assertThat(result)
                .isNotNull()
                .returns(etudiant.getFirstName(), EtudiantDTO::firstName)
                .returns(etudiant.getEmail(), EtudiantDTO::email)
                .returns(etudiant.getMatricule(), EtudiantDTO::matricule)
                .returns(etudiant.getPhoneNumber(), EtudiantDTO::phoneNumber);



    }

    @Test
    void doitLancerExceptionEmailExistant() {

        when(userAppRepository.findUserAppByEmail(anyString()))
                .thenReturn(Optional.of(etudiant));

        assertThatThrownBy(() ->
                etudiantService.creerCompteEtudiant(inscriptionEtudiantDTO))
                .isInstanceOf(EmailExistantException.class);

        verify(etudiantRepository, never())
                .save(any(Etudiant.class));
    }

    @Test
    void doitLancerExceptionMotDePasseDifferent() {

        inscriptionEtudiantDTO = new InscriptionEtudiantDTO(
                "Christelle",
                "Altineus",
                "438-297-8191",
                "christelle@gmail.com",
                "2226252",
                "INFORMATIQUE",
                "111111",
                "222222"
        );

        assertThatThrownBy(() ->
                etudiantService.creerCompteEtudiant(inscriptionEtudiantDTO))
                .isInstanceOf(MotDePasseNonCorrespondantException.class);

        verify(etudiantRepository, never())
                .save(any(Etudiant.class));
    }

    @Test
    void doitLancerExceptionMatriculeExistant() {

        when(userAppRepository.findUserAppByEmail(anyString()))
                .thenReturn(Optional.empty());

        when(etudiantRepository.findByMatricule(anyString()))
                .thenReturn(Optional.of(etudiant));

        assertThatThrownBy(() ->
                etudiantService.creerCompteEtudiant(inscriptionEtudiantDTO))
                .isInstanceOf(MatriculeExistantException.class);

        verify(etudiantRepository, never())
                .save(any(Etudiant.class));
    }

    @Test
    void doitLancerExceptionNumeroTelephoneExistant() {
        when(userAppRepository.findByPhoneNumber("438-297-8191"))
                .thenReturn(Optional.of(etudiant));

        assertThatThrownBy(() ->
                etudiantService.creerCompteEtudiant(inscriptionEtudiantDTO)
        ).isInstanceOf(NumeroTelephoneExistantException.class);

        verify(etudiantRepository, never()).save(any(Etudiant.class));
    }

    @Test
    void doitTeleverserCv() throws Exception{
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "cv.pdf",
                "application/pdf",
                "contenu pdf".getBytes()
        );

        when(etudiantRepository.findByCredentialsEmail("steveJean@gmail.com"))
                .thenReturn(Optional.of(etudiant));

        when(cvEtudiantRepository.findByEtudiant(etudiant))
                .thenReturn(Optional.empty());

        when(cvEtudiantRepository.save(any(CvEtudiant.class)))
                .thenAnswer(invocation -> {
                    CvEtudiant cv = invocation.getArgument(0);
                    cv.setId(1L);
                    return cv;
                });

        CvEtudiantDTO result = etudiantService.uploadCv(file, "steveJean@gmail.com");

        assertThat(result).isNotNull();
        assertThat(result.fileName()).isEqualTo("cv.pdf");
        assertThat(result.contentType()).isEqualTo("application/pdf");

        verify(cvEtudiantRepository).save(any(CvEtudiant.class));
    }

    @Test
    void doitRemplacerAncienCv() throws Exception{
        Path ancienFichier = Paths.get("uploads", "cvs", "ancien.pdf");
        Files.createDirectories(ancienFichier.getParent());
        Files.writeString(ancienFichier, "ancien contenu");

        CvEtudiant ancienCv = CvEtudiant.builder()
                .fileName("ancien.pdf")
                .contentType("application/pdf")
                .fileSize(10L)
                .storagePath(ancienFichier.toString())
                .uploadDate(LocalDateTime.now())
                .etudiant(etudiant)
                .build();
        ancienCv.setId(1L);

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "nouveau.pdf",
                "application/pdf",
                "nouveau contenu".getBytes()
        );

        when(etudiantRepository.findByCredentialsEmail("steveJean@gmail.com"))
                .thenReturn(Optional.of(etudiant));

        when(cvEtudiantRepository.findByEtudiant(etudiant))
                .thenReturn(Optional.of(ancienCv));

        when(cvEtudiantRepository.save(any(CvEtudiant.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CvEtudiantDTO result = etudiantService.uploadCv(file, "steveJean@gmail.com");

        assertThat(result.fileName()).isEqualTo("nouveau.pdf");
        assertThat(Files.exists(ancienFichier)).isFalse();

        verify(cvEtudiantRepository).save(any(CvEtudiant.class));
    }

    @Test
    void doitRetournerCv() throws Exception{
        CvEtudiant cv = CvEtudiant.builder()
                .fileName("cv.pdf")
                .contentType("application/pdf")
                .fileSize(10L)
                .storagePath("uploads/cvs/cv.pdf")
                .uploadDate(LocalDateTime.now())
                .etudiant(etudiant)
                .build();
        cv.setId(1L);

        when(etudiantRepository.findByCredentialsEmail("steveJean@gmail.com"))
                .thenReturn(Optional.of(etudiant));

        when(cvEtudiantRepository.findByEtudiant(etudiant))
                .thenReturn(Optional.of(cv));

        CvEtudiantDTO result = etudiantService.getCv("steveJean@gmail.com");

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.fileName()).isEqualTo("cv.pdf");
    }

    @Test
    void doitSupprimerCv() throws Exception {
        Path fichier = Paths.get("uploads", "cvs", "cv.pdf");
        Files.createDirectories(fichier.getParent());
        Files.writeString(fichier, "contenu");

        CvEtudiant cv = CvEtudiant.builder()
                .fileName("cv.pdf")
                .contentType("application/pdf")
                .fileSize(10L)
                .storagePath(fichier.toString())
                .uploadDate(java.time.LocalDateTime.now())
                .etudiant(etudiant)
                .build();
        cv.setId(1L);

        when(etudiantRepository.findByCredentialsEmail("steveJean@gmail.com"))
                .thenReturn(Optional.of(etudiant));

        when(cvEtudiantRepository.findByEtudiant(etudiant))
                .thenReturn(Optional.of(cv));

        etudiantService.supprimerCv("steveJean@gmail.com");

        assertThat(Files.exists(fichier)).isFalse();
        verify(cvEtudiantRepository).delete(cv);
    }


}






