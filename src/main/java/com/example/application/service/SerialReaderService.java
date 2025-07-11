package com.example.application.service;

import com.example.application.serial.SerialController;
import com.vaadin.flow.component.UI;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class SerialReaderService {

    private final SerialController serialController;

    public SerialReaderService() {
        this.serialController = SerialController.getInstance();
    }

    /**
     * Inicia la escucha y asegura que el callback se ejecute dentro del contexto UI.
     * @param onMessageReceived callback para manejar mensajes recibidos
     * @param ui contexto UI para acceder de forma segura
     */
    public void startListening(Consumer<String> onMessageReceived, UI ui) {
        serialController.startListening(line -> {
            ui.access(() -> onMessageReceived.accept(line));
        });
    }

    public void sendCommand(String command) {
        serialController.sendCommand(command);
    }

    public void closePort() {
        serialController.close();
    }

    public boolean isPortOpen() {
        return serialController.isPortOpen();
    }
}
