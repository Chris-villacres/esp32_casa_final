package com.example.application.views.myview;

import com.example.application.service.SerialReaderService;
import com.example.application.model.EventoSistema;
import com.example.application.repository.EventoSistemaRepository;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@PageTitle("⚡ Control de Luces y Accesos ⚡")
@Route("")
public class CasaView extends Composite<VerticalLayout> {

    private final SerialReaderService serialReaderService;
    private final Span motorEstadoLabel = new Span("⚙️ Estado del motor: --");

    private final List<EventoSistema> eventosSistema = new ArrayList<>();
    private final Grid<EventoSistema> eventosGrid = new Grid<>(EventoSistema.class, false);

    private final List<TemperaturaRegistro> temperaturaHistorial = new ArrayList<>();
    private final Grid<TemperaturaRegistro> temperaturaGrid = new Grid<>(TemperaturaRegistro.class, false);

    private int contadorLecturas = 0;

    private final EventoSistemaRepository eventoSistemaRepository;

    @Autowired
    public CasaView(SerialReaderService serialReaderService, EventoSistemaRepository eventoSistemaRepository) {
        this.serialReaderService = serialReaderService;
        this.eventoSistemaRepository = eventoSistemaRepository;

        VerticalLayout content = getContent();
        content.setWidthFull();
        content.setSpacing(true);

        if (!serialReaderService.isPortOpen()) {
            agregarEvento("⚠️ Puerto serial no está abierto. Verifica la conexión del ESP32.");
        }

        // --- Events Grid ---
        eventosGrid.addColumn(EventoSistema::getHora).setHeader("🕒 Hora").setAutoWidth(true);
        eventosGrid.addColumn(EventoSistema::getMensaje).setHeader("📝 Evento").setAutoWidth(true);
        eventosGrid.setItems(eventosSistema);
        eventosGrid.setWidth("400px");

        // --- Temperatures Grid ---
        temperaturaGrid.addColumn(TemperaturaRegistro::getNumero).setHeader("#").setAutoWidth(true);
        temperaturaGrid.addColumn(TemperaturaRegistro::getValor).setHeader("🌡️ Temperatura (°C)").setAutoWidth(true);
        temperaturaGrid.setItems(temperaturaHistorial);
        temperaturaGrid.setWidth("300px");

        // --- Layout: Two columns side by side ---
        VerticalLayout eventosLayout = new VerticalLayout(new H3("Eventos del Sistema"), eventosGrid);
        VerticalLayout temperaturaLayout = new VerticalLayout(new H3("Historial de Temperaturas"), temperaturaGrid);

        HorizontalLayout gridsLayout = new HorizontalLayout(eventosLayout, temperaturaLayout);
        gridsLayout.setWidthFull();
        gridsLayout.setSpacing(true);
        gridsLayout.setAlignItems(Alignment.START);

        // --- Botones de control ---
        VerticalLayout botonesLayout = new VerticalLayout();
        botonesLayout.setSpacing(true);
        botonesLayout.setAlignItems(Alignment.CENTER);
        botonesLayout.getStyle().set("padding", "20px");

        botonesLayout.add(
                new H3("💡 Luces Principales"),
                new HorizontalLayout(
                        createButton("ON", VaadinIcon.LIGHTBULB, "ON"),
                        createButton("OFF", VaadinIcon.LIGHTBULB, "OFF")
                ),
                new H3("🛏️ Luces de Cuarto"),
                new HorizontalLayout(
                        createButton("CUARTO ON", VaadinIcon.BED, "CUARTO ON"),
                        createButton("CUARTO OFF", VaadinIcon.BED, "CUARTO OFF")
                ),
                new H3("🌐 Luz Externa"),
                new HorizontalLayout(
                        createButton("EXTERNA ON", VaadinIcon.EXTERNAL_LINK, "EXTERNA ON"),
                        createButton("EXTERNA OFF", VaadinIcon.EXTERNAL_LINK, "EXTERNA OFF")
                ),
                new H3("🚪 Puerta"),
                new HorizontalLayout(
                        createButton("DOOR ON", VaadinIcon.ENTER_ARROW, "DOOR ON"),
                        createButton("DOOR OFF", VaadinIcon.EXIT, "DOOR OFF")
                ),
                new H3("🚗 Garage"),
                new HorizontalLayout(
                        createButton("GARAGE ON", VaadinIcon.CAR, "GARAGE ON"),
                        createButton("GARAGE OFF", VaadinIcon.CAR, "GARAGE OFF")
                ),
                new H3("🌀 Motor / Ventilador"),
                new HorizontalLayout(
                        createButton("MOTOR ON", VaadinIcon.PLAY_CIRCLE, "MOTOR ON"),
                        createButton("MOTOR OFF", VaadinIcon.STOP, "MOTOR OFF")
                )
        );

        // --- Estado del motor arriba de los grids ---
        VerticalLayout topLayout = new VerticalLayout(motorEstadoLabel);
        topLayout.setAlignItems(Alignment.CENTER);

        // --- Layout principal ---
        VerticalLayout mainLayout = new VerticalLayout(topLayout, gridsLayout, botonesLayout);
        mainLayout.setWidthFull();
        mainLayout.setSpacing(true);

        content.add(mainLayout);

        // Lectura continua
        serialReaderService.startListening(this::handleSerialResponse, UI.getCurrent());

        // Refresco automático
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
        }, 0, 1000);
    }

    private Button createButton(String text, VaadinIcon icon, String command) {
        Button button = new Button(text, new Icon(icon));
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        button.getStyle().set("margin", "5px");
        button.addClickListener(e -> {
            sendCommand(command);
            registrarEventoManual(command);
        });
        return button;
    }

    private void sendCommand(String command) {
        if (serialReaderService.isPortOpen()) {
            serialReaderService.sendCommand(command);
            agregarEvento("✅ Comando enviado: " + command);
        } else {
            agregarEvento("❌ Puerto no abierto. No se envió el comando: " + command);
        }
    }

    private void handleSerialResponse(String response) {
        System.out.println("🔍 Recibido: " + response);

        if (response.toLowerCase().contains("motor encendido")) {
            motorEstadoLabel.setText("⚡ Estado del motor: ENCENDIDO");
        } else if (response.toLowerCase().contains("motor apagado")) {
            motorEstadoLabel.setText("💤 Estado del motor: APAGADO");
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
            } catch (NumberFormatException ignored) {
            }
            // Do NOT save temperature readings to MongoDB
            return;
        }

        agregarEvento(response); // Only non-temperature events are saved
    }

    private void registrarEventoManual(String comando) {
        String descripcion;
        switch (comando) {
            case "ON": descripcion = "💡 Luces principales encendidas (manual)"; break;
            case "OFF": descripcion = "💡 Luces principales apagadas (manual)"; break;
            case "CUARTO ON": descripcion = "🛏️ Luces del cuarto encendidas (manual)"; break;
            case "CUARTO OFF": descripcion = "🛏️ Luces del cuarto apagadas (manual)"; break;
            case "EXTERNA ON": descripcion = "🌐 Luz externa encendida (manual)"; break;
            case "EXTERNA OFF": descripcion = "🌐 Luz externa apagada (manual)"; break;
            case "DOOR ON": descripcion = "🚪 Puerta abierta (manual)"; break;
            case "DOOR OFF": descripcion = "🚪 Puerta cerrada (manual)"; break;
            case "GARAGE ON": descripcion = "🚗 Garaje abierto (manual)"; break;
            case "GARAGE OFF": descripcion = "🚗 Garaje cerrado (manual)"; break;
            case "MOTOR ON": descripcion = "🌀 Motor encendido (manual)"; break;
            case "MOTOR OFF": descripcion = "🌀 Motor apagado (manual)"; break;
            default: descripcion = "📨 Comando enviado: " + comando; break;
        }
        agregarEvento(descripcion);
    }

    private void agregarEvento(String mensaje) {
        if (eventosSistema.size() >= 30) {
            eventosSistema.remove(0);
        }
        String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        EventoSistema evento = new EventoSistema(mensaje, hora);
        eventosSistema.add(evento);
        eventoSistemaRepository.save(evento);
    }

    // --- Clase para el historial de temperaturas ---
    public static class TemperaturaRegistro {
        private int numero;
        private double valor;

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