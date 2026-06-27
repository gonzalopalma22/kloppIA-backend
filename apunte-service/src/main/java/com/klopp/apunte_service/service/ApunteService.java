package com.klopp.apunte_service.service;
import com.klopp.apunte_service.dto.ApunteResponseDTO;
import com.klopp.apunte_service.model.Apunte;
import com.klopp.apunte_service.repository.ApunteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApunteService {

    private final ApunteRepository apunteRepository;
    private final GeminiService geminiService;

    public ApunteResponseDTO crear(String titulo, MultipartFile archivo,
            Long materiaId, Long usuarioId) throws IOException {
        byte[] pdfBytes = archivo.getBytes();
        String resumen = geminiService.generarResumen(pdfBytes);
        Apunte apunte = new Apunte();
        apunte.setTitulo(titulo);
        apunte.setResumen(resumen);
        apunte.setNombreArchivo(archivo.getOriginalFilename());
        apunte.setMateriaId(materiaId);
        apunte.setUsuarioId(usuarioId);
        Apunte guardado = apunteRepository.save(apunte);
        return mapToResponse(guardado);
    }

    public ApunteResponseDTO editarTitulo(Long id, Long usuarioId, String titulo) {
        Apunte apunte = apunteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Apunte no encontrado"));
        if (!apunte.getUsuarioId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para editar este apunte");
        }
        apunte.setTitulo(titulo);
        return mapToResponse(apunteRepository.save(apunte));
    }

    public List<ApunteResponseDTO> listarPorMateria(Long materiaId, Long usuarioId) {
        return apunteRepository
                .findByMateriaIdAndUsuarioIdOrderByCreatedAtDesc(materiaId, usuarioId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<ApunteResponseDTO> buscarPorTitulo(Long materiaId, Long usuarioId, String titulo) {
        return apunteRepository
                .findByMateriaIdAndUsuarioIdAndTituloContainingIgnoreCaseOrderByCreatedAtDesc(
                        materiaId, usuarioId, titulo)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public ApunteResponseDTO obtenerPorId(Long id, Long usuarioId) {
        Apunte apunte = apunteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Apunte no encontrado"));
        if (!apunte.getUsuarioId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para ver este apunte");
        }
        return mapToResponse(apunte);
    }

    public void eliminar(Long id, Long usuarioId) {
        Apunte apunte = apunteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Apunte no encontrado"));
        if (!apunte.getUsuarioId().equals(usuarioId)) {
            throw new RuntimeException("No tienes permiso para eliminar este apunte");
        }
        apunteRepository.deleteById(id);
    }

    private ApunteResponseDTO mapToResponse(Apunte apunte) {
        return new ApunteResponseDTO(
                apunte.getId(),
                apunte.getTitulo(),
                apunte.getResumen(),
                apunte.getNombreArchivo(),
                apunte.getMateriaId(),
                apunte.getUsuarioId(),
                apunte.getCreatedAt()
        );
    }
}