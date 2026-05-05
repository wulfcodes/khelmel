package io.wulfcodes.khelomilo.config;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import io.wulfcodes.khelomilo.service.bingo.BingoRoomManager;
import io.wulfcodes.khelomilo.router.AppRouter;
import io.wulfcodes.khelomilo.router.api.ApiRouter;
import io.wulfcodes.khelomilo.router.api.bingo.BingoApiRouter;
import io.wulfcodes.khelomilo.router.web.HomeRouter;
import io.wulfcodes.khelomilo.router.web.bingo.BingoWebRouter;
import io.wulfcodes.khelomilo.router.ws.WsRouter;
import io.wulfcodes.khelomilo.router.ws.bingo.BingoWsRouter;

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
