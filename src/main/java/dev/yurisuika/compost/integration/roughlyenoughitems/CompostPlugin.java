package dev.yurisuika.compost.integration.roughlyenoughitems;

import com.google.common.collect.Iterators;
import dev.yurisuika.compost.Compost;
import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import me.shedaniel.rei.plugin.composting.DefaultCompostingDisplay;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.ComposterBlock;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.*;

@Environment(EnvType.CLIENT)
public class CompostPlugin implements REIPluginV0 {

    @Override
    public void registerRecipeDisplays(RecipeHelper recipeHelper) {
        Object2FloatMap<ItemConvertible> compostables = ComposterBlock.ITEM_TO_LEVEL_INCREASE_CHANCE;
        Arrays.stream(Compost.config.items).forEach(group -> {
            int i = 0;
            Iterator<List<Object2FloatMap.Entry<ItemConvertible>>> iterator = Iterators.partition(compostables.object2FloatEntrySet().stream().sorted(Map.Entry.comparingByValue()).iterator(), 48);
            while (iterator.hasNext()) {
                List<Object2FloatMap.Entry<ItemConvertible>> entries = iterator.next();
                recipeHelper.registerDisplay(new DefaultCompostingDisplay(i, entries, compostables, new ItemStack(Registry.ITEM.get(new Identifier(group.item)))));
                i++;
            }
        });
    }

    @Override
    public Identifier getPluginIdentifier() {
        return new Identifier("compost", "compost_plugin");
    }

}