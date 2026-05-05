package io.wulfcodes.khelomilo.router.ws.bingo;

import com.google.inject.Inject;
import io.javalin.apibuilder.EndpointGroup;
import io.wulfcodes.khelomilo.controller.ws.bingo.BingoWsController;
import io.wulfcodes.khelomilo.service.bingo.BingoRoomManager;

import static io.javalin.apibuilder.ApiBuilder.ws;

public class BingoWsRouter implements EndpointGroup {

    private final BingoRoomManager roomManager;

    @Inject
    public BingoWsRouter(BingoRoomManager roomManager) {
        this.roomManager = roomManager;
    }

    @Override
    public void addEndpoints() {
        ws("/{roomId}/{username}", ws -> {
            ws.onConnect(ctx -> BingoWsController.onConnect(ctx, roomManager));
            ws.onMessage(ctx -> BingoWsController.onMessage(ctx, roomManager));
            ws.onClose(ctx -> BingoWsController.onClose(ctx, roomManager));
        });
    }
}
