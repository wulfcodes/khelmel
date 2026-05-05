package io.wulfcodes.khelomilo.router.api;

import com.google.inject.Inject;
import io.javalin.apibuilder.EndpointGroup;
import io.wulfcodes.khelomilo.controller.api.HealthController;
import io.wulfcodes.khelomilo.router.api.bingo.BingoApiRouter;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.path;

public class ApiRouter implements EndpointGroup {

    private final BingoApiRouter bingoApiRouter;

    @Inject
    public ApiRouter(BingoApiRouter bingoApiRouter) {
        this.bingoApiRouter = bingoApiRouter;
    }

    @Override
    public void addEndpoints() {
        get("/health", HealthController::check);
        path("/bingo", bingoApiRouter);
    }
}
