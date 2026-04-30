package io.wulfcodes.khelmel.config;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import io.wulfcodes.khelmel.service.bingo.BingoRoomManager;
import io.wulfcodes.khelmel.router.AppRouter;
import io.wulfcodes.khelmel.router.api.ApiRouter;
import io.wulfcodes.khelmel.router.api.bingo.BingoApiRouter;
import io.wulfcodes.khelmel.router.web.HomeRouter;
import io.wulfcodes.khelmel.router.web.bingo.BingoWebRouter;
import io.wulfcodes.khelmel.router.ws.WsRouter;
import io.wulfcodes.khelmel.router.ws.bingo.BingoWsRouter;

public class AppModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(AppRouter.class);
        bind(HomeRouter.class);
        bind(BingoWebRouter.class);
        bind(ApiRouter.class);
        bind(BingoApiRouter.class);
        bind(WsRouter.class);
        bind(BingoWsRouter.class);
        bind(BingoRoomManager.class).in(Singleton.class);
    }
}
