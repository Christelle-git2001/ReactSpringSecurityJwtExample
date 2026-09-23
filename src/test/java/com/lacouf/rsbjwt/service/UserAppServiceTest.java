package com.lacouf.rsbjwt.service;

import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.security.JwtTokenProvider;
import com.lacouf.rsbjwt.service.dto.LoginDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UserAppServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private Authentication authentication;

    private UserAppService userAppService;

    @Mock
    private UserAppRepository userAppRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userAppService = new UserAppService(
                authenticationManager,
                jwtTokenProvider,
                userAppRepository,
                null,
                null,
                null,
                null
        );
    }

    @Test
    void authentificationRetourneToken() {

        LoginDTO loginDTO =
                new LoginDTO("christelle@gmail.com", "111111");

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(jwtTokenProvider.generateToken(authentication))
                .thenReturn("fake-jwt-token");

        String token = userAppService.authenticateUser(loginDTO);

        assertEquals("fake-jwt-token", token);
    }


}