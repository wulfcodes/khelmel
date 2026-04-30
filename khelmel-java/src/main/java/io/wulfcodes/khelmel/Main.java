package io.wulfcodes.khelmel;

import com.google.inject.Guice;
import com.google.inject.Injector;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.DirectoryCodeResolver;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;
import io.wulfcodes.khelmel.config.AppModule;
import io.wulfcodes.khelmel.router.AppRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            log.info("Starting Khelmel application...");

            String envPort = System.getenv("PORT");
            if (envPort == null || envPort.isBlank()) {
                log.error("Environment variable PORT is not set. Aborting startup.");
                System.exit(1);
            }

            int port;
            try {
                port = Integer.parseInt(envPort);
            } catch (NumberFormatException e) {
                log.error("Environment variable PORT='{}' is not a valid integer. Aborting startup.", envPort);
                System.exit(1);
                return; // satisfy compiler
            }

            log.debug("Initializing Guice injector...");
            Injector injector = Guice.createInjector(new AppModule());
            AppRouter appRouter = injector.getInstance(AppRouter.class);
            log.debug("Guice injector initialized successfully.");

            Javalin.create(config -> {
                config.fileRenderer(new JavalinJte(buildTemplateEngine()));
                config.routes.apiBuilder(appRouter);
                config.staticFiles.add("/public");
            }).start(port);

            log.info("Khelmel started successfully on port {}", port);

        } catch (Exception e) {
            log.error("Unexpected error during application startup", e);
            System.exit(1);
        }
    }

    private static TemplateEngine buildTemplateEngine() {
        Path devTemplates = Path.of("src", "main", "jte");

        if (Files.exists(devTemplates)) {
            log.info("[JTE] Development mode — hot-reloading from {}", devTemplates.toAbsolutePath());
            return TemplateEngine.create(new DirectoryCodeResolver(devTemplates), ContentType.Html);
        }

        log.info("[JTE] Production mode — using precompiled templates");
        return TemplateEngine.createPrecompiled(ContentType.Html);
    }
}