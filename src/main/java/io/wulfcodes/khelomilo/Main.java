package io.wulfcodes.khelomilo;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;
import io.wulfcodes.khelomilo.config.AppModule;
import io.wulfcodes.khelomilo.factory.GsonMapperFactory;
import io.wulfcodes.khelomilo.factory.JteFactory;
import io.wulfcodes.khelomilo.router.AppRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            log.info("Starting KheloMilo application...");

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
                config.jsonMapper(GsonMapperFactory.build());
                config.fileRenderer(new JavalinJte(JteFactory.buildTemplateEngine()));
                config.routes.apiBuilder(appRouter);
                config.staticFiles.add("/public");
            }).start(port);

            log.info("KheloMilo started successfully on port {}", port);

        } catch (Exception e) {
            log.error("Unexpected error during application startup", e);
            System.exit(1);
        }
    }
}
