package dev.yurisuika.compost;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.yurisuika.compost.server.command.CompostCommand;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Mod("compost")
public class Compost {

    public static File file = new File(FMLPaths.CONFIGDIR.get().toFile(), "compost.json");
    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();
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

        Group(String item, double chance, int min, int max) {
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
            int maxCount = createItemStack(group).getItem().getMaxCount();
            int min = Math.max(Math.min(Math.min(group.min, maxCount), group.max), 0);
            int max = Math.max(Math.max(Math.min(group.max, maxCount), group.min), 1);
            group.chance = Math.max(0.0D, Math.min(group.chance, 1.0D));
            group.min = min;
            group.max = max;
        });
        saveConfig();
    }

    public static ItemStack createItemStack(Group group) {
        int index;
        Item item;
        if (group.item.contains("{")) {
            index = group.item.indexOf("{");
            item = Registry.ITEM.get(new Identifier(group.item.substring(0, index)));
        } else {
            index = 0;
            item = Registry.ITEM.get(new Identifier(group.item));
        }
        ItemStack itemStack = new ItemStack(item, ThreadLocalRandom.current().nextInt(group.min, group.max + 1));
        if (group.item.contains("{")) {
            NbtCompound nbt;
            try {
                nbt = StringNbtReader.parse(group.item.substring(index));
                itemStack.setNbt(nbt);
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }
        }
        return itemStack;
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

    @Mod.EventBusSubscriber(modid = "compost")
    public static class CommonForgeEvents {

        @SubscribeEvent
        public static void registerCommands(RegisterCommandsEvent event) {
            CompostCommand.register(event.getDispatcher(), event.getEnvironment());
        }

    }

    public Compost() {
        if (!file.exists()) {
            saveConfig();
        }
        loadConfig();

        MinecraftForge.EVENT_BUS.register(this);
    }

}