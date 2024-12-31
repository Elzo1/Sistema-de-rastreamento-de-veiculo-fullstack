package com.divMaster;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import com.divMaster.controller.VehicleController;
import com.divMaster.entity.Vehicle;
import com.divMaster.repository.VehicleRepository;

class VehicleControllerTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleController vehicleController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializa os mocks antes de cada teste
    }

    @Test
    void createVehicle_ShouldReturnSavedVehicle() {
        // Arrange: Criar veículo mockado e configurar repositório simulado
        Vehicle mockVehicle = new Vehicle();
        mockVehicle.setName("Carro Teste");
        when(vehicleRepository.save(mockVehicle)).thenReturn(mockVehicle);

        // Act: Chamar o método que será testado
        Vehicle result = vehicleController.createVehicle(mockVehicle);

        // Assert: Verificar se o resultado é igual ao mock
        assertEquals(mockVehicle, result);
        verify(vehicleRepository, times(1)).save(mockVehicle);
    }

    @Test
    void getVehicleById_ShouldReturnVehicle_WhenVehicleExists() {
        // Arrange: Configurar veículo mock e ID
        String id = "123";
        Vehicle mockVehicle = new Vehicle();
        mockVehicle.setId(id);
        when(vehicleRepository.findById(id)).thenReturn(Optional.of(mockVehicle));

        // Act: Chamar o método de busca por ID
        ResponseEntity<Vehicle> response = vehicleController.getVehicleById(id);

        // Assert: Verificar se o veículo foi retornado corretamente
        assertTrue(response.getBody() != null);
        assertEquals(mockVehicle, response.getBody());
    }

    @Test
    void getVehicleById_ShouldReturnNotFound_WhenVehicleDoesNotExist() {
        // Arrange: ID que não existe
        String id = "999";
        when(vehicleRepository.findById(id)).thenReturn(Optional.empty());

        // Act: Tentar buscar veículo com ID inexistente
        ResponseEntity<Vehicle> response = vehicleController.getVehicleById(id);

        // Assert: Certificar que o retorno é 404 (Not Found)
        assertTrue(response.getStatusCode().is4xxClientError());
    }
}
