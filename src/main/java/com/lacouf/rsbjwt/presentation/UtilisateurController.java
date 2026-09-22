package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.service.UserAppService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.lacouf.rsbjwt.service.dto.LoginDTO;

@RestController
@RequestMapping("/user")
public class UtilisateurController {
    private final UserAppService userAppService;

    public UtilisateurController (UserAppService userAppService) {
        this.userAppService = userAppService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO loginDTO) {
        String token = userAppService.authenticateUser(loginDTO);
        return ResponseEntity.ok(token);
    }
}
