package io.wulfcodes.khelomilo.factory;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.DirectoryCodeResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;

public class JteFactory {

    private static final Logger log = LoggerFactory.getLogger(JteFactory.class);

    private JteFactory() {}

    public static TemplateEngine buildTemplateEngine() {
        Path devTemplates = Path.of("src", "main", "jte");

        if (Files.exists(devTemplates)) {
            log.info("[JTE] Development mode — hot-reloading from {}", devTemplates.toAbsolutePath());
            return TemplateEngine.create(new DirectoryCodeResolver(devTemplates), ContentType.Html);
        }

        log.info("[JTE] Production mode — using precompiled templates");
        return TemplateEngine.createPrecompiled(ContentType.Html);
    }
}
