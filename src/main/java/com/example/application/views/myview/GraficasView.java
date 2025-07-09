package com.example.application.views.myview;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.ListSeries;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Gráficas")
@Route("graficas")
public class GraficasView extends Composite<VerticalLayout> {

    public GraficasView() {
        getContent().setWidth("100%");
        getContent().setAlignItems(FlexComponent.Alignment.CENTER);
        getContent().setSpacing(true);

        // Gráfica de temperatura
        Chart temperaturaChart = new Chart(ChartType.LINE);
        Configuration temperaturaConfig = temperaturaChart.getConfiguration();
        temperaturaConfig.setTitle("Temperatura");
        temperaturaConfig.addSeries(new ListSeries("Temperatura", 22, 23, 24, 25, 26)); // Datos de ejemplo

        // Gráfica de señal del sensor ultrasónico
        Chart sensorChart = new Chart(ChartType.COLUMN);
        Configuration sensorConfig = sensorChart.getConfiguration();
        sensorConfig.setTitle("Sensor Ultrasónico");
        sensorConfig.addSeries(new ListSeries("Distancia", 100, 120, 110, 130, 125)); // Datos de ejemplo

        getContent().add(temperaturaChart, sensorChart);
    }
}