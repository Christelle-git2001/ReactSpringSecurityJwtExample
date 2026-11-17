package com.lacouf.rsbjwt.security;

import com.lacouf.rsbjwt.security.exception.InvalidJwtTokenException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HexFormat;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class JwtTokenProviderTest {
    private JwtTokenProvider provider;
    private SecretKey cleTest;

    @BeforeEach
    void preparer() {
        String secretTest = "11".repeat(32);
        provider = new JwtTokenProvider();
        ReflectionTestUtils.setField(provider, "jwtSecret", secretTest);
        ReflectionTestUtils.setField(provider, "expirationInMs", 3_600_000);
        cleTest = Keys.hmacShaKeyFor(HexFormat.of().parseHex(secretTest));
    }

    @Test
    void doitGenererUnJetonValideAvecCourrielRoleEtExpiration() {
        var authentification = new UsernamePasswordAuthenticationToken("cindy@example.com", null,
                List.of(new SimpleGrantedAuthority("ETUDIANT")));
        Date avant = new Date();

        String jeton = provider.generateToken(authentification);

        assertThatNoException().isThrownBy(() -> provider.validateToken(jeton));
        assertThat(provider.getEmailFromJWT(jeton)).isEqualTo("cindy@example.com");
        var contenu = Jwts.parser().verifyWith(cleTest).build().parseSignedClaims(jeton).getPayload();
        assertThat(contenu.getExpiration()).isAfter(avant);
        assertThat(contenu.getExpiration().getTime() - contenu.getIssuedAt().getTime()).isEqualTo(3_600_000L);
        assertThat(contenu.get("authorities").toString()).contains("ETUDIANT");
    }

    @Test
    void doitRefuserUnJetonExpire() {
        String jeton = Jwts.builder().subject("cindy@example.com")
                .expiration(new Date(0)).signWith(cleTest).compact();

        assertThatExceptionOfType(InvalidJwtTokenException.class)
                .isThrownBy(() -> provider.validateToken(jeton))
                .withMessage("Expired JWT token");
    }

    @Test
    void doitRefuserUnJetonMalforme() {
        assertThatExceptionOfType(InvalidJwtTokenException.class)
                .isThrownBy(() -> provider.validateToken("pas-un-jwt"))
                .withMessage("Invalid JWT token");
    }

    @Test
    void doitRefuserLesJetonsVides() {
        String[] jetonsInvalides = {null, "", "   "};

        for (String jeton : jetonsInvalides) {
            assertThatExceptionOfType(InvalidJwtTokenException.class)
                    .isThrownBy(() -> provider.validateToken(jeton))
                    .withMessage("JWT claims string is empty");
        }
    }

    @Test
    void doitRefuserUnJetonNonSigne() {
        String jeton = Jwts.builder().subject("cindy@example.com").compact();

        assertThatExceptionOfType(InvalidJwtTokenException.class)
                .isThrownBy(() -> provider.validateToken(jeton))
                .withMessage("Unsupported JWT token");
    }

    @Test
    void doitRefuserUneSignatureProduiteAvecUneAutreCle() {
        SecretKey autreCle = Keys.hmacShaKeyFor(HexFormat.of().parseHex("22".repeat(32)));
        String jeton = Jwts.builder().subject("cindy@example.com").signWith(autreCle).compact();

        assertThatThrownBy(() -> provider.validateToken(jeton)).isInstanceOf(JwtException.class);
        assertThatThrownBy(() -> provider.getEmailFromJWT(jeton)).isInstanceOf(JwtException.class);
    }
}