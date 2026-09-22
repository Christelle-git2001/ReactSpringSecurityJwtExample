package com.lacouf.rsbjwt.presentation;

import com.lacouf.rsbjwt.service.UserAppService;
import com.lacouf.rsbjwt.service.dto.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.lacouf.rsbjwt.service.dto.LoginDTO;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:5173")
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

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMe(HttpServletRequest request){
        return ResponseEntity.accepted().contentType(MediaType.APPLICATION_JSON).body(
                userAppService.getMe(request.getHeader("Authorization")));
    }
}
