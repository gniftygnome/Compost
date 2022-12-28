package com.yurisuika.compost.world.level.block;

import com.yurisuika.compost.mixin.world.level.block.ComposterBlockInvoker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class ArrayOutputContainer extends SimpleContainer implements WorldlyContainer {

    private final BlockState state;
    private final LevelAccessor level;
    private final BlockPos pos;
    private boolean changed;

    public ArrayOutputContainer(BlockState state, LevelAccessor level, BlockPos pos, ItemStack[] outputItems) {
        super(outputItems);
        this.state = state;
        this.level = level;
        this.pos = pos;
    }

    public int getMaxStackSize() {
        return 64;
    }

    public int[] getSlotsForFace(Direction side) {
        return side == Direction.DOWN ? new int[]{0} : new int[0];
    }

    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) {
        return false;
    }

    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return !this.changed && dir == Direction.DOWN && !stack.isEmpty();
    }

    public void setChanged() {
        ComposterBlockInvoker.invokeEmpty(this.state, this.level, this.pos);
        this.changed = true;
    }

}