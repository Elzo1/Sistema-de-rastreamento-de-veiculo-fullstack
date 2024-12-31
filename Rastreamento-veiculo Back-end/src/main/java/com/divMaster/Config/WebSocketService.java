package com.divMaster.Config;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.divMaster.dto.VehicleDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WebSocketService extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketService.class);
    private static final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        logger.info("Conexão WebSocket estabelecida: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        logger.info("Conexão WebSocket encerrada: " + session.getId());
    }

    public void broadcastVehicleUpdate(VehicleDTO vehicleDTO) {
        String message;
        try {
            message = objectMapper.writeValueAsString(vehicleDTO);
        } catch (IOException e) {
            logger.error("Erro ao serializar mensagem: {}", e.getMessage());
            return;
        }

        for (WebSocketSession session : sessions) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (IOException e) {
                logger.error("Erro ao enviar mensagem para o WebSocket: {}", e.getMessage());
            }
        }
    }
}
