package com.klopp.auth_service.controller;

import com.klopp.auth_service.model.Usuario;
import com.klopp.auth_service.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth/admin/users")
@RequiredArgsConstructor 
public class AdminUserController {

    private final UsuarioRepository usuarioRepository; 

    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsers() {
        return ResponseEntity.ok(usuarioRepository.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        usuarioRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
public ResponseEntity<Usuario> getUserById(@PathVariable Long id) {
    return usuarioRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}

    @PutMapping("/{id}")
public ResponseEntity<Usuario> updateUser(@PathVariable Long id, @RequestBody Usuario datos) {
        return usuarioRepository.findById(id)
            .map(usuario -> {
                usuario.setNombre(datos.getNombre());
                usuario.setApellido(datos.getApellido());
                usuario.setEmail(datos.getEmail());
                usuario.setRol(datos.getRol());
                return ResponseEntity.ok(usuarioRepository.save(usuario));
             })
             .orElse(ResponseEntity.notFound().build());
 }
}