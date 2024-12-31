package com.divMaster.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.divMaster.entity.Vehicle;

@Repository // Marca essa interface como um repositório do Spring Data
public interface VehicleRepository extends MongoRepository<Vehicle, String> {
    // Método para buscar veículo por seu ID personalizado
    Optional<Vehicle> findByVehicleId(String vehicleId); // Método para buscar um veículo pelo ID do cliente
}
