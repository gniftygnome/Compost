package com.yurisuika.compost;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Stream;

public class Compost implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger();

    public static File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), "compost.json");
    public static Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();

    public static CompostConfig config = new CompostConfig();

    public static void saveConfig() {
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(gson.toJson(getConfig()));
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void loadConfig() {
        try {
            if (file.exists()) {
                StringBuilder contentBuilder = new StringBuilder();

                try (Stream<String> stream = Files.lines(file.toPath(), StandardCharsets.UTF_8)) {
                    stream.forEach(s -> contentBuilder.append(s).append("\n"));
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                config = gson.fromJson(contentBuilder.toString(), CompostConfig.class);
            } else {
                config = new CompostConfig();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setConfig(config);
    }

    public static void setConfig(CompostConfig config) {
        Compost.config = config;
    }

    public static CompostConfig getConfig() {
        return config;
    }

    @Override
    public void onInitialize() {
        LOGGER.info("Loading Compost!");

        if (!file.exists()) {
            saveConfig();
        }
        loadConfig();
    }

}
