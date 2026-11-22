package com.lacouf.rsbjwt.security;

import com.lacouf.rsbjwt.model.Etudiant;
import com.lacouf.rsbjwt.model.Enum.Departement;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.security.exception.AuthenticationException;
import com.lacouf.rsbjwt.security.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthProviderTest {
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserAppRepository utilisateurs;
    @InjectMocks private AuthProvider provider;
    private Etudiant etudiant;

    @BeforeEach
    void preparer() {
        etudiant = new Etudiant("Cindy", "Test", "cindy@example.com", "5145550100",
                Departement.INFORMATIQUE, "1234567", "hash-test");
    }

    @Test
    void doitAuthentifierUnUtilisateurEtConserverSonRole() {
        when(utilisateurs.findUserAppByEmail(etudiant.getEmail())).thenReturn(Optional.of(etudiant));
        when(passwordEncoder.matches("test", "hash-test")).thenReturn(true);

        Authentication result = provider.authenticate(
                new UsernamePasswordAuthenticationToken(etudiant.getEmail(), "test"));

        assertThat(result.isAuthenticated()).isTrue();
        assertThat(result.getName()).isEqualTo(etudiant.getEmail());
        assertThat(result.getAuthorities()).extracting(authorite -> authorite.getAuthority()).containsExactly("ETUDIANT");
        verify(passwordEncoder).matches("test", "hash-test");
    }

    @Test
    void doitRefuserUnMotDePasseIncorrect() {
        when(utilisateurs.findUserAppByEmail(etudiant.getEmail())).thenReturn(Optional.of(etudiant));
        when(passwordEncoder.matches("incorrect", "hash-test")).thenReturn(false);

        assertThatExceptionOfType(org.springframework.security.authentication.BadCredentialsException.class)
                .isThrownBy(() -> provider.authenticate(
                        new UsernamePasswordAuthenticationToken(etudiant.getEmail(), "incorrect")))
                .withCauseInstanceOf(AuthenticationException.class);
    }

    @Test
    void doitRefuserUnUtilisateurIntrouvable() {
        when(utilisateurs.findUserAppByEmail("cindy@example.com")).thenReturn(Optional.empty());

        assertThatExceptionOfType(org.springframework.security.authentication.BadCredentialsException.class)
                .isThrownBy(() -> provider.authenticate(
                        new UsernamePasswordAuthenticationToken("cindy@example.com", "test")))
                .withCauseInstanceOf(UserNotFoundException.class);
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void doitSupporterUniquementLesJetonsUtilisateurMotDePasse() {
        assertThat(provider.supports(UsernamePasswordAuthenticationToken.class)).isTrue();
        assertThat(provider.supports(Authentication.class)).isFalse();
    }
}