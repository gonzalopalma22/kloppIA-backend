package com.klopp.auth_service.service;

import com.klopp.auth_service.dto.JwtResponseDTO;
import com.klopp.auth_service.dto.LoginDTO;
import com.klopp.auth_service.dto.RegistroDTO;
import com.klopp.auth_service.model.Rol;
import com.klopp.auth_service.model.Usuario;
import com.klopp.auth_service.repository.UsuarioRepository;
import com.klopp.auth_service.security.JwtService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public void registro(RegistroDTO dto) {
    if (usuarioRepository.existsByEmail(dto.getEmail())) {
        throw new RuntimeException("El email ya está registrado");
    }

    Usuario usuario = new Usuario();
    usuario.setNombre(dto.getNombre());
    usuario.setApellido(dto.getApellido());
    usuario.setEmail(dto.getEmail());
    usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
    usuario.setRol(Rol.ROLE_USER);

    usuarioRepository.save(usuario);
}

    public JwtResponseDTO login(LoginDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(dto.getPassword(), usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }
        String token = jwtService.generarToken(usuario.getId(), usuario.getEmail(), usuario.getRol().name());

        return new JwtResponseDTO(token, usuario.getNombre(), 
                                  usuario.getApellido(), usuario.getEmail(), usuario.getRol().name());
    }
}