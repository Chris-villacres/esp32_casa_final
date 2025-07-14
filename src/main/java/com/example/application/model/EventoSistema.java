package com.example.application.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "eventos")
public class EventoSistema {
    @Id
    private String id;
    private String mensaje;
    private String hora;

    public EventoSistema() {}
    public EventoSistema(String mensaje, String hora) {
        this.mensaje = mensaje;
        this.hora = hora;
    }
    // getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public void save(EventoSistema evento) {
        //Guarda los datos en una base de datos de mongodb spring boot
        // Aquí se implementaría la lógica para guardar el evento en la base de datos
        // Por ejemplo, usando un repositorio de Spring Data MongoDB
        // eventoRepository.save(evento);
        // Para este ejemplo, simplemente imprimimos el evento
        System.out.println("Evento guardado: " + evento.getMensaje() + " a las " + evento.getHora());

    }
}