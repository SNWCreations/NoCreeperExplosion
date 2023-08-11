package snw.ncemod;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class ResourceLoader {

    public static InputStream read(String path) {
        return ResourceLoader.class.getResourceAsStream(path);
    }

    public static JsonObject loadLang(String langCode) throws IOException {
        try (InputStream stream = read("/assets/ncemod/lang/" + langCode + ".json")) {
            return JsonParser.parseReader(new BufferedReader(
                    new InputStreamReader(stream, StandardCharsets.UTF_8))).getAsJsonObject();
        }
    }

    private ResourceLoader() {
    }
}
