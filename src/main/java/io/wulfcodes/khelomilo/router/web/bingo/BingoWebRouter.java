package io.wulfcodes.khelomilo.router.web.bingo;

import io.javalin.apibuilder.EndpointGroup;
import io.wulfcodes.khelomilo.controller.web.bingo.BingoController;

import static io.javalin.apibuilder.ApiBuilder.get;

public class BingoWebRouter implements EndpointGroup {
    @Override
    public void addEndpoints() {
        get("/", BingoController::index);
    }
}
