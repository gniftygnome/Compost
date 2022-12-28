package com.yurisuika.compost.mixin.tileentity;

import com.yurisuika.compost.block.ArrayFullInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.IHopper;
import net.minecraft.util.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.tileentity.HopperTileEntity.addItem;

@Mixin(HopperTileEntity.class)
public abstract class HopperTileEntityMixin {

    @Inject(method = "tryTakeInItemFromSlot", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;copy()Lnet/minecraft/item/ItemStack;", shift = At.Shift.AFTER), cancellable = true)
    private static void injectTryTakeInItemFromSlot(IHopper hopper, IInventory inventory, int slot, Direction side, CallbackInfoReturnable<Boolean> cir) {
        if (inventory instanceof ArrayFullInventory) {
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack composterItemStack = addItem(inventory, hopper, inventory.removeItem(i, inventory.getItem(i).getCount()), null);
            }
        }
    }

}
