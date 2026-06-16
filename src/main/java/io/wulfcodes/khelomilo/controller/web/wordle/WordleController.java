package io.wulfcodes.khelomilo.controller.web.wordle;

import io.javalin.http.Context;
import java.util.Map;

public class WordleController {

    public static void index(Context ctx) {
        ctx.render("pages/wordle/index.jte", Map.of(
            "title", "Wordle — KheloMilo"
        ));
    }
}
