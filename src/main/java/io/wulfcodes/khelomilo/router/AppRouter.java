package io.wulfcodes.khelomilo.router;

import com.google.inject.Inject;
import io.javalin.apibuilder.EndpointGroup;
import io.wulfcodes.khelomilo.router.api.ApiRouter;
import io.wulfcodes.khelomilo.router.web.HomeRouter;
import io.wulfcodes.khelomilo.router.web.bingo.BingoWebRouter;
import io.wulfcodes.khelomilo.router.web.wordle.WordleWebRouter;
import io.wulfcodes.khelomilo.router.ws.WsRouter;

import static io.javalin.apibuilder.ApiBuilder.path;

public class AppRouter implements EndpointGroup {

    private final HomeRouter homeRouter;
    private final BingoWebRouter bingoWebRouter;
    private final WordleWebRouter wordleWebRouter;
    private final ApiRouter apiRouter;
    private final WsRouter wsRouter;

    @Inject
    public AppRouter(HomeRouter homeRouter,
                     BingoWebRouter bingoWebRouter,
                     WordleWebRouter wordleWebRouter,
                     ApiRouter apiRouter,
                     WsRouter wsRouter) {
        this.homeRouter = homeRouter;
        this.bingoWebRouter = bingoWebRouter;
        this.wordleWebRouter = wordleWebRouter;
        this.apiRouter = apiRouter;
        this.wsRouter = wsRouter;
    }

    @Override
    public void addEndpoints() {
        homeRouter.addEndpoints();
        path("/bingo", bingoWebRouter);
        path("/wordle", wordleWebRouter);
        path("/api", apiRouter);
        path("/ws", wsRouter);
    }
}
