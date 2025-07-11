package com.example.application.service;

import com.example.application.serial.SerialController;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class SerialReaderService {

    private final SerialController serialController;

    public SerialReaderService() {
        // Singleton del controlador serie
        this.serialController = SerialController.getInstance();
    }

    /**
     * Inicializa la escucha de mensajes entrantes desde el ESP32.
     * @param onMessageReceived Callback para registrar acciones automáticas.
     */
    public void startListening(Consumer<String> onMessageReceived) {
        serialController.startListening(onMessageReceived);
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
