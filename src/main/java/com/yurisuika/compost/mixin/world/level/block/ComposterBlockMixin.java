package com.yurisuika.compost.mixin.world.level.block;

import com.yurisuika.compost.Compost;
import com.yurisuika.compost.CompostConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.ThreadLocalRandom;

@Mixin(ComposterBlock.class)
public class ComposterBlockMixin {

    @Redirect(method = "extractProduce", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private static boolean redirectExtractProduce(Level level, Entity entity) {
        return false;
    }

    @Inject(method = "extractProduce", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z", shift = At.Shift.AFTER))
    private static void injectExtractProduce(BlockState state, Level level, BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
        for (CompostConfig.Group group : Compost.config.items) {
            double d = (double)(level.random.nextFloat() * 0.7F) + 0.15000000596046448D;
            double e = (double)(level.random.nextFloat() * 0.7F) + 0.06000000238418579D + 0.6D;
            double g = (double)(level.random.nextFloat() * 0.7F) + 0.15000000596046448D;
            ItemEntity itemEntity = new ItemEntity(level, (double)pos.getX() + d, (double)pos.getY() + e, (double)pos.getZ() + g, new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(group.item)), ThreadLocalRandom.current().nextInt(group.min, group.max + 1)));
            itemEntity.setDefaultPickUpDelay();
            if(ThreadLocalRandom.current().nextFloat() < group.chance) {
                level.addFreshEntity(itemEntity);
            }
        }
    }

}
