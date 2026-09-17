package com.lacouf.rsbjwt.service;

/*import com.lacouf.rsbjwt.model.Professeur;
import com.lacouf.rsbjwt.repository.ProfesseurRepository;
import com.lacouf.rsbjwt.service.dto.InscriptionProfesseurDto;
import com.lacouf.rsbjwt.service.dto.ProfesseurDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class ProfesseurServiceTest {

    @Mock
    private ProfesseurRepository professeurRepository ;

    @Mock
    private PasswordEncoder passwordEncoder ;

    @InjectMocks
    private ProfesseurService professeurService ;

    @Test
    public void doitCreerCompteProfesseur() throws Exception {

        Professeur professeur = Professeur.builder()
                .firstName("Test")
                .lastName("TestName")
                .email("test@test.com")
                .password(passwordEncoder.encode("Test123"))
                .matricule("00001")
                .phoneNumber("123-123-1234")
                .department("TestDepartment")
                .build();

        InscriptionProfesseurDto inscriptionProfesseurDto = new InscriptionProfesseurDto(
        "Test", "TestName","test@test.com", "Test123",
        "Test123", "00001", "123-123-1234", "TestDepartment");

        ProfesseurDto result = professeurService.creerCompte(inscriptionProfesseurDto);

        

    }

}*/
