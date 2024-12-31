package com.divMaster.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data // Lombok gera automaticamente getters, setters e construtores para a classe
@Document("vehicles") // Define que esta classe é um documento no MongoDB
public class Vehicle {

    @Id
    private String id; // Identificador único do veículo
    private String vehicleId; // ID do veículo (geralmente fornecido pelo cliente)
    private String name; // Nome do veículo
    private double latitude; // Latitude da posição atual do veículo
    private double longitude; // Longitude da posição atual do veículo
    private LocalDateTime timestamp; // Data/hora da última atualização de localização
}
