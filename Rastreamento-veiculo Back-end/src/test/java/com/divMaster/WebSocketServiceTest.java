package com.divMaster;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.divMaster.Config.WebSocketService;
import com.divMaster.dto.VehicleDTO;

class WebSocketServiceTest {

    @Test
    void broadcastVehicleUpdate_ShouldSendMessageToAllSessions() throws Exception {
        // Arrange
        WebSocketSession session1 = mock(WebSocketSession.class);
        WebSocketSession session2 = mock(WebSocketSession.class);
        WebSocketService service = new WebSocketService();
        service.afterConnectionEstablished(session1);
        service.afterConnectionEstablished(session2);

        // Act
        service.broadcastVehicleUpdate(new VehicleDTO("1", "V1", "Carro Teste", 10.0, 20.0, null));

        // Assert
        verify(session1, times(1)).sendMessage(any(TextMessage.class));
        verify(session2, times(1)).sendMessage(any(TextMessage.class));
    }
}
