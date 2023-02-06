package dev.yurisuika.compost;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.yurisuika.compost.server.command.CompostCommand;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Random;

@Mod("compost")
public class Compost {

    private static final Logger LOGGER = LoggerFactory.getLogger("compost");

    public static File file = new File(FMLPaths.CONFIGDIR.get().toFile(), "compost.json");
    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();

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
                item = ForgeRegistries.ITEMS.getValue(new Identifier(id));
            } else {
                item = ForgeRegistries.ITEMS.getValue(new Identifier(group.item));
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

    @Mod.EventBusSubscriber(modid = "compost")
    public static class ForgeEvents {

        @SubscribeEvent
        public static void onCommandsRegister(RegisterCommandsEvent event) {
            CompostCommand.register(event.getDispatcher());
        }

    }

    public Compost() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Loading Compost!");

        if (!file.exists()) {
            saveConfig();
        }
        loadConfig();
    }

}