package com.yurisuika.compost.mixin.world.block;

import com.yurisuika.compost.Compost;
import com.yurisuika.compost.CompostConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.ComposterBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.ThreadLocalRandom;

@Mixin(ComposterBlock.class)
public class ComposterBlockMixin {

    @Redirect(method = "extractProduce", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addFreshEntity(Lnet/minecraft/entity/Entity;)Z"))
    private static boolean redirectExtractProduce(World world, Entity entity) {
        return false;
    }

    @Inject(method = "extractProduce", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;addFreshEntity(Lnet/minecraft/entity/Entity;)Z", shift = At.Shift.AFTER))
    private static void injectExtractProduce(BlockState state, World world, BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
        for (CompostConfig.Group group : Compost.config.items) {
            double d = (double)(world.random.nextFloat() * 0.7F) + 0.15000000596046448D;
            double e = (double)(world.random.nextFloat() * 0.7F) + 0.06000000238418579D + 0.6D;
            double g = (double)(world.random.nextFloat() * 0.7F) + 0.15000000596046448D;
            ItemEntity itemEntity = new ItemEntity(world, (double)pos.getX() + d, (double)pos.getY() + e, (double)pos.getZ() + g, new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation(group.item)), ThreadLocalRandom.current().nextInt(group.min, group.max + 1)));
            itemEntity.setDefaultPickUpDelay();
            if(ThreadLocalRandom.current().nextFloat() < group.chance) {
                world.addFreshEntity(itemEntity);
            }
        }
    }

}
