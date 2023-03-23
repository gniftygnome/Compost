package dev.yurisuika.compost.mixin.block;

import dev.yurisuika.compost.block.ArrayComposterBlock;
import net.minecraft.block.*;
import net.minecraft.sound.BlockSoundGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Blocks.class)
public class BlocksMixin {

    @Redirect(method = "<clinit>", slice = @Slice(from = @At(value = "CONSTANT", args = "stringValue=composter")), at = @At(value = "NEW", target = "net/minecraft/block/ComposterBlock", ordinal = 0))
    private static ComposterBlock redirectComposter(AbstractBlock.Settings settings) {
        return new ArrayComposterBlock(AbstractBlock.Settings.of(Material.WOOD).strength(0.6f).sounds(BlockSoundGroup.WOOD));
    }

}