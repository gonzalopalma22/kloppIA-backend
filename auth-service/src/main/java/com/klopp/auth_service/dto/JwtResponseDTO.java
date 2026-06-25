package com.klopp.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponseDTO {
    private String token;
    private String nombre;
    private String apellido;
    private String email;
    private String rol;
}