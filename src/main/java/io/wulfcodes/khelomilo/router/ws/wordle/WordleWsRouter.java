package io.wulfcodes.khelomilo.router.ws.wordle;

import com.google.inject.Inject;
import io.javalin.apibuilder.EndpointGroup;
import io.wulfcodes.khelomilo.controller.ws.wordle.WordleWsController;
import io.wulfcodes.khelomilo.service.wordle.WordDictionaryService;
import io.wulfcodes.khelomilo.service.wordle.WordleRoomManager;

import static io.javalin.apibuilder.ApiBuilder.ws;

public class WordleWsRouter implements EndpointGroup {

    private final WordleRoomManager roomManager;
    private final WordDictionaryService dictionaryService;

    @Inject
    public WordleWsRouter(WordleRoomManager roomManager, WordDictionaryService dictionaryService) {
        this.roomManager = roomManager;
        this.dictionaryService = dictionaryService;
    }

    @Override
    public void addEndpoints() {
        ws("/{roomId}/{username}", ws -> {
            ws.onConnect(ctx -> WordleWsController.onConnect(ctx, roomManager));
            ws.onMessage(ctx -> WordleWsController.onMessage(ctx, roomManager, dictionaryService));
            ws.onClose(ctx -> WordleWsController.onClose(ctx, roomManager));
        });
    }
}
