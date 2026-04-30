package io.wulfcodes.khelmel.router.web.bingo;

import io.javalin.apibuilder.EndpointGroup;
import io.wulfcodes.khelmel.controller.web.bingo.BingoController;

import static io.javalin.apibuilder.ApiBuilder.get;

public class BingoWebRouter implements EndpointGroup {
    @Override
    public void addEndpoints() {
        get("/", BingoController::index);
    }
}
