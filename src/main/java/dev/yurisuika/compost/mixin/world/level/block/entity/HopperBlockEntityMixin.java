package dev.yurisuika.compost.mixin.world.level.block.entity;

import dev.yurisuika.compost.world.level.block.ArrayOutputContainer;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.world.level.block.entity.HopperBlockEntity.addItem;

@Mixin(HopperBlockEntity.class)
public abstract class HopperBlockEntityMixin {

    @Inject(method = "tryTakeInItemFromSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;copy()Lnet/minecraft/world/item/ItemStack;", shift = At.Shift.AFTER), cancellable = true)
    private static void injectTryTakeInItemFromSlot(Hopper hopper, Container container, int slot, Direction side, CallbackInfoReturnable<Boolean> cir) {
        if (container instanceof ArrayOutputContainer) {
            for (int i = 0; i < container.getContainerSize(); i++) {
                ItemStack composterItemStack = addItem(container, hopper, container.removeItem(i, container.getItem(i).getCount()), null);
            }
        }
    }

}