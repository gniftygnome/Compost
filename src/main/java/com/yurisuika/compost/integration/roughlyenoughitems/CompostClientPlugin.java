package com.yurisuika.compost.integration.roughlyenoughitems;

import com.google.common.collect.Iterators;
import com.yurisuika.compost.Compost;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.plugin.common.displays.DefaultCompostingDisplay;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.ComposterBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.*;

@Environment(EnvType.CLIENT)
public class CompostClientPlugin implements REIClientPlugin {

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        Arrays.stream(Compost.config.items).forEach(group -> {
            Iterator<List<EntryIngredient>> iterator = Iterators.partition(ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE.object2FloatEntrySet().stream().sorted(Map.Entry.comparingByValue()).map(entry -> EntryIngredients.of(entry.getKey())).iterator(), 35);
            while (iterator.hasNext()) {
                List<EntryIngredient> entries = iterator.next();
                registry.add(new DefaultCompostingDisplay(entries, Collections.singletonList(EntryIngredients.of(new ItemStack(Registries.ITEM.get(new Identifier(group.item)))))));
            }
        });
    }

}
