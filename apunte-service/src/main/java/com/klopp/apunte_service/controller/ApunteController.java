package com.klopp.apunte_service.controller;

import com.klopp.apunte_service.dto.ApunteResponseDTO;
import com.klopp.apunte_service.service.ApunteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/materias/{materiaId}/apuntes")
@RequiredArgsConstructor
public class ApunteController {

    private final ApunteService apunteService;

    @PostMapping
    public ResponseEntity<ApunteResponseDTO> crear(
            @PathVariable Long materiaId,
            @RequestParam("titulo") String titulo,
            @RequestParam("archivo") MultipartFile archivo,
            @RequestHeader("X-User-Id") Long userId) throws IOException { // <-- Leemos el email directo del Gateway
        
        return ResponseEntity.ok(apunteService.crear(titulo, archivo, materiaId, userId));
    }

    @GetMapping
    public ResponseEntity<List<ApunteResponseDTO>> listar(
            @PathVariable Long materiaId,
            @RequestHeader("X-User-Id") Long userId) {
        
        return ResponseEntity.ok(apunteService.listarPorMateria(materiaId, userId));
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ApunteResponseDTO>> buscar(
            @PathVariable Long materiaId,
            @RequestParam String titulo,
            @RequestHeader("X-User-Id") Long userId) {
        
        return ResponseEntity.ok(apunteService.buscarPorTitulo(materiaId, userId, titulo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApunteResponseDTO> obtener(
            @PathVariable Long materiaId,
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        
        return ResponseEntity.ok(apunteService.obtenerPorId(id, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(
            @PathVariable Long materiaId,
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        apunteService.eliminar(id, userId);
        return ResponseEntity.ok("Apunte eliminado correctamente");
    }
}