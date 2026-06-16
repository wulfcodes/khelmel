package io.wulfcodes.khelomilo.router.ws;

import com.google.inject.Inject;
import io.javalin.apibuilder.EndpointGroup;
import io.wulfcodes.khelomilo.router.ws.bingo.BingoWsRouter;
import io.wulfcodes.khelomilo.router.ws.wordle.WordleWsRouter;

import static io.javalin.apibuilder.ApiBuilder.path;

public class WsRouter implements EndpointGroup {

    private final BingoWsRouter bingoWsRouter;
    private final WordleWsRouter wordleWsRouter;

    @Inject
    public WsRouter(BingoWsRouter bingoWsRouter, WordleWsRouter wordleWsRouter) {
        this.bingoWsRouter = bingoWsRouter;
        this.wordleWsRouter = wordleWsRouter;
    }

    @Override
    public void addEndpoints() {
        path("/bingo", bingoWsRouter);
        path("/wordle", wordleWsRouter);
    }
}
