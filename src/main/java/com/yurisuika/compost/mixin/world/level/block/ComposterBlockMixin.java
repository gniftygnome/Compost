package com.yurisuika.compost.mixin.world.level.block;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.ComposterBlock;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ComposterBlock.class)
public class ComposterBlockMixin {

    @Redirect(method = "extractProduce", at = @At(value = "FIELD", target = "Lnet/minecraft/world/item/Items;BONE_MEAL:Lnet/minecraft/world/item/Item;", opcode = Opcodes.GETSTATIC))
    private static Item redirectEmptyFullComposter() {
        return Items.DIRT;
    }

    @Redirect(method = "getContainer", at = @At(value = "FIELD", target = "Lnet/minecraft/world/item/Items;BONE_MEAL:Lnet/minecraft/world/item/Item;", opcode = Opcodes.GETSTATIC))
    private Item redirectGetInventory() {
        return Items.DIRT;
    }

}
