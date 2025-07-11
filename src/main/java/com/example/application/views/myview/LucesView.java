package com.example.application.views.myview;

import com.example.application.service.SerialReaderService;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

@PageTitle("Luces")
@Route("luces")
public class LucesView extends Composite<VerticalLayout> {

    private final SerialReaderService serialReaderService;

    @Autowired
    public LucesView(SerialReaderService serialReaderService) {
        this.serialReaderService = serialReaderService;

        getContent().setWidth("100%");
        getContent().setAlignItems(Alignment.CENTER);
        getContent().setSpacing(true);

        // Verifica si el puerto está abierto al iniciar la vista
        if (!serialReaderService.isPortOpen()) {
            Notification.show(
                    "⚠️ Puerto serial no está abierto. Verifica la conexión del ESP32.",
                    5000,
                    Notification.Position.MIDDLE
            );
        }

        // Botón ON
        Button onButton = createButton("ON", VaadinIcon.LIGHTBULB, ButtonVariant.LUMO_PRIMARY);
        onButton.addClickListener(e -> sendCommandAndShowResponse("ON"));

        // Botón OFF
        Button offButton = createButton("OFF", VaadinIcon.LIGHTBULB, ButtonVariant.LUMO_ERROR);
        offButton.addClickListener(e -> sendCommandAndShowResponse("OFF"));

        // Botón CUARTO ON
        Button cuartoOnButton = createButton("CUARTO ON", VaadinIcon.BED, ButtonVariant.LUMO_PRIMARY);
        cuartoOnButton.addClickListener(e -> sendCommandAndShowResponse("CUARTO ON"));

        // Botón CUARTO OFF
        Button cuartoOffButton = createButton("CUARTO OFF", VaadinIcon.BED, ButtonVariant.LUMO_ERROR);
        cuartoOffButton.addClickListener(e -> sendCommandAndShowResponse("CUARTO OFF"));

        // Botón EXTERNA ON
        Button externaOnButton = createButton("EXTERNA ON", VaadinIcon.EXTERNAL_LINK, ButtonVariant.LUMO_PRIMARY);
        externaOnButton.addClickListener(e -> sendCommandAndShowResponse("EXTERNA ON"));

        // Botón EXTERNA OFF
        Button externaOffButton = createButton("EXTERNA OFF", VaadinIcon.EXTERNAL_LINK, ButtonVariant.LUMO_ERROR);
        externaOffButton.addClickListener(e -> sendCommandAndShowResponse("EXTERNA OFF"));

        getContent().add(
                onButton,
                offButton,
                cuartoOnButton,
                cuartoOffButton,
                externaOnButton,
                externaOffButton
        );
    }

    private Button createButton(String text, VaadinIcon icon, ButtonVariant variant) {
        Button button = new Button(text, new Icon(icon));
        button.addThemeVariants(variant);
        return button;
    }

    private void sendCommandAndShowResponse(String command) {
        if (serialReaderService.isPortOpen()) {
            serialReaderService.sendCommand(command);
            showResponse();
        } else {
            Notification.show(
                    "❌ Puerto no abierto. Comando no enviado: " + command,
                    4000,
                    Notification.Position.MIDDLE
            );
        }
    }

    private void showResponse() {
        String response = serialReaderService.readResponse();
        if (!response.isEmpty()) {
            Notification.show(response, 3000, Notification.Position.MIDDLE);
        }
    }
}
