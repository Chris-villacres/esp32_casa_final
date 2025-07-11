package com.example.application.views.myview;

import com.example.application.service.SerialReaderService;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.Command;
import com.vaadin.flow.component.UI;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@PageTitle("Control de Luces y Accesos")
@Route("luces")
public class LucesView extends Composite<VerticalLayout> {

    private final SerialReaderService serialReaderService;
    private final Label temperaturaLabel = new Label("Temperatura actual: -- °C");
    private final Label motorEstadoLabel = new Label("Estado del motor: --");

    private final List<TemperaturaRegistro> temperaturaHistorial = new ArrayList<>();
    private final Grid<TemperaturaRegistro> temperaturaGrid = new Grid<>(TemperaturaRegistro.class, false);

    private int contadorLecturas = 0;

    @Autowired
    public LucesView(SerialReaderService serialReaderService) {
        this.serialReaderService = serialReaderService;

        VerticalLayout content = getContent();
        content.setWidth("100%");
        content.setAlignItems(Alignment.CENTER);
        content.setSpacing(true);

        if (!serialReaderService.isPortOpen()) {
            Notification.show(
                    "⚠ Puerto serial no está abierto. Verifica la conexión del ESP32.",
                    5000,
                    Notification.Position.MIDDLE
            );
        }

        // Mostrar etiquetas de estado
        content.add(temperaturaLabel);
        content.add(motorEstadoLabel);

        // Configurar tabla de historial de temperaturas
        temperaturaGrid.addColumn(TemperaturaRegistro::getNumero).setHeader("#").setAutoWidth(true);
        temperaturaGrid.addColumn(TemperaturaRegistro::getValor).setHeader("Temperatura (°C)").setAutoWidth(true);
        temperaturaGrid.setItems(temperaturaHistorial);
        temperaturaGrid.setWidth("300px");
        content.add(temperaturaGrid);

        // Botones
        content.add(
                createButton("ON", VaadinIcon.LIGHTBULB, "ON"),
                createButton("OFF", VaadinIcon.LIGHTBULB, "OFF"),
                createButton("CUARTO ON", VaadinIcon.BED, "CUARTO ON"),
                createButton("CUARTO OFF", VaadinIcon.BED, "CUARTO OFF"),
                createButton("EXTERNA ON", VaadinIcon.EXTERNAL_LINK, "EXTERNA ON"),
                createButton("EXTERNA OFF", VaadinIcon.EXTERNAL_LINK, "EXTERNA OFF"),
                createButton("DOOR ON", VaadinIcon.EXIT, "DOOR ON"),
                createButton("DOOR OFF", VaadinIcon.EXIT, "DOOR OFF"),
                createButton("GARAGE ON", VaadinIcon.CAR, "GARAGE ON"),
                createButton("GARAGE OFF", VaadinIcon.CAR, "GARAGE OFF"),
                createButton("MOTOR ON", VaadinIcon.PLAY_CIRCLE, "MOTOR ON"),
                createButton("MOTOR OFF", VaadinIcon.STOP, "MOTOR OFF")
        );

        // Iniciar lector automático
        startBackgroundReader();
    }

    private Button createButton(String text, VaadinIcon icon, String command) {
        Button button = new Button(text, new Icon(icon));
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickListener(e -> sendCommand(command));
        return button;
    }

    private void sendCommand(String command) {
        if (serialReaderService.isPortOpen()) {
            serialReaderService.sendCommand(command);
        } else {
            Notification.show(
                    "❌ Puerto no abierto. Comando no enviado: " + command,
                    4000,
                    Notification.Position.MIDDLE
            );
        }
    }

    private void startBackgroundReader() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            String response = serialReaderService.readResponse();
            if (response != null && !response.trim().isEmpty()) {
                UI.getCurrent().access((Command) () -> handleSerialResponse(response.trim()));
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    private void handleSerialResponse(String response) {
        // Procesar temperatura
        if (response.startsWith("Temperatura:")) {
            temperaturaLabel.setText(response);

            String valorTexto = response.replace("Temperatura:", "").replace("C", "").trim();
            try {
                double valor = Double.parseDouble(valorTexto);
                contadorLecturas++;
                temperaturaHistorial.add(new TemperaturaRegistro(contadorLecturas, valor));
                temperaturaGrid.getDataProvider().refreshAll();
            } catch (NumberFormatException ignored) {
            }
            return;
        }

        // Estado motor
        if (response.contains("Motor DC ENCENDIDO")) {
            motorEstadoLabel.setText("Estado del motor: ENCENDIDO");
            return;
        }
        if (response.contains("Motor DC APAGADO")) {
            motorEstadoLabel.setText("Estado del motor: APAGADO");
            return;
        }

        // Notificaciones solo para puerta
        if (response.contains("Abriendo puerta")) {
            Notification.show("🔓 Puerta abierta automáticamente", 3000, Notification.Position.MIDDLE);
            return;
        }

        if (response.contains("Cerrando puerta")) {
            Notification.show("🔒 Puerta cerrada automáticamente", 3000, Notification.Position.MIDDLE);
        }
    }

    // Clase anidada
    public static class TemperaturaRegistro {
        private final int numero;
        private final double valor;

        public TemperaturaRegistro(int numero, double valor) {
            this.numero = numero;
            this.valor = valor;
        }

        public int getNumero() {
            return numero;
        }

        public double getValor() {
            return valor;
        }
    }
}