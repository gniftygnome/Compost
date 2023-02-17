package dev.yurisuika.compost.mixin.mods;

import me.shedaniel.rei.plugin.common.DefaultPlugin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.Iterator;

public class RoughlyEnoughItemsMixin {

    @Pseudo
    @Mixin(DefaultPlugin.class)
    public static class DefaultPluginMixin {

        @Redirect(method = "registerRecipeDisplays", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z"), slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/block/ComposterBlock;registerDefaultCompostableItems()V"), to = @At(value = "INVOKE", target = "Lme/shedaniel/rei/plugin/stripping/DummyAxeItem;getStrippedBlocksMap()Ljava/util/Map;")))
        private boolean redirectRegisterRecipeDisplays(Iterator instance) {
            return false;
        }

    }

}