package dev.yurisuika.compost;

import dev.yurisuika.compost.block.entity.ComposterBlockEntity;
import dev.yurisuika.compost.server.command.CompostCommand;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static dev.yurisuika.compost.client.option.CompostConfig.*;

@Mod("compost")
public class Compost {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, "compost");

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