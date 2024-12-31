package com.divMaster.exception;

// Exceção personalizada quando o veículo não é encontrado
public class VehicleNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    // Construtor que recebe o ID do veículo e passa a mensagem para a classe pai
    public VehicleNotFoundException(String id) {
        super("Veículo com ID " + id + " não encontrado.");
    }
}
