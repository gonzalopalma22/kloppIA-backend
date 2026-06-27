package com.klopp.apunte_service.service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generarResumen(byte[] pdfBytes) {
        String base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);

        Map<String, Object> request = Map.of(
            "contents", List.of(
                Map.of("parts", List.of(
                    Map.of("text",
                        "Eres un asistente académico experto en resumir apuntes universitarios. " +
                        "Resume el siguiente apunte en español, de forma clara y detallada. " +
                        "El resumen debe explicar los conceptos principales con sus ideas clave, " +
                        "no solo listar títulos o temas. " +
                        "Organiza el contenido por secciones temáticas con una breve introducción general al inicio. " +
                        "El resumen debe ser útil para estudiar, con explicaciones que tengan sentido por sí solas. " +
                        "Responde únicamente en español, sin importar el idioma del documento original."
                    ),
                    Map.of("inline_data", Map.of(
                        "mime_type", "application/pdf",
                        "data", base64Pdf
                    ))
                ))
            )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        String urlConKey = apiUrl + "?key=" + apiKey;
        ResponseEntity<String> response = restTemplate.postForEntity(urlConKey, entity, String.class);

        return extraerTexto(response.getBody());
    }

    private String extraerTexto(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            return root.path("candidates")
                       .get(0)
                       .path("content")
                       .path("parts")
                       .get(0)
                       .path("text")
                       .asText();
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar respuesta de Gemini");
        }
    }
}