package dev.yurisuika.compost.mixin.command.arguments;

import net.minecraft.command.arguments.ItemInput;
import net.minecraft.nbt.CompoundNBT;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemInput.class)
public interface ItemInputAccessor {

    @Accessor
    CompoundNBT getTag();

}