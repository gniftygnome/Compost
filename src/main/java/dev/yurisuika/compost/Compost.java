package dev.yurisuika.compost;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.yurisuika.compost.block.entity.ComposterBlockEntity;
import dev.yurisuika.compost.server.command.CompostCommand;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

@Mod("compost")
public class Compost {

    public static File file = new File(FMLPaths.CONFIGDIR.get().toFile(), "compost.json");
    public static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static Config config = new Config();

    public static class Config {

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
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadConfig() {
        try {
            if (file.exists()) {
                StringBuilder contentBuilder = new StringBuilder();
                try (Stream<String> stream = Files.lines(file.toPath(), StandardCharsets.UTF_8)) {
                    stream.forEach(s -> contentBuilder.append(s).append("\n"));
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                config = gson.fromJson(contentBuilder.toString(), Config.class);
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
                itemStack.setTag(nbt);
            } catch (CommandSyntaxException e) {
                e.printStackTrace();
            }
        }
        return itemStack;
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

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, "compost");

    public static final RegistryObject<BlockEntityType<ComposterBlockEntity>> COMPOSTER = BLOCK_ENTITIES.register("composter", () -> BlockEntityType.Builder.create(ComposterBlockEntity::new, Blocks.COMPOSTER).build(null));

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

        BLOCK_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

}