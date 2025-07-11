package com.example.application.serial;

import com.fazecast.jSerialComm.SerialPort;

import java.io.InputStream;
import java.util.Scanner;
import java.util.function.Consumer;

public class SerialController {

    private static SerialController instance;
    private SerialPort serialPort;
    private Thread listenerThread;

    private SerialController() {
        SerialPort[] ports = SerialPort.getCommPorts();
        for (SerialPort port : ports) {
            System.out.println("Puerto disponible: " + port.getSystemPortName());
        }

        if (ports.length > 0) {
            serialPort = ports[0]; // Usa el primer puerto disponible
            serialPort.setBaudRate(115200);
            serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);

            if (serialPort.openPort()) {
                System.out.println("✅ Puerto abierto: " + serialPort.getSystemPortName());
            } else {
                System.out.println("❌ No se pudo abrir el puerto: " + serialPort.getSystemPortName());
            }
        } else {
            System.out.println("⚠️ No se encontraron puertos seriales.");
        }
    }

    public static SerialController getInstance() {
        if (instance == null) {
            instance = new SerialController();
        }
        return instance;
    }

    public void sendCommand(String command) {
        if (serialPort != null && serialPort.isOpen()) {
            String fullCommand = command + "\n";
            serialPort.writeBytes(fullCommand.getBytes(), fullCommand.length());
            System.out.println("📤 Comando enviado: " + command);
        } else {
            System.out.println("❌ Puerto no abierto. No se pudo enviar el comando: " + command);
        }
    }

    public void startListening(Consumer<String> onMessageReceived) {
        if (serialPort == null || !serialPort.isOpen()) {
            System.out.println("⚠️ Puerto no abierto. No se puede iniciar listener.");
            return;
        }

        listenerThread = new Thread(() -> {
            try (InputStream in = serialPort.getInputStream();
                 Scanner scanner = new Scanner(in)) {

                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    System.out.println("📥 Mensaje recibido: " + line);
                    if (onMessageReceived != null) {
                        onMessageReceived.accept(line);
                    }
                }

            } catch (Exception e) {
                System.out.println("❌ Error en listener: " + e.getMessage());
                e.printStackTrace();
            }
        });

        listenerThread.start();
        System.out.println("👂 Listener iniciado para recibir mensajes automáticos.");
    }

    public void close() {
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
            System.out.println("🔌 Puerto cerrado correctamente.");
        }
    }

    public boolean isPortOpen() {
        return serialPort != null && serialPort.isOpen();
    }
}
