package io.wulfcodes.khelomilo.factory;

import com.google.gson.Gson;
import io.javalin.json.JsonMapper;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

public class GsonMapperFactory {

    private GsonMapperFactory() {}

    public static JsonMapper build() {
        Gson gson = new Gson();

        return new JsonMapper() {
            @NotNull @Override
            public String toJsonString(@NotNull Object obj, @NotNull Type type) {
                return gson.toJson(obj, type);
            }

            @NotNull @Override
            public <T> T fromJsonString(@NotNull String json, @NotNull Type type) {
                return gson.fromJson(json, type);
            }

            @NotNull @Override
            public <T> T fromJsonStream(@NotNull InputStream json, @NotNull Type type) {
                return gson.fromJson(new InputStreamReader(json), type);
            }
        };
    }
}
