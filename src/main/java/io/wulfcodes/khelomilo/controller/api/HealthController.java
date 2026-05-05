package io.wulfcodes.khelomilo.controller.api;

import io.javalin.http.Context;

import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class HealthController {

    private static final String VERSION = resolveVersion();

    private static String resolveVersion() {
        try (InputStream is = HealthController.class.getResourceAsStream(
                "/META-INF/maven/io.wulfcodes/khelomilo/pom.properties")) {
            if (is == null) return "unknown";
            Properties props = new Properties();
            props.load(is);
            return props.getProperty("version", "unknown");
        } catch (Exception e) {
            return "unknown";
        }
    }

    public static void check(Context ctx) {
        ctx.json(Map.of(
            "status", "ok",
            "version", VERSION
        ));
    }
}
