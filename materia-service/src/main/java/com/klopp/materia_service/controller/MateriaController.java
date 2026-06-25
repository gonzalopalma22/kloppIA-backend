package com.klopp.materia_service.controller;
import com.klopp.materia_service.dto.MateriaDTO;
import com.klopp.materia_service.dto.MateriaResponseDTO;
import com.klopp.materia_service.service.MateriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materias")
@RequiredArgsConstructor
public class MateriaController {

    private final MateriaService materiaService;

    @PostMapping
    public ResponseEntity<MateriaResponseDTO> crear(
            @Valid @RequestBody MateriaDTO dto,
            Authentication authentication) {
        Long usuarioId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(materiaService.crear(dto, usuarioId));
    }

    @GetMapping
    public ResponseEntity<List<MateriaResponseDTO>> listar(
            Authentication authentication) {
        Long usuarioId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(materiaService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<MateriaResponseDTO>> buscar(
            @RequestParam String nombre,
            Authentication authentication) {
        Long usuarioId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(materiaService.buscarPorNombre(usuarioId, nombre));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MateriaResponseDTO> obtener(
            @PathVariable Long id,
            Authentication authentication) {
        Long usuarioId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(materiaService.obtenerPorId(id, usuarioId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
            @PathVariable Long id,
            Authentication authentication) {
        Long usuarioId = Long.parseLong(authentication.getName());
        materiaService.eliminar(id, usuarioId);
        return ResponseEntity.ok("Materia eliminada correctamente");
    }
} 