package com.divMaster.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.divMaster.Config.WebSocketService;
import com.divMaster.dto.VehicleDTO;
import com.divMaster.entity.Vehicle;
import com.divMaster.repository.VehicleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final VehicleRepository repository;
    private final WebSocketService webSocketService;
    private final ObjectMapper objectMapper;

    public KafkaConsumerService(VehicleRepository repository, WebSocketService webSocketService, ObjectMapper objectMapper) {
        this.repository = repository;
        this.webSocketService = webSocketService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "vehicle-locations", groupId = "vehicle-tracking-group", containerFactory = "kafkaListenerContainerFactory")
    public void consumeVehicleLocation(String message) {
        try {
            // Parse da mensagem recebida
            Vehicle vehicle = objectMapper.readValue(message, Vehicle.class);
            
            // Atualiza o banco de dados
            repository.save(vehicle);

            // Transmite a atualização para o front-end
            VehicleDTO vehicleDTO = convertToDTO(vehicle);
            webSocketService.broadcastVehicleUpdate(vehicleDTO);

            logger.info("Veículo atualizado e transmitido: {}", vehicle.getId());
        } catch (Exception e) {
            logger.error("Erro ao processar mensagem Kafka: {}", e.getMessage());
        }
    }

    private VehicleDTO convertToDTO(Vehicle vehicle) {
        return new VehicleDTO(
            vehicle.getId(),
            vehicle.getVehicleId(),
            vehicle.getName(),
            vehicle.getLatitude(),
            vehicle.getLongitude(),
            vehicle.getTimestamp()
        );
    }
}
