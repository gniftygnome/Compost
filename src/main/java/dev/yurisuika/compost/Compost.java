package dev.yurisuika.compost;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.yurisuika.compost.server.command.CompostCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Random;

public class Compost implements ModInitializer {

    public static File file = new File(FabricLoader.getInstance().getConfigDir().toFile(), "compost.json");
    public static Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();
    public static Config config = new Config();

    public static class Config {

        public boolean shuffle = true;

        public Group[] items = {
            new Group("minecraft:dirt", 1.0D, 1, 1),
            new Group("minecraft:bone_meal", 1.0D, 1, 1)
        };

    }

    public static class Group {

        public String item;
        public double chance;
        public int min;
        public int max;

        public Group(String item, double chance, int min, int max) {
            this.item = item;
            this.chance = chance;
            this.min = min;
            this.max = max;
        }

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
            int min = Math.max(Math.min(Math.min(group.min, item.getMaxCount()), group.max), 0);
            int max = Math.max(Math.max(Math.min(group.max, item.getMaxCount()), group.min), 1);
            group.min = min;
            group.max = max;
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

    public static void insertGroup(int group, String item, double chance, int min, int max) {
        Group[] array = (Group[]) Array.newInstance(config.items.getClass().getComponentType(), config.items.length + 1);
        System.arraycopy(new Group[]{new Group(item, chance, min, max)}, 0, array, group, 1);
        if (group > 0) {
            System.arraycopy(config.items, 0, array, 0, group);
        }
        if (group < config.items.length) {
            System.arraycopy(config.items, group, array, group + 1, config.items.length - group);
        }
        config.items = array;
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

    public static void shuffleGroups(Random random) {
        for (int i = config.items.length; i > 1; i--) {
            ArrayUtils.swap(config.items, i - 1, random.nextInt(i), 1);
        }
        saveConfig();
    }

    @Override
    public void onInitialize() {
        if (!file.exists()) {
            saveConfig();
        }
        loadConfig();

        CommandRegistrationCallback.EVENT.register(CompostCommand::register);
    }

}