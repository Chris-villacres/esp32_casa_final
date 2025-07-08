package com.example.application.views.myview;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("My View")
@Route("")
@Menu(order = 0, icon = LineAwesomeIconUrl.PENCIL_RULER_SOLID)
public class MyViewView extends Composite<VerticalLayout> {

    public MyViewView() {
        FormLayout formLayout2Col = new FormLayout();
        Icon icon = new Icon();
        HorizontalLayout layoutRow = new HorizontalLayout();
        VerticalLayout layoutColumn2 = new VerticalLayout();
        Icon icon2 = new Icon();
        Button buttonPrimary = new Button();
        Button buttonPrimary2 = new Button();
        FormLayout formLayout2Col2 = new FormLayout();
        Icon icon3 = new Icon();
        Icon icon4 = new Icon();
        Button buttonPrimary3 = new Button();
        Button buttonPrimary4 = new Button();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        formLayout2Col.setWidth("100%");
        icon.setIcon("lumo:user");
        layoutRow.setHeightFull();
        formLayout2Col.setFlexGrow(1.0, layoutRow);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");
        layoutColumn2.setHeightFull();
        layoutRow.setFlexGrow(1.0, layoutColumn2);
        layoutColumn2.setWidth("100%");
        layoutColumn2.getStyle().set("flex-grow", "1");
        icon2.setIcon("lumo:user");
        icon2.setWidth("100%");
        buttonPrimary.setText("PUERTA");
        buttonPrimary.setWidth("min-content");
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonPrimary2.setText("LUCES");
        buttonPrimary2.setWidth("min-content");
        buttonPrimary2.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        formLayout2Col2.setWidth("100%");
        icon3.setIcon("lumo:user");
        icon4.setIcon("lumo:user");
        buttonPrimary3.setText("VENTILADOR");
        buttonPrimary3.setWidth("min-content");
        buttonPrimary3.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonPrimary4.setText("CORTINAS");
        buttonPrimary4.setWidth("min-content");
        buttonPrimary4.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        getContent().add(formLayout2Col);
        formLayout2Col.add(icon);
        formLayout2Col.add(layoutRow);
        layoutRow.add(layoutColumn2);
        layoutColumn2.add(icon2);
        formLayout2Col.add(buttonPrimary);
        formLayout2Col.add(buttonPrimary2);
        getContent().add(formLayout2Col2);
        formLayout2Col2.add(icon3);
        formLayout2Col2.add(icon4);
        formLayout2Col2.add(buttonPrimary3);
        formLayout2Col2.add(buttonPrimary4);
    }
}
