package com.klopp.auth_service.service;

import com.klopp.auth_service.dto.JwtResponseDTO;
import com.klopp.auth_service.dto.LoginDTO;
import com.klopp.auth_service.dto.RegistroDTO;
import com.klopp.auth_service.model.Rol;
import com.klopp.auth_service.model.Usuario;
import com.klopp.auth_service.repository.UsuarioRepository;
import com.klopp.auth_service.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Gonzalo");
        usuario.setApellido("Palma");
        usuario.setEmail("gonzalo@test.com");
        usuario.setPassword("hashedPassword");
        usuario.setRol(Rol.ROLE_USER);
    }

    @Test
    void registro_exitoso() {
        RegistroDTO dto = new RegistroDTO();
        dto.setNombre("Gonzalo");
        dto.setApellido("Palma");
        dto.setEmail("gonzalo@test.com");
        dto.setPassword("123456");

        when(usuarioRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("hashedPassword");

        authService.registro(dto);

        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void registro_emailDuplicado_lanzaExcepcion() {
        RegistroDTO dto = new RegistroDTO();
        dto.setEmail("gonzalo@test.com");

        when(usuarioRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.registro(dto));

        assertEquals("El email ya está registrado", ex.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void login_exitoso() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("gonzalo@test.com");
        dto.setPassword("123456");

        when(usuarioRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(dto.getPassword(), usuario.getPassword())).thenReturn(true);
        when(jwtService.generarToken(anyLong(), anyString(), anyString())).thenReturn("token123");

        JwtResponseDTO response = authService.login(dto);

        assertNotNull(response);
        assertEquals("token123", response.getToken());
        assertEquals("Gonzalo", response.getNombre());
        assertEquals("ROLE_USER", response.getRol());
    }

    @Test
    void login_usuarioNoEncontrado_lanzaExcepcion() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("noexiste@test.com");
        dto.setPassword("123456");

        when(usuarioRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.login(dto));

        assertEquals("Usuario no encontrado", ex.getMessage());
    }

    @Test
    void login_contrasenaIncorrecta_lanzaExcepcion() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("gonzalo@test.com");
        dto.setPassword("wrongpassword");

        when(usuarioRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches(dto.getPassword(), usuario.getPassword())).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.login(dto));

        assertEquals("Contraseña incorrecta", ex.getMessage());
    }
}