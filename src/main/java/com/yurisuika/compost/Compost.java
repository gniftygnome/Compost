package com.yurisuika.compost;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yurisuika.compost.server.command.CompostCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.Arrays;

public class Compost implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("compost");

    public static File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), "compost.json");
    public static Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();

    public static Config config = new Config();

    public static class Config {

        public boolean shuffle = true;

        public Group[] items = {
                new Group("minecraft:dirt", 1.0D, 1,1),
                new Group("minecraft:bone_meal", 1.0D, 1, 1)
        };

    }

    public static void saveConfig() {
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(gson.toJson(getConfig()));
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadConfig() {
        try {
            if (file.exists()) {
                config = gson.fromJson(Files.readString(file.toPath()), Config.class);
            } else {
                config = new Config();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        checkBounds();
        setConfig(config);
    }

    public static void setConfig(Config config) {
        Compost.config = config;
    }

    public static Config getConfig() {
        return config;
    }

    public static void checkBounds() {
        Arrays.stream(Compost.config.items).forEach(group -> {
            Item item;
            int index;
            if (group.item.contains("{")) {
                index = group.item.indexOf("{");
                String id = group.item.substring(0, index);
                item = Registry.ITEM.get(new Identifier(id));
            } else {
                item = Registry.ITEM.get(new Identifier(group.item));
            }
            group.chance = Math.max(0.0D, Math.min(group.chance, 1.0D));
            int maxCount = item.getMaxCount();
            group.max = Math.min(group.max, maxCount);
            group.min = Math.min(Math.min(group.min, maxCount), group.max);
        });
        saveConfig();
    }

    public static void setShuffle(boolean bool) {
        config.shuffle = bool;
        saveConfig();
    }

    public static void setGroup(int group, String item, double chance, int min, int max) {
        config.items[group] = new Group(item, chance, min, max);
        saveConfig();
    }

    public static Group getGroup(int group) {
        return config.items[group];
    }

    public static void addGroup(String item, double chance, int min, int max) {
        config.items = ArrayUtils.add(config.items, new Group(item, chance, min, max));
        saveConfig();
    }

    public static void removeGroup(int group) {
        config.items = ArrayUtils.remove(config.items, group);
        saveConfig();
    }

    public static void reverseGroups() {
        ArrayUtils.reverse(config.items);
        saveConfig();
    }

    public static class Group {

        public String item;
        public double chance;
        public int min;
        public int max;

        Group(String item, double chance, int min, int max) {
            this.item = item;
            this.chance = chance;
            this.min = min;
            this.max = max;
        }

    }

    @Override
    public void onInitialize() {
        LOGGER.info("Loading Compost!");

        if (!file.exists()) {
            saveConfig();
        }
        loadConfig();

        CommandRegistrationCallback.EVENT.register(CompostCommand::register);
    }

}