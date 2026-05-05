package io.wulfcodes.khelomilo.controller.web.bingo;

import io.javalin.http.Context;

import java.util.Map;

public class BingoController {

    public static void index(Context ctx) {
        ctx.render("pages/bingo/index.jte", Map.of(
            "title", "Bingo — KheloMilo"
        ));
    }
}
