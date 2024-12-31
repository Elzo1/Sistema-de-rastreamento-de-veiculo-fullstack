package com.divMaster.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.divMaster.entity.Vehicle;
import com.divMaster.repository.VehicleRepository;

/**
 * Controlador REST para gerenciar veículos.
 */
@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleRepository repository;

    // Construtor para injetar o repositório de veículos.
    public VehicleController(VehicleRepository repository) {
        this.repository = repository;
    }

    /**
     * Retorna todos os veículos armazenados no banco de dados.
     * 
     * @return Lista de veículos.
     */
    @GetMapping
    public List<Vehicle> getAllVehicles() {
        return repository.findAll();
    }

    /**
     * Retorna um veículo pelo ID.
     * 
     * @param id ID do veículo.
     * @return Detalhes do veículo.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable String id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Cria um novo veículo.
     * 
     * @param vehicle Objeto veículo enviado no corpo da requisição.
     * @return Veículo criado.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Vehicle createVehicle(@RequestBody Vehicle vehicle) {
        return repository.save(vehicle);
    }

    /**
     * Atualiza os dados de um veículo existente.
     * 
     * @param id             ID do veículo.
     * @param updatedVehicle Dados atualizados.
     * @return Veículo atualizado.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable String id, @RequestBody Vehicle updatedVehicle) {
        return repository.findById(id).map(vehicle -> {
            vehicle.setName(updatedVehicle.getName());
            vehicle.setLatitude(updatedVehicle.getLatitude());
            vehicle.setLongitude(updatedVehicle.getLongitude());
            vehicle.setTimestamp(updatedVehicle.getTimestamp());
            return ResponseEntity.ok(repository.save(vehicle));
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Exclui um veículo.
     * 
     * @param id ID do veículo.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVehicle(@PathVariable String id) {
        repository.deleteById(id);
    }
}
