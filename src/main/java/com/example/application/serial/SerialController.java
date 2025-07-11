package com.example.application.serial;

import com.fazecast.jSerialComm.SerialPort;

import java.io.InputStream;
import java.util.Scanner;
import java.util.function.Consumer;

public class SerialController {

    private static SerialController instance;
    private SerialPort serialPort;
    private Thread listenerThread;

    // Constructor privado: abre el puerto una sola vez
    private SerialController() {
        // Lista puertos disponibles
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

    // Singleton: siempre devuelve la misma instancia
    public static SerialController getInstance() {
        if (instance == null) {
            instance = new SerialController();
        }
        return instance;
    }

    // Envía comando con salto de línea
    public void sendCommand(String command) {
        if (serialPort != null && serialPort.isOpen()) {
            String fullCommand = command + "\n";
            serialPort.writeBytes(fullCommand.getBytes(), fullCommand.length());
            System.out.println("📤 Comando enviado: " + command);
        } else {
            System.out.println("❌ Puerto no abierto. No se pudo enviar el comando: " + command);
        }
    }

    // ✅ Listener: lee datos continuamente y usa el callback
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

    /*
    // Puedes eliminar este método si usas startListening()
    public String readResponse() {
        if (serialPort != null && serialPort.isOpen()) {
            byte[] buffer = new byte[1024];
            int numRead = serialPort.readBytes(buffer, buffer.length);
            if (numRead > 0) {
                String response = new String(buffer, 0, numRead).trim();
                System.out.println("📥 Respuesta recibida: " + response);
                return response;
            }
        }
        return "";
    }
    */

    // Cierra el puerto
    public void close() {
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.closePort();
            System.out.println("🔌 Puerto cerrado correctamente.");
        }
    }

    // Verifica si el puerto está abierto
    public boolean isPortOpen() {
        return serialPort != null && serialPort.isOpen();
    }
}
