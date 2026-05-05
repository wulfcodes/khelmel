package io.wulfcodes.khelomilo.controller.web;

import io.javalin.http.Context;

import java.util.Map;

public class HomeController {

    public static void index(Context ctx) {
        ctx.render("pages/home.jte", Map.of(
            "title", "KheloMilo — Thoda Khelo, Zyada Milo"
        ));
    }
}
