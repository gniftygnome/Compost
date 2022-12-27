package com.yurisuika.compost.mixin.command.argument;

import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemStackArgument.class)
public interface ItemStackArgumentAccessor {

    @Accessor
    NbtCompound getNbt();

}