package com.example.application.views.myview;

import com.example.application.service.SerialReaderService;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
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
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@PageTitle("Control de Luces y Accesos")
@Route("luces")
public class LucesView extends Composite<VerticalLayout> {

    private final SerialReaderService serialReaderService;
    private final Label motorEstadoLabel = new Label("Estado del motor: --");

    private final List<EventoSistema> eventosSistema = new ArrayList<>();
    private final Grid<EventoSistema> eventosGrid = new Grid<>(EventoSistema.class, false);

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

        content.add(motorEstadoLabel);

        eventosGrid.addColumn(EventoSistema::getHora).setHeader("Hora").setAutoWidth(true);
        eventosGrid.addColumn(EventoSistema::getMensaje).setHeader("Evento").setAutoWidth(true);
        eventosGrid.setItems(eventosSistema);
        eventosGrid.setWidth("600px");
        content.add(eventosGrid);

        temperaturaGrid.addColumn(TemperaturaRegistro::getNumero).setHeader("#").setAutoWidth(true);
        temperaturaGrid.addColumn(TemperaturaRegistro::getValor).setHeader("Temperatura (°C)").setAutoWidth(true);
        temperaturaGrid.setItems(temperaturaHistorial);
        temperaturaGrid.setWidth("300px");
        content.add(temperaturaGrid);

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

        serialReaderService.startListening(this::handleSerialResponse, UI.getCurrent());

        // Refresco automático de las tablas cada segundo
        UI ui = UI.getCurrent();
        Timer refrescoAutomatico = new Timer(true);
        refrescoAutomatico.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (ui != null) {
                    ui.access(() -> {
                        eventosGrid.getDataProvider().refreshAll();
                        temperaturaGrid.getDataProvider().refreshAll();
                    });
                }
            }
        }, 0, 1000); // Cada 1000 ms = 1 segundo
    }

    private Button createButton(String text, VaadinIcon icon, String command) {
        Button button = new Button(text, new Icon(icon));
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.addClickListener(e -> {
            sendCommand(command);
            registrarEventoManual(command);
        });
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

    private void handleSerialResponse(String response) {
        System.out.println("🔍 Recibido: " + response);

        if (response.toLowerCase().contains("motor encendido")) {
            motorEstadoLabel.setText("Estado del motor: ENCENDIDO");
        } else if (response.toLowerCase().contains("motor apagado")) {
            motorEstadoLabel.setText("Estado del motor: APAGADO");
        }

        if (response.toLowerCase().startsWith("temperatura:")) {
            try {
                String valorTexto = response.substring("Temperatura:".length()).replace("C", "").trim();
                double valor = Double.parseDouble(valorTexto);
                contadorLecturas++;
                if (temperaturaHistorial.size() >= 30) {
                    temperaturaHistorial.remove(0);
                }
                temperaturaHistorial.add(new TemperaturaRegistro(contadorLecturas, valor));
                // No necesitas llamar a refreshAll aquí porque ya lo hace el temporizador
            } catch (NumberFormatException ignored) {
            }
            return;
        }

        agregarEvento(response);
        Notification.show(response, 3000, Notification.Position.MIDDLE);
    }

    private void registrarEventoManual(String comando) {
        String descripcion;
        switch (comando) {
            case "ON": descripcion = "Luces principales encendidas (manual)"; break;
            case "OFF": descripcion = "Luces principales apagadas (manual)"; break;
            case "CUARTO ON": descripcion = "Luces del cuarto encendidas (manual)"; break;
            case "CUARTO OFF": descripcion = "Luces del cuarto apagadas (manual)"; break;
            case "EXTERNA ON": descripcion = "Luz externa encendida (manual)"; break;
            case "EXTERNA OFF": descripcion = "Luz externa apagada (manual)"; break;
            case "DOOR ON": descripcion = "Puerta abierta (manual)"; break;
            case "DOOR OFF": descripcion = "Puerta cerrada (manual)"; break;
            case "GARAGE ON": descripcion = "Garaje abierto (manual)"; break;
            case "GARAGE OFF": descripcion = "Garaje cerrado (manual)"; break;
            case "MOTOR ON": descripcion = "Motor encendido (manual)"; break;
            case "MOTOR OFF": descripcion = "Motor apagado (manual)"; break;
            default: descripcion = "Comando enviado: " + comando; break;
        }
        agregarEvento(descripcion);
        Notification.show(descripcion, 3000, Notification.Position.MIDDLE);
    }

    private void agregarEvento(String mensaje) {
        if (eventosSistema.size() >= 30) {
            eventosSistema.remove(0);
        }
        eventosSistema.add(new EventoSistema(mensaje));
        // No necesitas llamar a refreshAll aquí porque ya lo hace el temporizador
    }

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

    public static class EventoSistema {
        private final String mensaje;
        private final String hora;

        public EventoSistema(String mensaje) {
            this.mensaje = mensaje;
            this.hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        }

        public String getMensaje() {
            return mensaje;
        }

        public String getHora() {
            return hora;
        }
    }
}
