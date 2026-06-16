package io.wulfcodes.khelomilo.router.web.wordle;

import io.javalin.apibuilder.EndpointGroup;
import io.wulfcodes.khelomilo.controller.web.wordle.WordleController;

import static io.javalin.apibuilder.ApiBuilder.get;

public class WordleWebRouter implements EndpointGroup {
    @Override
    public void addEndpoints() {
        get("/", WordleController::index);
    }
}
