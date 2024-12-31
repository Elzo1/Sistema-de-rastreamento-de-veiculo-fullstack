package com.divMaster.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Gera automaticamente getters, setters, toString(), equals() e hashCode()
@NoArgsConstructor // Cria um construtor padrão sem argumentos
@AllArgsConstructor // Cria um construtor com todos os argumentos
public class VehicleDTO {
    private String id;
    private String vehicleId;
    private String name;
    private double latitude;
    private double longitude;
    private LocalDateTime timestamp;
} 