package com.example.application.service;

import com.fazecast.jSerialComm.SerialPort;
import org.springframework.stereotype.Service;

@Service
public class SerialReaderService {

    private SerialPort comPort;

    /**
     * Abre el puerto serie con el nombre y baudios especificados.
     * Si ya está abierto, lo cierra primero.
     */
    public void openPort(String portName, int baudRate) {
        if (comPort != null && comPort.isOpen()) {
            comPort.closePort();
        }
        comPort = SerialPort.getCommPort(portName);
        comPort.setBaudRate(baudRate);
        if (!comPort.openPort()) {
            throw new IllegalStateException("No se pudo abrir el puerto " + portName);
        }
    }

    /**
     * Envía un comando por el puerto serial seguido de un salto de línea.
     */
    public void sendCommand(String command) {
        if (comPort == null || !comPort.isOpen()) {
            throw new IllegalStateException("Puerto no abierto. Llama a openPort() primero.");
        }
        byte[] data = (command + "\n").getBytes();
        comPort.writeBytes(data, data.length);
    }

    /**
     * Lee una línea de texto desde el puerto serie hasta '\n'.
     * Bloquea hasta que llegan datos.
     */
    public String readLine() throws InterruptedException {
        if (comPort == null || !comPort.isOpen()) {
            throw new IllegalStateException("Puerto no abierto");
        }
        StringBuilder sb = new StringBuilder();
        while (true) {
            while (comPort.bytesAvailable() == 0) {
                Thread.sleep(10);
            }
            byte[] buf = new byte[1];
            comPort.readBytes(buf, 1);
            char c = (char) buf[0];
            if (c == '\n') {
                break;
            }
            sb.append(c);
        }
        return sb.toString().trim();
    }

    /**
     * Cierra el puerto serie si está abierto.
     */
    public void closePort() {
        if (comPort != null && comPort.isOpen()) {
            comPort.closePort();
        }
    }

    /**
     * Verifica si el puerto está abierto.
     */
    public boolean isPortOpen() {
        return comPort != null && comPort.isOpen();
    }
}
