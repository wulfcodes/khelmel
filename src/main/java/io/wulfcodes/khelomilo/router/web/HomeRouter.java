package io.wulfcodes.khelomilo.router.web;

import io.javalin.apibuilder.EndpointGroup;
import io.wulfcodes.khelomilo.controller.web.HomeController;

import static io.javalin.apibuilder.ApiBuilder.get;

public class HomeRouter implements EndpointGroup {
    @Override
    public void addEndpoints() {
        get("/", HomeController::index);
    }
}
