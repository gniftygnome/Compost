package dev.yurisuika.compost.mixin.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ComposterBlock.class)
public interface ComposterBlockInvoker {

    @Invoker("empty")
    static BlockState invokeEmpty(BlockState state, LevelAccessor level, BlockPos pos) {
        throw new AssertionError();
    }

}