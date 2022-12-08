package com.yurisuika.compost.integration.roughlyenoughitems;

import com.google.common.collect.Iterators;
import com.yurisuika.compost.Compost;
import com.yurisuika.compost.CompostConfig;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.forge.REIPlugin;
import me.shedaniel.rei.plugin.common.displays.DefaultCompostingDisplay;
import net.minecraft.block.ComposterBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@REIPlugin({Dist.CLIENT})
public class CompostClientPlugin implements REIClientPlugin {

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        for (CompostConfig.Group group : Compost.config.items) {
            int page = 0;
            Iterator<List<Object2FloatMap.Entry<IItemProvider>>> iterator = Iterators.partition(ComposterBlock.COMPOSTABLES.object2FloatEntrySet().stream().sorted(Map.Entry.comparingByValue()).iterator(), 48);
            while (iterator.hasNext()) {
                List<Object2FloatMap.Entry<IItemProvider>> entries = iterator.next();
                registry.add(DefaultCompostingDisplay.of(entries, Collections.singletonList(EntryIngredients.of(new ItemStack(Registry.ITEM.get(new ResourceLocation(group.item))))), page++));
            }
        }
    }

}
