package com.example.application.service;

import com.example.application.serial.SerialController;
import org.springframework.stereotype.Service;

@Service
public class SerialReaderService {

    private final SerialController serialController;

    public SerialReaderService() {
        // 🔑 Esta línea hace que se cree el Singleton
        this.serialController = SerialController.getInstance();
    }

    public void sendCommand(String command) {
        serialController.sendCommand(command);
    }

    public String readResponse() {
        return serialController.readResponse();
    }

    public void closePort() {
        serialController.close();
    }

    public boolean isPortOpen() {
        return serialController.isPortOpen();
    }
}
