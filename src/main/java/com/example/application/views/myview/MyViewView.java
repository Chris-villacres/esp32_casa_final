package com.example.application.views.myview;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("My View")
@Route("")
@Menu(order = 0, icon = LineAwesomeIconUrl.PENCIL_RULER_SOLID)
public class MyViewView extends Composite<VerticalLayout> {

    public MyViewView() {
        // Contenedor principal
        getContent().setWidth("100%");
        getContent().setAlignItems(Alignment.CENTER);
        getContent().getStyle().set("flex-grow", "1");
        getContent().setSpacing(true);

        // Contenedor en columna
        VerticalLayout column = new VerticalLayout();
        column.setWidthFull();
        column.setAlignItems(Alignment.CENTER);
        column.setSpacing(true);
        column.setPadding(false);

        // Tiles
        column.add(createTile(VaadinIcon.ENTER_ARROW, "PUERTA", null));
        column.add(createTile(VaadinIcon.LIGHTBULB, "LUCES", LucesView.class));
        column.add(createTile(VaadinIcon.SPINNER, "VENTILADOR", null));
        column.add(createTile(VaadinIcon.ANGLE_DOWN, "CORTINAS", null));
        column.add(createTile(VaadinIcon.CLIPBOARD_TEXT, "REGISTROS", RegistrosView.class));
        column.add(createTile(VaadinIcon.CHART, "GRÁFICAS", GraficasView.class)); // Nuevo tile agregado

        getContent().add(column);
    }

    private VerticalLayout createTile(VaadinIcon vaadinIcon, String label, Class<? extends Component> navigationTarget) {
        Icon icon = vaadinIcon.create();
        icon.setSize("40px");

        Button button = new Button(label);
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Si hay navegación, agregar listener para navegar
        if (navigationTarget != null) {
            button.addClickListener(e ->
                    button.getUI().ifPresent(ui -> ui.navigate(navigationTarget))
            );
        }

        VerticalLayout tile = new VerticalLayout(icon, button);
        tile.setAlignItems(Alignment.CENTER);
        tile.setPadding(false);
        tile.setSpacing(true);
        tile.setWidth("200px");

        tile.getStyle()
                .set("border", "1px solid #ccc")
                .set("border-radius", "8px")
                .set("background", "white")
                .set("box-shadow", "2px 2px 4px #ddd");

        return tile;
    }
}