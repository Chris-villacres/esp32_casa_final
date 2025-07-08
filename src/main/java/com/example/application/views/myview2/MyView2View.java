package com.example.application.views.myview2;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("My View2")
@Route("my-view")
@Menu(order = 1, icon = LineAwesomeIconUrl.PENCIL_RULER_SOLID)
public class MyView2View extends Composite<VerticalLayout> {

    public MyView2View() {
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
    }
}
