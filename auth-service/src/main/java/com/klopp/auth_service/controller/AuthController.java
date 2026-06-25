package com.klopp.auth_service.controller;
import com.klopp.auth_service.dto.JwtResponseDTO;
import com.klopp.auth_service.dto.LoginDTO;
import com.klopp.auth_service.dto.RegistroDTO;
import com.klopp.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/registro")
    public ResponseEntity<?> registro(@Valid @RequestBody RegistroDTO dto) {
        authService.registro(dto);
        return ResponseEntity.ok("Usuario registrado correctamente");
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDTO> login(@Valid @RequestBody LoginDTO dto) {
        JwtResponseDTO response = authService.login(dto);
        return ResponseEntity.ok(response);
    }
} 

