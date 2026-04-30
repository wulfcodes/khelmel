package io.wulfcodes.khelmel.router.api;

import com.google.inject.Inject;
import io.javalin.apibuilder.EndpointGroup;
import io.wulfcodes.khelmel.router.api.bingo.BingoApiRouter;

import static io.javalin.apibuilder.ApiBuilder.path;

public class ApiRouter implements EndpointGroup {

    private final BingoApiRouter bingoApiRouter;

    @Inject
    public ApiRouter(BingoApiRouter bingoApiRouter) {
        this.bingoApiRouter = bingoApiRouter;
    }

    @Override
    public void addEndpoints() {
        path("/bingo", bingoApiRouter);
    }
}
