package com.yurisuika.compost.mixin.world.level.block;

import net.minecraft.block.ComposterBlock;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ComposterBlock.FullInventory.class)
public class ComposterBlockFullInventoryMixin {

    @Redirect(method = "canTakeItemThroughFace", at = @At(value = "FIELD", target = "Lnet/minecraft/item/Items;BONE_MEAL:Lnet/minecraft/item/Item;", opcode = Opcodes.GETSTATIC))
    private Item redirectCanExtract() {
        return Items.DIRT;
    }

}
