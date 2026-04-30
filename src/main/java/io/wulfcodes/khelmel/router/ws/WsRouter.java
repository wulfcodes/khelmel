package io.wulfcodes.khelmel.router.ws;

import com.google.inject.Inject;
import io.javalin.apibuilder.EndpointGroup;
import io.wulfcodes.khelmel.router.ws.bingo.BingoWsRouter;

import static io.javalin.apibuilder.ApiBuilder.path;

public class WsRouter implements EndpointGroup {

    private final BingoWsRouter bingoWsRouter;

    @Inject
    public WsRouter(BingoWsRouter bingoWsRouter) {
        this.bingoWsRouter = bingoWsRouter;
    }

    @Override
    public void addEndpoints() {
        path("/bingo", bingoWsRouter);
    }
}
