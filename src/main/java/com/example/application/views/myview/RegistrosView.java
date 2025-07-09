package com.example.application.views.myview;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Registros")
@Route("registros")
public class RegistrosView extends Composite<VerticalLayout> {

    public RegistrosView() {
        getContent().setWidth("100%");
        getContent().setSpacing(true);

        // Crear la tabla (Grid)
        Grid<Registro> grid = new Grid<>(Registro.class, false);
        grid.addColumn(Registro::getTemperatura).setHeader("Temperatura (°C)");
        grid.addColumn(Registro::getDistancia).setHeader("Distancia (cm)");

        // Agregar datos de ejemplo
        grid.setItems(
                new Registro(25.5, 120.0),
                new Registro(26.0, 115.5),
                new Registro(24.8, 130.2)
        );

        getContent().add(grid);
    }

    // Clase interna para representar los registros
    public static class Registro {
        private final double temperatura;
        private final double distancia;

        public Registro(double temperatura, double distancia) {
            this.temperatura = temperatura;
            this.distancia = distancia;
        }

        public double getTemperatura() {
            return temperatura;
        }

        public double getDistancia() {
            return distancia;
        }
    }
}