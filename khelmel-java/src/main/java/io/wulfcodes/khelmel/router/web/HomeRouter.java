package io.wulfcodes.khelmel.router.web;

import io.javalin.apibuilder.EndpointGroup;
import io.wulfcodes.khelmel.controller.web.HomeController;

import static io.javalin.apibuilder.ApiBuilder.get;

public class HomeRouter implements EndpointGroup {
    @Override
    public void addEndpoints() {
        get("/", HomeController::index);
    }
}
