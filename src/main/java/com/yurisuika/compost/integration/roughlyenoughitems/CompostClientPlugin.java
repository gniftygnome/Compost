package com.yurisuika.compost.integration.roughlyenoughitems;

import com.google.common.collect.Iterators;
import com.yurisuika.compost.Compost;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.forge.REIPluginClient;
import me.shedaniel.rei.plugin.common.displays.DefaultCompostingDisplay;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

@REIPluginClient
public class  CompostClientPlugin implements REIClientPlugin {

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        Arrays.stream(Compost.config.items).forEach(group -> {
            Iterator<List<EntryIngredient>> iterator = Iterators.partition(ComposterBlock.COMPOSTABLES.object2FloatEntrySet().stream().sorted(Map.Entry.comparingByValue()).map(entry -> EntryIngredients.of(entry.getKey())).iterator(), 35);
            while (iterator.hasNext()) {
                List<EntryIngredient> entries = iterator.next();
                registry.add(new DefaultCompostingDisplay(entries, Collections.singletonList(EntryIngredients.of(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(group.item)))))));
            }
        });
    }

}
