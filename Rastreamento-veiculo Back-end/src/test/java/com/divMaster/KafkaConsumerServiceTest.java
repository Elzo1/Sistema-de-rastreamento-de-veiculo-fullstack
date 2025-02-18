package com.divMaster;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.divMaster.Config.WebSocketService;
import com.divMaster.entity.Vehicle;
import com.divMaster.repository.VehicleRepository;
import com.divMaster.service.KafkaConsumerService;
import com.fasterxml.jackson.databind.ObjectMapper;  

class KafkaConsumerServiceTest {

    private KafkaConsumerService consumerService;
    private VehicleRepository vehicleRepository;
    private WebSocketService webSocketService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        vehicleRepository = mock(VehicleRepository.class);
        webSocketService = mock(WebSocketService.class);
        objectMapper = new ObjectMapper();
        consumerService = new KafkaConsumerService(vehicleRepository, webSocketService, objectMapper);
    }

    @Test
    void consumeVehicleLocation_ShouldProcessMessageAndSaveVehicle() throws Exception {
        // Arrange
        String message = "{\"id\":\"1\",\"vehicleId\":\"V1\",\"name\":\"Carro Teste\",\"latitude\":10.0,\"longitude\":20.0,\"timestamp\":null}";
        Vehicle vehicle = objectMapper.readValue(message, Vehicle.class);

        // Act
        consumerService.consumeVehicleLocation(message);

        // Assert
        verify(vehicleRepository, times(1)).save(vehicle);
        verify(webSocketService, times(1)).broadcastVehicleUpdate(any());
    }
}
