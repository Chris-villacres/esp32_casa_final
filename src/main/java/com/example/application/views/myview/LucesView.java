package com.example.application.views.myview;

import com.example.application.service.SerialReaderService; // Corrección aquí
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Luces")
@Route("luces")
public class LucesView extends Composite<VerticalLayout> {

    private final SerialReaderService serialReaderService; // Corrección aquí

    @Autowired
    public LucesView(SerialReaderService serialReaderService) {
        this.serialReaderService = serialReaderService; // Corrección aquí

        getContent().setWidth("100%");
        getContent().setAlignItems(Alignment.CENTER);
        getContent().setSpacing(true);

        // Botón ON
        Button onButton = new Button("ON", new Icon(VaadinIcon.LIGHTBULB));
        onButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        onButton.addClickListener(e -> serialReaderService.sendCommand("ON"));

        // Botón OFF
        Button offButton = new Button("OFF", new Icon(VaadinIcon.LIGHTBULB));
        offButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        offButton.addClickListener(e -> serialReaderService.sendCommand("OFF"));

        // Botón CUARTO ON
        Button cuartoOnButton = new Button("CUARTO ON", new Icon(VaadinIcon.BED));
        cuartoOnButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        cuartoOnButton.addClickListener(e -> serialReaderService.sendCommand("CUARTO ON"));

        // Botón CUARTO OFF
        Button cuartoOffButton = new Button("CUARTO OFF", new Icon(VaadinIcon.BED));
        cuartoOffButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cuartoOffButton.addClickListener(e -> serialReaderService.sendCommand("CUARTO OFF"));

        // Botón EXTERNA ON
        Button externaOnButton = new Button("EXTERNA ON", new Icon(VaadinIcon.EXTERNAL_LINK));
        externaOnButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        externaOnButton.addClickListener(e -> serialReaderService.sendCommand("EXTERNA ON"));

        // Botón EXTERNA OFF
        Button externaOffButton = new Button("EXTERNA OFF", new Icon(VaadinIcon.EXTERNAL_LINK));
        externaOffButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        externaOffButton.addClickListener(e -> serialReaderService.sendCommand("EXTERNA OFF"));

        getContent().add(
                onButton,
                offButton,
                cuartoOnButton,
                cuartoOffButton,
                externaOnButton,
                externaOffButton
        );
    }
}
