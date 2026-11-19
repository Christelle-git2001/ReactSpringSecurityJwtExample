package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.service.dto.auth.LoginDTO;
import com.lacouf.rsbjwt.service.dto.utilisateur.UserDTO;
import com.lacouf.rsbjwt.service.UserAppService;
import com.lacouf.rsbjwt.security.exception.APIException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:5173")
public class UtilisateurAppController {
    private final UserAppService userAppService;

    public UtilisateurAppController(UserAppService userAppService) {
        this.userAppService = userAppService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDTO loginDTO) throws APIException {
        String token = userAppService.authenticateUser(loginDTO);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMe(HttpServletRequest request) throws APIException {
        return ResponseEntity.accepted().contentType(MediaType.APPLICATION_JSON).body(
                userAppService.getMe(request.getHeader("Authorization")));
    }
}
